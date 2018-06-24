package cn.demonk.memorystorage.table;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 表操作
 *
 * @param <T>
 */
public class Table<T> {
    private String mTableName;
    private List<Field> mFields;
    private List<Field> mPrimaryKey;

    public Table(Class<T> table) {
        mFields = new ArrayList<>();
        mPrimaryKey = new ArrayList<>();
        parser(table);
    }

    public String getTableName() {
        return mTableName;
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(mFields);
    }

    public List<Field> getPrimaryList() {
        return Collections.unmodifiableList(mPrimaryKey);
    }

    private void parser(Class<T> table) {
        if (table == null) {
            return;
        }

        TableName tableName = table.getAnnotation(TableName.class);
        if (tableName == null) {
            return;
        }

        this.mTableName = tableName.name();

        Field[] fields = table.getDeclaredFields();
        for (Field field : fields) {
            Key keyAnnotation = field.getAnnotation(Key.class);
            if (keyAnnotation == null)
                continue;

            field.setAccessible(true);
            mFields.add(field);
            if (keyAnnotation.primary()) {
                mPrimaryKey.add(field);
            }
        }
    }

    private String getSqliteType(Class type) {
        if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
            return "INTEGER";
        } else if (type == String.class) {
            return "TEXT";
        } else if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
            return "REAL";
        }
        return "NULL";
    }

    /**
     * 构建创建表sql
     *
     * @return
     */
    public String getCreateTableSql() {
        final StringBuilder primaryKey = new StringBuilder();
        final StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(mTableName + "(");
        Iterator<Field> iterator = mFields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            Key key = field.getAnnotation(Key.class);

            sb.append(key.name());
            sb.append(' ');
            sb.append(getSqliteType(field.getType()));
            sb.append(' ');

            if (key.primary()) {
                if (TextUtils.isEmpty(primaryKey)) {
                    primaryKey.append("PRIMARY KEY(");
                } else {
                    primaryKey.append(',');
                }
                primaryKey.append(key.name());
            }

            if (iterator.hasNext()) {
                sb.append(',');
            }
        }

        if (!TextUtils.isEmpty(primaryKey)) {
            sb.append(',');
            sb.append(primaryKey);
            sb.append(')');
        }

        sb.append(')');

        Log.e("demonk", "create sql:" + sb.toString());
        return sb.toString();
    }

    public ContentValues getInsertContent(T record) throws IllegalAccessException {
        ContentValues contentValues = new ContentValues();
        Iterator<Field> iterator = mFields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            Key key = field.getAnnotation(Key.class);

            contentValues.put(key.name(), String.valueOf(field.get(record)));
        }
        return contentValues;
    }

    public String getUpdateSql(T record) throws IllegalAccessException {
        if (record == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(mTableName);
        sb.append(" SET ");

        Iterator<Field> iterator = mFields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            Key key = field.getAnnotation(Key.class);

            sb.append(key.name());
            sb.append('=');

            if (Aggregation.SUM == key.aggregation()) {
                //相加聚合
                sb.append(key.name());
                sb.append('+');
            }

            boolean isString = field.getType().equals(String.class);
            if (isString) {
                sb.append('\'');
            }
            sb.append(field.get(record));
            if (isString) {
                sb.append('\'');
            }

            if (iterator.hasNext()) {
                sb.append(',');
            }
        }
        Log.e("demonk", "update sql=" + sb.toString());
        return sb.toString();
    }

    public String getUpdateForceSql(T record) throws IllegalAccessException {
        if (record == null) {
            return "";
        }

        Map<Field, Object> kv = new HashMap<>(mFields.size());
        for (Field field : mFields) {
            Object value = field.get(record);
            kv.put(field, value);
        }

        final StringBuilder sql = new StringBuilder("INSERT OR REPLACE INTO ");
        sql.append(mTableName);

        //主键条件
        final StringBuilder whereSB = new StringBuilder(" WHERE ");
        final int primarySize = mPrimaryKey.size();
        for (int i = 0; i < primarySize; i++) {
            Field field = mPrimaryKey.get(i);
            Object object = kv.get(field);
            boolean isStr = object instanceof String;

            Key key = field.getAnnotation(Key.class);
            whereSB.append(key.name());
            whereSB.append('=');
            if (isStr) whereSB.append('\'');
            whereSB.append(object);
            if (isStr) whereSB.append('\'');
            if (i + 1 < primarySize) {
                whereSB.append(" AND ");
            }
        }

        final StringBuilder columns = new StringBuilder();
        final StringBuilder values = new StringBuilder();

        Iterator<Map.Entry<Field, Object>> iter = kv.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Field, Object> entry = iter.next();
            Field field = entry.getKey();
            Object value = entry.getValue();
            boolean isStr = value instanceof String;

            Key key = field.getAnnotation(Key.class);
            columns.append(key.name());
            if (isStr) values.append('\'');
            values.append(String.valueOf(value));
            if (isStr) values.append('\'');

            if (Aggregation.SUM == key.aggregation()) {
                values.append("+IFNULL((");
                values.append("SELECT ").append(key.name()).append(" FROM ").append(mTableName);
                values.append(whereSB);
                values.append("),0)");
            }

            if (iter.hasNext()) {
                columns.append(',');
                values.append(',');
            }
        }

        sql.append('(');
        sql.append(columns);
        sql.append(')').append(' ');

        sql.append("values(");
        sql.append(values);
        sql.append(')');

        Log.e("demonk", "update or insert sql:" + sql);
        return sql.toString();
    }


    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TableName {
        String name();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        String name();

        boolean primary() default false;

        Aggregation aggregation() default Aggregation.NONE;
    }

    public enum Aggregation {
        NONE,
        SUM,
        COUNT,
        AGV
    }
}

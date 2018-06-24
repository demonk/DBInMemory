package cn.demonk.memorystorage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.demonk.memorystorage.table.Table;

/**
 * 联立底层db,操纵数据表
 */
public class CacheDatabase<T> {
    private static final String FILE_DB_ALIAS = "file_db";
    private Table<T> mTable;

    public CacheDatabase(Table<T> table) {
        mTable = table;

        final String fileDbPath = getFileDB().getPath();
        SQLiteDatabase memoryDB = getMemoryDB();
        String attachSql = String.format("attach database '%s' as %s", fileDbPath, FILE_DB_ALIAS);
        memoryDB.execSQL(attachSql);

        createTable();
    }


    private void createTable() {
        String createSql = mTable.getCreateTableSql();
        execSql(createSql);
    }

    /**
     * 更新数据（只更memory）
     *
     * @param record
     */
    public void write(T record) {
        //OPT:INSERT OR REPLACE INTO table_name(open_count,package,cycle,time_cost) values(2+IFNULL((select open_count from table_name where package='cn.demonk.dbinmemory5'),0),'cn.demonk.dbinmemory5',2018062420,1000+IFNULL((select time_cost from table_name where  package='cn.demonk.dbinmemory5'),0));
//        String insertOrUpdateSql = null;
//        try {
//            ContentValues contentValue = mTable.getInsertContent(record);
//            long insertResult = getMemoryDB().insert(mTable.getTableName(), null, contentValue);
//            if (insertResult == -1) {
//                insertOrUpdateSql = mTable.getUpdateSql(record);
//                getMemoryDB().execSQL(insertOrUpdateSql);
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        try {
            String insertOrUpdateSql = mTable.getUpdateForceSql(record);
            getMemoryDB().execSQL(insertOrUpdateSql);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查找记录(只会在file_db中查找）
     *
     * @param selection
     * @param selectionArgs
     * @return dont't forget close cursor
     */
    public Cursor query(String[] selection, String[] selectionArgs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selection.length; i++) {
            sb.append(selection[i]);
            sb.append("=?");
            if (i + 1 < selection.length) {
                sb.append(" AND ");
            }
        }

        Cursor cursor = getFileDB().query(
                mTable.getTableName(),
                getFieldName(),
                sb.toString(),
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    private String[] getFieldName() {
        List<Field> fieldList = mTable.getFields();
        String[] fieldName = new String[fieldList.size()];

        for (int i = 0; i < fieldList.size(); i++) {
            fieldName[i] = fieldList.get(i).getAnnotation(Table.Key.class).name();
        }
        return fieldName;
    }


    /**
     * 删除记录
     *
     * @param sample
     */
    public void delete(T sample) {

    }

    /**
     * 同步数据库(memory-->file)
     *
     * @return
     */
    public void sync() {
        //SAMPLE
        //        INSERT OR REPLACE INTO
        //        file_db.table_name
        //        SELECT
        //        mem.table_name.open_count+IFNULL(file_db.table_name.open_count,0),
        //        mem.table_name.package,
        //        mem.table_name.cycle,
        //        mem.table_name.time_cost+IFNULL(file_db.table_name.time_cost,0)
        //        FROM
        //        table_name mem.table_name LEFT JOIN file_db.table_name
        //        ON mem.table_name.package=file_db.table_name.package AND mem.table_name.cycle=file_db.table_name.cycle
        //        GROUP BY mem.table_name.package,mem.table_name.cycle

        List<Field> fieldList = mTable.getFields();

        List<Field> primaryKeyList = new ArrayList<>();

        for (Field field : fieldList) {
            Table.Key key = field.getAnnotation(Table.Key.class);
            if (key.primary()) {
                primaryKeyList.add(field);
            }
        }

        final String memDbAliasName = "mem" + "_" + mTable.getTableName();
        final String memDbName = memDbAliasName + ".";
        final String fileDbAliasName = FILE_DB_ALIAS + "." + mTable.getTableName();
        final String fileDbName = fileDbAliasName + ".";//文件数据库表的别称,为了不冲突

        StringBuilder sb = new StringBuilder("INSERT OR REPLACE INTO ");
        sb.append(FILE_DB_ALIAS);
        sb.append('.');
        sb.append(mTable.getTableName());
        sb.append(" SELECT ");

        Iterator<Field> iter = fieldList.iterator();
        //select
        while (iter.hasNext()) {
            Field field = iter.next();
            Table.Key key = field.getAnnotation(Table.Key.class);

            sb.append(memDbName).append(key.name());
            if (key.aggregation() == Table.Aggregation.SUM) {
                sb.append('+');
                sb.append("IFNULL(");
                sb.append(fileDbName).append(key.name());
                sb.append(",0)");
            }

            if (iter.hasNext()) {
                sb.append(',');
            }
        }

        //from
        sb.append(" FROM ");

        //join
        sb.append(mTable.getTableName());
        sb.append(' ').append(memDbAliasName);
        sb.append(" LEFT JOIN ");
        sb.append(FILE_DB_ALIAS).append('.').append(mTable.getTableName());

        //on
        sb.append(" ON ");
        iter = primaryKeyList.iterator();
        while (iter.hasNext()) {
            Field field = iter.next();
            Table.Key key = field.getAnnotation(Table.Key.class);

            sb.append(memDbName).append(key.name());
            sb.append('=');
            sb.append(fileDbName).append(key.name());

            if (iter.hasNext()) {
                sb.append(" AND ");//目前只有AND
            }
        }

        //group by
        sb.append(" GROUP BY ");
        iter = primaryKeyList.iterator();
        while (iter.hasNext()) {
            Field field = iter.next();
            Table.Key key = field.getAnnotation(Table.Key.class);

            sb.append(memDbName).append(key.name());

            if (iter.hasNext()) {
                sb.append(',');
            }
        }

        Log.e("demonk", "sync=" + sb);

        getMemoryDB().execSQL(sb.toString());
    }

    private void execSql(String sql) {
        getMemoryDB().execSQL(sql);
        getFileDB().execSQL(sql);
    }

    private SQLiteDatabase getFileDB() {
        return DatabaseHelper.instance().getFileDb().getWritableDatabase();
    }

    private SQLiteDatabase getMemoryDB() {
        return DatabaseHelper.instance().getMemoryDb().getWritableDatabase();
    }
}

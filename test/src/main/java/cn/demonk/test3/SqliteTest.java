package cn.demonk.test3;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/22/18.
 */
public class SqliteTest {

    public static void test(Activity ctx) {
        File file_db = new File(ctx.getCacheDir(), "file_database.db");
        SQLiteDatabase.openOrCreateDatabase(file_db.getAbsolutePath(), null);

        //        SQLiteOpenHelper dbHelper = new DBHandler(ctx, "test");
        SQLiteOpenHelper memoryHelper = new DBHandler(ctx, null);
        //        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //        SQLiteDatabase memoryDB = SQLiteDatabase.create(null);
        SQLiteDatabase memoryDB = memoryHelper.getWritableDatabase();
        String sql = String.format("ATTACH DATABASE '%s' AS %s", file_db.getAbsolutePath(), "file_db");
        memoryDB.execSQL(sql);

        ContentValues cv = new ContentValues();
        cv.put("package", "com.demonk.cn");
        cv.put("open_count", 50);
        cv.put("time_cost", 90);

        memoryDB.insert("rism", null, cv);

        cv = new ContentValues();
        cv.put("package", "com.demonk.cnn");
        cv.put("open_count", 80);
        cv.put("time_cost", 90);

        memoryDB.insert("rism", null, cv);

        //        insert into player_count(player_id,count) value(1,1) on duplicate key update count=count+1;//插入或更新

        String updateSql = "update rism set open_count=open_count+30,time_cost=time_cost+10000 where package='com.demonk.cn'";
        memoryDB.execSQL(updateSql);

        Cursor cursor = memoryDB.query("rism", new String[]{"package,open_count,time_cost,cur_time"}, "", null, "", "", "");
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            int open_count = cursor.getInt(1);
            int time_cost = cursor.getInt(2);
            long time = cursor.getLong(3);
            Log.e("demonk", "package=" + packageName + ",open_count=" + open_count + ",time_cost=" + time_cost + ",time=" + time);
        }

        //        memoryDB.execSQL("INSERT OR REPLACE INTO file_db.rism SELECT * FROM rism");
        memoryDB.execSQL("INSERT OR REPLACE INTO file_db.rism " +
                "SELECT rism.package, " +
                "rism.open_count+IFNULL(rism_fd.open_count,0)," +
                "rism.time_cost+IFNULL(rism_fd.time_cost,0)," +
                "rism.cur_time " +
                "FROM rism LEFT JOIN file_db.rism rism_fd " +
                "ON rism.package=rism_fd.package and rism.cur_time=rism_fd.cur_time " +
                "group by rism.package,rism.cur_time");

        String detach = String.format("detach database '%s' ", "file_db");
        memoryDB.execSQL(detach);
    }
}

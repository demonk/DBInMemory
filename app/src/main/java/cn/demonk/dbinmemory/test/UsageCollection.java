package cn.demonk.dbinmemory.test;

import android.database.Cursor;
import android.util.Log;

import cn.demonk.memorystorage.CacheDatabase;
import cn.demonk.memorystorage.table.Table;

public class UsageCollection {


    public static void testCreate() {
        Table<LogTable> logTable = new Table(LogTable.class);
        CacheDatabase<LogTable> cdb = new CacheDatabase(logTable);

        LogTable table = new LogTable();
        table.openCount = 1;
        table.packageName = "cn.demonk.dbinmemory";
        table.timeCost = 1;
        table.period = 2018062422;
        cdb.write(table);

        table.timeCost = 2;
        cdb.write(table);

        cdb.sync();

        Cursor cursor = cdb.query(new String[]{"package", "cycle"}, new String[]{"cn.demonk.dbinmemory", "2018062422"});
        while (cursor.moveToNext()) {
            String a = cursor.getString(0);
            String b = cursor.getString(1);
            String c = cursor.getString(2);
            String d = cursor.getString(3);
            Log.e("demonk", String.format("%s,%s,%s,%s", a, b, c, d));
        }
    }
}

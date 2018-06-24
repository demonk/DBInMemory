package cn.demonk.test3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by guosen.lgs@alibaba-inc.com on 6/22/18.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                     int version) {
        //必须通过super调用父类的构造函数
        super(context, name, factory, version);
    }

    public DBHandler(Context context, String name) {
        this(context, name, VERSION);
    }

    public DBHandler(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        System.out.println("create a Database");
        db.execSQL("create table rism(package varchar(20),open_count integer,time_cost integer,cur_time TIMESTAMP default (strftime('%Y%m%d%H','now','localtime')), PRIMARY KEY(package,cur_time))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        System.out.println("update a Database");
    }
}

package cn.demonk.memorystorage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.demonk.memorystorage.utils.AppContext;

/**
 * save log as db record
 */
public class DatabaseHelper {
    private static final String DB_NAME = "log_collection.db";
    private static final int VERSION = 1;

    private Database mMemoryDb;
    private Database mFileDb;

    private static class DatabaseHelperInstance {
        private static DatabaseHelper INSTANCE = new DatabaseHelper();
    }

    public static final DatabaseHelper instance() {
        return DatabaseHelperInstance.INSTANCE;
    }

    private DatabaseHelper() {
        mMemoryDb = new Database(null, VERSION);
        mFileDb = new Database(DB_NAME, VERSION);
    }

    /**
     * 获取文件数据库
     *
     * @return
     */
    public Database getFileDb() {
        return mFileDb;
    }

    /**
     * 获取内存数据库
     *
     * @return
     */
    public Database getMemoryDb() {
        return mMemoryDb;
    }

    public static class Database extends SQLiteOpenHelper {

        public Database(String name, int VERSION) {
            this(name, null, VERSION);
        }

        public Database(String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(AppContext.appContext(), name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}

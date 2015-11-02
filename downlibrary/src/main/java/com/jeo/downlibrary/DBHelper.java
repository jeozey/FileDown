package com.jeo.downlibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 志文 on 2015/10/31 0031.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DownFile.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "FILE_DOWN";
    public static final String _ID = "_ID";
    public static final String NAME = "NAME";
    public static final String URL = "URL";
    public static final String PATH = "PATH";
    public static final String MD5 = "MD5";
    public static final String FINISH_SIZE = "FINISH_SIZE";
    public static final String ALL_SIZE = "ALL_SIZE";
    public static final String STATUS = "STATUS";
    public static final String CREATE_TIME = "CREATE_TIME";

    private static final String DB_CREATE = "CREATE TABLE IF NOT EXISTS FILE_DOWN " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " VARCHAR," + URL + " VARCHAR,PATH VARCHAR," + PATH + " VARCHAR," +
            MD5 + " VARCHAR," + FINISH_SIZE + " LONG," + ALL_SIZE + " LONG," + STATUS + " VARCHAR," + CREATE_TIME + " DATE)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //当DB_VERSION大于现有的版本时候 会调用此方法，一样用于修改表结构等
    }
}

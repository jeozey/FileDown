package com.jeo.filedown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.style.TtsSpan;

import com.jeo.filedown.FileItem;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 志文 on 2015/10/31 0031.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context){
        helper = new DBHelper(context);
        
        db = helper.getWritableDatabase();
    }
    
    public void add(List<FileItem> files){
        db.beginTransaction();
        try {
            for (FileItem item :
                    files) {
                db.execSQL("INSERT INTO "+DBHelper.TABLE_NAME+" VALUES(NULL,?,?,?,?,?,?)", new Object[]{item.getName(), item.getUrl(), item.getMd5(),
                item.getPath(),item.getCurrentLength(),item.getAllLength()});
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    public void setAllSize(FileItem file){
        ContentValues values = new ContentValues();
        values.put(DBHelper.ALL_SIZE,file.getAllLength());
        db.update(DBHelper.TABLE_NAME, values, DBHelper._ID, new String[]{"" + file.getId()});
    }
    public void setCurrentSize(FileItem file){
        ContentValues values = new ContentValues();
        values.put(DBHelper.CURRENT_SIZE,file.getCurrentLength());
        db.update(DBHelper.TABLE_NAME,values,DBHelper._ID,new String[]{""+file.getId()});
    }

    public List<FileItem> getAllFilesNotDown(){
        ArrayList<FileItem> files = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from "+DBHelper.TABLE_NAME+" where "+DBHelper.CURRENT_SIZE+"<"+"DBHelper.ALL_SIZE",null);
        while (cursor.moveToNext()){
            FileItem item = new FileItem();
            item.setId(cursor.getInt(cursor.getColumnIndex(DBHelper._ID)));
            item.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            item.setUrl(cursor.getString(cursor.getColumnIndex(DBHelper.URL)));
            item.setMd5(cursor.getString(cursor.getColumnIndex(DBHelper.MD5)));
            item.setCurrentLength(cursor.getLong(cursor.getColumnIndex(DBHelper.CURRENT_SIZE)));
            item.setAllLength(cursor.getLong(cursor.getColumnIndex(DBHelper.ALL_SIZE)));

//            try {
//                String myDate =cursor.getString(cursor.getColumnIndex("datetime("+DBHelper.CREATE_TIME+",'localtime')"));
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                Date date = format.parse(myDate);
//                item.setCreateTime(date));
//            }catch (ParseException e){
//                e.printStackTrace();
//            }

        }
        cursor.close();
        return files;
    }

    public void closeDB(){
        db.close();
    }
}

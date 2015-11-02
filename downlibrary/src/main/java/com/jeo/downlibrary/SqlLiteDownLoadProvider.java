package com.jeo.downlibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class SqlLiteDownLoadProvider implements DownLoadProvider{
    private static SqlLiteDownLoadProvider instance ;
    private static DBHelper helper;
    private static SQLiteDatabase db;

    private SqlLiteDownLoadProvider(){

    }

    public static  synchronized SqlLiteDownLoadProvider getInstance(Context context){
        if(instance==null) {
            helper = new DBHelper(context);
            db = helper.getWritableDatabase();

            instance = new SqlLiteDownLoadProvider();
        }
        return instance;
    }

    private ContentValues createDownLoadTaskValues(DownLoadTask task){
        ContentValues values = new ContentValues();
        values.put(DBHelper.NAME,task.getName());
        values.put(DBHelper.URL,task.getUrl());
        values.put(DBHelper.MD5,task.getMd5());
        values.put(DBHelper.PATH,task.getPath());
        values.put(DBHelper.FINISH_SIZE,task.getFinishSize());
        values.put(DBHelper.ALL_SIZE,task.getAllSize());
        values.put(DBHelper.STATUS,task.getStatus());
        return values;
    }

    private DownLoadTask restoreDownLoadTaskFromCursor(Cursor cursor){
        DownLoadTask task = new DownLoadTask();
        task.setId(cursor.getInt(cursor.getColumnIndex(DBHelper._ID)));
        task.setName(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
        task.setUrl(cursor.getString(cursor.getColumnIndex(DBHelper.URL)));
        task.setMd5(cursor.getString(cursor.getColumnIndex(DBHelper.MD5)));
        task.setFinishSize(cursor.getLong(cursor.getColumnIndex(DBHelper.FINISH_SIZE)));
        task.setAllSize(cursor.getLong(cursor.getColumnIndex(DBHelper.ALL_SIZE)));
        task.setStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.STATUS)));
        return task;
    }
    @Override
    public void saveDownTask(DownLoadTask task) {
        try {

//            db.execSQL("INSERT INTO "+DBHelper.TABLE_NAME+" VALUES(NULL,?,?,?,?,?,?,?,date())", new Object[]{task.getName(), task.getUrl(), task.getMd5(),
//                    task.getPath(),task.getFinishSize(),task.getAllSize(),task.getStatus()});
            ContentValues values = createDownLoadTaskValues(task);
            db.insert(DBHelper.TABLE_NAME,null,values);

            notifyDownLoadStatusChanged(task);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateDownTask(DownLoadTask task) {
        try {
            ContentValues values = createDownLoadTaskValues(task);
            db.update(DBHelper.TABLE_NAME, values, DBHelper._ID+"=?",new String[]{""+task.getId()});

            notifyDownLoadStatusChanged(task);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public DownLoadTask findDownLoadTaskById(int id) {
        DownLoadTask task = null;
        Cursor cursor = db.query(DBHelper.TABLE_NAME,null,DBHelper._ID+"=?",new String[]{""+id},null,null,null);
        if(cursor.moveToNext()){
            task =restoreDownLoadTaskFromCursor(cursor);
        }
        cursor.close();

        return task;
    }

    @Override
    public DownLoadTask findDownLoadTask(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        DownLoadTask task = null;
        Cursor cursor = db.query(DBHelper.TABLE_NAME,columns,selection,selectionArgs,groupBy,having,orderBy);
        if(cursor.moveToNext()){
            task =restoreDownLoadTaskFromCursor(cursor);
        }
        cursor.close();

        return task;
    }

    @Override
    public List<DownLoadTask> getAllDownLoadTask() {
        List<DownLoadTask> tasks = new ArrayList<>();
        DownLoadTask task = null;
        Cursor cursor = db.query(DBHelper.TABLE_NAME,null,null,null,null,null,DBHelper.STATUS);
        if(cursor.moveToNext()){
            task =restoreDownLoadTaskFromCursor(cursor);
            tasks.add(task);
        }
        cursor.close();

        return tasks;
    }

    @Override
    public void notifyDownLoadStatusChanged(DownLoadTask task) {

    }

    @Override
    public void clearAllData() {
        db.delete(DBHelper.TABLE_NAME,null,null);
    }

    @Override
    public void delete(int id) {
        db.delete(DBHelper.TABLE_NAME,DBHelper._ID+"=?",new String[]{""+id});
    }
}

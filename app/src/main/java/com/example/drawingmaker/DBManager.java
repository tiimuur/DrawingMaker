package com.example.drawingmaker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private final DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void openDB(){
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDB(String title, String pic){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TITLE, String.valueOf(title));
        contentValues.put(Constants.PIC, String.valueOf(pic));
        db.insert(Constants.TABLE_NAME, null, contentValues);
    }

    public void deleteInDB(int id){
        String selection = Constants._ID + "=" + id;
        db.delete(Constants.TABLE_NAME, selection, null);
    }

    @SuppressLint("Range")
    public List<ListItem> getFromDB(String searching){
        List<ListItem> tempList = new ArrayList<>();
        String selection = Constants.TITLE + " like ?";
        Cursor cursor = db.query(Constants.TABLE_NAME, null, selection,
                new String[]{"%" + searching + "%"}, null, null, null);
        while (cursor.moveToNext()){
            ListItem item = new ListItem();
            String title = cursor.getString(cursor.getColumnIndex(Constants.TITLE));
            String pic = cursor.getString(cursor.getColumnIndex(Constants.PIC));
            int _id = cursor.getInt(cursor.getColumnIndex(Constants._ID));
            item.setId(_id);
            item.setTitle(title);
            item.setPic(pic);
            tempList.add(item);
        }
        cursor.close();
        return tempList;
    }

    @SuppressLint("Range")
    public List<ListItem> getFromDB(){
        List<ListItem> tempList = new ArrayList<>();
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null,
                null, null, null, null);
        while (cursor.moveToNext()){
            ListItem item = new ListItem();
            String title = cursor.getString(cursor.getColumnIndex(Constants.TITLE));
            String pic = cursor.getString(cursor.getColumnIndex(Constants.PIC));
            int _id = cursor.getInt(cursor.getColumnIndex(Constants._ID));
            item.setId(_id);
            item.setTitle(title);
            item.setPic(pic);
            tempList.add(item);
        }
        cursor.close();
        return tempList;
    }



    public void closeDB(){
        dbHelper.close();
    }

}

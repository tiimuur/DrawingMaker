package com.example.drawingmaker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void openDB(){
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDB(String title, String pic){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TITLE, String.valueOf(title));
        contentValues.put(Constants.PIC, pic);
        db.insert(Constants.TABLE_NAME, null, contentValues);
    }

    public List<String> getTitlesFromDB(){
        List<String> tempList = new ArrayList<>();
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null,
                null, null, null, null);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(Constants.TITLE));
            tempList.add(title);
        }
        cursor.close();
        return tempList;
    }

    @SuppressLint("Range")
    public String getPicFromDB(){
        Cursor cursor = db.query(Constants.TABLE_NAME, null, null,
                null, null, null, null);
        return cursor.getString(cursor.getColumnIndex(Constants.PIC));
    }


    public void closeDB(){
        dbHelper.close();
    }

}

package com.example.drawingmaker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;


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

    public void insertToDB(String title, byte[] move, byte[] colors, byte[] brushes){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TITLE, String.valueOf(title));
        contentValues.put(Constants.MOVE, move);
        contentValues.put(Constants.COLOR, colors);
        contentValues.put(Constants.BRUSH, brushes);
        db.insert(Constants.TABLE_NAME, null, contentValues);
    }
    public void insertToDB(String title){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.TITLE, String.valueOf(title));
        db.insert(Constants.TABLE_NAME, null, contentValues);
    }

    public void insertToDB(byte[] move){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.MOVE, move);
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
    public byte[] getMoveListFromDB(String title){
        byte[] move;
        Cursor cursor = db.query(Constants.TABLE_NAME, null, Constants.MOVE_SELECTION + title,
                null, null, null, null);
        move = cursor.getBlob(cursor.getColumnIndex(Constants.MOVE));
        cursor.close();
        return move;
    }

    @SuppressLint("Range")
    public byte[] getColorListFromDB(String title){
        byte[] move;
        Cursor cursor = db.query(Constants.TABLE_NAME, null, Constants.MOVE_SELECTION + title,
                null, null, null, null);
        move = cursor.getBlob(cursor.getColumnIndex(Constants.MOVE));
        cursor.close();
        return move;
    }

    @SuppressLint("Range")
    public byte[] getBrushListFromDB(String title){
        byte[] move;
        Cursor cursor = db.query(Constants.TABLE_NAME, null, Constants.MOVE_SELECTION + title,
                null, null, null, null);
        move = cursor.getBlob(cursor.getColumnIndex(Constants.MOVE));
        cursor.close();
        return move;
    }

    public void closeDB(){
        dbHelper.close();
    }

}

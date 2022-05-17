package com.example.drawingmaker;

public class Constants {
    public static final String TABLE_NAME = "Pictures";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String PIC = "pic_link";
    public static final String DB_NAME = "picturesDb.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " ( " + _ID + " INTEGER PRIMARY KEY," +
            TITLE + " TEXT," + PIC + " TEXT)";

    public static final  String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

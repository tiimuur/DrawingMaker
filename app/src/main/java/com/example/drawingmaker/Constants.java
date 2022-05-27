package com.example.drawingmaker;

public class Constants {
    public static final String TABLE_NAME = "Pictures";
    public static final String _ID = "_id";
    public static final String TITLE = "title";
    public static final String MOVE = "move";
    public static final String COLOR = "color";
    public static final String BRUSH = "brush";
    public static final String DB_NAME = "picturesDb.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " ( " + _ID + " INTEGER PRIMARY KEY," +
            TITLE + " TEXT," + MOVE + " BLOB," + COLOR + " BLOB," + BRUSH + " BLOB)" ;

    public static final  String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String MOVE_SELECTION = "SELECT " + MOVE + " FROM " + TABLE_NAME + " WHERE " + TITLE + " = ";
    public static final String COLOR_SELECTION = "SELECT " + COLOR + " FROM " + TABLE_NAME + " WHERE " + TITLE + " = ";
    public static final String BRUSH_SELECTION = "SELECT " + BRUSH + " FROM " + TABLE_NAME + " WHERE " + TITLE + " = ";
}

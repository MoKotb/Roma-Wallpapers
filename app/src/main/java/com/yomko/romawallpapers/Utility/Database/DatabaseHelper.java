package com.yomko.romawallpapers.Utility.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "roma.db";
    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME + " ("
                + DatabaseContract.DatabaseEntry.IMAGE_UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DatabaseContract.DatabaseEntry.IMAGE_ID + " TEXT, "
                + DatabaseContract.DatabaseEntry.IMAGE_NAME + " TEXT, "
                + DatabaseContract.DatabaseEntry.IMAGE_URL + " TEXT, "
                + DatabaseContract.DatabaseEntry.USER_ID + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.IMAGE_TABLE_NAME);
        onCreate(db);
    }
}

package com.street.larch.a420.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by irteza.arif on 2017-04-25.
 */

public class BrosDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "contacts.db";

    private static final int DATABASE_VERSION = 1;

    public BrosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                BrosContract.BrosEntry.TABLE_NAME + " (" +
                BrosContract.BrosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BrosContract.BrosEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                BrosContract.BrosEntry.COLUMN_NUMBER + " INTEGER NOT NULL," +
                BrosContract.BrosEntry.COLUMN_MESSAGE + " TEXT NOT NULL, " +
                BrosContract.BrosEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");";
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BrosContract.BrosEntry.TABLE_NAME);
        onCreate(db);
    }
}

package com.example.mysavings.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class TransactionDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "transaction.db";
    private static final int DATABASE_VERSION = 1;

    public TransactionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY, description TEXT, type TEXT, value DOUBLE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
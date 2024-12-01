package com.example.gp_test;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ResultsDB";
    private static final String TABLE_NAME = "SavedResults";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_CODE = "Code";

    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_CODE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEntry(String name, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_CODE, code);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> result = new HashMap<>();
                result.put("name", cursor.getString(cursor.getColumnIndex(COL_NAME)));
                result.put("code", cursor.getString(cursor.getColumnIndex(COL_CODE)));
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultList;
    }
}

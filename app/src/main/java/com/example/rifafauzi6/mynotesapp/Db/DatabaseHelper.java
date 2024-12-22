package com.example.rifafauzi6.mynotesapp.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.NoteColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dbnoteapp";
    private static final int DATABASE_VERSION = 3;

    private static final String SQL_CREATE_TABLE_NOTE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s REAL NOT NULL)",
            DatabaseContract.TABLE_NOTE,
            NoteColumns._ID,
            NoteColumns.TITLE,
            NoteColumns.DESCRIPTION,
            NoteColumns.DATE,
            NoteColumns.PRICE

    );

    private static final String SQL_CREATE_TABLE_USER = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_USERS,
            NoteColumns._ID,
            DatabaseContract.UserColumns.EMAIL,
            DatabaseContract.UserColumns.PASSWORD
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_TABLE_NOTE);

        db.execSQL(SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // Hanya menambah kolom jika perlu
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE " + DatabaseContract.TABLE_NOTE + " ADD COLUMN " + DatabaseContract.NoteColumns.PRICE + " REAL NOT NULL DEFAULT 0");
            }
        }
    }

}

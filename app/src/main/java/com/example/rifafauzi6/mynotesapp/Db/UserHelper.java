package com.example.rifafauzi6.mynotesapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.rifafauzi6.mynotesapp.Entity.User;

public class UserHelper {
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    public UserHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.UserColumns.EMAIL, user.getEmail());
        values.put(DatabaseContract.UserColumns.PASSWORD, user.getPassword());
        return database.insert(DatabaseContract.TABLE_USERS, null, values);
    }


    public boolean checkUserExist(String email) {
        String[] columns = {DatabaseContract.UserColumns._ID};
        String selection = DatabaseContract.UserColumns.EMAIL + " = ?";
        String[] selectionArgs = {email};  // Mengecek berdasarkan email
        Cursor cursor = database.query(DatabaseContract.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        return cursorCount > 0;
    }


    public User getUserByEmail(String email) {
        String[] columns = {DatabaseContract.UserColumns._ID, DatabaseContract.UserColumns.EMAIL, DatabaseContract.UserColumns.PASSWORD};
        String selection = DatabaseContract.UserColumns.EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = database.query(DatabaseContract.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserColumns._ID));
            String userEmail = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserColumns.EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserColumns.PASSWORD));
            cursor.close();
            return new User(id, userEmail, password);
        }
        cursor.close();
        return null;
    }
}

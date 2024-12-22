package com.example.rifafauzi6.mynotesapp.Db;

import android.provider.BaseColumns;

public class DatabaseContract {

    static String TABLE_NOTE = "note";

    public static final class NoteColumns implements BaseColumns {
        static String TITLE = "title";
        static String DESCRIPTION = "description";
        static String DATE = "date";
        static String PRICE = "price";
    }

    public static final String TABLE_USERS = "users";

    public static final class UserColumns implements BaseColumns {

        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }
}

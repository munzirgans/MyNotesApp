package com.example.rifafauzi6.mynotesapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.rifafauzi6.mynotesapp.Entity.Note;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.NoteColumns.TITLE;
import static com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.NoteColumns.DATE;
import static com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.TABLE_NOTE;
import static com.example.rifafauzi6.mynotesapp.Db.DatabaseContract.NoteColumns.PRICE;

public class NoteHelper {
    private static String DATABASE_TABLE = TABLE_NOTE;
    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    public NoteHelper(Context context){
        this.context = context;
    }

    public NoteHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    /**
     * Gunakan method ini untuk ambil semua note yang ada
     * Otomatis di parsing ke dalam model Note
     * @return hasil query berbentuk array model note
     */
    public ArrayList<Note> query(){
        ArrayList<Note> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null, null, null, null, null, _ID + " DESC", null);
        cursor.moveToFirst();
        Note note;
        if (cursor.getCount() > 0) {
            do {
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                note.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE)));

                arrayList.add(note);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    /**
     * Gunakan method ini untuk query insert
     * @param note model note yang akan dimasukkan
     * @return id dari data yang baru saja dimasukkan
     */
    public long insert(Note note) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TITLE, note.getTitle());
        initialValues.put(DESCRIPTION, note.getDescription());
        initialValues.put(DATE, note.getDate());
        initialValues.put(PRICE, note.getPrice());  // Menambahkan price ke insert
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Gunakan method ini untuk query update
     * @param note model note yang akan diubah
     * @return int jumlah dari row yang ter-update, jika tidak ada yang diupdate maka nilainya 0
     */
    public int update(Note note) {
        ContentValues args = new ContentValues();
        args.put(TITLE, note.getTitle());
        args.put(DESCRIPTION, note.getDescription());
        args.put(DATE, note.getDate());
        args.put(PRICE, note.getPrice());  // Menambahkan price ke update
        return database.update(DATABASE_TABLE, args, _ID + "= '" + note.getId() + "'", null);
    }
    /**
     * Gunakan method ini untuk query delete
     * @param id id yang akan di delete
     * @return int jumlah row yang di delete
     */
    public int delete(int id){
        return database.delete(TABLE_NOTE, _ID + " = '"+id+"'", null);
    }

    public double getTotalPrice() {
        double total = 0;

        Cursor cursor = database.rawQuery("SELECT SUM(" + PRICE + ") FROM " + DATABASE_TABLE, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
            cursor.close();
        }

        return total;
    }
}

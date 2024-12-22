package com.example.rifafauzi6.mynotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.rifafauzi6.mynotesapp.Db.NoteHelper;
import com.example.rifafauzi6.mynotesapp.Entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormAddUpdateActivity extends AppCompatActivity
        implements View.OnClickListener{
    EditText edtTitle, edtDescription, edtPrice;
    Button btnSubmit;

    public static String EXTRA_NOTE = "extra_note";
    public static String EXTRA_POSITION = "extra_position";

    private boolean isEdit = false;
    public static int REQUEST_ADD = 100;
    public static int RESULT_ADD = 101;
    public static int REQUEST_UPDATE = 200;
    public static int RESULT_UPDATE = 201;
    public static int RESULT_DELETE = 301;

    private Note note;
    private int position;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_update);

        edtTitle = (EditText)findViewById(R.id.edt_title);
        edtDescription = (EditText)findViewById(R.id.edt_description);
        edtPrice = (EditText)findViewById(R.id.edt_price);  // Menambahkan edtPrice untuk harga
        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        noteHelper = new NoteHelper(this);
        noteHelper.open();

        note = getIntent().getParcelableExtra(EXTRA_NOTE);

        if (note != null){
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }

        String actionBarTitle = null;
        String btnTitle = null;

        if (isEdit){
            actionBarTitle = "Ubah";
            btnTitle = "Update";
            edtTitle.setText(note.getTitle());
            edtDescription.setText(note.getDescription());
            edtPrice.setText(String.valueOf(note.getPrice()));  // Menampilkan harga saat edit
        }else{
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmit.setText(btnTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null){
            noteHelper.close();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit){
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String priceString = edtPrice.getText().toString().trim();  // Mengambil input harga

            boolean isEmpty = false;

            // Jika ada field yang kosong, tampilkan error
            if (TextUtils.isEmpty(title)){
                isEmpty = true;
                edtTitle.setError("Field can not be blank");
            }

            if (TextUtils.isEmpty(priceString)){
                isEmpty = true;
                edtPrice.setError("Field can not be blank");
            }

            if (!isEmpty){
                double price = 0;
                try {
                    price = Double.parseDouble(priceString);  // Mengkonversi harga ke tipe double
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    edtPrice.setError("Invalid price format");
                    return;
                }

                Note newNote = new Note();
                newNote.setTitle(title);
                newNote.setDescription(description);
                newNote.setPrice(price);  // Menambahkan harga pada objek Note

                Intent intent = new Intent();

                if (isEdit){
                    newNote.setDate(note.getDate());  // Menyimpan tanggal lama jika editing
                    newNote.setId(note.getId());  // Menyimpan ID lama jika editing
                    noteHelper.update(newNote);  // Update ke database

                    intent.putExtra(EXTRA_POSITION, position);  // Mengirimkan posisi yang diperbarui
                    setResult(RESULT_UPDATE, intent);  // Menetapkan hasil UPDATE
                    finish();  // Menutup activity
                }else{
                    newNote.setDate(getCurrentDate());  // Menyimpan tanggal saat ini untuk catatan baru
                    noteHelper.insert(newNote);  // Menyimpan catatan baru ke database

                    setResult(RESULT_ADD);  // Menetapkan hasil ADD
                    finish();  // Menutup activity
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit){
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    final int ALERT_DIALOG_CLOSE = 10;
    final int ALERT_DIALOG_DELETE = 20;

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle = null, dialogMessage = null;

        if (isDialogClose){
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        }else{
            dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
            dialogTitle = "Hapus Note";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isDialogClose){
                            finish();
                        }else{
                            noteHelper.delete(note.getId());  // Menghapus catatan dari database
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);  // Mengirim posisi yang dihapus
                            setResult(RESULT_DELETE, intent);  // Menetapkan hasil DELETE
                            finish();
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

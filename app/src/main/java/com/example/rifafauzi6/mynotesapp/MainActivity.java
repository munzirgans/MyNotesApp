package com.example.rifafauzi6.mynotesapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rifafauzi6.mynotesapp.Adapter.NoteAdapter;
import com.example.rifafauzi6.mynotesapp.Db.NoteHelper;
import com.example.rifafauzi6.mynotesapp.Entity.Note;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.example.rifafauzi6.mynotesapp.FormAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;

    private LinkedList<Note> list;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    private static final double BUDGET_LIMIT = 1000000;
    private static final String CHANNEL_ID = "mynotesapp";
    private static final String CHANNEL_NAME = "MyNotesApp";
    private static final String CHANNEL_DESC = "My Notes App Notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Notes");

        rvNotes = (RecyclerView)findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        noteHelper = new NoteHelper(this);
        noteHelper.open();

        list = new LinkedList<>();

        adapter = new NoteAdapter(this);
        adapter.setListNotes(list);
        rvNotes.setAdapter(adapter);

        new LoadNoteAsync().execute();


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add){
            Intent intent = new Intent(MainActivity.this, FormAddUpdateActivity.class);
            startActivityForResult(intent, FormAddUpdateActivity.REQUEST_ADD);
        }
    }

    private class LoadNoteAsync extends AsyncTask<Void, Void, ArrayList<Note>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return noteHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            progressBar.setVisibility(View.GONE);

            list.addAll(notes);
            adapter.setListNotes(list);
            adapter.notifyDataSetChanged();

            double totalPrice = noteHelper.getTotalPrice();
            TextView tvTotalAngka = findViewById(R.id.tv_total_angka);
            tvTotalAngka.setText("Rp. " + String.format("%,.2f", totalPrice));

            if (totalPrice > BUDGET_LIMIT) {
                displayNotification();
            }

            if (list.size() == 0){
                showSnackbarMessage("Tidak ada data saat ini");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Akan dipanggil jika request codenya ADD
        if (requestCode == FormAddUpdateActivity.REQUEST_ADD){
            if (resultCode == FormAddUpdateActivity.RESULT_ADD){
                new LoadNoteAsync().execute();
                showSnackbarMessage("Satu item berhasil ditambahkan");
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }
        // Update dan Delete memiliki request code sama akan tetapi result codenya berbeda
        else if (requestCode == REQUEST_UPDATE) {
            /*
            Akan dipanggil jika result codenya UPDATE
            Semua data di load kembali dari awal
            */
            if (resultCode == FormAddUpdateActivity.RESULT_UPDATE) {
                new LoadNoteAsync().execute();
                showSnackbarMessage("Satu item berhasil diubah");
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);
            }
            /*
            Akan dipanggil jika result codenya DELETE
            Delete akan menghapus data dari list berdasarkan dari position
            */
            else if (resultCode == FormAddUpdateActivity.RESULT_DELETE) {
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                list.remove(position);
                adapter.setListNotes(list);
                adapter.notifyDataSetChanged();
                showSnackbarMessage("Satu item berhasil dihapus");
            }
        }

        double totalPrice = noteHelper.getTotalPrice();
        TextView tvTotalAngka = findViewById(R.id.tv_total_angka);
        tvTotalAngka.setText("Rp. " + String.format("%,.0f", totalPrice));

        if(totalPrice > BUDGET_LIMIT){
            displayNotification();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null){
            noteHelper.close();
        }
    }

    /**
     * Tampilkan snackbar
     * @param message inputan message
     */
    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("MyNotesApp Notifications")
                .setSmallIcon(R.drawable.ic_login)
                .setContentText("Pengeluaran anda sudah melebihi batas Rp. 1,000,000!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat mNotifMgr = NotificationManagerCompat.from(this);
        mNotifMgr.notify(1, mBuilder.build());
    }
}

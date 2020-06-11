package com.example.senny.noteapp;

/**
 * Created by Senny on 02.06.2020.
 */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.List;
import android.app.AlarmManager;
import java.util.Calendar;
import java.util.Random;
import android.app.PendingIntent;
import android.content.Context;
import android.os.CountDownTimer;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.net.ConnectivityManager;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    private NoteViewModel noteViewModel;
    private TimeUtils timeUtils;
    CountDownTimer cTimer = null;
    SharedPreferences prefs = null;
    private static MainActivity instance;
    ProgressBar progress_horizontal;
    int progrss = 0;
    boolean isFirst = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("com.example.senny.noteapp", MODE_PRIVATE);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        timeUtils = new TimeUtils();
        instance = this;

        if (prefs.getBoolean("firstrun", true)) {
            isFirst = true;
            progressBar.setVisibility(View.VISIBLE);
            SendFakeRequest();
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        if(!isFirst) {
            new fakeRefreshNotesTask().execute();
        }

        setTitle("Заметки");

        int hours = 23;
        int minute = 59;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AssignTask(calendar);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Заметка удалена!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_DATETIME, note.getDisplayDatetime());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });

        noteViewModel.getCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String date = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE);
            String time = data.getStringExtra(AddEditNoteActivity.EXTRA_TIME);
            String datetime = data.getStringExtra(AddEditNoteActivity.EXTRA_DATETIME);
            Note note = new Note(title, description, date, time, datetime);
            noteViewModel.insert(note);
            Toast.makeText(this, "Заметка добавлена!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Замеетка не может быть обновлена!", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String date = data.getStringExtra(AddEditNoteActivity.EXTRA_DATE);
            String time = data.getStringExtra(AddEditNoteActivity.EXTRA_TIME);
            String datetime = data.getStringExtra(AddEditNoteActivity.EXTRA_DATETIME);
            Note note = new Note(title, description, date, time, datetime);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Заметка обновлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Заметка не сохранена!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "Заметки удалены!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AssignTask(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ChangeNoteDateFormat.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ChangeNoteDateFormat.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    void SendFakeRequest() {
        if(!isNetworkConnected()) {
            TextView noInternet = findViewById(R.id.no_internet);
            ProgressBar progressBar = findViewById(R.id.progressBar);

            progressBar.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
            return;
        }

        cTimer = new CountDownTimer(8000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Toast.makeText(MainActivity.this, "seconds remaining: " + String.valueOf(millisUntilFinished / 1000), Toast.LENGTH_SHORT).show();
            }

            public void onFinish() {
                // - таймер почему то заканчиваеться раньше
                // - поэтому на счет компенсации вместо 5 сек, ставлю 8
                gettingFakeAnswer();
                //Toast.makeText(MainActivity.this, "Timer finish!", Toast.LENGTH_SHORT).show();
            }
        };
        cTimer.start();
    }

    void gettingFakeAnswer(){
        String date = timeUtils.GetCurrentDate();
        String time = timeUtils.GetCurrentTime();
        ProgressBar progressBar = findViewById(R.id.progressBar);

        cancelTimer();
        progressBar.setVisibility(View.GONE);
        noteViewModel.insert(new Note("Новая заметка 1", "", date, time, time));
        noteViewModel.insert(new Note("Новая заметка 2", "", date, time, time));
        noteViewModel.insert(new Note("Новая заметка 3", "", date, time, time));
    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public static MainActivity  getInstace(){
        return instance;
    }

    void onConnectionLose(){
        if (!isFirst) return;

        TextView noInternet = findViewById(R.id.no_internet);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        cancelTimer();
        progressBar.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
    }

    void onConnectionRestored(){
        if (!isFirst) return;

        TextView noInternet = findViewById(R.id.no_internet);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        SendFakeRequest();
        progressBar.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
    }

    private static class fakeRefreshNotesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Отключить касание?
            /*MainActivity.getInstace().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity.getInstace().progress_horizontal = (ProgressBar) MainActivity.getInstace().findViewById(R.id.progressBar_horizontal);
            final Random random = new Random();

            try {
                for(int i=0; i<100; i++){
                    MainActivity.getInstace().progrss = MainActivity.getInstace().progrss + random.nextInt(10-0);
                    MainActivity.getInstace().progrss = MainActivity.getInstace().progrss+i;
                    MainActivity.getInstace().progress_horizontal.setProgress(MainActivity.getInstace().progrss);
                    if(MainActivity.getInstace().progrss>=100) break;
                    Thread.sleep(500);
                }
            }
            catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            //MainActivity.getInstace().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            super.onPostExecute(result);
            MainActivity.getInstace().progress_horizontal.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
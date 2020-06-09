package com.example.senny.noteapp;

/**
 * Created by Senny on 02.06.2020.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.codinginflow.architectureexample.EXTRA_ID";
    public static final String EXTRA_TITLE =
            "com.codinginflow.architectureexample.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.codinginflow.architectureexample.EXTRA_DESCRIPTION";
    public static final String EXTRA_DATE =
            "com.codinginflow.architectureexample.EXTRA_DATE";
    public static final String EXTRA_TIME =
            "com.codinginflow.architectureexample.EXTRA_TIME";
    public static final String EXTRA_DATETIME =
            "com.codinginflow.architectureexample.EXTRA_DATETIME";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private String Title;
    private String Description;
    private String Date;
    private String Time;
    private String DateTime;
    private TimeUtils timeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        timeUtils = new TimeUtils();

        if (intent.hasExtra(EXTRA_ID)) {
            setContentView(R.layout.activity_add_note);

            editTextTitle = findViewById(R.id.edit_text_title);
            editTextDescription = findViewById(R.id.edit_text_description);
            DateTime = timeUtils.GetCurrentTime();

            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

            setTitle("Редактирование");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
        }
        else{
            Title = "Новая заметка";
            Description = "";
            DateTime = timeUtils.GetCurrentTime();
            saveDeafaultNote();
        }
    }

    private void saveDeafaultNote() {
        if (Title.trim().isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите заголовок!", Toast.LENGTH_SHORT).show();
            return;
        }
        Date = timeUtils.GetCurrentDate();
        Time = timeUtils.GetCurrentTime();

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, Title);
        data.putExtra(EXTRA_DESCRIPTION, Description);
        data.putExtra(EXTRA_DATE, Date);
        data.putExtra(EXTRA_TIME, Time);
        data.putExtra(EXTRA_DATETIME, DateTime);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        if (title.trim().isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите заголовок!", Toast.LENGTH_SHORT).show();
            return;
        }

        Date = timeUtils.GetCurrentDate();
        Time = timeUtils.GetCurrentTime();

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_DATE, Date);
        data.putExtra(EXTRA_TIME, Time);
        data.putExtra(EXTRA_DATETIME, DateTime);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
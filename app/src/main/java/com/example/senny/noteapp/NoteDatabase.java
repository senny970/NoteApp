package com.example.senny.noteapp;

/**
 * Created by Senny on 03.06.2020.
 */

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;
    public abstract NoteDao noteDao();
    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private TimeUtils timeUtils;
        private PopulateDbAsyncTask(NoteDatabase db) {
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            timeUtils = new TimeUtils();

            noteDao.insert(new Note("Новая заметка 1", "", "00.00.0000", "00:00", "00:00"));
            noteDao.insert(new Note("Новая заметка 2", "", "00.00.0000", "00:00", "00:00"));
            noteDao.insert(new Note("Новая заметка 3", "", "00.00.0000", "00:00", "00:00"));
            return null;
        }
    }
}
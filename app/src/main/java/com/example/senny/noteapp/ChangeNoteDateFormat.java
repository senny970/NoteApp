package com.example.senny.noteapp;

/**
 * Created by Senny on 08.06.2020.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.os.AsyncTask;
import java.util.List;

public class ChangeNoteDateFormat extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());
        new ChangeNoteDateFormatTask().execute(context);
    }

    private static class ChangeNoteDateFormatTask extends AsyncTask<Context, Void, Void> {
        private List<Note> notes;
        private NoteDao noteDao;
        private TimeUtils timeUtils;
        private NoteDatabase database;
        private String Date, CurrentDate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Context... ctx) {
            database = NoteDatabase.getInstance(ctx[0]);
            noteDao = database.noteDao();
            timeUtils = new TimeUtils();
            notes = noteDao.getAll();

            for(Note n: notes){
                CurrentDate = timeUtils.GetCurrentDate();
                Date = n.getDate();
                if(Date.equals(CurrentDate))
                    n.setDisplayDateTime(Date);
            }

            noteDao.update(notes);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
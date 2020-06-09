package com.example.senny.noteapp;

/**
 * Created by Senny on 07.06.2020.
 */

import java.util.Calendar;
import java.util.Date;
import java.text.*;

public class TimeUtils {
    TimeUtils(){}

    public String GetCurrentDate() {
        Calendar calendar;
        Date currentDate;
        String formattedDate;

        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        formattedDate = df.format(currentDate);
        return formattedDate;
    }

    public String GetCurrentTime() {
        Calendar calendar;
        Date currentDate;
        String formattedDate;

        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        formattedDate = df.format(currentDate);
        return formattedDate;
    }
}

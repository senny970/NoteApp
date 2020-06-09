package com.example.senny.noteapp;

/**
 * Created by Senny on 02.06.2020.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    protected int id;
    protected String title;
    protected String description;
    protected String date;
    protected String time;
    protected String displaydatetime;

    public Note(String title, String description, String date, String time, String displaydatetime) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.displaydatetime = displaydatetime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {return date;}

    public String getTime() {return time;}

    public String getDisplayDatetime(){
        return displaydatetime;
    }

    public void setDisplayDateTime(String datetime){
        this.displaydatetime = datetime;
    }

}

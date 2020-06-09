package com.example.senny.noteapp;

/**
 * Created by Senny on 03.06.2020.
 */

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;
@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);
    @Update
    void update(Note note);
    @Update
    void update(List<Note> notes);
    @Delete
    void delete(Note note);
    @Query("DELETE FROM note_table")
    void deleteAllNotes();
    //@Query("SELECT * FROM note_table ORDER BY priority DESC")
    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note_table")
    List<Note> getAll();
}

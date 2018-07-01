package com.ofoegbuvgmail.journalboss.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM `journal entries` ORDER BY entryDate")
    LiveData<List<JournalEntry>> loadAllEntries();

    @Insert
    void insertEntry(JournalEntry accidentEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(JournalEntry accidentEntry);

    @Delete
    void deleteEntry(JournalEntry taskEntry);

    // COMPLETED (1) Wrap the return type with LiveData
    @Query("SELECT * FROM `journal entries` WHERE id = :id")
    LiveData<JournalEntry> loadEntryById(int id);
}

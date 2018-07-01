package com.ofoegbuvgmail.journalboss.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journal entries")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String entryHeading;
    private String entryDescription;
    private String entryDate;
    private String entryTime;

    public JournalEntry() {
    }

    @Ignore
    public JournalEntry(int id, String entryHeading, String entryDescription, String entryDate, String entryTime) {
        this.id = id;
        this.entryHeading = entryHeading;
        this.entryDescription = entryDescription;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
    }

    public JournalEntry(String entryHeading, String entryDescription, String  entryDate, String entryTime) {
        this.entryHeading = entryHeading;
        this.entryDescription = entryDescription;
        this.entryDate = entryDate;
        this.entryTime = entryTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntryHeading() {
        return entryHeading;
    }

    public void setEntryHeading(String entryHeading) {
        this.entryHeading = entryHeading;
    }

    public String getEntryDescription() {
        return entryDescription;
    }

    public void setEntryDescription(String entryDescription) {
        this.entryDescription = entryDescription;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }
}

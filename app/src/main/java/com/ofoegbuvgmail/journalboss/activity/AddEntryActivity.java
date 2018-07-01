package com.ofoegbuvgmail.journalboss.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ofoegbuvgmail.journalboss.AppExecutors;
import com.ofoegbuvgmail.journalboss.R;
import com.ofoegbuvgmail.journalboss.database.AppDatabase;
import com.ofoegbuvgmail.journalboss.database.JournalEntry;

import java.util.Calendar;

public class AddEntryActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        View.OnClickListener {

    private final String TAG = AddEntryActivity.class.getSimpleName();

    private static final int DEFAULT_TASK_ID = -1;
    private int mTaskId = DEFAULT_TASK_ID;
    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    private EditText entryHeader;
    private EditText entryDescription;
    private Button datePicker;
    private Button timePicker;
    private Button updateEntryButton;
    private String dateOfEntry;
    private String timeOfEntry;
    // Member variable for the Database
    private AppDatabase mDb;
    //firebase parameters
    private CollectionReference collectionReference;
    private FirebaseFirestore mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private final String DATABASE_NAME = "Journal Entries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            updateEntryButton.setText(R.string.update_entry);
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                Log.d(TAG, "Actively retrieving a specific entry from the DataBase");

                final LiveData<JournalEntry> task = mDb.journalDao().loadEntryById(mTaskId);
                // COMPLETED (4) Observe tasks and move the logic from runOnUiThread to onChanged
                task.observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        // COMPLETED (5) Remove the observer as we do not need it any more
                        task.removeObserver(this);
                        Log.d(TAG, "Receiving database update from LiveData");
                        populateUI(journalEntry);
                        updateEntryButton.setEnabled(true);
                    }
                });
            }
        }
        //mTaskId = getIntent().getIntExtra("id", -1);
        //Initialized the views

        //Initialize the ROOM Database
        mDb = AppDatabase.getInstance(this);

        //firebase parameters
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this));

        datePicker.setOnClickListener(v -> datePickerDialog.show());
        timePicker.setOnClickListener(v -> timePickerDialog.show());


    }

    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return;
        }

        entryHeader.setText(journalEntry.getEntryHeading());
        entryDescription.setText(journalEntry.getEntryDescription());
        datePicker.setText(journalEntry.getEntryDate());
        timePicker.setText(journalEntry.getEntryTime());

    }

    private void initViews() {
        entryHeader = findViewById(R.id.editTextEntryHeader);
        entryDescription = findViewById(R.id.editTextEntryDescription);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        updateEntryButton = findViewById(R.id.btn_update_entry);
        updateEntryButton.setOnClickListener(this);
    }

    private void saveDataToDatabase() {

        String entryHeading = entryHeader.getText().toString();
        String entryBody = entryDescription.getText().toString();

        JournalEntry journalEntry = new JournalEntry(entryHeading, entryBody, dateOfEntry, timeOfEntry);

        AppExecutors.getInstance().diskIO().execute(() -> {
                    if (mTaskId == DEFAULT_TASK_ID) {
                        // insert new task
                        mDb.journalDao().insertEntry(journalEntry);
                        Log.d(TAG, "Entry saved in Room Database");

                    } else {
                        //update task
                        journalEntry.setId(mTaskId);
                        mDb.journalDao().updateEntry(journalEntry);
                        Log.d(TAG, "Task updated in Database");
                    }
                    AddEntryActivity.this.finish();
                });


//        collectionReference.document( mFirebaseUser.getUid()).update(journalEntry)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Document SnapShot Successfully updated"))
//                .addOnFailureListener(e -> Log.w(TAG, "Error updating Document", e));

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year + "/" + month + "/" + dayOfMonth;
        datePicker.setText(date);
        dateOfEntry = date;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        timePicker.setText(time);
        timeOfEntry = time;
    }

    @Override
    public void onClick(View v) {

        saveDataToDatabase();
    }
}

package com.ofoegbuvgmail.journalboss.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ofoegbuvgmail.journalboss.R;

public class JournalDetailActivity extends AppCompatActivity {

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;

    private int mTaskId = DEFAULT_TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

    }

    private void launchUpdateActivity(){
        Intent intent = new Intent(this, AddEntryActivity.class);
        intent.putExtra("id", mTaskId);
    }

}

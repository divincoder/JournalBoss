package com.ofoegbuvgmail.journalboss.activity;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.ofoegbuvgmail.journalboss.AppExecutors;
import com.ofoegbuvgmail.journalboss.EntriesAdapter;
import com.ofoegbuvgmail.journalboss.R;
import com.ofoegbuvgmail.journalboss.database.AppDatabase;
import com.ofoegbuvgmail.journalboss.database.JournalEntry;
import com.ofoegbuvgmail.journalboss.database.UserDB;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements UserDB.UserSignout, EntriesAdapter.ItemClickListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView entriesRecyclerView;
    private EntriesAdapter mAdapter;
    private AppDatabase mDb;
    private CollectionReference collectionReference;
    private FirebaseFirestore mFirebaseDatabase;
    private final String DATABASE_NAME = "Journal Entries";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_entries);
        setSupportActionBar(toolbar);

        entriesRecyclerView = findViewById(R.id.recyclerView_entries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new EntriesAdapter(this, this);
        entriesRecyclerView.setAdapter(mAdapter);

        //Initialized the ROOM Database
        mDb = AppDatabase.getInstance(this);

        //Initialize firebase instances
        mFirebaseDatabase = FirebaseFirestore.getInstance();

        //Create a collection of Users Reference
        collectionReference = mFirebaseDatabase.collection(DATABASE_NAME);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirebaseDatabase.setFirestoreSettings(settings);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    int position = viewHolder.getAdapterPosition();
                    List<JournalEntry> journalEntries = mAdapter.getDataEntries();
                    mDb.journalDao().deleteEntry(journalEntries.get(position));
                    deleteFromFirebase(journalEntries.get(position).getEntryHeading());
                });
            }
        }).attachToRecyclerView(entriesRecyclerView);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
            startActivity(intent);

        });

        //Retrieve All Journal Entries
        retrieveJournalEntries();
    }

    private void retrieveJournalEntries() {
        Log.d(TAG, "Actively retrieving the accidents from the DataBase");
        LiveData<List<JournalEntry>> tasks = mDb.journalDao().loadAllEntries();
        tasks.observe(this, dataEntries -> {
            Log.d(TAG, "Receiving database update from LiveData");
            mAdapter.setDataEntries(dataEntries);
        });
    }

    private void deleteFromFirebase(String document) {

        collectionReference.document(document).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Document SnapShot Successfully deleted"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting Document", e));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            UserDB userDb = new UserDB(this);
            userDb.signOut(this, this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSignoutSuccess() {
        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSignoutFailed() {
        Toasty.error(this, "An error occurred please retry again",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch JournalDetailActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(MainActivity.this, JournalDetailActivity.class);
        //intent.putExtra(JournalDetailActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}

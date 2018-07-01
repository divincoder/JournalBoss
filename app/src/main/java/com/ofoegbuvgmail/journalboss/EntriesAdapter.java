package com.ofoegbuvgmail.journalboss;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ofoegbuvgmail.journalboss.database.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.DataViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<JournalEntry> mDataEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public EntriesAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new DataViewHolder that holds the view for each task
     */
    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.entry_item, parent, false);

        return new DataViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {
        // Determine the values of the wanted data
        JournalEntry dataEntry = mDataEntries.get(position);

//        //set values
        holder.entryHeading.setText(dataEntry.getEntryHeading());
        holder.entryDate.setText(dataEntry.getEntryDate());
        holder.entryTime.setText(dataEntry.getEntryTime());
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mDataEntries == null) {
            return 0;
        }
        return mDataEntries.size();
    }

    public List<JournalEntry> getDataEntries() {
        return mDataEntries;
    }

    /**
     * When data changes, this method updates the list of dataEntries
     * and notifies the adapter to use the new values on it
     */
    public void setDataEntries(List<JournalEntry> dataEntries) {
        mDataEntries = dataEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables
        TextView entryHeading;
        TextView entryDate;
        TextView entryTime;
        /**
         * Constructor for the DataViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public DataViewHolder(View itemView) {
            super(itemView);
            entryHeading = itemView.findViewById(R.id.text_item_entry_header);
            entryDate = itemView.findViewById(R.id.text_item_entry_date);
            entryTime = itemView.findViewById(R.id.text_item_entry_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mDataEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
package com.example.jjfan.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

// starter code: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
public class RecyclerViewAdapterUpcoming extends RecyclerView.Adapter<RecyclerViewAdapterUpcoming.ViewHolder> {

    //    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private JSONArray allEvents;
    private ViewHolder currentViewHolder;
    private int currentPosition;
    private Context context;
    private static final String TAG = VenueFragment.class.getSimpleName();

    // data is passed into the constructor
    RecyclerViewAdapterUpcoming(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.allEvents=jsonArray;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_upcoming_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject event;
        try{
            event = allEvents.getJSONObject(position);

            try{
                String title=event.getString("displayName");
                holder.myTextView_title.setText(title);
            }catch (Exception e){}

            try{
                String artist=event.getJSONArray("performance").getJSONObject(0).getString("displayName");
                holder.myTextView_artist.setText(artist);
            }catch (Exception e){}

            String dateTime = "";
            try {
                String date_String = event.getJSONObject("start").getString("date");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
                Date date_Date = sdf.parse(date_String);
                date_String = sdf2.format(date_Date);
                dateTime += date_String;
                dateTime += " ";
                try{
                    String time = event.getJSONObject("start").getString("time");
                    Log.e(TAG, time);
                    if (time!=null && !time.equals("null")){
                        dateTime+=time;
                    }
                }catch (Exception e){}
            }catch (Exception e) {}
            holder.myTextView_dateTime.setText(dateTime);

            try{
                String type=event.getString("type");
                holder.myTextView_type.setText("Type: " + type);
            }catch (Exception e){}

            try{
                final String url = event.getString("uri");
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "clicked!!!"+url);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        context.startActivity(i);
                    }
                });
            }catch (Exception e){}

        }catch (Exception e){
            return;
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return allEvents.length();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView myTextView_title;
        TextView myTextView_artist;
        TextView myTextView_dateTime;
        TextView myTextView_type;

        ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.upcoming_layout);
            myTextView_artist = itemView.findViewById(R.id.upcoming_artist);
            myTextView_title = itemView.findViewById(R.id.upcoming_title);
            myTextView_dateTime = itemView.findViewById(R.id.upcoming_time);
            myTextView_type = itemView.findViewById(R.id.upcoming_type);
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        //return mData.get(id);
        try{
            return allEvents.getJSONObject(id).getString("name");
        }catch (Exception e){
            return null;
        }
    }
    JSONObject getEvent(int id){
        try{
            return allEvents.getJSONObject(id);
        }catch(Exception e){
            return null;
        }
    }
    ViewHolder getCurrentViewHolder(){
        if (currentViewHolder!=null) return currentViewHolder;
        else return null;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}



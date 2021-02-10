package com.example.jjfan.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

// starter code: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Serializable {

//    private List<String> mData;
    private static final String TAG = SearchResultsActivity.class.getSimpleName();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private JSONArray allEvents;
    private ViewHolder currentViewHolder;
    private int currentPosition;
    private Context context;
    private FavouritesList favouritesList;



    // data is passed into the constructor
    RecyclerViewAdapter(Context context, JSONArray jsonArray) {
        this.mInflater = LayoutInflater.from(context);
        this.allEvents=jsonArray;
        this.context = context;
        favouritesList=FavouritesList.getInstance();

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        String animal = mData.get(position);
//        holder.myTextView.setText(animal);
        JSONObject event;
        try{
            event = allEvents.getJSONObject(position);

            try{
                String cat = event.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                switch(cat){
                    case "Music":
                        holder.imgView_icon.setImageResource(R.drawable.music_icon);
                        break;
                    case "Sports":
                        holder.imgView_icon.setImageResource(R.drawable.sport_icon);
                        break;
                    case "Arts & Theatre":
                        holder.imgView_icon.setImageResource(R.drawable.art_icon);
                        break;
                    case "Miscellaneous":
                        holder.imgView_icon.setImageResource(R.drawable.miscellaneous_icon);
                        break;
                    case "Film":
                        holder.imgView_icon.setImageResource(R.drawable.film_icon);
                        break;
                    default:
                        break;
                }
            }catch (Exception e){}

            try{
                String title=event.getString("name");
                holder.myTextView_title.setText(title);
            }catch (Exception e){return;}

            try{
                String venue=event.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name");
                holder.myTextView_venue.setText(venue);
            }catch (Exception e){}

            try{
                String dateTime=event.getJSONObject("dates").getJSONObject("start").getString("localDate") + " " + event.getJSONObject("dates").getJSONObject("start").getString("localTime");
                holder.myTextView_dateTime.setText(dateTime);
            }catch (Exception e){}

            if (favouritesList.isFav(event.toString())){
                holder.imgView_fav.setImageResource(R.drawable.heart_fill_red);
                holder.imgView_fav.setTag("FAV");
            }else{
                holder.imgView_fav.setImageResource(R.drawable.heart_outline_black);
                holder.imgView_fav.setTag("NOFAV");
            }
        }catch (Exception e){
            return;
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return allEvents.length();
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

    void setFavStatus(ImageView favImageView, int currentPosition){

        if (favImageView.getTag()=="NOFAV"){
            favImageView.setTag("FAV");
            favImageView.setImageResource(R.drawable.heart_fill_red);
            try{
                JSONObject event = allEvents.getJSONObject(currentPosition);
                Intent intent1 = new Intent();
                intent1.putExtra("type", "addFav");
                intent1.putExtra("jsonObj", event.toString());
                intent1.setAction("setUpFavList");
                context.sendBroadcast(intent1);
                Log.e(TAG, "add fav intent sent");
            } catch (Exception e){}
        }else if (favImageView.getTag()=="FAV"){
            favImageView.setTag("NOFAV");
            favImageView.setImageResource(R.drawable.heart_outline_black);
            try{
                JSONObject event = allEvents.getJSONObject(currentPosition);
                Intent intent1 = new Intent();
                intent1.putExtra("type", "deleteFav");
                intent1.putExtra("jsonObj", event.toString());
                intent1.setAction("setUpFavList");
                context.sendBroadcast(intent1);
                Log.e(TAG, "delete fav intent sent");
            } catch (Exception e){}


        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }




    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        ImageView imgView_icon;
        TextView myTextView_title;
        TextView myTextView_venue;
        TextView myTextView_dateTime;
        ImageView imgView_fav;

        ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.event_layout);
            imgView_icon = itemView.findViewById(R.id.imageView_icon);
            myTextView_title = itemView.findViewById(R.id.textView_event);
            myTextView_venue = itemView.findViewById(R.id.textView_venue);
            myTextView_dateTime = itemView.findViewById(R.id.textView_dateTime);
            imgView_fav = itemView.findViewById(R.id.imageView_fav);
            imgView_fav.setOnClickListener(this);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = getAdapterPosition();

                    try{
                        Intent event = new Intent(context, EventActivity.class);
                        Bundle b = new Bundle();
                        b.putString("key", allEvents.getJSONObject(currentPosition).toString());
                        event.putExtras(b);
                        context.startActivity(event);
                    }catch(Exception e){}
                }
            });
        }

        @Override
        // click on heart
        public void onClick(View view) {
            if (mClickListener != null){
                currentPosition = getAdapterPosition();
                currentViewHolder=this;
                int addOrRemove=0;
                if (imgView_fav.getTag()=="NOFAV") addOrRemove=1;
                else if (imgView_fav.getTag()=="FAV") addOrRemove=2;
                setFavStatus(this.imgView_fav, currentPosition);

                mClickListener.onItemClick(myTextView_title, addOrRemove);
            }
        }
    }
}



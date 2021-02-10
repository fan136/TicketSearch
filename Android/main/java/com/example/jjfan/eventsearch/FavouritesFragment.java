package com.example.jjfan.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class FavouritesFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener{
    public RecyclerViewAdapter adapter;
    public JSONArray favEvents;
    private static final String TAG = FavouritesFragment.class.getSimpleName();
    View myFragmentView;
    TextView noRecords;
    MyReceiver myReceiver;
    IntentFilter intentFilter;
    RecyclerView recyclerView;
    FavouritesList favouritesList;


    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        favEvents=new JSONArray();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_favourites, container, false);
        favouritesList=FavouritesList.getInstance();

        noRecords = (TextView)myFragmentView.findViewById(R.id.no_records);

        recyclerView = myFragmentView.findViewById(R.id.recyclerView_fav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getContext(), favEvents){
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                JSONObject event;
                try{
                    event = favEvents.getJSONObject(position);

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

                    holder.imgView_fav.setImageResource(R.drawable.heart_fill_red);
                    holder.imgView_fav.setTag("FAV");

                }catch (Exception e){
                    return;
                }
            }
        };

        // add divider
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        updateView();
        setUpReceiver();
        return myFragmentView;
    }

    public void updateView(){
        if (favEvents!=null && favEvents.length()!=0){
            noRecords.setVisibility(View.INVISIBLE);
        }else{
            noRecords.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onItemClick(View view, int addOrRemove) {
        String title = ((TextView)view).getText().toString();
        if (addOrRemove==1){
            title+=" was added to favourites";
        }else if (addOrRemove==2){
            title+=" was removed from favourites";
        }
        Toast toast1 = Toast.makeText(getContext(), title, Toast.LENGTH_SHORT);
        View view1 = toast1.getView();
        view1.getBackground().setColorFilter(getResources().getColor(R.color.ToastBackground), PorterDuff.Mode.SRC_IN);
        TextView text1 = view1.findViewById(android.R.id.message);
        text1.setTextSize(14);
        text1.setTextColor(getResources().getColor(R.color.ToastText));
        toast1.show();
    }

    public void addFavEvent(String favEventString) {
        Log.e(TAG, "request to add one fav");
        try{
            JSONObject favEvent = new JSONObject(favEventString);
            favEvents.put(favEvent);
            favouritesList.addEvent(favEventString);

        }catch (Exception e){
            //TODO report fail
        }
        adapter.notifyDataSetChanged();
        updateView();
    }

    public void deleteFavEvent(String favEventString){
        Log.e(TAG, "request to delete one fav");
        try{
            for (int i=0; i<favEvents.length(); i++){
                String event = favEvents.getJSONObject(i).toString();
                if (event.equals(favEventString)){
                    favEvents.remove(i);
                    Log.e(TAG, favEventString);
                    favouritesList.removeEvent(favEventString);
                    break;
                }
            }
        }catch (Exception e){}
        adapter.notifyDataSetChanged();
        updateView();
    }

    private void setUpReceiver(){
        myReceiver = new MyReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "intent received in inner receiver");
                    String type = intent.getStringExtra("type");
                    String jsonObjString = intent.getStringExtra("jsonObj");
                    if (type.equals("addFav")){
                        addFavEvent(jsonObjString);
                    }else if (type.equals("deleteFav")){
                        deleteFavEvent(jsonObjString);
                    }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction("setUpFavList");
        getContext().registerReceiver(myReceiver, intentFilter);
        Log.e(TAG, "Receiver set up");
    }

}

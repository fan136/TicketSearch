package com.example.jjfan.eventsearch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.net.Uri.encode;




public class EventFragment extends Fragment{
    private JSONObject event_json;
    private EventActivity eventActivity;
    private static final String TAG = EventActivity.class.getSimpleName();
    private String tweet;
    private List<String> artists;
    private LinearLayout progressBar2;
    private View myFragmentView;


    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_event, container, false);
        eventActivity = (EventActivity)this.getActivity();
        event_json = eventActivity.getEvent();
        progressBar2 = (LinearLayout)myFragmentView.findViewById(R.id.progress_bar2);
        render(myFragmentView);
        return myFragmentView;
    }

    public String getTweet() {
        return tweet;
    }

    public List<String> getArtists() {
        return artists;
    }

    private void render(View view){
        TextView tv_artist = view.findViewById(R.id.textView_event_artist);
        TextView tv_venue = view.findViewById(R.id.textView_event_venue);
        TextView tv_time = view.findViewById(R.id.textView_event_time);
        TextView tv_cat = view.findViewById(R.id.textView_event_cat);
        TextView tv_price = view.findViewById(R.id.textView_event_price);
        TextView tv_ticket = view.findViewById(R.id.textView_event_ticket);
        TextView tv_buy = view.findViewById(R.id.textView_event_buy);
        TextView tv_seat = view.findViewById(R.id.textView_event_seat);

        LinearLayout line_artist = view.findViewById(R.id.line_artist);
        LinearLayout line_venue = view.findViewById(R.id.line_venue);
        LinearLayout line_time = view.findViewById(R.id.line_time);
        LinearLayout line_cat = view.findViewById(R.id.line_cat);
        LinearLayout line_price = view.findViewById(R.id.line_price);
        LinearLayout line_ticket = view.findViewById(R.id.line_ticket);
        LinearLayout line_buy = view.findViewById(R.id.line_buy);
        LinearLayout line_seat = view.findViewById(R.id.line_seat);

        String event_detail_name="";
        String event_venue="";
        String url="";
        if (event_json==null) return;
        try {
            event_detail_name = event_json.getString("name");
            eventActivity.setTitle(event_detail_name);
        }catch (Exception e){}
        try {
            // srtist_name
            artists=new ArrayList<>();
            String artist_team="";
            JSONArray at = event_json.getJSONObject("_embedded").getJSONArray("attractions");
            if (at.length()>0) {
                for (int i = 0; i < at.length(); i++) {
                    if (at.getJSONObject(i).getString("name")!=null) {
                        String name = at.getJSONObject(i).getString("name");
                        artist_team += (name + " | ");
                        artists.add(name);
                        try{
                            if (at.getJSONObject(i).getJSONArray("classifications").getJSONObject(0)
                                    .getJSONObject("segment").getString("name").equals("Music")){
                                artists.add("Music");
                            }
                        }catch(Exception e){
                            artists.add(null);
                        }
                    }
                }
                if (artist_team.length()>3) {
                    artist_team = artist_team.substring(0, artist_team.length()-3);
                }
                if (artist_team.length()>0){
                    tv_artist.setText(artist_team);
                }else{
                    line_artist.setVisibility(View.GONE);
                }
                Log.e(TAG, artist_team);
            }
        }catch (Exception e){
            line_artist.setVisibility(View.GONE);
        }
        try {
            // venue
            event_venue = event_json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            if (event_venue!=null && event_venue.length()!=0){
                tv_venue.setText(event_venue);
            }else{
                line_venue.setVisibility(View.GONE);
            }
        }catch (Exception e){
            line_venue.setVisibility(View.GONE);
        }
        try {
            // time
            String event_date = "";
            JSONObject start = event_json.getJSONObject("dates").getJSONObject("start");
            if (start.getString("localDate")!=null){
                String dateString = start.getString("localDate");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
                Date date = sdf.parse(dateString);
                event_date += sdf2.format(date);

            }
            if (start.getString("localTime")!=null){
                event_date += " ";
                event_date += start.getString("localTime");
            }
            if (!event_date.equals("")){
                tv_time.setText(event_date);
            }else {
                line_time.setVisibility(View.GONE);
            }
        }catch (Exception e){
            line_time.setVisibility(View.GONE);
        }
        try {
            // cat
            String event_cat = "";
            try {
                event_cat+= event_json.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name");
            }catch (Exception e){}
            try {
                String rest = event_json.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                if (event_cat.length()>0) event_cat+= " | ";
                event_cat+= rest;
            }catch (Exception e){}

            if (event_cat.length()>0){
                tv_cat.setText(event_cat);
            }else{
                line_cat.setVisibility(View.GONE);
            }
        }catch (Exception e){
            line_cat.setVisibility(View.GONE);
        }
        try {
            // price
            String pricerange = "$";
            try {
                pricerange+= event_json.getJSONArray("priceRanges").getJSONObject(0).getString("min");
            }catch (Exception e){}
            try {
                String rest = event_json.getJSONArray("priceRanges").getJSONObject(0).getString("max");
                if (pricerange.length()>1) pricerange+= " ~ $";
                pricerange+=rest;
            }catch (Exception e){}

            if (pricerange.length()>1){
                tv_price.setText(pricerange);
            }else{
                line_price.setVisibility(View.GONE);
            }
        }catch (Exception e){
            line_cat.setVisibility(View.GONE);
        }
        try {
            // ticket status
            tv_ticket.setText(event_json.getJSONObject("dates").getJSONObject("status").getString("code"));
        }catch (Exception e){
            line_ticket.setVisibility(View.GONE);
        }
        try {
            // buy ticket
            url = event_json.getString("url");
            String buy = "<html><a href=\"";
            buy+=url;
            buy+="\">Ticketmaster</a><html>";
            tv_buy.setText(Html.fromHtml(buy));
            tv_buy.setMovementMethod(LinkMovementMethod.getInstance());

        }catch (Exception e){
            line_buy.setVisibility(View.GONE);
        }
        try {
            // seat map
            String seat = "<html><a href=\"";
            seat+=event_json.getJSONObject("seatmap").getString("staticUrl");
            seat+="\">View Here</a><html>";
            tv_seat.setText(Html.fromHtml(seat));
            tv_seat.setMovementMethod(LinkMovementMethod.getInstance());

        }catch (Exception e){
            line_seat.setVisibility(View.GONE);
        }
        progressBar2.setVisibility(View.GONE);

    }
}




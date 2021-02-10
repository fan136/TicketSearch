package com.example.jjfan.eventsearch;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.net.Uri.encode;


public class ArtistFragment extends Fragment{
    private List<String> artists;
    private static final String TAG = ArtistFragment.class.getSimpleName();
    private View myFragmentView;
    private EventActivity eventActivity;
    private JSONObject event_json;


    public ArtistFragment() {
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
        myFragmentView = inflater.inflate(R.layout.fragment_artist, container, false);
        eventActivity = (EventActivity)this.getActivity();
        event_json = eventActivity.getEvent();

        artists=new ArrayList<>();
        Log.e(TAG, "start artist fragment");
        try{
            JSONArray at = event_json.getJSONObject("_embedded").getJSONArray("attractions");
            if (at.length()>0) {
                for (int i = 0; i < at.length() && i<2; i++) {
                    if (at.getJSONObject(i).getString("name") != null) {
                        String name = at.getJSONObject(i).getString("name");
                        artists.add(name);
                        try {
                            if (at.getJSONObject(i).getJSONArray("classifications").getJSONObject(0)
                                    .getJSONObject("segment").getString("name").equals("Music")) {
                                artists.add("Music");
                            }else{
                                artists.add("null");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "get music status fail");
                            artists.add("null");
                        }
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG, "get artist names fail");
        }
        if (artists!=null && artists.size()>0){
            Log.e(TAG, String.valueOf(artists.size()));
            for (int i=0; i<artists.size(); i+=2){
                Log.e(TAG, artists.get(i));
                render(artists.get(i), artists.get(i+1), i/2);
            }
        }
        return myFragmentView;
    }
    private void render(String name, String type, final int i){
        if (type.equals("Music")) {
            renderSpotify(name, i);
        }
        renderCustomSearch(name, i);
    }
    private void renderSpotify(final String name, final int i){
        if (i==0) ((TableLayout)myFragmentView.findViewById(R.id.spotify1)).setVisibility(View.VISIBLE);
        else ((TableLayout)myFragmentView.findViewById(R.id.spotify2)).setVisibility(View.VISIBLE);
        String url ="http://giddytent780hw8.us-east-2.elasticbeanstalk.com/spotify/" + name;
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // res.setText("SUCCESS: " + url);
                // TODO add progress bar
                Log.e(TAG, response.toString());
                //renderSpotify(response, i);
                try {
                    JSONArray items = response.getJSONObject("body").getJSONObject("artists").getJSONArray("items");
                    JSONObject artist=null;
                    int p=-1;
                    for (p=0; p<items.length();p++){
                        if (items.getJSONObject(p).getString("name").equals(name)){
                            artist = items.getJSONObject(p);
                            break;
                        }
                    }
                    if (artist!=null) {
                        String showName = name;
                        String follower = artist.getJSONObject("followers").getString("total");
                        String popularity = artist.getString("popularity");
                        String checkAt= "<html><a href=\"" + artist.getJSONObject("external_urls").getString("spotify")+"\">Spotify</a></html>";

                        if (i==0){
                            TextView tv_name1 = (TextView)myFragmentView.findViewById(R.id.spotify_name1);
                            TextView tv_follower1 = (TextView)myFragmentView.findViewById(R.id.spotify_follower1);
                            TextView tv_popularity1 = (TextView)myFragmentView.findViewById(R.id.spotify_popularity1);
                            TextView tv_checkAt1 = (TextView)myFragmentView.findViewById(R.id.spotify_checkAt1);
                            tv_name1.setText(showName);
                            tv_follower1.setText(follower);
                            tv_popularity1.setText(popularity);
                            tv_checkAt1.setText(Html.fromHtml(checkAt));
                            tv_checkAt1.setMovementMethod(LinkMovementMethod.getInstance());
                        } else if (i==1){
                            TextView tv_name2 = (TextView)myFragmentView.findViewById(R.id.spotify_name2);
                            TextView tv_follower2 = (TextView)myFragmentView.findViewById(R.id.spotify_follower2);
                            TextView tv_popularity2 = (TextView)myFragmentView.findViewById(R.id.spotify_popularity2);
                            TextView tv_checkAt2 = (TextView)myFragmentView.findViewById(R.id.spotify_checkAt2);
                            tv_name2.setText(showName);
                            tv_follower2.setText(follower);
                            tv_popularity2.setText(popularity);
                            tv_checkAt2.setText(Html.fromHtml(checkAt));
                            tv_checkAt2.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }

                }catch(Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                // res.setText("FAIL: "+ url);
                // TODO: Handle error
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void renderCustomSearch(String name, final int i){
        if (i==0) ((TextView)myFragmentView.findViewById(R.id.title1)).setText(name);
        else ((TextView)myFragmentView.findViewById(R.id.title2)).setText(name);

        String url ="http://giddytent780hw8.us-east-2.elasticbeanstalk.com/customSearch/" + name;
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // res.setText("SUCCESS: " + url);
                // TODO add progress bar
                Log.e(TAG, response.toString());
                //renderCustomSearch(response, i);
                try {
                    JSONArray items = response.getJSONArray("items");
                    int len = items.length();
                    Log.e(TAG, String.valueOf(len));
                    TableLayout table;
                    if (i==0) table = (TableLayout)myFragmentView.findViewById(R.id.customSearch1);
                    else table = (TableLayout)myFragmentView.findViewById(R.id.customSearch2);

                    for (int i = 0; i < len && i<8; i++) {
                        TableRow tr = new TableRow(getContext());
                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        ImageView view = new ImageView(getContext());
                        String imageUri = items.getJSONObject(i).getString("link");
                        Picasso.with(getContext()).load(imageUri).resize(1400, 0).into(view);
                        tr.setGravity(Gravity.CENTER_HORIZONTAL);
                        tr.addView(view);
                        table.addView(tr);
                    }

                }catch(Exception e){}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                // res.setText("FAIL: "+ url);
                // TODO: Handle error
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

}




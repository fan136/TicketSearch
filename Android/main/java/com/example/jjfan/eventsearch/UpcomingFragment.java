package com.example.jjfan.eventsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class UpcomingFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener{
    private static final String TAG = UpcomingFragment.class.getSimpleName();
    private View myFragmentView;
    private EventActivity eventActivity;
    private JSONObject event_json;
    private String venueName;
    private JSONArray upcomingEvents;
    private JSONArray sortedEvents;
    RecyclerViewAdapterUpcoming adapter;
    RecyclerView recyclerView;
    RecyclerViewAdapterUpcoming.ItemClickListener listener;
    private String sortType;
    private String sortDir;
    Spinner spinner_sortDirection;
    private LinearLayout progressbar;

    public UpcomingFragment() {
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
        myFragmentView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        eventActivity = (EventActivity)this.getActivity();
        event_json = eventActivity.getEvent();
        progressbar = (LinearLayout)myFragmentView.findViewById(R.id.progress_bar3);
        try{
            venueName = event_json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            Log.e(TAG, "getVenueName successful");
            render();
        }catch (Exception e){
            Log.e(TAG, "getVenueName fail");
            renderNoResults();
        }

        Spinner spinner_sortType = (Spinner) myFragmentView.findViewById(R.id.spinner_sortType);
        spinner_sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                sortType = (String)parent.getItemAtPosition(position);
                updateCardView(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortType = "Default";
            }
        });
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(myFragmentView.getContext(),
                R.array.sortType_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sortType.setAdapter(adapter1);

        spinner_sortDirection = (Spinner) myFragmentView.findViewById(R.id.spinner_sortDirection);
        spinner_sortDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                Log.e(TAG, "spinner clicked?");
                sortDir = (String)parent.getItemAtPosition(position);
                updateCardView(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortDir = "Ascending";
            }
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(myFragmentView.getContext(),
                R.array.sortDtrection_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sortDirection.setAdapter(adapter2);
        spinner_sortDirection.setEnabled(false);

        return myFragmentView;
    }
    private void render() {
        String url = "http://giddytent780hw8.us-east-2.elasticbeanstalk.com/upcoming/" + venueName;
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO add progress bar
                Log.e(TAG, response.toString());
                Log.e(TAG, "upcoming request success");

                try {
                    JSONArray jsonArray = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
                    Log.e(TAG, "jsonArray parse success");
                    Log.e(TAG, jsonArray.toString());

                    if (jsonArray==null || jsonArray.length()==0) renderNoResults();
                    else{
                        upcomingEvents = new JSONArray();
                        sortedEvents = new JSONArray();
                        for (int i=0; i<5 && i<jsonArray.length(); i++){
                            Log.e(TAG, String.valueOf(i)+" success");

                            JSONObject event = jsonArray.getJSONObject(i);
                            upcomingEvents.put(event);
                            sortedEvents.put(event);
                        }
                        recyclerView = myFragmentView.findViewById(R.id.recyclerView_upcoming);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        display();
                    }
                }catch (Exception e){
                    renderNoResults();
                    Log.e(TAG, "JSON parse fail");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                Log.e(TAG, "upcoming request fail");
                // TODO: Handle error
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void backToDefault(){
        spinner_sortDirection.setEnabled(false);
        try{
            int len = sortedEvents.length();
            for (int i=0; i<len; i++){
                sortedEvents.remove(i);
            }
            for (int i=0; i<len; i++){
                sortedEvents.put(upcomingEvents.getJSONObject(i));
            }
        }catch (Exception e){}
    }

    private void sort(){
//        <item>Default</item>
//        <item>Event Name</item>
//        <item>Time</item>
//        <item>Artist</item>
//        <item>Type</item>
        if (sortType.equals("Default")){
            backToDefault();
            return;
        }
        spinner_sortDirection.setEnabled(true);
        try{
            List<JSONObject> tmpList = new ArrayList<>();
            int len = sortedEvents.length();
            for (int i=0; i<len; i++){
                tmpList.add(sortedEvents.getJSONObject(0));
                sortedEvents.remove(0);
            }
            Collections.sort(tmpList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    int compVal = 0;
                    switch (sortType){
                        case "Event Name":
                            try{
                                String name1 = o1.getString("displayName");
                                String name2 = o2.getString("displayName");
                                compVal = name1.compareToIgnoreCase(name2);
                                break;
                            }catch (Exception e){
                                break;
                            }
                        case "Time":
                            try{
                                String date1 = o1.getJSONObject("start").getString("date");
                                String date2 = o2.getJSONObject("start").getString("date");
                                if (!date1.equals(date2)){
                                    compVal= date1.compareTo(date2);
                                    break;
                                }else{
                                    compVal = o1.getJSONObject("start").getString("time").compareTo(o2.getJSONObject("start").getString("time"));
                                    break;
                                }
                            }catch (Exception e){
                                break;
                            }
                        case "Artist":
                            try{
                                String art1 = o1.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                String art2 = o2.getJSONArray("performance").getJSONObject(0).getString("displayName");
                                compVal = art1.compareToIgnoreCase(art2);
                                break;
                            }catch (Exception e){
                                break;
                            }
                        case "Type":
                            try{
                                compVal = o1.getString("type").toLowerCase().compareTo(o2.getString("type").toLowerCase());
                                break;
                            }catch (Exception e){
                                break;
                            }
                        default:
                            break;
                    }
                    return compVal;
                }
            });
            for (int i=0; i<len; i++){
                sortedEvents.put(tmpList.get(i));
            }
            if (sortDir.equals("Descending")) reverse();
        }catch (Exception e){}
    }
    private void reverse(){
        if (sortType.equals("Default")){
            backToDefault();
            return;
        }
        try{
            Log.e(TAG, sortedEvents.toString());
            int len = sortedEvents.length();
            JSONArray newArray = new JSONArray();
            for (int i=0; i<len; i++){
                newArray.put(sortedEvents.getJSONObject(0));
                sortedEvents.remove(0);
            }
            for (int i=len-1; i>=0; i--){
                sortedEvents.put(newArray.getJSONObject(i));
            }

            Log.e(TAG, sortedEvents.toString());
        }catch (Exception e){}
    }
    private void display(){
        //display "sorted" events
        // set up the RecyclerView
        progressbar.setVisibility(View.GONE);
        Log.e(TAG, "Now in display()");
        Log.e(TAG, String.valueOf(sortedEvents.length()));
        adapter = new RecyclerViewAdapterUpcoming(getContext(), sortedEvents);
        adapter.setClickListener(listener);
        recyclerView.setAdapter(adapter);
    }
    private void updateCardView(int i){
        if (adapter==null) return;

        if (i==1) reverse();
        else if (i==0) sort();
        adapter.notifyDataSetChanged();
    }

    private void renderNoResults(){
        // TODO mute spinner buttons
        progressbar.setVisibility(View.GONE);
        ((TextView)myFragmentView.findViewById(R.id.textView_noRecords)).setVisibility(View.VISIBLE);
    }
    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, adapter.getItem(position) + " was added to favourites", Toast.LENGTH_SHORT).show();

    }
}




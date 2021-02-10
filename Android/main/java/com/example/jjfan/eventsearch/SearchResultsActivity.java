package com.example.jjfan.eventsearch;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class SearchResultsActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{
    RecyclerViewAdapter adapter;
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static JSONObject currentEvent;
    private TextView noResults;
    private LinearLayout progressBar1;
    MyReceiver myReceiver;
    IntentFilter intentFilter;

    TextView res;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        noResults = (TextView)findViewById(R.id.no_results_search);
        progressBar1 = (LinearLayout)findViewById(R.id.progress_bar1);
        url = SearchFragment.URL;
        // res = (TextView)findViewById(R.id.textView_response);

        if (url==null || url.length()==0){
            // TODO: no records
        } else{
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // res.setText("SUCCESS: " + url);
                        // TODO add progress bar
                        progressBar1.setVisibility(View.GONE);
                        renderResults(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                        progressBar1.setVisibility(View.GONE);
                        noResults.setVisibility(View.VISIBLE);
                        // TODO: Handle error

                    }
                });
            requestQueue.add(jsonObjectRequest);
        }
        setUpReceiver();
    }
    private void renderResults(JSONObject jsonObject){
        // TODO add already chosen headrts
        JSONArray allEvents;
        try{
            allEvents = (JSONArray) ((JSONObject)jsonObject.get("_embedded")).get("events");
            if (allEvents==null || allEvents.length()==0){
                noResults.setVisibility(View.VISIBLE);
                return;
            }
            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RecyclerViewAdapter(this, allEvents);

            // add divider
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

        }catch (Exception e){
            allEvents=null;
            noResults.setVisibility(View.VISIBLE);
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
        Toast toast1 = Toast.makeText(this, title, Toast.LENGTH_SHORT);
        View view1 = toast1.getView();
        view1.getBackground().setColorFilter(getResources().getColor(R.color.ToastBackground), PorterDuff.Mode.SRC_IN);
        TextView text1 = view1.findViewById(android.R.id.message);
        text1.setTextSize(14);
        text1.setTextColor(getResources().getColor(R.color.ToastText));
        toast1.show();
    }

    private void setUpReceiver(){
        myReceiver = new MyReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "intent received in search results receiver");
                adapter.notifyDataSetChanged();
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction("setUpFavList");
        registerReceiver(myReceiver, intentFilter);
        Log.e(TAG, "Receiver in search results set up");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}


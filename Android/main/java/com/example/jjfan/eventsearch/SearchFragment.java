package com.example.jjfan.eventsearch;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import static android.net.Uri.encode;


public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private Spinner spinnerUnit;
    private AppCompatAutoCompleteTextView autoCompleteTextView;
    private String category = "All";
    private String unit = "Miles";
    private EditText distance;
    private RadioGroup from;
    private RadioButton current;
    private RadioButton other;
    private EditText fromTypeIn;
    private Button submit;
    private Button clear;
    private TextView validation1;
    private TextView validation2;

    private static final String TAG = SearchFragment.class.getSimpleName();
    public static String URL;
    private List<String> candidates=new ArrayList<>();
    private AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private View myFragmentView;



    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        spinner = (Spinner) myFragmentView.findViewById(R.id.spinner_category);
        spinner.setOnItemSelectedListener(this);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(myFragmentView.getContext(),
            R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinnerUnit = (Spinner) myFragmentView.findViewById(R.id.spinner_unit);
        spinnerUnit.setOnItemSelectedListener(this);
        final ArrayAdapter<CharSequence> adapterUnit = ArrayAdapter.createFromResource(myFragmentView.getContext(),
                R.array.unit_array, android.R.layout.simple_spinner_item);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapterUnit);


        autoCompleteTextView = (AppCompatAutoCompleteTextView) myFragmentView.findViewById(R.id.auto_complete_edit_text);
        autoCompleteTextView.requestFocus();
        distance = (EditText) myFragmentView.findViewById(R.id.editText_distance);
        from = (RadioGroup) myFragmentView.findViewById(R.id.radioGroup_location);
        from.check(R.id.radioButton_current);
        current = (RadioButton) myFragmentView.findViewById(R.id.radioButton_current);
        other = (RadioButton) myFragmentView.findViewById(R.id.radioButton_fromTypeIn);
        fromTypeIn = (EditText) myFragmentView.findViewById(R.id.editText_location);
        fromTypeIn.setEnabled(false);
        submit = (Button) myFragmentView.findViewById(R.id.button_submit);
        clear = (Button) myFragmentView.findViewById(R.id.button_clear);
        validation1 = (TextView)myFragmentView.findViewById(R.id.textView_validation1);
        validation2 = (TextView)myFragmentView.findViewById(R.id.textView_validation2);

//        final TextView selectedText = myFragmentView.findViewById(R.id.selected_item);

        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(getContext(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        autoComplete();
                    }else{
                        candidates.clear();
                        autoSuggestAdapter.setData(candidates);
                        autoSuggestAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromTypeIn.setEnabled(false);
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromTypeIn.setEnabled(true);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()){
                    URL = getUrl();
                    Intent seacrhResults = new Intent(getContext(), SearchResultsActivity.class);
                    getContext().startActivity(seacrhResults);

                }else{
                    Toast toast = Toast.makeText(getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.getBackground().setColorFilter(getResources().getColor(R.color.ToastBackground), PorterDuff.Mode.SRC_IN);
                    TextView text = view.findViewById(android.R.id.message);
                    text.setTextSize(14);
                    text.setTextColor(getResources().getColor(R.color.ToastText));
                    toast.show();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText(null);
                spinner.setSelection(0);
                spinnerUnit.setSelection(0);
                category = "All";
                distance.setText(null);
                from.check(R.id.radioButton_current);
                fromTypeIn.setText(null);
                validation1.setVisibility(View.GONE);
                validation2.setVisibility(View.GONE);
                fromTypeIn.setEnabled(false);
                candidates.clear();
                autoSuggestAdapter.setData(candidates);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        });
        return myFragmentView;
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if (((Spinner)parent).getId() == R.id.spinner_category){
            this.category = (String)parent.getItemAtPosition(pos);
        }else{
            this.unit = (String)parent.getItemAtPosition(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        this.category = "All";
        this.unit = "Miles";
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

    }
    private Location getLocation(){
        return ((MainActivity)this.getActivity()).getLocation();
    }
    private boolean validation(){
        boolean valid = true;
        if (autoCompleteTextView.getText().toString().length()==0){
            validation1.setVisibility(View.VISIBLE);
            valid = false;
        }else{
            validation1.setVisibility(View.GONE);
        }

        if (other.isChecked() && fromTypeIn.getText().toString().length()==0){
            validation2.setVisibility(View.VISIBLE);
            valid = false;
        }else{
            validation2.setVisibility(View.GONE);
        }
        return valid;
    }
    private String getUrl(){
        String k = autoCompleteTextView.getText().toString();
        String c = category;
        String d = distance.getText().toString();
        if (d.equals("")) d="10";
        String f;
        if (current.isChecked()){
            f="null";
        }else{
            f=fromTypeIn.getText().toString();
        }
        Location location = getLocation();
        Log.e(TAG, String.valueOf(location.getLatitude()));
        StringBuilder url = new StringBuilder("http://giddytent780hw8.us-east-2.elasticbeanstalk.com/search/");
        url.append(encode(k));
        url.append("/");
        url.append(c);
        url.append("/");
        url.append(d);
        url.append("/");
        url.append(unit);
        url.append("/");
        url.append(String.format("%.3f", location.getLatitude()));
        url.append("/");
        url.append(String.format("%.3f", location.getLongitude()));
        url.append("/");
        url.append(encode(f));
        return url.toString();
    }
    private void autoComplete(){

        final String word = autoCompleteTextView.getText().toString();
        Log.e(TAG, word);
        if (word.length()==0) return;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        String url = "http://giddytent780hw8.us-east-2.elasticbeanstalk.com/autocompletion/" + word;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "autocomplte request successful");
                try{
                    candidates.clear();
                    JSONArray words = response.getJSONObject("_embedded").getJSONArray("attractions");
                    for (int i=0; i<words.length(); i++){
                        candidates.add(words.getJSONObject(i).getString("name"));
                    }
                    Log.e(TAG, String.valueOf(candidates.size()));
                    if (candidates.size()>0) Log.e(TAG, candidates.get(0));
                }catch (Exception e){}
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(candidates);
                autoSuggestAdapter.notifyDataSetChanged();
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


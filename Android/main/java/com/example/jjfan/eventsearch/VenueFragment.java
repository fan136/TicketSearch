package com.example.jjfan.eventsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;


public class VenueFragment extends Fragment{
    private static final String TAG = VenueFragment.class.getSimpleName();
    private View myFragmentView;
    private EventActivity eventActivity;
    private JSONObject event_json;
    private JSONObject venueJson;
    private SupportMapFragment mapFragment;
    private String lat;
    private String lon;

    public VenueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_venue, container, false);
        eventActivity = (EventActivity)this.getActivity();
        event_json = eventActivity.getEvent();
        //render
        render();
        //map
        // https://code.luasoftware.com/tutorials/android/supportmapfragment-in-fragment/

        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Venue"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });
        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        return myFragmentView;
    }

    private void render(){
        TextView tv_name = (TextView)myFragmentView.findViewById(R.id.venue_name);
        TextView tv_address = (TextView)myFragmentView.findViewById(R.id.venue_address);
        TextView tv_city = (TextView)myFragmentView.findViewById(R.id.venue_city);
        TextView tv_phone = (TextView)myFragmentView.findViewById(R.id.venue_phone);
        TextView tv_hour = (TextView)myFragmentView.findViewById(R.id.venue_hour);
        TextView tv_general = (TextView)myFragmentView.findViewById(R.id.venue_general);
        TextView tv_child = (TextView)myFragmentView.findViewById(R.id.venue_child);

        try{
            venueJson = event_json.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            try{
                lon = venueJson.getJSONObject("location").getString("longitude");
                lat = venueJson.getJSONObject("location").getString("latitude");
            }catch (Exception e){}
            try{
                String name = venueJson.getString("name");
                tv_name.setText(name);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_name_line)).setVisibility(View.GONE);
            }
            try{
                String address = venueJson.getJSONObject("address").getString("line1");
                tv_address.setText(address);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_address_line)).setVisibility(View.GONE);
            }
            try{
                String city = venueJson.getJSONObject("city").getString("name");
                city+=", ";
                city+= venueJson.getJSONObject("state").getString("name");
                tv_city.setText(city);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_city_line)).setVisibility(View.GONE);
            }
            try{
                String phone = venueJson.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
                tv_phone.setText(phone);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_phone_line)).setVisibility(View.GONE);
            }
            try{
                String openHour = venueJson.getJSONObject("boxOfficeInfo").getString("openHoursDetail");
                tv_hour.setText(openHour);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_hour_line)).setVisibility(View.GONE);
            }
            try{
                String generalRule = venueJson.getJSONObject("generalInfo").getString("generalRule");
                tv_general.setText(generalRule);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_general_line)).setVisibility(View.GONE);
            }
            try{
                String childRule = venueJson.getJSONObject("generalInfo").getString("childRule");
                tv_child.setText(childRule);
            }catch (Exception e){
                ((TableRow)myFragmentView.findViewById(R.id.venue_child_line)).setVisibility(View.GONE);
            }

        }catch (Exception e){}
    }

}




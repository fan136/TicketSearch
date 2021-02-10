package com.example.jjfan.eventsearch;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.net.Uri.encode;

public class EventActivity extends AppCompatActivity{
    private JSONObject event;
    private TabLayout tabLayout_event;
    private ViewPager viewPager_event;
    private FavouritesList favouritesList;


    private static final String TAG = EventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        favouritesList=FavouritesList.getInstance();

        Bundle b = getIntent().getExtras();
        if (b != null){
            try{
                event = new JSONObject(b.getString("key"));
                Log.e(TAG, event.toString());

                tabLayout_event = (TabLayout) findViewById(R.id.tabs_event);
                viewPager_event = (ViewPager) findViewById(R.id.viewpager_event);
                setupViewPager(viewPager_event);
                tabLayout_event.setupWithViewPager(viewPager_event);
                tabLayout_event.getTabAt(0).setIcon(R.drawable.info_outline);
                tabLayout_event.getTabAt(1).setIcon(R.drawable.artist);
                tabLayout_event.getTabAt(2).setIcon(R.drawable.venue);
                tabLayout_event.getTabAt(3).setIcon(R.drawable.upcoming);

            }catch (Exception e){
                // TODO error
            }
        }else{
            // TODO error
        }

        //add Map after initialize fragments
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EventFragment(), "EVENT");
        adapter.addFragment(new ArtistFragment(),"ARTIST(S)");
        adapter.addFragment(new VenueFragment(),"VENUE");
        adapter.addFragment(new UpcomingFragment(),"UPCOMING");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        MenuItem favItem = menu.getItem(0);
        if (favouritesList.isFav(event.toString())){
            favItem.setTitle("FAV");
            favItem.setIcon(R.drawable.heart_fill_red);
        }else{
            favItem.setTitle("NOFAV");
            favItem.setIcon(R.drawable.heart_fill_white);
        }

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e(TAG, "item clicked------------>");
        Log.e(TAG, String.valueOf(id));
        // set up fav
        if (id == R.id.mybutton_fav){
            setUpFav(item);
        }
        // set up tweet
        else if (id==R.id.mybutton_tweet){
            setUpTweet();
        }
        //back button
        else if (id == 16908332) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setUpFav(MenuItem item){
        Log.e(TAG, "onOptionsFav");
        if (item.getTitle().toString().equals("NOFAV")){
            item.setTitle("FAV");
            item.setIcon(R.drawable.heart_fill_red);
            try{
                favouritesList.addEvent(event.toString());
                Intent intent1 = new Intent();
                intent1.putExtra("type", "addFav");
                intent1.putExtra("jsonObj", event.toString());
                intent1.setAction("setUpFavList");
                sendBroadcast(intent1);
                Log.e(TAG, "add fav intent sent");
                showToast(1);
            } catch (Exception e){}
        }else{
            item.setTitle("NOFAV");
            item.setIcon(R.drawable.heart_fill_white);
            try{
                favouritesList.removeEvent(event.toString());
                Intent intent1 = new Intent();
                intent1.putExtra("type", "deleteFav");
                intent1.putExtra("jsonObj", event.toString());
                intent1.setAction("setUpFavList");
                sendBroadcast(intent1);
                Log.e(TAG, "delete fav intent sent");
                showToast(2);
            } catch (Exception e){}
        }

    }
    private void setUpTweet(){
        Log.e(TAG, "onOptionsTweet");
        String tweet = "";
        try{
            String name =  event.getString("name");
            String venue = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            String url = event.getString("url");
            tweet = "https://twitter.com/intent/tweet?text=" + "Check out " + name + " located at "
                    + venue + ". Website: " + url + "#CSCI571EventSearch";
            Log.e(TAG, tweet);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(tweet));
            startActivity(i);

        }catch(Exception e){}
    }
    private void showToast(int addOrRemove){
        try{
            String title = event.getString("name");
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
        }catch (Exception e){}
    }
    public JSONObject getEvent() {
        return event;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}







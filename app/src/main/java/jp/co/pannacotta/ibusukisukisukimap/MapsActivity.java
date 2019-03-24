package jp.co.pannacotta.ibusukisukisukimap;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<OnsenData> onsenDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.infowindow_onsen, null);
                TextView onsenNameTextView = view.findViewById(R.id.onsenNameTextView);
                TextView urlTextView = view.findViewById(R.id.urlTextView);
                ImageView heartImageView = view.findViewById(R.id.heartImageView);
                String markerId = marker.getId();

                for(int i=0; i<onsenDataList.size(); i++){
                    OnsenData onsenData = onsenDataList.get(i);
                    if(onsenData.markerId.equals(markerId)){
                        onsenNameTextView.setText(onsenData.title);
                        urlTextView.setText(onsenData.url);
                        heartImageView.setImageResource(getResources().getIdentifier(onsenData.image, "drawable", getPackageName()));
                    }
                }
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng ibusukiCamera = new LatLng(31.222541, 130.604898);
        CameraPosition cameraPos = new CameraPosition.Builder().target(ibusukiCamera).zoom(12.0f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        MarkerOptions options = new MarkerOptions();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.onsen_icon);
        options.icon(icon);
        InputStream inputStream = null;

        try{
            inputStream = getAssets().open("onsen_list.json");


        }catch (IOException e){

        }

        JsonReader jsonReader = new JsonReader( new InputStreamReader( inputStream ) );
        onsenDataList = new Gson().fromJson(jsonReader, new TypeToken<ArrayList<OnsenData>>() {
        }.getType());

        for(int i=0; i< onsenDataList.size(); i++ ){
            OnsenData onsenData = onsenDataList.get(i);
                    LatLng latLng = new LatLng(onsenData.lat,onsenData.lng);
                    String title = onsenData.title;
                    String url = onsenData.url;
            Marker marker = mMap.addMarker(new MarkerOptions().icon(icon).position(latLng).title(title).snippet(url));
            onsenData.markerId = marker.getId();
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                final CustomTabsIntent tabsIntent = builder
                        .setShowTitle(false)
                        .setToolbarColor(ContextCompat.getColor(MapsActivity.this,R.color.colorPrimary))
                        .enableUrlBarHiding().build();
                tabsIntent.launchUrl(MapsActivity.this, Uri.parse(marker.getSnippet()));

            }
        });
    }
}

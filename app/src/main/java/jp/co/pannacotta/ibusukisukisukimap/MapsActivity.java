package jp.co.pannacotta.ibusukisukisukimap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng ibusukiCamera = new LatLng(31.2256762,130.5520578);
        CameraPosition cameraPos = new CameraPosition.Builder().target(ibusukiCamera).zoom(11.0f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        InputStream inputStream = null;

        try{
            inputStream = getAssets().open("onsen_list.json");


        }catch (IOException e){

        }

        JsonReader jsonReader = new JsonReader( new InputStreamReader( inputStream ) );
        List<OnsenData> onsenDataList = new Gson().fromJson(jsonReader, new TypeToken<ArrayList<OnsenData>>() {
        }.getType());

        for(int i=0; i< onsenDataList.size(); i++ ){
            onsenDataList.get(i);
                    LatLng latLng = new LatLng(onsenDataList.get(i).lat,onsenDataList.get(i).lng);
                    String title = onsenDataList.get(i).title;
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
    }
}

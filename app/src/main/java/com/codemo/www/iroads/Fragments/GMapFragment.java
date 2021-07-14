package com.codemo.www.iroads.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.codemo.www.iroads.Database.SensorData;
import com.codemo.www.iroads.MainActivity;
import com.codemo.www.iroads.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.apache.log4j.chainsaw.Main;

/**
 * A simple {@link Fragment} subclass.
 *
 * Edited by Achintha Isuru 2021/06/05
 */
public class GMapFragment extends Fragment implements OnMapReadyCallback {


    private static final String TAG = "GMapFragment";
    private static MainActivity activity;
    private static GoogleMap gmap;
    private static Marker marker;
    private LocationManager locationManager;

    public GMapFragment() {
        // Required empty public constructor
    }

    public static void setActivity(MainActivity Activity) {
        activity = Activity;
    }

    /**
     * Method used to get current location on FAB clicked
     */

    private static void updateLocationOnFabClicked() {
        double lat = Double.parseDouble(SensorData.getMlat());
        double lon = Double.parseDouble(SensorData.getMlon());

        if (lat != 0.0) {
            zoomToLocation(lat, lon, 15.0f);
        }
    }

    /**
     * Method used to initially zoom to last known location
     */

    private static void autoZoomLocation(Location location) {
        double lat = Double.parseDouble(SensorData.getMlat());
        double lon = Double.parseDouble(SensorData.getMlon());

        if (lat != 0.0) {
            zoomToLocation(lat, lon, 15.0f);
        } else {
            LatLng sri_lanka = new LatLng(8.068590, 80.654578);
            try{
                zoomToLocation(location.getLatitude(), location.getLongitude(), 15.0f);
            } catch (Exception e){
                zoomToLocation(sri_lanka.latitude, sri_lanka.longitude, 7.0f);
            }

        }
    }

    /**
     * create the marker and zoom to the location
     */
    private static void zoomToLocation(double lat, double lon, float zoomLevel) {
        LatLng loc = new LatLng(lat, lon);

        if (marker == null) {
            marker = gmap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title("You are Here!")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
//            marker.showInfoWindow();
        } else {
            marker.setPosition(loc);
        }
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
    }


    /**
     * @return last best location
     */
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
                    }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocationOnFabClicked();
            }
        });

        // autoZoom();

        FloatingActionButton infoBtn = view.findViewById(R.id.infoBtn);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Visit iroads.projects.mrt.ac.lk for more info.", Snackbar.LENGTH_LONG)
                        .setAction("go !", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent browserIntent = new
                                        Intent(Intent.ACTION_VIEW,
                                        Uri.parse(getString(R.string.page_address)));
                                startActivity(browserIntent);

                            }
                        }).show();
            }
        });
        Log.d("rht", "aaaaaaaaaaaaaaaaaaaa.....map fragment created....aaaaaaaaaaaaaaaaaaaaaa***");
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.initMap().getMapAsync(this);
//        NavigationHandler.navigateTo("mapFragment");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        Log.d("ssssssssssssssssssssss", "sssssssssssssssssssssssssssssssssss");
        marker = null;
        float zoomLevel = 7.0f;
        autoZoomLocation(getLastBestLocation());
    }

}

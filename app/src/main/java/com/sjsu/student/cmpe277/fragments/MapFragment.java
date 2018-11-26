package com.sjsu.student.cmpe277.fragments;

import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sjsu.student.cmpe277.R;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MapFragment extends DialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private IntentFilter connectivityIntentFilter;
    private View dialogView;
    private Boolean isOpenOnly;
    private Integer maxPrice;
    private Integer radius;
    private Place previousPlace;
    private LocationRequest locationRequest;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;

    static double latitude, longitude;

    private LocationManager locationManager;
    private Location lastlocation;
    private Marker currentLocationmMarker;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;

    private String recordingTitle;
    private String recordingName;
    static String locationAddress;


    public MapFragment() {
        // Required empty public constructor
    }

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        return view;
    }

    public MapFragment newInstance(Double latitude, Double longitude, String recordingTitle, String locationAddress) {
        MapFragment f = new MapFragment();
        if (latitude != null && longitude != null) {
            f.latitude = latitude;
            f.longitude = longitude;
            f.locationAddress = locationAddress;
        } else {
            //Use default
            latitude = 37.3382;
            longitude = -121.8863;
            locationAddress = "San Jose";
        }
        f.recordingTitle = recordingTitle;
        f.recordingName = recordingTitle;
        return f;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectivityIntentFilter = new IntentFilter(CONNECTIVITY_ACTION);

        /*
         *   Obtain the SupportMapFragment and get notified when the map is ready to be used.
         */

        final SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        parseResults(latitude, longitude);
    }

    public void parseResults(double latitude, double longitude) {
        //Empty map for new data points.
        mMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Recording location for " + recordingName)
                .snippet(locationAddress);

        Marker m = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        builder.include(m.getPosition());

        //try block to avoid forming bounds when no locations are selected.
        try {
            LatLngBounds latLngBounds = builder.build();
            int padding = 10;

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
            mMap.animateCamera(cu);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 10));

        } catch (IllegalStateException | ParseException | NullPointerException e) {
            //Do not animate/move camera.
        }
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
    }
}

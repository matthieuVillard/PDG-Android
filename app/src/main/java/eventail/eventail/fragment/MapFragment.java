package eventail.eventail.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eventail.eventail.R;
import eventail.eventail.activity.EventActivity;
import eventail.eventail.activity.MainActivity;
import eventail.eventail.activity.UserActivity;
import eventail.eventail.models.Event;
import eventail.eventail.service.tracker.GPSListener;
import eventail.eventail.service.tracker.GPSTracker;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.LocationTask;
import eventail.eventail.service.webService.task.LocationUpdateTask;
import eventail.eventail.service.webService.task.UserTask;
import eventail.eventail.service.webService.task.UsersListTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GPSListener, GoogleMap.OnMapLongClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int FINE_LOCATION_PERMISSION = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static int CREATE_EVENT_REQUEST = 1;

    private GPSTracker gps;
    private GoogleMap googleMap;

    private View view;
    private Marker currPos = null;

    public MapFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivityForResult(intent, CREATE_EVENT_REQUEST);
            }
        });

        final Button button = (Button) view.findViewById(R.id.button1);

        com.google.android.gms.maps.MapFragment mapFragment = new com.google.android.gms.maps.MapFragment();
        getFragmentManager().beginTransaction().add(R.id.frame_layout1, mapFragment).commit();
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        gps = new GPSTracker(view.getContext(), this);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        this.googleMap = googleMap;

        new LocationTask(getActivity(), new WebTaskHandler() {
            @Override
            public void handleSuccess(JSONObject response) {
                try {
                    JSONArray data = response.getJSONObject("data").getJSONArray("users");
                    for(int i = 0; i < data.length(); i++){
                        JSONObject user = data.getJSONObject(i);
                        if(!user.isNull("location")) {
                            String location = user.getString("location");
                            LatLng latLng = new LatLng(
                                    Double.parseDouble(location.substring(0, location.indexOf(","))),
                                    Double.parseDouble(location.substring(location.indexOf(",") + 1))
                            );

                            MapFragment.this.googleMap.addMarker(new MarkerOptions()
                                    .title(user.getString("username"))
                                    .position(latLng));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        new LocationUpdateTask(getActivity()).execute(location.getLatitude(), location.getLongitude());

        /*
        if(currPos != null){
            currPos.remove();
        }
        LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        currPos = googleMap.addMarker(new MarkerOptions()
                .title("My position")
                .snippet("This is were i'm currently standing.")
                .position(currentPos));
                */
    }

    @Override
    public void onInitialized(Location location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onEnabled(Location location) {
        if(location != null){
            LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
            /*
            currPos = googleMap.addMarker(new MarkerOptions()
                    .title("My position")
                    .snippet("This is were i'm currently standing.")
                    .position(currentPos));*/
        }
    }

    @Override
    public void onDisabled() {
        /*
        if(currPos != null){
            currPos.remove();
            currPos = null;
        }*/
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        //showEvent(latLng, 100);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_EVENT_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                Event event = (Event)data.getExtras().getSerializable("event");
                showEvent(event);
            }
        }
    }

    private void showEvent(Event event){
        //red outline
        int strokeColor = 0xffff0000;
        //opaque red fill
        int shadeColor = 0x44ff0000;

        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(event.getRadius()).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().title(event.getName()).snippet(event.getPlace()).position(latLng);
        googleMap.addMarker(markerOptions);
    }
}

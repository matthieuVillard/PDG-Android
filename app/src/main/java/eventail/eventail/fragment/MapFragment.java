package eventail.eventail.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import eventail.eventail.R;
import eventail.eventail.activity.events.CreateEventActivity;
import eventail.eventail.models.Credential;
import eventail.eventail.models.Event;
import eventail.eventail.models.InterestPoint;
import eventail.eventail.models.User;
import eventail.eventail.service.tracker.GPSListener;
import eventail.eventail.service.tracker.GPSTracker;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.events.InterestPointsTask;
import eventail.eventail.service.webService.task.auth.LaunchTask;
import eventail.eventail.service.webService.task.users.LocationTask;
import eventail.eventail.service.webService.task.events.NearbyEventsTask;
import eventail.eventail.service.webService.task.users.NearbyUsersTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GPSListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static int FINE_LOCATION_PERMISSION = 1;

    public static int CREATE_EVENT_REQUEST = 1;
    public static int MODIFY_EVENT_REQUEST = 2;
    public static int DELETE_EVENT_REQUEST = 3;

    private GPSTracker gps;
    private GoogleMap googleMap;

    private View view;
    private EventFragment fragment;
    private FloatingActionButton fab;
    private FloatingActionButton bottomFab;

    private AppBarLayout appBar;

    private Map<Integer, Pair<Marker, Circle>> events = new HashMap<>();
    private Set<Marker> poi = new HashSet<>();
    private Map<Integer, Marker> users = new HashMap<>();

    public MapFragment() {

    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivityForResult(intent, CREATE_EVENT_REQUEST);
            }
        });

        com.google.android.gms.maps.MapFragment mapFragment = new com.google.android.gms.maps.MapFragment();
        getFragmentManager().beginTransaction().add(R.id.frame_layout1, mapFragment).commit();
        mapFragment.getMapAsync(this);

        View bottomSheet = getActivity().findViewById(R.id.bottom_sheet);
        bottomFab = (FloatingActionButton) getActivity().findViewById(R.id.bottom_sheet_fab);
        bottomFab.setVisibility(View.INVISIBLE);
        bottomSheet.setVisibility(View.INVISIBLE);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(!Credential.getInstance(getActivity()).isActive()) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Unactive Application")
                    .setMessage("This client is not active. Do you wish to use it?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LaunchTask(getActivity()).execute();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public void onPause(){
        if(fragment != null){
            getFragmentManager().beginTransaction().remove(fragment).commit();
            fragment = null;
        }
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);

                } else {
                    googleMap.setMyLocationEnabled(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;


        if(getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
            } else {
                googleMap.setMyLocationEnabled(true);
            }

            gps = new GPSTracker(view.getContext(), this);

            googleMap.setTrafficEnabled(false);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    toggle();
                }
            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTag() != null) {
                        show();
                        final Event event = (Event) marker.getTag();
                        for (Marker m : poi) {
                            m.remove();
                        }
                        if (fragment == null) {
                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                            int margin = (int) getResources().getDimension(R.dimen.fab_margin);
                            int bottomMargin = (int) getResources().getDimension(R.dimen.bottom_sheet_peek_height) + 150;
                            params.setMargins(margin, margin, margin, bottomMargin);

                            fragment = EventFragment.newInstance(event);
                            getFragmentManager().beginTransaction().replace(R.id.bottom_sheet, fragment).commit();
                            bottomFab.setVisibility(View.VISIBLE);
                            bottomFab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (gps.getLocation() != null) {
                                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", gps.getLatitude(), gps.getLongitude(), event.getLatitude(), event.getLongitude());
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            fragment.loadEvent(event);
                        }
                        new InterestPointsTask(getActivity(), new WebTaskHandler<List<InterestPoint>>() {
                            @Override
                            public void handleSuccess(List<InterestPoint> response) {
                                for (InterestPoint point : response) {
                                    showInterestPoint(point);
                                }
                            }
                        }).execute(event.getId());
                    }
                    return false;
                }
            });

            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    loadData();
                }
            });

            if(!gps.canGetLocation()) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(46.916639, 8.234862)).zoom(6.5f).build();
                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (getActivity() != null && Credential.getInstance(getActivity()).isActive()) {
            new LocationTask(getActivity()).execute(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onInitialized(Location location) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(13).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onEnabled(Location location) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_EVENT_REQUEST || requestCode == MODIFY_EVENT_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                Event event = (Event)data.getExtras().getSerializable("event");
                showEvent(event);
            }
        }
        else if(requestCode == DELETE_EVENT_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                Event event = (Event)data.getExtras().getSerializable("event");
                if(events.containsKey(event.getId())) {
                    events.get(event.getId()).first.remove();
                    events.get(event.getId()).second.remove();
                    events.remove(event.getId());

                    for (Marker m : poi) {
                        m.remove();
                    }
                }
            }
        }
    }

    private void toggle(){
        if (appBar.getVisibility() != View.VISIBLE) {
            show();
        } else {
            hide();
        }
    }

    private void show(){
        appBar.setVisibility(View.VISIBLE);
        fab.show();
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if(fragment != null){
            fragment.show();
            bottomFab.show();
        }
    }

    private void hide(){
        appBar.setVisibility(View.INVISIBLE);
        fab.hide();
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(fragment != null){
            fragment.hide();
            bottomFab.setVisibility(View.INVISIBLE);
        }
    }

    private void loadData(){
        if(getActivity() != null) {
            VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
            Location center = new Location("center");
            center.setLatitude(vr.latLngBounds.getCenter().latitude);
            center.setLongitude(vr.latLngBounds.getCenter().longitude);
            Location left = new Location("left");
            left.setLatitude(vr.latLngBounds.northeast.latitude);
            left.setLongitude(vr.latLngBounds.northeast.longitude);
            new NearbyEventsTask(getActivity(), new WebTaskHandler<List<Event>>() {
                @Override
                public void handleSuccess(List<Event> events) {
                    for (Event event : events) {
                        showEvent(event);
                    }
                }
            }).execute(center.getLatitude(), center.getLongitude(), (double) center.distanceTo(left));

            new NearbyUsersTask(getActivity(), new WebTaskHandler<List<User>>() {
                @Override
                public void handleSuccess(List<User> users) {
                    for (User user : users) {
                        showUser(user);
                    }
                }
            }).execute(center.getLatitude(), center.getLongitude(), (double) center.distanceTo(left));
        }
    }

    private void showEvent(Event event){
        LatLng latLng = new LatLng(event.getLatitude(), event.getLongitude());

        if(gps.canGetLocation() && gps.getLocation() != null) {
            Location evLoc = new Location("evLoc");
            evLoc.setLatitude(event.getLatitude());
            evLoc.setLongitude(event.getLongitude());
            Location myLoc = new Location("myLoc");
            myLoc.setLatitude(gps.getLatitude());
            myLoc.setLongitude(gps.getLongitude());

            event.setDistance((int)evLoc.distanceTo(myLoc));
        }

        if(events.containsKey(event.getId())){
            Pair<Marker, Circle> pair = events.get(event.getId());
            pair.first.setPosition(latLng);
            pair.second.setCenter(latLng);
            pair.second.setRadius(event.getRadius());
        }
        else{
            //red outline
            int strokeColor = 0xffff0000;
            //opaque red fill
            int shadeColor = 0x44ff0000;

            CircleOptions circleOptions = new CircleOptions().center(latLng).radius(event.getRadius()).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
            Circle circle = googleMap.addCircle(circleOptions);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng);

            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(event);
            events.put(event.getId(), new Pair<Marker, Circle>(marker, circle));
        }
    }

    private void showInterestPoint(InterestPoint point){
        LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(point.getTitle()).snippet(point.getDescription());

        Marker marker = googleMap.addMarker(markerOptions);
        poi.add(marker);
    }

    private void showUser(User user){
        if(user.getLatitude() != null && user.getLongitude() != null){
            LatLng latLng = new LatLng(user.getLatitude(), user.getLongitude());

            int distance = 0;

            if(gps.canGetLocation() && gps.getLocation() != null) {
                Location evLoc = new Location("evLoc");
                evLoc.setLatitude(user.getLatitude());
                evLoc.setLongitude(user.getLongitude());
                Location myLoc = new Location("myLoc");
                myLoc.setLatitude(gps.getLatitude());
                myLoc.setLongitude(gps.getLongitude());

               distance = (int)evLoc.distanceTo(myLoc);
            }

            if(users.containsKey(user.getId())){
                Marker marker = users.get(user.getId());
                marker.setTitle(user.getFirstname() + " " + user.getLastname());
                if(distance > 0){
                    if(distance > 1000){
                        marker.setSnippet(distance / 1000 + " km");
                    }
                    else{
                        marker.setSnippet(distance + " m");
                    }
                }
                else{
                    marker.setSnippet("");
                }

                marker.setPosition(latLng);
            }
            else{
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(user.getFirstname() + " " + user.getLastname());

                if(distance > 0){
                    if(distance > 1000){
                        markerOptions.snippet(distance / 1000 + " km");
                    }
                    else{
                        markerOptions.snippet(distance + " m");
                    }
                }

                Marker marker = googleMap.addMarker(markerOptions);
                users.put(user.getId(), marker);
            }
        }
    }
}

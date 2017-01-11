package eventail.eventail.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eventail.eventail.R;
import eventail.eventail.activity.events.EditEventActivity;
import eventail.eventail.lib.BottomSheetBehaviorGoogleMapsLike;
import eventail.eventail.lib.MergedAppBarLayoutBehavior;
import eventail.eventail.models.Credential;
import eventail.eventail.models.Event;
import eventail.eventail.service.webService.task.events.DeleteEventTask;

import static eventail.eventail.fragment.MapFragment.CREATE_EVENT_REQUEST;


public class EventFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "event";
    private static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private static final DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

    private Event event;

    private BottomSheetBehaviorGoogleMapsLike behavior;
    private MergedAppBarLayoutBehavior mergedAppBarLayoutBehavior;
    private TextView bottom_sheet_title;
    private TextView bottom_sheet_description;
    private TextView bottom_sheet_distance;

    private TextView date;
    private TextView duration;
    private TextView description;
    private TextView location;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(Event event) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event)getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_event, container, false);

        View bottomSheet = getActivity().findViewById( R.id.bottom_sheet );
        bottomSheet.setVisibility(View.VISIBLE);
        behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);

        final AppBarLayout mergedAppBarLayout = (AppBarLayout) getActivity().findViewById(R.id.merged_appbarlayout);
        mergedAppBarLayoutBehavior = MergedAppBarLayoutBehavior.from(mergedAppBarLayout);
        mergedAppBarLayoutBehavior.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
            }
        });

        behavior.addBottomSheetCallback(new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehaviorGoogleMapsLike.State int newState) {
                if(newState == BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED && getActivity() != null){
                    Toolbar expanded =(Toolbar) mergedAppBarLayout.findViewById(R.id.expanded_toolbar);
                    expanded.getMenu().clear();
                    if(Credential.getInstance(getActivity()).getUserId() == event.getOwner()) {
                        if(!event.isSpontaneous()) {
                            expanded.inflateMenu(R.menu.menu_modify);
                            expanded.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getItemId() == R.id.action_modify) {
                                        Intent intent = new Intent(getActivity(), EditEventActivity.class);
                                        intent.putExtra("event", event);
                                        startActivity(intent);
                                        return true;
                                    }
                                    return false;
                                }
                            });
                        }
                        else{
                            expanded.inflateMenu(R.menu.menu_delete);
                            expanded.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getItemId() == R.id.action_delete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle(R.string.leave_dialog_title)
                                                .setMessage(R.string.leave_event_msg)
                                                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    new DeleteEventTask(getActivity()).execute(event);
                                                    }
                                                })
                                                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                    }
                                                });
                                        builder.create().show();
                                        return true;
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottom_sheet_title = (TextView)view.findViewById(R.id.bottom_sheet_title);
        bottom_sheet_description = (TextView)view.findViewById(R.id.bottom_sheet_description);
        bottom_sheet_distance = (TextView)view.findViewById(R.id.bottom_sheet_distance);
        date = (TextView) view.findViewById(R.id.date);
        duration = (TextView) view.findViewById(R.id.duration);
        location = (TextView) view.findViewById(R.id.location);
        description = (TextView) view.findViewById(R.id.description);

        loadEvent(event);

        // Inflate the layout for this fragment
        return view;
    }

    public void loadEvent(Event event){
        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
        mergedAppBarLayoutBehavior.setToolbarTitle(event.getTitle());
        bottom_sheet_title.setText(event.getTitle());
        bottom_sheet_description.setText(event.getDescription());
        if(event.getDistance() != null){
            if(event.getDistance() > 1000){
                bottom_sheet_distance.setText((event.getDistance() / 1000) + " km");
            }
            else{
                bottom_sheet_distance.setText(event.getDistance() + " m");
            }
        }
        else{
            bottom_sheet_distance.setText("");
        }

        date.setText(df.format(event.getStartDate().getTime()));

        long durationMillis = event.getEndDate().getTimeInMillis() - event.getStartDate().getTimeInMillis();
        long days = durationMillis / (24 * 60 * 60 * 1000);
        durationMillis -= days * 24 * 60 * 60 * 1000;
        long hours = durationMillis / (60 * 60 * 1000);
        durationMillis -= hours * 60 * 60 * 1000;
        long minutes = durationMillis / (60 * 1000);

        StringBuilder s = new StringBuilder();
        if(days > 0){
            s.append(days + " " + getString(R.string.label_event_day));
            if(days > 1){
                s.append("s");
            }
        }
        if(hours > 0){
            if(days > 0){
                s.append(", ");
            }
            s.append(hours + " " + getString(R.string.label_event_hour));
            if(hours > 1){
                s.append("s");
            }
        }
        if(minutes > 0){
            if(days > 0 || hours > 0){
                s.append(", ");
            }
            s.append(minutes + " " + getString(R.string.label_event_minute));
            if(minutes > 1){
                s.append("s");
            }
        }

        if(s.toString().isEmpty()){
            duration.setText("-");
        }
        else {
            duration.setText(s);
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    event.getLatitude(),
                    event.getLongitude(),
                    // In this sample, get just a single address.
                    1);

            if(!addresses.isEmpty()) {

                android.location.Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }

                location.setText(TextUtils.join(", ", addressFragments));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        description.setText(event.getDescription());
    }

    public void show(){
        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
        behavior.setHideable(false);
    }

    public void hide(){
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
    }

    public void toggle(){
        if(behavior.getState() == BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN){
            show();
        }
        else{
            hide();
        }
    }
}

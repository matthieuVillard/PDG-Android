package eventail.eventail.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eventail.eventail.R;
import eventail.eventail.fragment.DatePickerDialog;
import eventail.eventail.fragment.TimePickerDialog;
import eventail.eventail.models.Event;

public class EventActivity extends AppCompatActivity implements DatePickerDialog.DatePickerDialogListener, TimePickerDialog.TimePickerDialogListener {

    private static int PLACE_PICKER_REQUEST = 1;

    private EditText name;

    private TextView startDate;
    private TextView startTime;
    private TextView endDate;
    private TextView endTime;
    private TextView dateReceiver;
    private TextView timeReceiver;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

    private TextView locationLbl;
    private Place location;
    private SeekBar radius;
    private TextView radiusTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        }

        name = (EditText) findViewById(R.id.name);
        startDate = (TextView) findViewById(R.id.start_date);
        startTime = (TextView) findViewById(R.id.start_time);
        endDate = (TextView) findViewById(R.id.end_date);
        endTime = (TextView) findViewById(R.id.end_time);
        locationLbl = (TextView) findViewById(R.id.location);
        radius = (SeekBar) findViewById(R.id.radius);
        radiusTxt = (TextView) findViewById(R.id.radiusTxt);

        View.OnClickListener dateListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateReceiver = (TextView)view;
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = null;
                if(dateReceiver.getText() != null && dateReceiver.getText().length() != 0){
                    try {
                        datePickerDialog = DatePickerDialog.newInstance(df.parse(dateReceiver.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(EventActivity.this, "Date format error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    datePickerDialog = new DatePickerDialog();
                }
                if(datePickerDialog != null) {
                    datePickerDialog.show(fm, "fragment_date_picker");
                }
            }
        };

        View.OnClickListener timeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeReceiver = (TextView)view;
                FragmentManager fm = getSupportFragmentManager();
                TimePickerDialog timePickerDialog = null;
                if(timeReceiver.getText() != null && timeReceiver.getText().length() != 0){
                    try {
                        timePickerDialog = TimePickerDialog.newInstance(tf.parse(timeReceiver.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(EventActivity.this, "Time format error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    timePickerDialog = new TimePickerDialog();
                }
                if(timePickerDialog != null) {
                    timePickerDialog.show(fm, "fragment_time_picker");
                }
            }
        };


        startDate.setOnClickListener(dateListener);
        startTime.setOnClickListener(timeListener);
        endDate.setOnClickListener(dateListener);
        endTime.setOnClickListener(timeListener);

        locationLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(EventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Toast.makeText(EventActivity.this, "Location service error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(EventActivity.this, "Location service error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusTxt.setText(getString(R.string.hint_event_radius) + " : " + i + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.leave_dialog_title)
                .setMessage(R.string.leave_event_msg)
                .setPositiveButton(R.string.btn_leave_event, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EventActivity.super.onBackPressed();
                        overridePendingTransition(R.anim.slid_stay, R.anim.slid_out);
                    }
                })
                .setNegativeButton(R.string.btn_keep_editing_event, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create().show();
    }

    @Override
    public void onDateChange(Date date) {
        if(dateReceiver != null){
            dateReceiver.setText(df.format(date));
        }
    }

    @Override
    public void onTimeChange(Calendar time) {
        if(timeReceiver != null){
            timeReceiver.setText(tf.format(time.getTime()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                location = PlacePicker.getPlace(this, data);
                locationLbl.setText(location.getName());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if(id == R.id.action_create){
            if(!name.getText().toString().isEmpty() && location != null && radius.getProgress() > 0) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("event", new Event(name.getText().toString(), location, radius.getProgress()));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

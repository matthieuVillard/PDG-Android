package eventail.eventail.activity.events;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Calendar;

import eventail.eventail.R;
import eventail.eventail.fragment.DatePickerDialog;
import eventail.eventail.fragment.TimePickerDialog;
import eventail.eventail.models.Event;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.events.CreateEventTask;

public class CreateEventActivity extends AppCompatActivity {

    private static int PLACE_PICKER_REQUEST = 1;

    protected Calendar startCal = Calendar.getInstance();
    protected Calendar endCal = Calendar.getInstance();

    protected CheckBox spontaneous;
    protected EditText name;
    protected TextView startDate;
    protected TextView startTime;
    protected TextView endDate;
    protected TextView endTime;
    protected DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    protected DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);

    protected EditText description;

    protected TextView locationLbl;
    protected LatLng location;
    protected SeekBar radius;
    protected TextView radiusTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        }

        name = (EditText) findViewById(R.id.name);
        spontaneous = (CheckBox) findViewById(R.id.spontaneous);
        startDate = (TextView) findViewById(R.id.start_date);
        startTime = (TextView) findViewById(R.id.start_time);
        endDate = (TextView) findViewById(R.id.end_date);
        endTime = (TextView) findViewById(R.id.end_time);
        description = (EditText) findViewById(R.id.description);
        locationLbl = (TextView) findViewById(R.id.location);
        radius = (SeekBar) findViewById(R.id.radius);
        radiusTxt = (TextView) findViewById(R.id.radiusTxt);

        spontaneous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startTime.setEnabled(!b);
                startDate.setEnabled(!b);
                endTime.setEnabled(!b);
                endDate.setEnabled(!b);
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(startCal);
                datePickerDialog.setOnDateChangeListener(new DatePickerDialog.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(Calendar date) {
                        startDate.setText(df.format(date.getTime()));
                        startCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        startCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        startCal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    }
                });
                datePickerDialog.show(fm, "fragment_date_picker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(endCal);
                datePickerDialog.setOnDateChangeListener(new DatePickerDialog.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(Calendar date) {
                        endDate.setText(df.format(date.getTime()));
                        endCal.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                        endCal.set(Calendar.MONTH, date.get(Calendar.MONTH));
                        endCal.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    }
                });
                datePickerDialog.show(fm, "fragment_date_picker");
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(startCal);
                timePickerDialog.setOnTimeChangedListener(new TimePickerDialog.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(Calendar time) {
                        startTime.setText(tf.format(time.getTime()));
                        startCal.set(Calendar.HOUR, time.get(Calendar.HOUR));
                        startCal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                        startCal.set(Calendar.SECOND, time.get(Calendar.SECOND));
                    }
                });
                timePickerDialog.show(fm, "fragment_time_picker");
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(endCal);
                timePickerDialog.setOnTimeChangedListener(new TimePickerDialog.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(Calendar time) {
                        endTime.setText(tf.format(time.getTime()));
                        endCal.set(Calendar.HOUR, time.get(Calendar.HOUR));
                        endCal.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                        endCal.set(Calendar.SECOND, time.get(Calendar.SECOND));
                    }
                });
                timePickerDialog.show(fm, "fragment_time_picker");
            }
        });


        locationLbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(CreateEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateEventActivity.this, "Location service error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(CreateEventActivity.this, "Location service error : " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                        CreateEventActivity.super.onBackPressed();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                location = place.getLatLng();
                locationLbl.setText(place.getName());
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
                new CreateEventTask(this, new WebTaskHandler<Event>() {
                    @Override
                    public void handleSuccess(Event event) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("event", event);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }).execute(new Event(name.getText().toString(), description.getText().toString(), startCal, endCal, spontaneous.isChecked(), location.latitude, location.longitude, radius.getProgress()));
            }
            else{
                Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

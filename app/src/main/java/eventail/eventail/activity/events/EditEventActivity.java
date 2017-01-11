package eventail.eventail.activity.events;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eventail.eventail.R;
import eventail.eventail.models.Event;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.events.DeleteEventTask;
import eventail.eventail.service.webService.task.events.EditEventTask;

public class EditEventActivity extends CreateEventActivity {

    private Event event;

    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cancel = (Button)findViewById(R.id.cancel);

        event = (Event) getIntent().getSerializableExtra("event");
        name.setText(event.getTitle());
        description.setText(event.getDescription());
        startCal.setTime(event.getStartDate().getTime());
        startDate.setText(df.format(startCal.getTime()));
        startTime.setText(tf.format(startCal.getTime()));
        endCal.setTime(event.getEndDate().getTime());
        endDate.setText(df.format(endCal.getTime()));
        endTime.setText(tf.format(endCal.getTime()));
        spontaneous.setChecked(event.isSpontaneous());
        location = new LatLng(event.getLatitude(), event.getLongitude());
        locationLbl.setText(event.getLatitude() + ", " + event.getLongitude());

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(
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

                locationLbl.setText(TextUtils.join(", ", addressFragments));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        radius.setProgress(event.getRadius());
        radiusTxt.setText(getString(R.string.hint_event_radius) + " : " + event.getRadius() + "m");

        cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                builder.setTitle(R.string.leave_dialog_title)
                        .setMessage(R.string.leave_event_msg)
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new DeleteEventTask(EditEventActivity.this, new WebTaskHandler<Void>() {
                                    @Override
                                    public void handleSuccess(Void response) {
                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("event", event);
                                        //returnIntent.putExtra("event", );
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        EditEventActivity.this.finish();
                                    }
                                }).execute(event);
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_modify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if(id == R.id.action_modify){
            if(!name.getText().toString().isEmpty() && location != null && radius.getProgress() > 0) {

                event.setTitle(name.getText().toString());
                event.setDescription(description.getText().toString());
                event.setStartDate(startCal);
                event.setEndDate(endCal);
                event.setSpontaneous(spontaneous.isChecked());
                event.setLatitude(location.latitude);
                event.setLongitude(location.longitude);
                event.setRadius(radius.getProgress());

                new EditEventTask(this, new WebTaskHandler<Event>() {
                    @Override
                    public void handleSuccess(Event event) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("event", event);
                        //returnIntent.putExtra("event", );
                        setResult(Activity.RESULT_OK, returnIntent);
                        EditEventActivity.this.finish();
                    }
                }).execute(event);

            }
            else{
                Toast.makeText(this, "Invalid fields", Toast.LENGTH_LONG);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

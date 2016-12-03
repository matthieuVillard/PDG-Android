package eventail.eventail.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import eventail.eventail.R;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.UsersListTask;

public class UsersList extends AppCompatActivity {

    private TextView longitude, latitude, GPSState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        final Button button = (Button) findViewById(R.id.button1);

        latitude = (TextView) findViewById(R.id.viewLatitude);
        longitude = (TextView) findViewById(R.id.viewLongitude);
        GPSState = (TextView) findViewById(R.id.GPSState);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new UsersListTask(UsersList.this).execute();
            }
        });
    }
}

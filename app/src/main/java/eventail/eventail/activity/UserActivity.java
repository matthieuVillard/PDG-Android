package eventail.eventail.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import eventail.eventail.Eventail;
import eventail.eventail.R;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.TokenTask;
import eventail.eventail.service.webService.task.UserTask;

/**
 * A login screen that offers login via email/password.
 */
public class UserActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserTask mUserTask = null;

    // UI references.
    private EditText username = null;
    private EditText firstName = null;
    private EditText lastName = null;
    private EditText emailField = null;
    private EditText passwordField = null;
    private Button login = null;
    private LinearLayout register = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Link to GUI elements
        username        = (EditText) findViewById(R.id.username);
        firstName       = (EditText) findViewById(R.id.firstname);
        lastName        = (EditText) findViewById(R.id.lastname);
        emailField      = (EditText) findViewById(R.id.email);
        passwordField   = (EditText) findViewById(R.id.password);

        mUserTask = new UserTask(this, new WebTaskHandler() {
            @Override
            public void handleSuccess(JSONObject response) {
                username.setText(response.optString("username"));
                firstName.setText(response.optString("firstname"));
                lastName.setText(response.optString("lastname"));
                emailField.setText(response.optString("mail"));

                Toast.makeText(UserActivity.this, response.optString("firstname"), Toast.LENGTH_LONG).show();
            }
        });
        mUserTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


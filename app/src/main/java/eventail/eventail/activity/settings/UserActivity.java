package eventail.eventail.activity.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.models.User;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.users.EditUserTask;
import eventail.eventail.service.webService.task.users.UserTask;

/**
 * A login screen that offers login via email/password.
 */
public class UserActivity extends AppCompatActivity {

    // UI references.
    private EditText username = null;
    private EditText firstName = null;
    private EditText lastName = null;
    private EditText emailField = null;
    private EditText passwordField = null;
    private Button save = null;

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
        save            = (Button) findViewById(R.id.save);

        new UserTask(this, new WebTaskHandler<User>() {
            @Override
            public void handleSuccess(User user) {
                username.setText(user.getUsername());
                firstName.setText(user.getFirstname());
                lastName.setText(user.getLastname());
                emailField.setText(user.getEmail());
            }
        }).execute(Credential.getInstance(this).getUserId());

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditUserTask(UserActivity.this, new WebTaskHandler<Void>() {
                    @Override
                    public void handleSuccess(Void response) {
                        finish();
                    }
                }).execute(String.valueOf(Credential.getInstance(UserActivity.this).getUserId()), username.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
            }
        });
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


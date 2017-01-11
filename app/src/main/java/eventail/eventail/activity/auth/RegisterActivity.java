package eventail.eventail.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.auth.RegisterTask;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private RegisterTask mRegTask = null;

    // UI references.
    private LinearLayout login = null;
    private EditText firstnameField = null;
    private EditText lastnameField = null;
    private EditText emailField = null;
    private EditText usernameField = null;
    private EditText passwordField = null;
    private Button register = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        login = (LinearLayout) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Link to GUI elements
        firstnameField      = (EditText) findViewById(R.id.firstname);
        lastnameField      = (EditText) findViewById(R.id.lastname);
        usernameField      = (EditText) findViewById(R.id.username);
        emailField      = (EditText) findViewById(R.id.email);
        passwordField   = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.register);

        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        Credential credential = Credential.getInstance(this);
        if(credential != null && credential.getExpires().after(Calendar.getInstance())){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mRegTask != null) {
            mRegTask.cancel(true);
        }
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

    private void attemptRegister() {
        if (mRegTask != null && mRegTask.isCancelled()) {
            return;
        }

        // Reset errors.
        firstnameField.setError(null);
        lastnameField.setError(null);
        usernameField.setError(null);
        emailField.setError(null);
        passwordField.setError(null);

        // Store values at the time of the login attempt.
        String firstname = firstnameField.getText().toString();
        String lastname = lastnameField.getText().toString();
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            focusView = passwordField;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.error_field_required));
            focusView = emailField;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameField.setError(getString(R.string.error_field_required));
            focusView = usernameField;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastname)) {
            lastnameField.setError(getString(R.string.error_field_required));
            focusView = lastnameField;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            firstnameField.setError(getString(R.string.error_field_required));
            focusView = firstnameField;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mRegTask = new RegisterTask(this, new WebTaskHandler<Void>() {
                @Override
                public void handleSuccess(Void response) {
                    finish();
                }
            });
            mRegTask.execute(username, firstname, lastname, email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}


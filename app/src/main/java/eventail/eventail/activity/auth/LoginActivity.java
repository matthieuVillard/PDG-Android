package eventail.eventail.activity.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.webService.task.auth.TokenTask;
import eventail.eventail.service.webService.handler.WebTaskHandler;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private TokenTask mAuthTask = null;

    private static int INTERNET = 0;

    // UI references.
    private EditText emailField = null;
    private EditText passwordField = null;
    private Button login = null;
    private LinearLayout register = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET);
        }

        // Link to GUI elements
        emailField      = (EditText) findViewById(R.id.email);

        passwordField   = (EditText) findViewById(R.id.password);
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        login           = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
				attemptLogin();
            }
        });

        register = (LinearLayout) findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String email = PreferenceManager.getDefaultSharedPreferences(this)
                                        .getString("user_email", "");
        if(!email.isEmpty()){
            emailField.setText(email);
            passwordField.requestFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mAuthTask != null) {
            mAuthTask.cancel(true);
        }
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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == INTERNET) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null && mAuthTask.isCancelled()) {
            return;
        }

        // Reset errors.
        emailField.setError(null);
        passwordField.setError(null);

        // Store values at the time of the login attempt.
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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new TokenTask(this, new WebTaskHandler<Void>() {
                @Override
                public void handleSuccess(Void response) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            mAuthTask.execute(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}


package eventail.eventail.service.webService.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import eventail.eventail.activity.auth.LoginActivity;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.auth.ExtendTask;

/**
 * Created by matthieu.villard on 18.10.2016.
 */
public abstract class WebTask<T, R> extends AsyncTask<T, Void, HttpResponse> {

    public static final String TAG = WebTask.class.getSimpleName();
    protected Context context;
    private Exception exception = null;
    private WebTaskHandler handler;
    private ProgressDialog progress;

    public WebTask(Context context, WebTaskHandler handler) {
        this.handler = handler;
        this.context = context;
    }

    public WebTask(Context context, WebTaskHandler handler, String progressMessage) {
        this.handler = handler;
        this.context = context;
        progress = new ProgressDialog(context);
        progress.setMessage(progressMessage);
    }

    public WebTask(Context context) {
        this.context = context;
    }

    public WebTask(Context context, String progressMessage) {
        this.context = context;
        progress = new ProgressDialog(context);
        progress.setMessage(progressMessage);
    }

    protected abstract HttpResponse request(T... params) throws JSONException, IOException;
    protected abstract boolean isResponseValid(HttpResponse response);

    @Override
    protected void onPreExecute(){
        exception = null;
        if(progress != null){
            progress.show();
        }
        Credential credential = Credential.getInstance(context);
        if(credential != null){
            if(!credential.getExpires().after(Calendar.getInstance())){
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            else {
                Calendar comp = Calendar.getInstance();
                comp.setTime(credential.getExpires().getTime());
                comp.add(Calendar.HOUR, -12);
                if (!Calendar.getInstance().before(comp)) {
                    new ExtendTask(context).execute();
                }
            }
        }
    }

    @Override
    protected HttpResponse doInBackground(T... params) {
        try {
            return request(params);
        } catch (JSONException e) {
            e.printStackTrace();
            exception = e;
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(final HttpResponse response) {
        if(response != null){
            try {
                hideProgress();
                if (isResponseValid(response)) {
                    if(handler != null) {
                        handler.handleSuccess(handleSuccess(response.getBody()));
                    }
                }
                else {
                    handleError(response);
                }
            } catch (Exception e){
                handleException(e);
            }
        }
        else {
            handleException(exception);
        }
    }

    @Override
    protected void onCancelled() {
        handleCancel();
    }

    protected void hideProgress(){
        if(progress != null && progress.isShowing()){
            progress.dismiss();
        }
    }

    public void handleException(Exception exception){
        hideProgress();
        Log.e(TAG, "API Request error", exception);
        Toast.makeText(context, "API Request error : " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    public abstract R handleSuccess(HttpBody response) throws JSONException, ParseException;

    public void handleError(HttpResponse response) throws JSONException {
        System.out.println("error " + response.getCode() + " " + response.getMessage());
        if (response.getBody() != null && response.getBody().asJson().has("message")) {
            JSONObject json = response.getBody().asJson();
            Log.i(TAG, json.getString("message"));
            Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, response.getMessage());
            Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void handleCancel(){
        hideProgress();
    }
}
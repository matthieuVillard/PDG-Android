package eventail.eventail.service.webService.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import eventail.eventail.service.http.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;

/**
 * Created by matthieu.villard on 18.10.2016.
 */
public abstract class WebTask<T> extends AsyncTask<T, Void, HttpResponse> {

    protected Context context;
    private boolean isRunning = false;
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


    public boolean isRunning(){
        return isRunning;
    }

    protected abstract HttpResponse request(T... params) throws JSONException, IOException;
    protected abstract boolean isResponseValid(HttpResponse response);

    @Override
    protected void onPreExecute(){
        exception = null;
        if(progress != null){
            progress.show();
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
        isRunning = false;
        if(response != null){
            try {
                hideProgress();
                if (isResponseValid(response)) {
                    handleSuccess(response.asJson());
                    if(handler != null) {
                        handler.handleSuccess(response.asJson());
                    }
                }
                else {
                    handleError(response.asJson());
                }
            } catch (JSONException e){
                e.printStackTrace();
                handleException(e);
            }
        }
        else {
            handleException(exception);
        }
    }

    @Override
    protected void onCancelled() {
        isRunning = false;
        handleCancel();
    }

    protected void hideProgress(){
        if(progress != null && progress.isShowing()){
            progress.dismiss();
        }
    }

    public void handleException(Exception exception){
        hideProgress();
        Toast.makeText(context, "Unexpected error : " + exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    public abstract void handleSuccess(JSONObject response);

    public void handleError(JSONObject response){
        if(response.has("message")) {
            Toast.makeText(context, response.optString("message"), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Unexpected API Result", Toast.LENGTH_LONG).show();
        }
    }

    public void handleCancel(){
        hideProgress();
    }
}
package eventail.eventail.service.webService.task.users;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class LocationTask extends WebTask<Double, Void> {

    public LocationTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public LocationTask(Context context, WebTaskHandler handler, String progressMessage) {
        super(context, handler, progressMessage);
    }

    public LocationTask(Context context) {
        super(context);
    }

    public LocationTask(Context context, String progressMessage) {
        super(context, progressMessage);
    }

    @Override
    protected HttpResponse request(Double... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/self/location";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.put(url, headers)
                          .field("lat", params[0])
                          .field("lon", params[1])
                          .response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 204) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) {
        return null;
    }

    @Override
    public void handleError(HttpResponse response) throws JSONException {
        if(response.getCode() != 409) {
            super.handleError(response);
        }
        else{
            Credential.getInstance(context).setActive(false);
            Credential.register(Credential.getInstance(context), context);
        }
    }
}

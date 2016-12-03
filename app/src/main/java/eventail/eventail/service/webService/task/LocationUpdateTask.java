package eventail.eventail.service.webService.task;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.Eventail;
import eventail.eventail.service.http.HttpResponse;
import eventail.eventail.service.http.HttpsClient;
import eventail.eventail.service.webService.conf.WebConfig;
import eventail.eventail.service.webService.handler.WebTaskHandler;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class LocationUpdateTask extends WebTask<Double> {

    public LocationUpdateTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public LocationUpdateTask(Context context, WebTaskHandler handler, String progressMessage) {
        super(context, handler, progressMessage);
    }

    public LocationUpdateTask(Context context) {
        super(context);
    }

    public LocationUpdateTask(Context context, String progressMessage) {
        super(context, progressMessage);
    }

    @Override
    protected HttpResponse request(Double... params) throws JSONException, IOException {
        String url = WebConfig.serverUrl + "/users/self/location";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Eventail.getToken());
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
    public void handleSuccess(JSONObject response) {

    }
}

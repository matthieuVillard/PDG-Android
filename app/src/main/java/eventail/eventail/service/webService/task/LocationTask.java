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

public class LocationTask extends WebTask {

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
    protected HttpResponse request(Object... params) throws JSONException, IOException {
        String url = WebConfig.serverUrl + "/graphql";
        String request = "{users{username location}}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Eventail.getToken());
        headers.put("Content-Type", "text/plain");
        return HttpsClient.post(url, headers, request)
                          .response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    public void handleSuccess(JSONObject response) {

    }
}

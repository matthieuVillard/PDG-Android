package eventail.eventail.service.webService.task;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eventail.eventail.Eventail;
import eventail.eventail.service.http.HttpResponse;
import eventail.eventail.service.http.HttpsClient;
import eventail.eventail.service.webService.conf.WebConfig;
import eventail.eventail.service.webService.handler.WebTaskHandler;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class StatusTask extends WebTask {

    public StatusTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Checking system status ...");
    }

    public StatusTask(Context context) {
        super(context, "Checking system status ...");
    }

    @Override
    protected HttpResponse request(Object... params) throws JSONException, IOException {
        String url = WebConfig.serverUrl + "/status";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Eventail.getInstance().getToken());
        return HttpsClient.get(url, headers).response();
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

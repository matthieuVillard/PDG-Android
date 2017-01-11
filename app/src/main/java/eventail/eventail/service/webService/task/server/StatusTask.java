package eventail.eventail.service.webService.task.server;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

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

public class StatusTask extends WebTask<Object, JSONObject> {

    public StatusTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Checking system status ...");
    }

    public StatusTask(Context context) {
        super(context, "Checking system status ...");
    }

    @Override
    protected HttpResponse request(Object... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/status";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
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
    public JSONObject handleSuccess(HttpBody response) {
        try {
            return response.asJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

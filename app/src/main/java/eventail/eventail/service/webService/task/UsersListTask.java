package eventail.eventail.service.webService.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

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

public class UsersListTask extends WebTask<String> {

    public UsersListTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public UsersListTask(Context context, WebTaskHandler handler, String progressMessage) {
        super(context, handler, progressMessage);
    }

    public UsersListTask(Context context) {
        super(context);
    }

    public UsersListTask(Context context, String progressMessage) {
        super(context, progressMessage);
    }

    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = WebConfig.serverUrl + "/users";
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
        Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
    }
}

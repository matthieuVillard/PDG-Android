package eventail.eventail.service.webService.task.auth;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class LaunchTask extends WebTask<String, Void> {

    public LaunchTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Launching ...");
    }

    public LaunchTask(Context context) {
        super(context, "Launching ...");
    }


    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/auth/launch";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.post(url, headers).response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) throws JSONException, ParseException {
        Credential.getInstance(context).setActive(response.asJson().getBoolean("active"));
        Credential.register(Credential.getInstance(context), context);
        return null;
    }
}

package eventail.eventail.service.webService.task.users;

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

public class InviteUserTask extends WebTask<Integer, Void> {

    public InviteUserTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Sending request...");
    }

    public InviteUserTask(Context context) {
        super(context, "Sending request...");
    }


    @Override
    protected HttpResponse request(Integer ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/self/friends/requests/" + params[0];
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.put(url, headers).response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 204) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) throws JSONException, ParseException {
        return null;
    }
}

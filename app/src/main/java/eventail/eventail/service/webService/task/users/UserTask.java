package eventail.eventail.service.webService.task.users;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.models.User;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class UserTask extends WebTask<Integer, User> {

    public UserTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Retrieving data ...");
    }

    public UserTask(Context context) {
        super(context, "Retrieving data ...");
    }

    @Override
    protected HttpResponse request(Integer... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/" + params[0];
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
    public User handleSuccess(HttpBody response) throws JSONException {
        return User.fromJson(response.asJson());
    }
}

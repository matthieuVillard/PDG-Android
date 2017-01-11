package eventail.eventail.service.webService.task.users;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class FriendsTask extends SearchUserTask {

    public FriendsTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public FriendsTask(Context context) {
        super(context);
    }


    @Override
    protected HttpResponse request(String ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/self/friends";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.get(url, headers).response();
    }
}

package eventail.eventail.service.webService.task.users;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.models.User;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class NearbyUsersTask extends WebTask<Double, List<User>> {

    public NearbyUsersTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public NearbyUsersTask(Context context) {
        super(context);
    }


    @Override
    protected HttpResponse request(Double ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/nearby?lat=" + params[0] + "&lon=" + params[1] + "&radius=" + params[2] ;
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
    public List<User> handleSuccess(HttpBody response) throws JSONException, ParseException {
        List<User> users = new ArrayList<>();
        JSONArray array = response.asJsonList();
        for(int i = 0; i < array.length(); i++){
            users.add(User.fromJson(array.getJSONObject(i)));
        }
        return users;
    }
}

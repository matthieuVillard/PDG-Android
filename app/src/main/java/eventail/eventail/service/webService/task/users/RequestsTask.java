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
import eventail.eventail.models.Request;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class RequestsTask extends WebTask<Void, List<Request>> {

    public RequestsTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public RequestsTask(Context context) {
        super(context);
    }


    @Override
    protected HttpResponse request(Void ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/self/friends/requests";
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
    public List<Request> handleSuccess(HttpBody response) throws JSONException, ParseException {
        List<Request> requests = new ArrayList<>();
        JSONArray array = response.asJsonList();
        for(int i = 0; i < array.length(); i++){
            requests.add(Request.fromJson(array.getJSONObject(i)));
        }
        return requests;
    }
}

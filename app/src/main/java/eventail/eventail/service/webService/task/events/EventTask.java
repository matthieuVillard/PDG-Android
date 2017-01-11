package eventail.eventail.service.webService.task.events;

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
import eventail.eventail.models.Event;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class EventTask extends WebTask<Integer, Event> {

    public EventTask(Context context, WebTaskHandler handler) {
        super(context, handler);
    }

    public EventTask(Context context) {
        super(context);
    }


    @Override
    protected HttpResponse request(Integer ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/events/" + params[0] ;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.get(url, headers).response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 200 || response.getCode() == 404) {
            return true;
        }
        return false;
    }

    @Override
    public Event handleSuccess(HttpBody response) throws JSONException, ParseException {
        return Event.fromJson(response.asJson());
    }
}

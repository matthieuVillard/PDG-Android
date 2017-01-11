package eventail.eventail.service.webService.task.events;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
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

public class DeleteEventTask extends WebTask<Event, Void> {

    public DeleteEventTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Deleting event...");
    }

    public DeleteEventTask(Context context) {
        super(context, "Deleting event...");
    }


    @Override
    protected HttpResponse request(Event ... params) throws JSONException, IOException {
        Event event = params[0];
        String url = context.getString(R.string.server_url) + "/events/" + event.getId();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        return HttpsClient.delete(url, headers).response();
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

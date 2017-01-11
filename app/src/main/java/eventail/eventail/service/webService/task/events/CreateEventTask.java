package eventail.eventail.service.webService.task.events;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class CreateEventTask extends WebTask<Event, Event> {

    public CreateEventTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Creating event...");
    }

    public CreateEventTask(Context context) {
        super(context, "Creating event...");
    }


    @Override
    protected HttpResponse request(Event ... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/events";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        Event event = params[0];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return HttpsClient.post(url, headers)
                .field("title", event.getTitle())
                .field("desc", event.getDescription())
                .field("begin", df.format(event.getStartDate().getTime()))
                .field("end", df.format(event.getEndDate().getTime()))
                .field("spontaneous", event.isSpontaneous())
                .field("location", new double[]{event.getLatitude(), event.getLongitude()})
                .field("radius", event.getRadius()).response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 201) {
            return true;
        }
        return false;
    }

    @Override
    public Event handleSuccess(HttpBody response) throws JSONException, ParseException {
        return Event.fromJson(response.asJson());
    }
}

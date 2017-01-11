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

public class EditEventTask extends WebTask<Event, Event> {

    public EditEventTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Editing event...");
    }

    public EditEventTask(Context context) {
        super(context, "Editing event...");
    }


    @Override
    protected HttpResponse request(Event ... params) throws JSONException, IOException {
        Event event = params[0];
        String url = context.getString(R.string.server_url) + "/events/" + event.getId();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        System.out.println("edit");
        return HttpsClient.patch(url, headers)
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
        if(response.getCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    public Event handleSuccess(HttpBody response) throws JSONException, ParseException {
        return Event.fromJson(response.asJson());
    }
}

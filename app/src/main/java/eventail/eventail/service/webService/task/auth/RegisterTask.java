package eventail.eventail.service.webService.task.auth;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

import eventail.eventail.R;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class RegisterTask extends WebTask<String, Void> {

    public RegisterTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Registering ...");
    }

    public RegisterTask(Context context) {
        super(context, "Registering ...");
    }


    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/auth/register";
        return HttpsClient.post(url)
                .field("username", params[0])
                .field("firstname", params[1])
                .field("lastname", params[2])
                .field("mail", params[3])
                .field("password", params[4])
                .response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 201) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) throws JSONException, ParseException {
        return null;
    }
}

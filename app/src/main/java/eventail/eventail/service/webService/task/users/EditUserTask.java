package eventail.eventail.service.webService.task.users;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.request.PatchRequest;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class EditUserTask extends WebTask<String, Void> {

    public EditUserTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Saving data ...");
    }

    public EditUserTask(Context context) {
        super(context, "Saving data ...");
    }

    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/users/" + params[0];
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Token " + Credential.getInstance(context).getToken());

        PatchRequest client = HttpsClient.patch(url, headers);
        if(params[1] != null && !params[1].isEmpty()){
            client.field("username", params[1]);
        }
        if(params[2] != null && !params[2].isEmpty()){
            client.field("firstname", params[2]);
        }
        if(params[3] != null && !params[3].isEmpty()){
            client.field("lastname", params[3]);
        }
        if(params[4] != null && !params[4].isEmpty()){
            client.field("mail", params[4]);
        }
        if(params[5] != null && !params[5].isEmpty()){
            client.field("password", params[5]);
        }
        return client.response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) throws JSONException {
        return null;
    }
}

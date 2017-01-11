package eventail.eventail.service.webService.task.auth;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import eventail.eventail.R;
import eventail.eventail.models.Credential;
import eventail.eventail.service.http.request.HttpsClient;
import eventail.eventail.service.http.response.HttpBody;
import eventail.eventail.service.http.response.HttpResponse;
import eventail.eventail.service.webService.handler.WebTaskHandler;
import eventail.eventail.service.webService.task.WebTask;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class TokenTask extends WebTask<String, Void> {

    public TokenTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Logging in ...");
    }

    public TokenTask(Context context) {
        super(context, "Logging in ...");
    }


    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = context.getString(R.string.server_url) + "/auth/token";
        return HttpsClient.post(url).field("mail", params[0])
                .field("pass", params[1])
                .response();
    }

    @Override
    protected boolean isResponseValid(HttpResponse response) {
        if(response.getCode() == 200) {
            return true;
        }
        return false;
    }

    @Override
    public Void handleSuccess(HttpBody response) throws JSONException, ParseException {
        JSONObject res = response.asJson();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar expires = Calendar.getInstance();
        expires.setTime(df.parse(res.getString("expires")));
        Credential.register(new Credential(res.getString("token"), expires, res.getJSONObject("user").getInt("id")), context);
        return null;
    }
}

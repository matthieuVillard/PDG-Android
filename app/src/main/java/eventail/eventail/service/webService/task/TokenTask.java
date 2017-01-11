package eventail.eventail.service.webService.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import eventail.eventail.Eventail;
import eventail.eventail.service.http.HttpsClient;
import eventail.eventail.service.http.HttpResponse;
import eventail.eventail.service.webService.conf.WebConfig;
import eventail.eventail.service.webService.handler.WebTaskHandler;


/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class TokenTask extends WebTask<String> {

    public TokenTask(Context context, WebTaskHandler handler) {
        super(context, handler, "Logging in ...");
    }

    public TokenTask(Context context) {
        super(context, "Logging in ...");
    }


    @Override
    protected HttpResponse request(String... params) throws JSONException, IOException {
        String url = WebConfig.serverUrl + "/" + WebConfig.tokenPath;
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
    public void handleSuccess(JSONObject response) {
        Eventail.getInstance().login(response.optString("token"));
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", response.optString("token"));
        editor.commit();
    }
}

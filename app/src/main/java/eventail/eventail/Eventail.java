package eventail.eventail;

/**
 * Created by matthieu.villard on 19.10.2016.
 */
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import eventail.eventail.models.Event;

public class Eventail extends Application {

    public static final String TAG = Eventail.class.getSimpleName();

    private static Eventail instance;

    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized Eventail getInstance(){
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public void login(String token){
        if(token != null && !token.isEmpty()) {
            SharedPreferences sharedPref = getSharedPreferences("credentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", token);
            editor.apply();
        }
    }

    public void logout(){
        SharedPreferences sharedPref = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("token");
        editor.commit();
    }

    public String getToken(){
        return getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("token", "");
    }
}
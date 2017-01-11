package eventail.eventail.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by matthieu.villard on 21.12.2016.
 */

public class Credential {
    private String token;
    private Calendar expires;
    private int userId;
    private boolean active;

    public Credential(String token, Calendar expires, int userId){
        this.token = token;
        this.expires = expires;
        this.userId = userId;
        this.active = true;
    }

    public String getToken(){
        return token;
    }

    public Calendar getExpires(){
        return expires;
    }

    public int getUserId(){
        return userId;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean isActive(){
        return active;
    }

    public static Credential getInstance(Context context){
        String credential = context.getSharedPreferences("credentials", Context.MODE_PRIVATE).getString("credential", "");
        if(credential.isEmpty()){
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(credential, Credential.class);
    }

    public static void register(Credential credential, Context context){
        if(credential != null) {
            SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            Gson gson = new Gson();
            editor.putString("credential", gson.toJson(credential));
            editor.commit();
        }
    }

    public static void unregister(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("credential");
        editor.commit();
    }
}

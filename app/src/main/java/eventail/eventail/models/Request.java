package eventail.eventail.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by matthieu.villard on 10.01.2017.
 */

public class Request {

    private User user;

    private Calendar date;

    private Request(User user, Calendar date){
        this.user = user;
        this.date = date;
    }

    public User getUser(){
        return user;
    }

    public Calendar getDate(){
        return date;
    }

    public static Request fromJson(JSONObject json) throws JSONException, ParseException {
        Calendar date = null;
        if(json.has("date") && !json.isNull("date")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            date = Calendar.getInstance();
            date.setTime(df.parse(json.getString("date")));
        }
        return new Request(User.fromJson(json.getJSONObject("user")), date);
    }
}

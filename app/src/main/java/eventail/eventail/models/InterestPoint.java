package eventail.eventail.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matthieu.villard on 10.01.2017.
 */

public class InterestPoint {

    private int id;

    private int event;

    private String title;

    private String description;

    private double latitude;

    private double longitude;

    public InterestPoint(int event, String title, String description, double latitude, double longitude){
        this.event = event;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private InterestPoint(int id, int event, String title, String description, double latitude, double longitude){
        this(event, title, description, latitude, longitude);
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public int getEvent(){
        return event;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public static InterestPoint fromJson(JSONObject json) throws JSONException {
        return new InterestPoint(
            json.getInt("id"),
            json.getInt("event"),
            json.getString("title"),
            json.getString("desc"),
            json.getJSONArray("location").getDouble(0),
            json.getJSONArray("location").getDouble(1)
        );
    }
}

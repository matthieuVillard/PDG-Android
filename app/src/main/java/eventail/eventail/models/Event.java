package eventail.eventail.models;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by matthieu.villard on 30.11.2016.
 */

public class Event implements Serializable {
    private int id;
    private String title;
    private String description;
    private Calendar startDate;
    private Calendar endDate;
    private boolean spontaneous;
    private double latitude;
    private double longitude;
    private int radius;
    private Integer distance;
    private int owner;

    public Event(String title, String description, Calendar startDate, Calendar endDate, boolean spontaneous, double latitude, double longitude, int radius){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.spontaneous = spontaneous;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.description = description;
    }

    private Event(int id, String title, String description, Calendar startDate, Calendar endDate, boolean spontaneous, double latitude, double longitude, int radius, int owner){
        this(title, description, startDate, endDate, spontaneous, latitude, longitude, radius);
        this.id = id;
        this.owner = owner;
    }

    private Event(int id, String title, String description, Calendar startDate, Calendar endDate, boolean spontaneous, double latitude, double longitude, int radius, Integer distance, int owner){
        this(id, title, description, startDate, endDate, spontaneous, latitude, longitude, radius, owner);
        this.distance = distance;
    }

    public int getId(){
        return id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setStartDate(Calendar startDate){
        this.startDate = startDate;
    }

    public Calendar getStartDate(){
        return startDate;
    }

    public void setEndDate(Calendar endDate){
        this.endDate = endDate;
    }

    public Calendar getEndDate(){
        return endDate;
    }

    public void setSpontaneous(boolean spontaneous){
        this.spontaneous = spontaneous;
    }

    public boolean isSpontaneous(){
        return spontaneous;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setDistance(Integer distance){
        this.distance = distance;
    }

    public Integer getDistance(){
        return distance;
    }

    public int getOwner(){
        return owner;
    }

    public static Event fromJson(JSONObject json) throws ParseException, JSONException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar start = Calendar.getInstance();
        start.setTime(df.parse(json.getString("begin")));
        Calendar end = Calendar.getInstance();
        end.setTime(df.parse(json.getString("end")));
        return new Event(
                json.getInt("id"),
                json.getString("title"),
                json.getString("desc"),
                start,
                end,
                json.getBoolean("spontaneous"),
                json.getJSONArray("location").getDouble(0),
                json.getJSONArray("location").getDouble(1),
                json.getInt("radius"),
                json.getInt("owner")
        );
    }

}

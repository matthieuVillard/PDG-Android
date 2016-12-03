package eventail.eventail.models;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by matthieu.villard on 30.11.2016.
 */

public class Event implements Serializable {
    private String name;
    private String place;
    double latitude;
    double longitude;
    private int radius;

    public Event(String name, Place location, int radius){
        this.name = name;
        this.place = location.getName().toString();
        this.latitude = location.getLatLng().latitude;
        this.longitude = location.getLatLng().longitude;
        this.radius = radius;
    }

    public String getName(){
        return name;
    }

    public String getPlace(){
        return place;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public int getRadius(){
        return  radius;
    }

}

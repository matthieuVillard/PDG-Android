package eventail.eventail.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matthieu.villard on 10.01.2017.
 */

public class User {

    private int id;

    private String username;

    private String firstname;

    private String lastname;

    private String email;

    private Double latitude;

    private Double longitude;

    private User(int id, String username, String firstname, String lastname, String email, Double latitude, Double longitude){
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getFirstname(){
        return firstname;
    }

    public String getLastname(){
        return lastname;
    }

    public String getEmail(){
        return email;
    }

    public Double getLatitude(){
        return latitude;
    }

    public Double getLongitude(){
        return longitude;
    }

    public static User fromJson(JSONObject json) throws JSONException {
        return new User(
            json.optInt("id"),
            json.optString("username"),
            json.optString("firstname"),
            json.optString("lastname"),
            json.optString("mail"),
            (json.has("location") && !json.isNull("location")) ? json.getJSONArray("location").getDouble(0) : null,
            (json.has("location") && !json.isNull("location")) ? json.getJSONArray("location").getDouble(1) : null
        );
    }
}

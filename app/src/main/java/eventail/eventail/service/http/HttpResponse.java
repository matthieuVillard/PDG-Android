package eventail.eventail.service.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class HttpResponse implements Serializable {
    private int code;
    private String message;
    private String body = "{}";

    public HttpResponse(int code, String message, String body){
        this.code = code;
        this.message = message;
        if(body != null) {
            this.body = body;
        }
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public String getBody(){
        return body;
    }

    public JSONObject asJson() throws JSONException {
        return new JSONObject(body);
    }
}

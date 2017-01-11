package eventail.eventail.service.http.response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by matthieu.villard on 18.10.2016.
 */

public class HttpResponse implements Serializable {
    private int code;
    private String message;
    private HttpBody body;

    public HttpResponse(int code, String message, String content){
        this.code = code;
        this.message = message;
        if(content != null) {
            this.body = new HttpBody(content);
        }
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public HttpBody getBody(){
        return body;
    }
}

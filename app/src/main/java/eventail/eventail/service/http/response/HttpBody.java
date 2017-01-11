package eventail.eventail.service.http.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matthieu.villard on 21.12.2016.
 */

public class HttpBody {
    private String content;

    public HttpBody(String content){
        this.content = content;
    }

    public String string(){
        return content;
    }

    public JSONObject asJson() throws JSONException {
        return new JSONObject(content);
    }

    public JSONArray asJsonList() throws JSONException {
        return new JSONArray(content);
    }
}

package eventail.eventail.service.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created by matthieu.villard on 02.11.2016.
 */

public class PutRequest extends Request {

    protected PutRequest(String url) throws IOException {
        super(url);
        connection.setRequestMethod("PUT");
    }

    protected PutRequest(String url, final Map<String, String> headers) throws IOException {
        super(url, headers);
        connection.setRequestMethod("PUT");
    }
}

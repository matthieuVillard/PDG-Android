package eventail.eventail.service.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by matthieu.villard on 02.11.2016.
 */

public class PostRequest extends Request {


    protected PostRequest(String url) throws IOException {
        super(url);
        connection.setRequestMethod("POST");
    }

    protected PostRequest(String url, String body) throws IOException {
        super(url, body);
        connection.setRequestMethod("POST");
    }

    protected PostRequest(String url, final Map<String, String> headers) throws IOException {
        super(url, headers);
        connection.setRequestMethod("POST");
    }

    protected PostRequest(String url, final Map<String, String> headers, String body) throws IOException {
        super(url, headers, body);
        connection.setRequestMethod("POST");
    }
}

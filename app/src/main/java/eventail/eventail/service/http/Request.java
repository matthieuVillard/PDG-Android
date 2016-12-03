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

public class Request extends HttpsClient {

    private JSONObject params;
    private String body = "";

    protected Request(String url) throws IOException {
        super(url);
        connection.setDoOutput(true);
        params = new JSONObject();
        body = params.toString();
    }

    protected Request(String url, String body) throws IOException {
        super(url);
        this.body = body;
        connection.setDoOutput(true);
        params = new JSONObject();
    }

    protected Request(String url, final Map<String, String> headers) throws IOException {
        super(url, headers);
        connection.setDoOutput(true);
        params = new JSONObject();
        body = params.toString();
    }

    protected Request(String url, final Map<String, String> headers, String body) throws IOException {
        super(url, headers);
        this.body = body;
        //connection.setDoOutput(true);
        params = new JSONObject();
    }

    public Request field(String name, String value) throws JSONException {
        params.put(name, value);
        body = params.toString();
        return this;
    }

    public Request field(String name, Double value) throws JSONException {
        params.put(name, value);
        body = params.toString();
        return this;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse response() throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        out.write(body);
        out.close();

        return super.response();
    }
}

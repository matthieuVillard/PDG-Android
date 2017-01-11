package eventail.eventail.service.http.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import eventail.eventail.service.http.response.HttpResponse;

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

    public Request field(String name, int value) throws JSONException {
        params.put(name, value);
        body = params.toString();
        return this;
    }

    public Request field(String name, boolean value) throws JSONException {
        params.put(name, value);
        body = params.toString();
        return this;
    }

    public Request field(String name, double[] values) throws JSONException {
        JSONArray array = new JSONArray();
        for(double value : values){
            array.put(value);
        }
        params.put(name, array);
        body = params.toString();
        return this;
    }

    public Request field(String name, JSONObject jsonObject) throws JSONException {
        params.put(name, jsonObject);
        body = params.toString();
        return this;
    }

    public Request body(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse response() throws IOException {
        System.out.println(body);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        out.write(body);
        out.close();

        return super.response();
    }
}

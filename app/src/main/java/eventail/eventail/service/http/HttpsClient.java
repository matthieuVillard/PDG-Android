package eventail.eventail.service.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by matthieu.villard on 17.10.2016.
 */

public class HttpsClient {

    protected HttpsURLConnection connection;

    protected HttpsClient(String url) throws IOException {
        URL address = new URL(url);
        connection = (HttpsURLConnection) address.openConnection();
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setUseCaches(false);
    }

    protected HttpsClient(String url, final Map<String, String> headers) throws IOException {
        this(url);
        for(Iterator<String> it = headers.keySet().iterator(); it.hasNext();){
            String key = it.next();
            connection.setRequestProperty(key, headers.get(key));
        }
    }

    public HttpResponse response() throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            inputStream = connection.getErrorStream();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        return new HttpResponse(connection.getResponseCode(), connection.getResponseMessage(), in.readLine());
    }

    public static PostRequest post(String url) throws IOException {
        return new PostRequest(url);
    }

    public static PostRequest post(String url, String body) throws IOException {
        return new PostRequest(url, body);
    }

    public static PostRequest post(String url, Map<String, String> headers) throws IOException {
        return new PostRequest(url, headers);
    }

    public static PostRequest post(String url, Map<String, String> headers, String body) throws IOException {
        return new PostRequest(url, headers, body);
    }

    public static PutRequest put(String url) throws IOException {
        return new PutRequest(url);
    }

    public static PutRequest put(String url, Map<String, String> headers) throws IOException {
        return new PutRequest(url, headers);
    }

    public static HttpsClient get(String url) throws IOException {
        return new HttpsClient(url);
    }

    public static HttpsClient get(String url, Map<String, String> headers) throws IOException {
        return new HttpsClient(url, headers);
    }
}

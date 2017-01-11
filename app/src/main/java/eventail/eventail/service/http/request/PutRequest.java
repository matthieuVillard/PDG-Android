package eventail.eventail.service.http.request;

import java.io.IOException;
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

package eventail.eventail.service.http.request;

import java.io.IOException;
import java.util.Map;

/**
 * Created by matthieu.villard on 02.11.2016.
 */

public class PatchRequest extends Request {

    protected PatchRequest(String url) throws IOException {
        super(url);
        connection.setRequestMethod("PATCH");
    }

    protected PatchRequest(String url, final Map<String, String> headers) throws IOException {
        super(url, headers);
        connection.setRequestMethod("PATCH");
    }
}

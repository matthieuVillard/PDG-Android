package eventail.eventail.service.webService.handler;

import org.json.JSONObject;

import eventail.eventail.service.http.response.HttpBody;

/**
 * Created by matthieu.villard on 19.10.2016.
 */

public interface WebTaskHandler<T> {
    public void handleSuccess(T response);
}

package io.yaas;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/9/15.
 */
public class Common {
    public final static long COMMUNICATION_TIMEOUT = 10000;

    public static void checkContentTypeIsApplicationJson(HttpServerRequest req) {
        checkNotNull(req);

        String contentType = req.headers().get("content-type");
        if (contentType == null || !contentType.equalsIgnoreCase("application/json")) {
            throw new IllegalArgumentException();
        }
    }

    public static String checkBody(String body) {
        checkNotNull(body);
        checkArgument(body.trim().length() > 0);
        return body;
    }

    public static void checkResponse(Vertx vertx, Container container, AsyncResult<Message<JsonObject>> asyncResult, Handler<Void> onSuccess, Handler<Void> onError) {
        if (asyncResult.succeeded() && !"error".equalsIgnoreCase(asyncResult.result().body().getString("status"))) {
            onSuccess.handle(null);
        } else {
            container.logger().error(asyncResult.cause());
            onError.handle(null);
        }
    }
}

package io.yaas;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/9/15.
 */
public class Common {
    public final static long COMMUNICATION_TIMEOUT = 10000;

    static void execute(Container container, Message message, Handler<Future> handler) {
        try {
            Future<?> future = new DefaultFutureResult<>().setHandler((f) -> {
                if (f.succeeded()) {
                    JsonObject result = JsonObject.class.cast(f.result());
                    if (result == null) {
                        result = new JsonObject();
                    }
                    message.reply(result.putString("status", "ok"));
                } else {
                    container.logger().error(f.cause());
                    JsonObject err = new JsonObject();
                    err.putString("status", "error");
                    err.putString("message", f.cause() != null ? f.cause().getClass().getSimpleName() + ": " + f.cause().getMessage() : "unknown cause");
                    message.reply(err);
                }
            });

            handler.handle(future);
        } catch (
                Exception e
                )

        {
            container.logger().error(e);
            JsonObject err = new JsonObject();
            err.putString("status", "error");
            err.putString("message", e.getClass().getSimpleName() + ": " + e.getMessage());
            message.reply(err);
        }

    }

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

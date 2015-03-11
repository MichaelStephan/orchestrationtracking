package io.yaas.workflow;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * Created by i303874 on 3/9/15.
 */
public class MainVerticle extends Verticle {
    private final static int DEFAULT_API_HTTP_PORT = 8080;

    private void propagateFutureResult(AsyncResult prevFuture, Future nextFuture) {
        if (prevFuture.succeeded()) {
            nextFuture.setResult(null);
        } else {
            container.logger().error(prevFuture.cause());
            nextFuture.setFailure(prevFuture.cause());
        }
    }

    private int getHttpAPIPort() {
        try {
            return getContainer().config().getInteger("PORT");
        } catch (Exception e) {
            String tmpPort = System.getenv().get("PORT");
            try {
                return Integer.parseInt(tmpPort);
            } catch (Exception e1) {
                //ignore;
            }

            return DEFAULT_API_HTTP_PORT;
        }
    }

    public void start() {
        Future<Void> serviceAPILoaded = new DefaultFutureResult<>();
        Future<Void> serviceLoaded = new DefaultFutureResult<>();
        Future<Void> cassandraLoaded = new DefaultFutureResult<>();

        serviceLoaded.setHandler((result) -> {
            if (result.succeeded()) {
                JsonObject config = new JsonObject();
                config.putNumber("PORT", getHttpAPIPort());

                container.deployVerticle("io.yaas.OrchestrationTrackingServiceAPIVerticle", config, 1, (result1) -> {
                    propagateFutureResult(result1, serviceAPILoaded);
                });
            }
        });

        cassandraLoaded.setHandler((result) -> {
            if (result.succeeded()) {
                container.deployVerticle("io.yaas.OrchestrationTrackingServiceVerticle", 1, (result1) -> {
                    propagateFutureResult(result1, serviceLoaded);
                });
            }
        });

        {
            JsonObject config = new JsonObject();
            config.putString("address", "persistor");
            config.putArray("hosts", new JsonArray().addString("localhost"));
            config.putNumber("port", 9042);
            config.putString("keyspace", "orchestrationtracking");

            container.deployModule("com.insanitydesign~vertx-mod-cassandra-persistor~0.4.1", config, 1, (result) -> {
                propagateFutureResult(result, cassandraLoaded);
            });
        }
    }
}

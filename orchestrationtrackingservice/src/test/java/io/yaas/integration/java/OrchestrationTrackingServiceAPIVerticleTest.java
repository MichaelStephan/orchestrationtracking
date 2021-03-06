package io.yaas.integration.java;

import io.yaas.workflow.OrchestrationTrackingServiceAPIVerticle;
import io.yaas.workflow.OrchestrationTrackingServiceVerticle;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.CaseInsensitiveMultiMap;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class OrchestrationTrackingServiceAPIVerticleTest extends TestVerticle {

    private final static int PORT = 1025;

    private final static String HOST = "127.0.0.1";

    private final static MultiMap STANDARD_HEADERS = new CaseInsensitiveMultiMap().add("Content-Type", "application/json");

    private HttpClient getClient() {
        HttpClient client = vertx.createHttpClient();
        client.setHost(HOST);
        client.setPort(PORT);
        return client;
    }

    @Test
    public void testNotFoundRoute() {
        HttpClient client = getClient();
        client.getNow("/doesnotexist", STANDARD_HEADERS, (response) -> {
            assertEquals(404, response.statusCode());
            client.close();
            testComplete();
        });
    }

    @Test
    public void testGetWorkflowsRoute() {
        Handler<? extends Message> handler = (message) -> {
            message.reply(new JsonObject().putObject("result", new JsonObject()));
        };

        vertx.eventBus().registerHandler(OrchestrationTrackingServiceVerticle.LIST_WORKFLOWS_ADDRESS, handler, (result) -> {
            assertTrue(result.succeeded());

            HttpClient client = getClient();
            client.getNow("/workflows", STANDARD_HEADERS, (response) -> {
                assertEquals(200, response.statusCode());

                vertx.eventBus().unregisterHandler(OrchestrationTrackingServiceVerticle.LIST_WORKFLOWS_ADDRESS, handler, result2 -> {
                    client.close();
                    testComplete();
                });
            });
        });
    }

    @Test
    public void testPostWorkflowsRoute() {
        String wid = "123";
        String name = "name";
        int version = 1;
        String state = "started";

        Handler<? extends Message> handler = (Message<JsonObject> message) -> {
            assertEquals(name, message.body().getString("name"));
            assertEquals(version, message.body().getNumber("version"));

            message.reply(new JsonObject()
                    .putString("wid", wid)
                    .putString("name", name)
                    .putNumber("version", version)
                    .putString("astate", state));
        };

        vertx.eventBus().registerHandler(OrchestrationTrackingServiceVerticle.CREATE_AND_START_WORKFLOW_ADDRESS, handler, (result) -> {
            assertTrue(result.succeeded());

            HttpClient client = getClient();
            client.post("/workflows", (response) -> {
                assertEquals(201, response.statusCode());

                response.bodyHandler(body -> {
                    JsonObject json = new JsonObject(body.getString(0, body.length()));
                    assertEquals(wid, json.getString("wid"));
                    assertEquals(name, json.getString("name"));
                    assertEquals(version, json.getNumber("version"));
                    assertEquals(state, json.getString("astate"));

                    vertx.eventBus().unregisterHandler(OrchestrationTrackingServiceVerticle.CREATE_AND_START_WORKFLOW_ADDRESS, handler, result2 -> {
                        client.close();
                        testComplete();
                    });
                });
            })
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject()
                            .putString("name", name)
                            .putNumber("version", version)
                            .encode());
        });
    }

    @Test
    public void testPutWorkflowRoute() {
        String wid = "123";

        Handler<? extends Message> handler = (Message<JsonObject> message) -> {
            assertEquals(wid, message.body().getString("wid"));

            message.reply(new JsonObject().putString("wid", wid));
        };

        vertx.eventBus().registerHandler(OrchestrationTrackingServiceVerticle.UPDATE_WORKFLOW_ADDRESS, handler, (result) -> {
            assertTrue(result.succeeded());

            HttpClient client = getClient();
            client.put("/workflows/" + wid, (response) -> {
                assertEquals(200, response.statusCode());

                vertx.eventBus().unregisterHandler(OrchestrationTrackingServiceVerticle.UPDATE_WORKFLOW_ADDRESS, handler, result2 -> {
                    client.close();
                    testComplete();
                });
            })
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject().encode());
        });
    }

    @Test
    public void testPostWorkflowActionsRoute() {
        String wid = "123";
        String aid = "456";
        String name = "name";
        String timestamp = "789";
        String state = "state";
        int version = 1;

        Handler<? extends Message> handler = (Message<JsonObject> message) -> {
            assertEquals(name, message.body().getString("name"));
            assertEquals(version, message.body().getNumber("version"));

            message.reply(new JsonObject()
                    .putString("wid", wid)
                    .putString("aid", wid)
                    .putNumber("version", version)
                    .putString("timestamp", timestamp)
                    .putString("astate", state));
        };

        vertx.eventBus().registerHandler(OrchestrationTrackingServiceVerticle.CREATE_AND_START_ACTION_ADDRESS, handler, (result) -> {
            assertTrue(result.succeeded());

            HttpClient client = getClient();
            client.post("/workflows/" + wid + "/actions", (response) -> {
                assertEquals(201, response.statusCode());

                response.bodyHandler(body -> {
                    JsonObject json = new JsonObject(body.getString(0, body.length()));
                    assertEquals(wid, json.getString("wid"));
                    assertEquals(version, json.getNumber("version"));

                    vertx.eventBus().unregisterHandler(OrchestrationTrackingServiceVerticle.CREATE_AND_START_ACTION_ADDRESS, handler, result2 -> {
                        client.close();
                        testComplete();
                    });
                });
            })
                    .putHeader("Content-Type", "application/json")
                    .end(new JsonObject()
                            .putString("name", name)
                            .putNumber("version", version)
                            .putString("aid", aid)
                            .encode());
        });
    }

    @Override
    public void start() {
        initialize();
        container.deployVerticle(OrchestrationTrackingServiceAPIVerticle.class.getName(), new JsonObject().putNumber("PORT", PORT), (result) -> {
            if (result.failed()) {
                container.logger().error(result.cause());
                fail("verticle could not be deployed");
            }

            startTests();
        });
    }
}

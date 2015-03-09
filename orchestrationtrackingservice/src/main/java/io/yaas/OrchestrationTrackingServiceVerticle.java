package io.yaas;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.UUID;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class OrchestrationTrackingServiceVerticle extends Verticle {

    private final static int DEFAULT_API_HTTP_PORT = 8080;

    private final static long COMMUNICATON_TIMEOUT = 10000;

    private final static String CREATE_AND_START_WORKFLOW_ADDRESS = "workflow.create_and_start";

    private final static String UPDATE_WORKFLOW_ADDRESS = "workflow.update";

    private final static String CREATE_AND_START_TASK_ADDRESS = "task.create_and_start";

    private final static String UPDATE_TASK_ADDRESS = "task.update";

    private JsonObject getCreateAndStartWorkflowResponse(String id) {
        JsonObject json = new JsonObject();
        json.putString("id", id);
        return json;
    }

    private void registerHandlers() {
        vertx.eventBus().registerHandler(CREATE_AND_START_WORKFLOW_ADDRESS, (message) -> {
            container.logger().info("Received CREATE_AND_START_WORKFLOW_ADDRESS message: " + message.body().toString());
            message.reply(getCreateAndStartWorkflowResponse(UUID.randomUUID().toString()));
        });

        vertx.eventBus().registerHandler(UPDATE_WORKFLOW_ADDRESS, (message) -> {
            container.logger().info("Received UPDATE_WORKFLOW_ADDRESS message: " + message.body().toString());
            message.reply();
        });

        vertx.eventBus().registerHandler(CREATE_AND_START_TASK_ADDRESS, (message) -> {
            container.logger().info("Received CREATE_AND_START_TASK_ADDRESS message: " + message.body().toString());
            message.reply();
        });

        vertx.eventBus().registerHandler(UPDATE_TASK_ADDRESS, (message) -> {
            container.logger().info("Received UPDATE_TASK_ADDRESS message: " + message.body().toString());
            message.reply();
        });

        container.logger().info("Registered handlers");
    }

    private int getAPIHttpPort() {
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

    private void checkContentTypeIsApplicationJson(HttpServerRequest req) {
        String contentType = req.headers().get("content-type");
        if (contentType == null || !contentType.equalsIgnoreCase("application/json")) {
            throw new IllegalArgumentException();
        }
    }

    private String checkBody(String body) {
        if (body == null || body.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        return body;
    }

    private void extractBodyAndSendToAddress(HttpServerRequest req, String address, ExtractBodyAndSendToAddress handler) {
        final StringBuffer body = new StringBuffer("");
        req.dataHandler((buffer) -> {
            body.append(buffer);
        });

        req.endHandler((ignore) -> {
            try {
                checkContentTypeIsApplicationJson(req);
                handler.handle(address, new JsonObject(checkBody(body.toString())));
            } catch (IllegalArgumentException e) {
                req.response().setStatusCode(400).end();
            } catch (Exception e) {
                req.response().setStatusCode(500).end();
            }
        });

        container.logger().info(req.path() + " request processed");
    }

    private void registerAPI() {
        RouteMatcher rm = new RouteMatcher();

        rm.post("/workflows", (req) -> {
            extractBodyAndSendToAddress(req, CREATE_AND_START_WORKFLOW_ADDRESS, (address, body) -> {
                vertx.eventBus().sendWithTimeout(address, body, COMMUNICATON_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    if (asyncResult.succeeded()) {
                        req.response().setStatusCode(201).end(asyncResult.result().body().toString());
                    } else {
                        container.logger().error(asyncResult.cause());
                        req.response().setStatusCode(500).end();
                    }
                });
            });
        });

        rm.put("/workflows/:wid", (req) -> {
            extractBodyAndSendToAddress(req, UPDATE_WORKFLOW_ADDRESS, (address, body) -> {
                body.putString("wid", req.params().get("wid"));
                vertx.eventBus().sendWithTimeout(address, body, COMMUNICATON_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    if (asyncResult.succeeded()) {
                        req.response().setStatusCode(200).end();
                    } else {
                        container.logger().error(asyncResult.cause());
                        req.response().setStatusCode(500).end();
                    }
                });
            });
        });

        rm.post("/workflows/:wid/tasks", (req) -> {
            extractBodyAndSendToAddress(req, CREATE_AND_START_TASK_ADDRESS, (address, body) -> {
                body.putString("wid", req.params().get("wid"));
                vertx.eventBus().sendWithTimeout(address, body, COMMUNICATON_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    if (asyncResult.succeeded()) {
                        req.response().setStatusCode(201).end();
                    } else {
                        container.logger().error(asyncResult.cause());
                        req.response().setStatusCode(500).end();
                    }
                });
            });
        });

        rm.put("/workflows/:wid/tasks/:tid", (req) -> {
            extractBodyAndSendToAddress(req, UPDATE_TASK_ADDRESS, (address, body) -> {
                body.putString("wid", req.params().get("wid"));
                body.putString("tid", req.params().get("tid"));
                vertx.eventBus().sendWithTimeout(address, body, COMMUNICATON_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    if (asyncResult.succeeded()) {
                        req.response().setStatusCode(200).end();
                    } else {
                        container.logger().error(asyncResult.cause());
                        req.response().setStatusCode(500).end();
                    }
                });
            });
        });

        HttpServer server = vertx.createHttpServer();
        int port = getAPIHttpPort();
        server.requestHandler(rm).listen(port, "localhost");
        container.logger().info("Registered API at " + port);
    }

    public void start() {
        registerHandlers();

        registerAPI();

        container.logger().info("OrchestrationTrackingServiceVerticle started");
    }
}

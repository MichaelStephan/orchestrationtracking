package io.yaas.workflow;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/*
  API verticle that accepts the REST calls (similar to JAX-RS resource)
 */
public class OrchestrationTrackingServiceAPIVerticle extends Verticle {

    private int getAPIHttpPort() {
        try {
            return getContainer().config().getInteger("PORT");
        } catch (Exception e) {
            throw new IllegalStateException("PORT configuration does not exist or could not be parsed to integer");
        }
    }

    private void handleMessage(HttpServerRequest req, String address, MessageHandler handler) {
        checkNotNull(req);
        checkNotNull(address);
        checkArgument(address.length() > 0);
        checkNotNull(handler);

        final StringBuffer body = new StringBuffer("");
        req.dataHandler((buffer) -> {
            body.append(buffer);
        });

        req.endHandler((ignore) -> {
            try {
                Common.checkContentTypeIsApplicationJson(req);
                handler.handle(address, "GET".equals(req.method()) ? new JsonObject() : new JsonObject(Common.checkBody(body.toString())));
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

        rm.get("/workflows", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.LIST_WORKFLOWS_ADDRESS, (address, body) -> {
                container.logger().info("received GET /workflows " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        container.logger().debug("asyncResult.result().body().getArray(\"result\")=" + asyncResult.result().body().getArray("result").encode());
                        req.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(asyncResult.result().body().getArray("result").asArray().encode());
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.post("/workflows", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.CREATE_AND_START_WORKFLOW_ADDRESS, (address, body) -> {
                container.logger().info("received POST /workflows " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(201).putHeader("Content-Type", "application/json").end(asyncResult.result().body().encode());
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.put("/workflows/:wid", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.UPDATE_WORKFLOW_ADDRESS, (address, body) -> {
                body.putString("wid", checkNotNull(req.params().get("wid")));
                container.logger().info("received PUT /workflows/:wid " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(200).putHeader("Content-Type", "application/json").end();
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.post("/workflows/:wid/actions", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.CREATE_AND_START_ACTION_ADDRESS, (address, body) -> {
                body.putString("wid", checkNotNull(req.params().get("wid")));
                container.logger().info("received POST /workflows/:wid/actions " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(201).putHeader("Content-Type", "application/json").end(asyncResult.result().body().encode());
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.put("/workflows/:wid/actions/:aid/:timestamp", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.UPDATE_ACTION_ADDRESS, (address, body) -> {
                body.putString("wid", checkNotNull(req.params().get("wid")));
                body.putString("aid", checkNotNull(req.params().get("aid")));
                body.putString("timestamp", checkNotNull(req.params().get("timestamp")));
                container.logger().info("received PUT /workflows/:wid/actions/:aid/:timestamp " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(200).putHeader("Content-Type", "application/json").end();
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.get("/workflows/:wid/actions/:aid/last", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.GET_LAST_ACTION_ADDRESS, (address, body) -> {
                body.putString("wid", checkNotNull(req.params().get("wid")));
                body.putString("aid", checkNotNull(req.params().get("aid")));
                container.logger().info("received GET /workflows/:wid/actions/:aid/last " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(asyncResult.result().body().getObject("result").encode());
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        rm.get("/workflows/:wid/actions/:aid/last/data", (req) -> {
            handleMessage(req, OrchestrationTrackingServiceVerticle.GET_LAST_ACTION_DATA_ADDRESS, (address, body) -> {
                body.putString("wid", checkNotNull(req.params().get("wid")));
                body.putString("aid", checkNotNull(req.params().get("aid")));
                container.logger().info("received GET /workflows/:wid/actions/:aid/last/data " + body);
                vertx.eventBus().sendWithTimeout(address, body, Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                        req.response().setStatusCode(200).putHeader("Content-Type", "application/json").end(asyncResult.result().body().encode());
                    }, (ignore) -> {
                        req.response().setStatusCode(500).end();
                    });
                });
            });
        });

        HttpServer server = vertx.createHttpServer();
        int port = getAPIHttpPort();
        server.requestHandler(rm).listen(port, "0.0.0.0");
        container.logger().info("Registered API at " + port);
    }

    public void start() {
        registerAPI();

        container.logger().info("OrchestrationTrackingServiceAPIVerticle started");
    }
}

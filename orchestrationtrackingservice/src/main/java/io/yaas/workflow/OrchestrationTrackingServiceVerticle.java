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

import com.datastax.driver.core.utils.UUIDs;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class OrchestrationTrackingServiceVerticle extends Verticle {

    public final static String LIST_WORKFLOWS_ADDRESS = "workflows.list";

    public final static String GET_WORKFLOW_ADDRESS = "workflow.info";

    public final static String CREATE_AND_START_WORKFLOW_ADDRESS = "workflow.create_and_start";

    public final static String UPDATE_WORKFLOW_ADDRESS = "workflow.update";

    public final static String CREATE_AND_START_ACTION_ADDRESS = "action.create_and_start";

    public final static String UPDATE_ACTION_ADDRESS = "action.update";

    public final static String UPDATE_ACTION_ERROR_STATE_ADDRESS = "action.error.state.update";

    public final static String GET_ACTION_DATA_ADDRESS = "action.data_get";

    private enum WorkflowState {STARTED, SUCCEEDED, COMPENSATED, FAILED}

    private enum ActionState {STARTED, SUCCEEDED, FAILED}

    private enum ActionErrorState {STARTED, SUCCEEDED, FAILED}

    private void registerCassandraStructures() {
        Future<Void> createKeyspace = new DefaultFutureResult<>();
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "raw").putString("statement", "CREATE KEYSPACE yaas WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 }"), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("CREATE KEYSPACE yaas successful");
                createKeyspace.setResult(null);
            }, (ignore) -> {
                container.logger().error("CREATE KEYSPACE yaas failed", asyncResult.cause());
                createKeyspace.setResult(null);
            });
        });

        createKeyspace.setHandler((ignore) -> {
            vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "raw").putString("statement", "CREATE TABLE yaas.workflows(wid uuid, name text, version int, wstate text, PRIMARY KEY(wid))"), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                Common.checkResponse(vertx, container, asyncResult, (ignore1) -> {
                    container.logger().info("CREATE TABLE yaas.workflows  successful");
                }, (ignore1) -> {
                    container.logger().error("CREATE TABLE yaas.workflow failed", asyncResult.cause());
                });
            });

            vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "raw").putString("statement", "CREATE TABLE yaas.actions(wid uuid, timestamp timeuuid, aid text, astate text, aestate text, data text, PRIMARY KEY(wid, aid, timestamp))"), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                Common.checkResponse(vertx, container, asyncResult, (ignore1) -> {
                    container.logger().info("CREATE TABLE yaas.actions successful");
                }, (ignore1) -> {
                    container.logger().error("CREATE TABLE yaas.actions failed", asyncResult.cause());
                });
            });
        });

    }

    private void registerHandlers() {
        vertx.eventBus().registerHandler(LIST_WORKFLOWS_ADDRESS, (ignore) -> {
            Common.execute(container, ignore, (result) -> {
                container.logger().info("Received LIST_WORKFLOWS_ADDRESS message");
                listWorkflows(result);
            });
        });

        vertx.eventBus().registerHandler(CREATE_AND_START_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received CREATE_AND_START_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                createAndStartWorkflow(result, checkNotNull(message.body().getString("name")), message.body().getInteger("version"));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received UPDATE_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                updateWorkflow(result, checkNotNull(message.body().getString("wid")), WorkflowState.valueOf(checkNotNull(message.body().getString("wstate")).toUpperCase()));
            });
        });

        vertx.eventBus().registerHandler(CREATE_AND_START_ACTION_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received CREATE_AND_START_ACTION_ADDRESS message: " + checkNotNull(message).body().toString());
                createAndStartAction(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("aid")), checkNotNull(message.body().getString("name")), checkNotNull(message.body().getString("version")));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_ACTION_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received UPDATE_ACTION_ADDRESS message: " + checkNotNull(message).body().toString());
                JsonObject data = message.body().getObject("data", new JsonObject());
                updateAction(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("aid")), checkNotNull(message.body().getString("timestamp")), ActionState.valueOf(checkNotNull(message.body().getString("astate")).toUpperCase()), data.encode());
            });
        });

        vertx.eventBus().registerHandler(UPDATE_ACTION_ERROR_STATE_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received UPDATE_ACTION_ERROR_STATE_ADDRESS message: " + checkNotNull(message).body().toString());
                updateActionErrorState(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("aid")), checkNotNull(message.body().getString("timestamp")), ActionErrorState.valueOf(checkNotNull(message.body().getString("aestate")).toUpperCase()));
            });
        });

        vertx.eventBus().registerHandler(GET_ACTION_DATA_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received GET_ACTION_DATA_ADDRESS message: " + checkNotNull(message).body().toString());
                getActionData(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("aid")), checkNotNull(message.body().getString("timestamp")));
            });
        });

        container.logger().info("Registered handlers");
    }


    public void start() {
        registerCassandraStructures();

        registerHandlers();

        container.logger().info("OrchestrationTrackingServiceVerticle started");
    }

    private void listWorkflows(Future<JsonObject> result) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "raw").putString("statement", "SELECT * FROM yaas.workflows"), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonArray>> asyncResult) -> {
            Common.checkArrayResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("SELECT FROM yaas.workflows successful");
                result.setResult(new JsonObject().putArray("result", asyncResult.result().body().asArray()));
            }, (ignore) -> {
                container.logger().info("SELECT FROM yaas.workflows failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void createAndStartWorkflow(Future<JsonObject> result, String name, int version) {
        String wid = UUID.randomUUID().toString();
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "INSERT INTO yaas.workflows (wid, name, version, wstate) VALUES (?,?,?,?)").putArray("values", new JsonArray().addArray(new JsonArray().addString(wid).addString(name).addNumber(version).addString("started"))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("INSERT INTO yaas.workflows successful");
                result.setResult(new JsonObject().putString("wid", wid).putString("name", name).putNumber("version", version).putString("state", "started"));
            }, (ignore) -> {
                container.logger().info("INSERT INTO yaas.workflows failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void updateWorkflow(Future<Void> result, String wid, WorkflowState state) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "UPDATE yaas.workflows SET wstate=? WHERE wid=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(state.name().toLowerCase()).addString(wid))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("UPDATE yaas.workflows SET wstate successful");
                result.setResult(null);
            }, (ignore) -> {
                Throwable cause = asyncResult.cause();
                container.logger().info("UPDATE yaas.workflows SET wstate failed", cause);
                cause.printStackTrace();
                result.setFailure(cause);
            });
        });
    }

    private void createAndStartAction(Future<JsonObject> result, String wid, String aid, String name, String version) {
        // TODO UUIDs.timeBased() may lead to collisions and therefore needs to carry random bits along timestamp !!!
        String uniqueTimestamp = UUIDs.timeBased().toString();
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "INSERT INTO yaas.actions (wid, timestamp, aid, astate) VALUES (?,?,?,?)").putArray("values", new JsonArray().addArray(new JsonArray().addString(wid).addString(uniqueTimestamp).addString(aid).addString("started"))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("INSERT INTO yaas.actions successful");
                result.setResult(new JsonObject().putString("wid", wid).putString("aid", aid).putString("version", version).putString("timestamp", uniqueTimestamp).putString("astate", "started"));
            }, (ignore) -> {
                container.logger().info("INSERT INTO yaas.actions failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void updateAction(Future<Void> result, String wid, String aid, String timestamp, ActionState state, String data) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "UPDATE yaas.actions SET astate=?, data=? WHERE wid=? AND aid=? AND timestamp=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(state.name().toLowerCase()).addString(data).addString(wid).addString(aid).addString(timestamp))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("UPDATE yaas.actions SET astate successful");
                result.setResult(null);
            }, (ignore) -> {
                container.logger().info("UPDATE yaas.actions SET astate failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void updateActionErrorState(Future<Void> result, String wid, String aid, String timestamp, ActionErrorState state) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "UPDATE yaas.actions SET aestate=? WHERE wid=? AND aid=? AND timestamp=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(state.name().toLowerCase()).addString(wid).addString(aid).addString(timestamp))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("UPDATE yaas.actions SET astate successful");
                result.setResult(null);
            }, (ignore) -> {
                container.logger().info("UPDATE yaas.actions SET astate failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void getActionData(Future<JsonObject> result, String wid, String aid, String timestamp) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "SELECT data from yaas.actions WHERE wid=? AND aid=? AND timestamp=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(wid).addString(aid).addString(timestamp))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonArray>> asyncResult) -> {
            Common.checkArrayResponse(vertx, container, asyncResult, (ignore) -> {
                JsonArray array = new JsonArray();
                for (Iterator<Object> i = asyncResult.result().body().iterator(); i.hasNext(); ) {
                    array.addObject(new JsonObject().putObject("data", new JsonObject(JsonObject.class.cast(i.next()).getString("data"))));
                }

                if (array.size() > 0) {
                    container.logger().info("SELECT data from yaas.actions ... successful", asyncResult.cause());
                    result.setResult(new JsonObject().putObject("result", array.get(0)));
                } else {
                    container.logger().info("SELECT data from yaas.actions ... failed", asyncResult.cause());
                    result.setFailure(new NoSuchElementException());
                }
            }, (ignore) -> {
                container.logger().info("SELECT data from yaas.actions ... failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

}

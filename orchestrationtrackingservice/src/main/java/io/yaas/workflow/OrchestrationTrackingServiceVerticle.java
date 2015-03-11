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

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class OrchestrationTrackingServiceVerticle extends Verticle {

    public final static String CREATE_AND_START_WORKFLOW_ADDRESS = "workflow.create_and_start";

    public final static String UPDATE_WORKFLOW_ADDRESS = "workflow.update";

    public final static String CREATE_AND_START_TASK_ADDRESS = "task.create_and_start";

    public final static String UPDATE_TASK_ADDRESS = "task.update";

    private enum WorkflowState {COMPLETED, FAILED}

    private enum TaskState {COMPLETED, FAILED}

    private void registerCassandraStructures() {
        {
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

                vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "raw").putString("statement", "CREATE TABLE yaas.tasks(wid uuid, timestamp timeuuid, tid text, tstate text, PRIMARY KEY(wid, timestamp))"), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
                    Common.checkResponse(vertx, container, asyncResult, (ignore1) -> {
                        container.logger().info("CREATE TABLE yaas.tasks successful");
                    }, (ignore1) -> {
                        container.logger().error("CREATE TABLE yaas.tasks failed", asyncResult.cause());
                    });
                });
            });

        }
    }

    private void registerHandlers() {
        vertx.eventBus().registerHandler(CREATE_AND_START_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received CREATE_AND_START_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                createAndStartWorkflow(result, checkNotNull(message.body().getString("name")), message.body().getInteger("version"));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received UPDATE_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                updateWorkflow(result, checkNotNull(message.body().getString("wid")), WorkflowState.valueOf(checkNotNull(message.body().getString("state")).toUpperCase()));
            });
        });

        vertx.eventBus().registerHandler(CREATE_AND_START_TASK_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received CREATE_AND_START_TASK_ADDRESS message: " + checkNotNull(message).body().toString());
                createAndStartTask(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("tid")));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_TASK_ADDRESS, (Message<JsonObject> message) -> {
            Common.execute(container, message, (result) -> {
                container.logger().info("Received UPDATE_TASK_ADDRESS message: " + checkNotNull(message).body().toString());
                updateTask(result, checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("tid")), TaskState.valueOf(checkNotNull(message.body().getString("state")).toUpperCase()));
            });
        });

        container.logger().info("Registered handlers");
    }


    public void start() {
        registerCassandraStructures();

        registerHandlers();

        container.logger().info("OrchestrationTrackingServiceVerticle started");
    }

    private void createAndStartWorkflow(Future<JsonObject> result, String name, int version) {
        String wid = UUID.randomUUID().toString();
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "INSERT INTO yaas.workflows (wid, name, version) VALUES (?,?,?)").putArray("values", new JsonArray().addArray(new JsonArray().addString(wid).addString(name).addNumber(version))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("INSERT INTO yaas.workflows successful");
                result.setResult(new JsonObject().putString("id", wid));
            }, (ignore) -> {
                container.logger().info("INSERT INTO yaas.workflows failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void updateWorkflow(Future<Void> result, String wid, WorkflowState state) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "UPDATE yaas.workflows SET wstate=? WHERE wid=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(state.name().toUpperCase()).addString(wid))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("UPDATE yaas.workflows SET wstate successful");
                result.setResult(null);
            }, (ignore) -> {
                container.logger().info("UPDATE yaas.workflows SET wstate failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void createAndStartTask(Future<Void> result, String wid, String tid) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "INSERT INTO yaas.tasks (wid, timestamp, tid) VALUES (?,?,?)").putArray("values", new JsonArray().addArray(new JsonArray().addString(wid).addString(UUIDs.timeBased().toString()).addString(tid))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("INSERT INTO yaas.tasks successful");
                result.setResult(null);
            }, (ignore) -> {
                container.logger().info("INSERT INTO yaas.tasks failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }

    private void updateTask(Future<Void> result, String wid, String tid, TaskState state) {
        vertx.eventBus().sendWithTimeout("persistor", new JsonObject().putString("action", "prepared").putString("statement", "UPDATE yaas.tasks SET tstate=? WHERE wid=? AND tid=?").putArray("values", new JsonArray().addArray(new JsonArray().addString(state.name().toUpperCase()).addString(wid).addString(tid))), Common.COMMUNICATION_TIMEOUT, (AsyncResult<Message<JsonObject>> asyncResult) -> {
            Common.checkResponse(vertx, container, asyncResult, (ignore) -> {
                container.logger().info("UPDATE yaas.tasks SET tstate successful");
                result.setResult(null);
            }, (ignore) -> {
                container.logger().info("UPDATE yaas.tasks SET tstate failed", asyncResult.cause());
                result.setFailure(asyncResult.cause());
            });
        });
    }
}

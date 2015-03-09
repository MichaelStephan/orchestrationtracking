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

import org.vertx.java.core.Future;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.impl.DefaultFutureResult;
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

    void execute(Message<?> message, Command command) {
        try {
            Future<?> future = command.execute();
            future.setHandler((f) -> {
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
                    err.putString("message", f.cause().getClass().getSimpleName() + ": " + f.cause().getMessage());
                    message.reply(err);
                }
            });
        } catch (Exception e) {
            container.logger().error(e);
            JsonObject err = new JsonObject();
            err.putString("status", "error");
            err.putString("message", e.getClass().getSimpleName() + ": " + e.getMessage());
            message.reply(err);
        }
    }

    private JsonObject getCreateAndStartWorkflowResponse(String id) {
        checkNotNull(id);

        JsonObject json = new JsonObject();
        json.putString("id", id);
        return json;
    }

    private void registerCassandraStructures() {
        
    }

    private void registerHandlers() {
        vertx.eventBus().registerHandler(CREATE_AND_START_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            execute(message, () -> {
                container.logger().info("Received CREATE_AND_START_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                return createAndStartWorkflow(checkNotNull(message.body().getString("name")), message.body().getInteger("version"));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_WORKFLOW_ADDRESS, (Message<JsonObject> message) -> {
            execute(message, () -> {
                container.logger().info("Received UPDATE_WORKFLOW_ADDRESS message: " + checkNotNull(message).body().toString());
                return updateWorkflow(checkNotNull(message.body().getString("wid")), WorkflowState.valueOf(checkNotNull(message.body().getString("state")).toUpperCase()));
            });
        });

        vertx.eventBus().registerHandler(CREATE_AND_START_TASK_ADDRESS, (Message<JsonObject> message) -> {
            execute(message, () -> {
                container.logger().info("Received CREATE_AND_START_TASK_ADDRESS message: " + checkNotNull(message).body().toString());
                return createAndStartTask(checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("tid")));
            });
        });

        vertx.eventBus().registerHandler(UPDATE_TASK_ADDRESS, (Message<JsonObject> message) -> {
            execute(message, () -> {
                container.logger().info("Received UPDATE_TASK_ADDRESS message: " + checkNotNull(message).body().toString());
                return updateTask(checkNotNull(message.body().getString("wid")), checkNotNull(message.body().getString("tid")), TaskState.valueOf(checkNotNull(message.body().getString("state")).toUpperCase()));
            });
        });

        container.logger().info("Registered handlers");
    }


    public void start() {
        registerHandlers();

        container.logger().info("OrchestrationTrackingServiceVerticle started");
    }

    private Future<JsonObject> createAndStartWorkflow(String name, int version) {
        return new DefaultFutureResult<JsonObject>().setResult(getCreateAndStartWorkflowResponse(UUID.randomUUID().toString()));
    }

    private Future<Void> updateWorkflow(String wif, WorkflowState state) {
        return new DefaultFutureResult<Void>().setResult(null);
    }

    private Future<Void> createAndStartTask(String wif, String tid) {
        return new DefaultFutureResult<Void>().setResult(null);
    }

    private Future<Void> updateTask(String wif, String tid, TaskState state) {
        return new DefaultFutureResult<Void>().setResult(null);
    }
}

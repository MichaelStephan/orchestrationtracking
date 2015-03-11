package io.yaas.workflow;

import org.vertx.java.core.json.JsonObject;

/**
 * Created by i303874 on 3/9/15.
 */
public interface ExtractBodyAndSendToAddress {
    void handle(String address, JsonObject object);
}

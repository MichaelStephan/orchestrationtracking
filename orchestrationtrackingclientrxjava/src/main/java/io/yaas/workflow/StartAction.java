package io.yaas.workflow;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.UUID;

/**
 * Created by i303874 on 3/11/15.
 */
public class StartAction extends Action {
    public StartAction(List<Action> successors) {
        super(UUID.randomUUID().toString(), "start", (action, arguments, result) -> {
            result.set(new ActionResult(action, new ImmutableMap.Builder<String, Object>().putAll(arguments).build()));
            return result;
        }, successors);
    }
}

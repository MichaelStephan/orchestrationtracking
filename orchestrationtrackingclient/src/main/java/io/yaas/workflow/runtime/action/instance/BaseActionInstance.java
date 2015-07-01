package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.action.Action;
import io.yaas.workflow.graph.BaseNode;
import io.yaas.workflow.runtime.ActionInstance;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by D032705 on 29.06.2015.
 */
abstract class BaseActionInstance extends BaseNode<ActionInstance> implements ActionInstance {

    protected Action action;

    public BaseActionInstance(String id, Action action) {
        super(id);
        this.action = checkNotNull(action);
    }
    public String toString() {
        return getId();
    }

    @Override
    public Action getAction() {
        return action;
    }
    @Override
    public String getName() {
        return this.action.getName();
    }

    @Override
    public String getVersion() {
        return this.action.getVersion();
    }
}

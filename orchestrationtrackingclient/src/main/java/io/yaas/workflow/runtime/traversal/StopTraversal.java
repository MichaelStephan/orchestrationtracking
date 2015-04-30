package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.runtime.ActionInstance;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by D032705 on 30.04.2015.
 */
public class StopTraversal implements TraversalStrategy {
    private static TraversalStrategy TRAVERSAL;
    public static TraversalStrategy getInstance() {
        if (TRAVERSAL == null) {
            TRAVERSAL = new StopTraversal();
        }
        return TRAVERSAL;
    }


    @Override
    public Collection<ActionInstance> getNext(ActionInstance current) {
        return Collections.EMPTY_LIST;
    }
}

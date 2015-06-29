package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.traversal.ForwardIterator;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleAction extends BaseAction {

    public SimpleAction(String name, String version, Workflow w) {
        super(name, version, w);
    }

    public SimpleAction(String name, String version) {
        super(name, version);
    }

    public SimpleAction(String name, String version, ActionFunction f) {
        super(name, version, f);
    }


}


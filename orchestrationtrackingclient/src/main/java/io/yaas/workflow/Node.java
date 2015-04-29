package io.yaas.workflow;

import java.util.*;

/**
 * Created by D032705 on 28.04.2015.
 */
public class Node {
    private Set<Node> _successors = new HashSet<Node>();
    private Set<Node> _predecessors = new HashSet<Node>();

    public Set<Node> getSuccessors() {
        return _successors;
    }

    public Set<Node> getPredecessors() {
        return _predecessors;
    }

    public void addSuccessor(Node successor) {
        _successors.add(successor);
        successor.addPredecessor(this);
    }

    public void removeSuccessor(Node successor) {
        _successors.remove(successor);
        successor.removePredecessor(this);
    }

    public void addPredecessor(Node predecessor) {
        _predecessors.add(predecessor);
    }

    public void removePredecessor(Node predecessor) {
        _predecessors.remove(predecessor);
    }

    public void  insertAfter(Node successor) {
        for (Node node: getSuccessors()) {
            successor.addSuccessor(node);
            removeSuccessor(node);
        }
        addSuccessor(successor);
    }

    public void insertBefore(Node successor) {
        List<Node> predecessors = new ArrayList<Node>();
        for (Node node: getPredecessors()) { // otherwise concurrent modification
            predecessors.add(node);
        }
        for (Node node: predecessors) {
            node.addSuccessor(successor);
            node.removeSuccessor(this);
        }
        successor.addSuccessor(this);
    }

    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}

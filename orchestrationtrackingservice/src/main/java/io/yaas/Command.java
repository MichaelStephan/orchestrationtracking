package io.yaas;

import org.vertx.java.core.Future;

/**
 * Created by i303874 on 3/9/15.
 */
public interface Command<T> {
    Future<T> execute();
}

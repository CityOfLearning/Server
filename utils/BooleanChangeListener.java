package com.dyn.server.utils;

import java.util.EventListener;

/**
 * Listener interface for classes interested in knowing about a boolean
 * flag change.
 */
public interface BooleanChangeListener extends EventListener {

    public void stateChanged(BooleanChangeEvent event);

}
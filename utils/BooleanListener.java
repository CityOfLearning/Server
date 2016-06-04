package com.dyn.server.utils;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

/**
 * This class uses the EventQueue to process its events, but you should only
 * really do this if the changes you make have an impact on part of a GUI eg.
 * adding a button to a JFrame.
 *
 * Otherwise, you should create your own event dispatch thread that can handle
 * change events
 */
public class BooleanListener implements BooleanChangeDispatcher {

	private boolean flag;
	private List<BooleanChangeListener> listeners;

	public BooleanListener(boolean initialFlagState) {
		flag = initialFlagState;
		listeners = new ArrayList<BooleanChangeListener>();
	}

	@Override
	public void addBooleanChangeListener(BooleanChangeListener listener) {
		listeners.add(listener);
	}

	private void dispatchEvent() {
		final BooleanChangeEvent event = new BooleanChangeEvent(this);
		for (BooleanChangeListener l : listeners) {
			dispatchRunnableOnEventQueue(l, event);
		}
	}

	private void dispatchRunnableOnEventQueue(final BooleanChangeListener listener, final BooleanChangeEvent event) {

		EventQueue.invokeLater(() -> listener.stateChanged(event));
	}

	@Override
	public boolean getFlag() {
		return flag;
	}

	@Override
	public void setFlag(boolean flag) {
		if (this.flag != flag) {
			this.flag = flag;
			dispatchEvent();
		}
	}

}

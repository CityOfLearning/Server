package com.dyn.server.utils;

public interface BooleanChangeDispatcher {

	public void addBooleanChangeListener(BooleanChangeListener listener);

	public boolean getFlag();

	public void setFlag(boolean flag);

}

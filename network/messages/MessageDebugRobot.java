package com.dyn.server.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageDebugRobot implements IMessage {

	public enum CommandType {
		FORWARD, BACK, RIGHT, LEFT, RUN, INTERACT, PLACE, BREAK, CLIMB, JUMP, SAY;
	}

	private int robotId;
	private int commandOrdinal;
	private int amount;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageDebugRobot() {
	}

	public MessageDebugRobot(int robotId, CommandType command, int amount) {
		this.robotId = robotId;
		commandOrdinal = command.ordinal();
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		robotId = buf.readInt();
		commandOrdinal = buf.readInt();
		amount = buf.readInt();
	}

	public int getAmount() {
		return amount;
	}

	public CommandType getCommand() {
		return CommandType.values()[commandOrdinal];
	}

	public int getRobotId() {
		return robotId;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(robotId);
		buf.writeInt(commandOrdinal);
		buf.writeInt(amount);
	}
}

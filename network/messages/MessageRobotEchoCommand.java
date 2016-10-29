package com.dyn.server.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageRobotEchoCommand implements IMessage {

	private String command;
	private int robotId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageRobotEchoCommand() {
	}

	public MessageRobotEchoCommand(String command, int robotid) {
		this.command = command;
		robotId = robotid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		command = ByteBufUtils.readUTF8String(buf);
		robotId = buf.readInt();
	}

	public String getCommand() {
		return command;
	}

	public int getId() {
		return robotId;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, command);
		buf.writeInt(robotId);

	}
}

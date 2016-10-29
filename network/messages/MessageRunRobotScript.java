package com.dyn.server.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageRunRobotScript implements IMessage {

	private String script;
	private int robotId;
	private boolean echo;

	public MessageRunRobotScript() {
	}

	public MessageRunRobotScript(String script, int robotId, boolean shouldEcho) {
		this.script = script;
		this.robotId = robotId;
		echo = shouldEcho;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		script = ByteBufUtils.readUTF8String(buf);
		robotId = buf.readInt();
		echo = buf.readBoolean();
	}

	public boolean getEcho() {
		return echo;
	}

	public int getId() {
		return robotId;
	}

	public String getScript() {
		return script;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, script);
		buf.writeInt(robotId);
		buf.writeBoolean(echo);
	}
}

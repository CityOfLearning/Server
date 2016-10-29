package com.dyn.server.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageToggleRobotFollow implements IMessage {

	private boolean toggle;
	private int entityId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageToggleRobotFollow() {
	}

	public MessageToggleRobotFollow(int id, boolean toggle) {
		this.toggle = toggle;
		entityId = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		toggle = buf.readBoolean();
		entityId = buf.readInt();
	}

	public int getEntityId() {
		return entityId;
	}

	public boolean shouldFollow() {
		return toggle;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(toggle);
		buf.writeInt(entityId);
	}
}

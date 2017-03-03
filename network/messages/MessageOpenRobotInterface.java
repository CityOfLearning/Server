package com.dyn.server.network.messages;

import com.dyn.robot.RobotMod;
import com.dyn.robot.gui.RobotGuiHandler;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenRobotInterface implements IMessage {

	public static class Handler implements IMessageHandler<MessageOpenRobotInterface, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenRobotInterface message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				player.openGui(RobotMod.instance, RobotGuiHandler.getGuiID(), player.worldObj, (int) player.posX,
						(int) player.posY, (int) player.posZ);
			});
			return null;
		}
	}

	private int entityId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageOpenRobotInterface() {
	}

	public MessageOpenRobotInterface(int id) {
		entityId = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
	}

	public int getEntityId() {
		return entityId;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
	}
}

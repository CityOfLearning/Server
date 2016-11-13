package com.dyn.server.network.messages;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenRobotInventory implements IMessage {

	public static class Handler implements IMessageHandler<MessageOpenRobotInventory, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenRobotInventory message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				World world = player.worldObj;
				EntityRobot robot = (EntityRobot) world.getEntityByID(message.getEntityId());
				player.displayGUIChest(robot.m_inventory);
			});
			return null;
		}
	}

	private int entityId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageOpenRobotInventory() {
	}

	public MessageOpenRobotInventory(int id) {
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

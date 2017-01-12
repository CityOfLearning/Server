package com.dyn.server.network.messages;

import com.dyn.robot.RobotMod;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenRobotRemoteInterface implements IMessage {

	public static class Handler implements IMessageHandler<MessageOpenRobotRemoteInterface, IMessage> {
		@Override
		public IMessage onMessage(final MessageOpenRobotRemoteInterface message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = Minecraft.getMinecraft().thePlayer.worldObj;
				EntityRobot robot = (EntityRobot) world.getEntityByID(message.getEntityId());
				RobotMod.proxy.openRemoteInterface(robot);
			});
			return null;
		}
	}

	private int entityId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageOpenRobotRemoteInterface() {
	}

	public MessageOpenRobotRemoteInterface(int id) {
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

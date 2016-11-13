package com.dyn.server.network.messages;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTeleportRobot implements IMessage {

	public static class Handler implements IMessageHandler<MessageTeleportRobot, IMessage> {
		@Override
		public IMessage onMessage(final MessageTeleportRobot message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				World world = player.worldObj;
				EntityRobot robot = (EntityRobot) world.getEntityByID(message.getEntityId());
				BlockPos pos = player.getPosition();
				robot.travelToDimension(player.dimension);
				robot.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
			});
			return null;
		}
	}

	private int entityId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageTeleportRobot() {
	}

	public MessageTeleportRobot(int id) {
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

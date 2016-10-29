package com.dyn.server.network.handlers;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageTeleportRobot;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerTeleportRobot implements IMessageHandler<MessageTeleportRobot, IMessage> {
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

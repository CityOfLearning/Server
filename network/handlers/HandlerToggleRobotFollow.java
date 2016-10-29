package com.dyn.server.network.handlers;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageToggleRobotFollow;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerToggleRobotFollow implements IMessageHandler<MessageToggleRobotFollow, IMessage> {
	@Override
	public IMessage onMessage(final MessageToggleRobotFollow message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(message.getEntityId());
			robot.setIsFollowing(message.shouldFollow());
		});
		return null;
	}
}

package com.dyn.server.network.handlers;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageOpenRobotInventory;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerOpenRobotInventory implements IMessageHandler<MessageOpenRobotInventory, IMessage> {
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

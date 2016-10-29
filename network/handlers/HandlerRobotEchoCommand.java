package com.dyn.server.network.handlers;

import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageRobotEchoCommand;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRobotEchoCommand implements IMessageHandler<MessageRobotEchoCommand, IMessage> {
	@Override
	public IMessage onMessage(final MessageRobotEchoCommand message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> {
			System.out.println(message.getCommand());
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			World world = player.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(message.getId());
			robot.addMessage(message.getCommand());
		});
		return null;
	}
}

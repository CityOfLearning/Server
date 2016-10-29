package com.dyn.server.network.handlers;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.dyn.robot.api.RobotAPI;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageRunRobotScript;

import mobi.omegacentauri.raspberryjammod.process.RunPythonShell;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRunRobotScript implements IMessageHandler<MessageRunRobotScript, IMessage> {
	@Override
	public IMessage onMessage(final MessageRunRobotScript message, final MessageContext ctx) {
		// TODO
		// we might want to filter out the other minecraft API calls to prevent
		// cheating
		// we need some way of severely limiting what commands the students can
		// use in
		// certain situations
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		EntityRobot robot = (EntityRobot) world.getEntityByID(message.getId());
		robot.startExecutingCode();

		RobotAPI.setRobotId(message.getId(), ctx.getServerHandler().playerEntity);
		RobotAPI.setRobotEcho(message.getId(), message.getEcho());

		ServerMod.proxy.addScheduledTask(() -> RunPythonShell.run(
				Arrays.asList(message.getScript().split(Pattern.quote("\n"))), ctx.getServerHandler().playerEntity));
		return null;
	}
}

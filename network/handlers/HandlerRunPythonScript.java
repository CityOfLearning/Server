package com.dyn.server.network.handlers;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageRunPythonScript;

import mobi.omegacentauri.raspberryjammod.process.RunPythonShell;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRunPythonScript implements IMessageHandler<MessageRunPythonScript, IMessage> {
	@Override
	public IMessage onMessage(final MessageRunPythonScript message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> RunPythonShell.run(
				Arrays.asList(message.getScript().split(Pattern.quote("\n"))), ctx.getServerHandler().playerEntity));
		return null;
	}
}

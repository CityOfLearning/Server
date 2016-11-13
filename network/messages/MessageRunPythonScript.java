package com.dyn.server.network.messages;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import mobi.omegacentauri.raspberryjammod.process.RunPythonShell;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRunPythonScript implements IMessage {

	public static class Handler implements IMessageHandler<MessageRunPythonScript, IMessage> {
		@Override
		public IMessage onMessage(final MessageRunPythonScript message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(
					() -> RunPythonShell.run(Arrays.asList(message.getScript().split(Pattern.quote("\n"))),
							ctx.getServerHandler().playerEntity));
			return null;
		}
	}

	private String script;

	public MessageRunPythonScript() {
	}

	public MessageRunPythonScript(String script) {
		this.script = script;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		script = ByteBufUtils.readUTF8String(buf);
	}

	public String getScript() {
		return script;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, script);
	}
}

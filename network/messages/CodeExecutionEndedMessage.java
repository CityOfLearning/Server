package com.dyn.server.network.messages;

import com.dyn.render.RenderMod;
import com.dyn.robot.RobotMod;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CodeExecutionEndedMessage implements IMessage {
	public static class Handler implements IMessageHandler<CodeExecutionEndedMessage, IMessage> {

		@Override
		public IMessage onMessage(CodeExecutionEndedMessage message, MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				// the respective proxies should check if it can validly handle
				// the error

				// The Robot
				RobotMod.proxy.handleCodeExecutionEnded();

				// General Purpose IDE
				RenderMod.proxy.handleCodeExecutionEnded();
			});
			return null;
		}

	}

	private String code;

	public CodeExecutionEndedMessage() {
	}

	public CodeExecutionEndedMessage(String code) {
		this.code = code;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		code = ByteBufUtils.readUTF8String(buf);
	}

	public String getCode() {
		return code;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, code);
	}
}

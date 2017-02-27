package com.dyn.server.network.messages;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.dyn.robot.api.RobotAPI;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import mobi.omegacentauri.raspberryjammod.process.RunPythonShell;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRunRobotScript implements IMessage {

	public static class Handler implements IMessageHandler<MessageRunRobotScript, IMessage> {
		@Override
		public IMessage onMessage(final MessageRunRobotScript message, final MessageContext ctx) {
			// TODO
			// we might want to filter out the other minecraft API calls to
			// prevent cheating we need some way of severely limiting what
			// commands the students can use in certain situations
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			World world = player.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(message.getId());
			robot.clearProgramPath();
			robot.startExecutingCode();

			RobotAPI.setRobotId(message.getId(), ctx.getServerHandler().playerEntity);

			ServerMod.proxy.addScheduledTask(
					() -> RunPythonShell.run(Arrays.asList(message.getScript().split(Pattern.quote("\n"))),
							ctx.getServerHandler().playerEntity, true, message.getId()));
			return null;
		}
	}

	private String script;
	private int robotId;

	private boolean echo;

	public MessageRunRobotScript() {
	}

	public MessageRunRobotScript(String script, int robotId, boolean shouldEcho) {
		this.script = script;
		this.robotId = robotId;
		echo = shouldEcho;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		script = ByteBufUtils.readUTF8String(buf);
		robotId = buf.readInt();
		echo = buf.readBoolean();
	}

	public boolean getEcho() {
		return echo;
	}

	public int getId() {
		return robotId;
	}

	public String getScript() {
		return script;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, script);
		buf.writeInt(robotId);
		buf.writeBoolean(echo);
	}

}

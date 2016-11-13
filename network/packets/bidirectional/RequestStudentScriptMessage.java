package com.dyn.server.network.packets.bidirectional;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.robot.RobotMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class RequestStudentScriptMessage extends AbstractMessage<RequestStudentScriptMessage> {

	private int playerId;
	private String script;

	public RequestStudentScriptMessage() {
		playerId = 0;
		script = "_";
	}

	public RequestStudentScriptMessage(String script, int id) {
		this.script = script;
		playerId = id;
	}

	
	/****
	 * from the client grab the current console text
	 * on the server send the result back to the mentor who requested it
	 */
	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			NetworkManager.sendToServer(new RequestStudentScriptMessage(
					RobotMod.proxy.getProgrammingInterface().getConsoleText(), playerId));
		} else {
			EntityPlayerMP mentor = null;
			for (World w : MinecraftServer.getServer().worldServers) {
				for (EntityPlayer p : w.playerEntities) {
					if (p.getEntityId() == playerId) {
						mentor = (EntityPlayerMP) p;
						break;
					}
				}
			}
			NetworkManager.sendTo(new MentorRequstScriptMessage(script, 0), mentor);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		playerId = buffer.readInt();
		script = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(playerId);
		buffer.writeString(script);
	}

}

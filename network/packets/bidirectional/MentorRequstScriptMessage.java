package com.dyn.server.network.packets.bidirectional;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class MentorRequstScriptMessage extends AbstractMessage<MentorRequstScriptMessage> {

	private int playerId;
	private String script;
	
	public MentorRequstScriptMessage(){
	}
	
	public MentorRequstScriptMessage(String script, int id){
		this.script = script;
		playerId = id;
	}
	
	/****
	 * from the client set the script and flag
	 * on the server send the request to the student
	 */
	@Override
	public void process(EntityPlayer player, Side side) {
		if(side.isClient()){
			DYNServerMod.studentSctipt = script;
			DYNServerMod.studentScriptMessageRecieved.setFlag(true);
		} else {
			EntityPlayerMP student = null;
			for (World w : MinecraftServer.getServer().worldServers) {
				for (EntityPlayer p : w.playerEntities) {
					if (p.getEntityId() == playerId) {
						student = (EntityPlayerMP) p;
						break;
					}
				}
			}
			NetworkManager.sendTo(new RequestStudentScriptMessage("_", player.getEntityId()), student);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		script = buffer.readStringFromBuffer(buffer.readableBytes());
		playerId = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(script);
		buffer.writeInt(playerId);
	}

}

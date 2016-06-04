package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class FeedPlayerMessage extends AbstractServerMessage<FeedPlayerMessage> {

	private String player_name;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public FeedPlayerMessage() {
	}

	public FeedPlayerMessage(String username) {
		player_name = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			for (EntityPlayerMP p : ServerMod.proxy.getServerUsers()) {
				if (p.getDisplayNameString().equals(player_name)) {
					p.getFoodStats().setFoodLevel(100);
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		player_name = buffer.readStringFromBuffer(100);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(player_name);
	}
}

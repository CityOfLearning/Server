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

	public FeedPlayerMessage(int id, String uuid, String username) {
		player_name = username;
	}

	// if there are any class fields, be sure to provide a constructor that
	// allows
	// for them to be initialized, and use that constructor when sending the
	// packet
	public FeedPlayerMessage(String uuid) {
		player_name = "";
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

package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.names.manager.NamesManager;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.SyncClientNamesMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class SyncNamesMessage extends AbstractServerMessage<SyncNamesMessage> {
	private String dynName;
	private String playerName;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public SyncNamesMessage() {
	}

	// if there are any class fields, be sure to provide a constructor that
	// allows
	// for them to be initialized, and use that constructor when sending the
	// packet

	public SyncNamesMessage(String dyn_name, String mc_name) {
		dynName = dyn_name;
		playerName = mc_name;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			NamesManager.addUsername(playerName, dynName);
			for (EntityPlayerMP p : ServerMod.proxy.getServerUsers()) {
				PacketDispatcher.sendTo(new SyncClientNamesMessage(dynName, playerName), p);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		dynName = buffer.readStringFromBuffer(100);
		playerName = buffer.readStringFromBuffer(100);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(dynName);
		buffer.writeString(playerName);
	}
}

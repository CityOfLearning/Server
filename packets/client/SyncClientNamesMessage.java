package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.names.manager.NamesManager;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class SyncClientNamesMessage extends AbstractClientMessage<SyncClientNamesMessage> {

	// the info needed to increment a requirement
	private String dynName;
	private String playerName;

	// this packet should only be sent when a player is in the right dimension
	// so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public SyncClientNamesMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public SyncClientNamesMessage(String dyn_name, String mc_name) {
		dynName = dyn_name;
		playerName = mc_name;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			NamesManager.addUsername(playerName, dynName);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		dynName = buffer.readStringFromBuffer(100);
		playerName = buffer.readStringFromBuffer(100);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(dynName);
		buffer.writeString(playerName);
	}
}

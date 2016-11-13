package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.SyncSkinsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class SyncSkinsServerMessage extends AbstractServerMessage<SyncSkinsServerMessage> {

	// the info needed to increment a requirement
	private String skin;
	private String playerName;

	// this packet should only be sent when a player is in the right dimension
	// so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public SyncSkinsServerMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public SyncSkinsServerMessage(String mc_name, String skin) {
		this.skin = skin;
		playerName = mc_name;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			NetworkManager.sendToAll(new SyncSkinsMessage(playerName, skin));
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		int totalBytes = buffer.readableBytes();
		skin = buffer.readStringFromBuffer(totalBytes);
		playerName = buffer.readStringFromBuffer(totalBytes);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(skin);
		buffer.writeString(playerName);
	}
}

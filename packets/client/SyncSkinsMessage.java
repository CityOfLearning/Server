package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;
import com.rabbit.gui.utils.SkinManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class SyncSkinsMessage extends AbstractClientMessage<SyncSkinsMessage> {

	// the info needed to increment a requirement
	private String skin;
	private String playerName;

	// this packet should only be sent when a player is in the right dimension
	// so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public SyncSkinsMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public SyncSkinsMessage(String mc_name, String skin) {
		this.skin = skin;
		playerName = mc_name;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			if (skin.equals("reset")) {
				SkinManager.removeSkinTexture(playerName);
			} else {
				SkinManager.setSkinTexture(playerName, skin);
			}
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

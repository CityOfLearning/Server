package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerStatusMessage extends AbstractClientMessage<PlayerStatusMessage> {

	private boolean frozen;
	private boolean muted;
	private boolean mode;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public PlayerStatusMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public PlayerStatusMessage(boolean freeze, boolean muted, boolean isCreative) {
		frozen = freeze;
		this.muted = muted;
		mode = isCreative;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			DYNServerMod.playerStatus = new boolean[] { frozen, muted, mode };
			DYNServerMod.playerStatusReturned.setFlag(true);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		frozen = buffer.readBoolean();
		muted = buffer.readBoolean();
		mode = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(frozen);
		buffer.writeBoolean(muted);
		buffer.writeBoolean(mode);
	}
}

package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.instructor.TeacherMod;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class FreezePlayerMessage extends AbstractClientMessage<FreezePlayerMessage> {

	// the info needed to increment a requirement
	private boolean frozen;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public FreezePlayerMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public FreezePlayerMessage(boolean freeze) {
		frozen = freeze;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			TeacherMod.frozen = frozen;
			player.capabilities.allowEdit = frozen;
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		frozen = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(frozen);
	}
}

package com.dyn.server.network.packets.client;

import java.io.IOException;

import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class MutePlayerAudioMessage extends AbstractClientMessage<MutePlayerAudioMessage> {

	// the info needed to increment a requirement
	private boolean frozen;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MutePlayerAudioMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public MutePlayerAudioMessage(boolean freeze) {
		frozen = freeze;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MASTER, 0);
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

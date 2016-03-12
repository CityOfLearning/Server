package com.dyn.server.packets.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class AchievementProgressMessage extends AbstractClientMessage<AchievementProgressMessage> {

	// the info needed to increment a requirement
	private String data;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public AchievementProgressMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public AchievementProgressMessage(List<String> achData) {
		for (String s : achData) {
			this.data += "|" + s;
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		//we have to split a lot here, the pipe character is each achievement
		//tabs are titles and new lines are the items within each requirement set
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		//this could potentially be a giant file...
		this.data = buffer.readStringFromBuffer(1000000);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(this.data);
	}
}

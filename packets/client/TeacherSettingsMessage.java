package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class TeacherSettingsMessage extends AbstractClientMessage<TeacherSettingsMessage> {

	// the info needed to increment a requirement
	private String data;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public TeacherSettingsMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public TeacherSettingsMessage(String[] users) {
		for (String s : users) {
			data += " " + s;
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			String[] users = data.split(" ");
			ServerMod.usernames.clear();
			for (String u : users) {
				if ((u != null) && !u.equals("null")) {
					ServerMod.usernames.add(u);
				}
			}
			ServerMod.usernames.remove(null);

		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		data = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(data);
	}
}

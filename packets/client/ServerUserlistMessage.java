package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class ServerUserlistMessage extends AbstractClientMessage<ServerUserlistMessage> {

	// the info needed to increment a requirement
	private String data;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public ServerUserlistMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public ServerUserlistMessage(String[] users) {
		if (users != null) {
			for (String s : users) {
				data += " " + s;
			}
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			String[] users = data.split(" ");
			DYNServerMod.usernames.clear();
			for (String u : users) {
				if ((u != null) && !u.equals("null")) {
					DYNServerMod.usernames.add(u);
				}
			}
			DYNServerMod.usernames.remove(null);
			DYNServerMod.serverUserlistReturned.setFlag(true);

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

package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.dyn.DYNServerMod;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class WorldNamesMessage extends AbstractClientMessage<WorldNamesMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public WorldNamesMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public WorldNamesMessage(List<String> worlds) {
		for (String s : worlds) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient() && !data.isEmpty()) {
			DYNServerMod.worlds.clear();
			for (String s : data.split(Pattern.quote("|"))) {
				String[] dimWorld = s.split(Pattern.quote(":"));
				DYNServerMod.worlds.put(Integer.parseInt(dimWorld[0]), dimWorld[1]);
			}
			DYNServerMod.worldsMessageRecieved.setFlag(true);
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

package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.dyn.admin.AdminUI;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class WorldZonesMessage extends AbstractClientMessage<WorldZonesMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public WorldZonesMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public WorldZonesMessage(List<String> zones) {
		for (String s : zones) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient() && !data.isEmpty()) {
			AdminUI.zones.clear();
			for (String s : data.split(Pattern.quote("|"))) {
				String[] subStr = s.split(Pattern.quote("^"));
				AdminUI.zones.put(Integer.parseInt(subStr[0]), subStr[1]);
			}
			AdminUI.zonesMessageRecieved.setFlag(true);
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

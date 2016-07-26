package com.dyn.server.packets.client;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.dyn.admin.AdminUI;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class ZonePermissionsMessage extends AbstractClientMessage<ZonePermissionsMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public ZonePermissionsMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public ZonePermissionsMessage(List<String> perms) {
		for (String s : perms) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient()) {
			AdminUI.permissions.clear();
			for (String s : data.split(Pattern.quote("|"))) {
				AdminUI.permissions.add(s);
			}
			AdminUI.permissionsMessageRecieved.setFlag(true);
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

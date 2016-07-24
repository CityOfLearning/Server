package com.dyn.server.packets.client;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.dyn.admin.AdminUI;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class GroupNamesMessage extends AbstractClientMessage<GroupNamesMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public GroupNamesMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public GroupNamesMessage(List<String> groups) {
		for (String s : groups) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient()) {
			AdminUI.groups.clear();
			for (String s : data.split(Pattern.quote("|"))) {
				AdminUI.groups.add(s);
			}
			AdminUI.groupsMessageRecieved.setFlag(true);
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

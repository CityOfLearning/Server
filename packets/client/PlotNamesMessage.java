package com.dyn.server.packets.client;

import java.io.IOException;
import java.util.List;

import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;
import com.dyn.student.StudentUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class PlotNamesMessage extends AbstractClientMessage<PlotNamesMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public PlotNamesMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public PlotNamesMessage(List<String> achData) {
		for (String s : achData) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient()) {
			StudentUI.plots.clear();
			for (String s : data.split("[|]")) {// splits the achievements
				StudentUI.plots.add(s);
			}
			StudentUI.needsRefresh.setFlag(true);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// this could potentially be a giant file...
		data = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(data);
	}
}

package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

import com.dyn.render.RenderMod;
import com.dyn.server.ServerMod;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.commons.selections.AreaBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class WorldZoneAreasMessage extends AbstractClientMessage<WorldZoneAreasMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public WorldZoneAreasMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public WorldZoneAreasMessage(Collection<AreaZone> zones) {
		for (AreaZone zone : zones) {
			data += (zone.getName() + "^" + zone.getArea().toString() + "|").replace(" ", "");
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient() && !data.isEmpty()) {
			ServerMod.proxy.addScheduledTask(() -> {
			RenderMod.zoneAreas.clear();
			for (String s : data.split(Pattern.quote("|"))) {
				String[] subStr = s.split(Pattern.quote("^"));
				RenderMod.zoneAreas.put(subStr[0], AreaBase.fromString(subStr[1]));
			}
			RenderMod.zoneAreasMessageRecieved.setFlag(true);
			});
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

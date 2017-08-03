package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.Collection;

import com.dyn.render.RenderMod;
import com.dyn.server.ServerMod;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class WorldZoneAreasMessage extends AbstractClientMessage<WorldZoneAreasMessage> {

	// the info needed to increment a requirement
	private NBTTagCompound data;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public WorldZoneAreasMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public WorldZoneAreasMessage(Collection<AreaZone> zones) {
		data = new NBTTagCompound();
		data.setInteger("length", zones.size());
		int index = 0;
		for (AreaZone zone : zones) {
			NBTTagCompound area = new NBTTagCompound();
			area.setString("name", zone.getName());
			area.setLong("p1", zone.getArea().getHighPoint().getBlockPos().toLong());
			area.setLong("p2", zone.getArea().getLowPoint().getBlockPos().toLong());
			data.setTag("" + index, area);
			index++;
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient() && data.hasKey("length") && (data.getInteger("length") > 0)) {
			ServerMod.proxy.addScheduledTask(() -> {
				int length = data.getInteger("length");
				RenderMod.zoneAreas.clear();
				for (int i = 0; i < length; i++) {
					String name = data.getCompoundTag("" + i).getString("name");
					BlockPos p1 = BlockPos.fromLong(data.getCompoundTag("" + i).getLong("p1"));
					BlockPos p2 = BlockPos.fromLong(data.getCompoundTag("" + i).getLong("p2"));
					RenderMod.zoneAreas.put(name, new AreaBase(new Point(p1), new Point(p2)));
				}
				RenderMod.zoneAreasMessageRecieved.setFlag(true);
			});
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		data = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeNBTTagCompoundToBuffer(data);
	}
}

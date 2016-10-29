package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.network.NetworkDispatcher;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.WorldZonesMessage;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestWorldZonesMessage extends AbstractServerMessage<RequestWorldZonesMessage> {

	String worldName;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestWorldZonesMessage() {
	}

	public RequestWorldZonesMessage(String world) {
		worldName = world;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {

			List<String> zones = new ArrayList<String>();

			Multiworld multiworld = ModuleMultiworld.getMultiworldManager().getMultiworld(worldName);
			WorldServer world = multiworld != null ? multiworld.getWorldServer()
					: APIRegistry.namedWorldHandler.getWorld(worldName);
			for (AreaZone zone : APIRegistry.perms.getServerZone().getWorldZone(world.provider.getDimensionId())
					.getAreaZones()) {
				zones.add(zone.getId() + "^" + zone.getName());
			}

			NetworkDispatcher.sendTo(new WorldZonesMessage(zones), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		worldName = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(worldName);
	}
}

package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.WorldZoneAreasMessage;
import com.dyn.server.network.packets.client.WorldZonesMessage;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestWorldZonesMessage extends AbstractServerMessage<RequestWorldZonesMessage> {

	boolean grabArea;
	int dimId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestWorldZonesMessage() {
	}

	public RequestWorldZonesMessage(int dimId, boolean grabArea) {
		this.dimId = dimId;
		this.grabArea = grabArea;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			Multiworld multiworld = ModuleMultiworld.getMultiworldManager().getMultiworld(dimId);

			WorldServer world = multiworld != null ? multiworld.getWorldServer()
					: APIRegistry.namedWorldHandler.getWorld(APIRegistry.namedWorldHandler.getWorldName(dimId));

			if (world != null) {
				if (!grabArea) {
					NetworkManager.sendTo(
							new WorldZonesMessage(APIRegistry.perms.getServerZone().getWorldZone(dimId).getAreaZones()),
							(EntityPlayerMP) player);
				} else {
					NetworkManager.sendTo(
							new WorldZoneAreasMessage(
									APIRegistry.perms.getServerZone().getWorldZone(dimId).getAreaZones()),
							(EntityPlayerMP) player);
				}
			} else {
				DYNServerMod.logger.error("Could not find world");
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		grabArea = buffer.readBoolean();
		dimId = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(grabArea);
		buffer.writeInt(dimId);
	}
}

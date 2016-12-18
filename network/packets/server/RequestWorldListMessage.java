package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.WorldNamesMessage;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestWorldListMessage extends AbstractServerMessage<RequestWorldListMessage> {

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestWorldListMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {

			List<String> worlds = new ArrayList<>();

			for (Multiworld world : ModuleMultiworld.getMultiworldManager().getWorlds()) {
				worlds.add(world.getDimensionId() + ": " + world.getName());
			}

			NetworkManager.sendTo(new WorldNamesMessage(worlds), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

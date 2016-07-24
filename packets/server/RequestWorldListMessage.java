package com.dyn.server.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.WorldNamesMessage;
import com.forgeessentials.api.APIRegistry;

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

			List<String> worlds = new ArrayList<String>();

			for (String world : APIRegistry.namedWorldHandler.getWorldNames()) {
				worlds.add(world);
			}

			PacketDispatcher.sendTo(new WorldNamesMessage(worlds), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

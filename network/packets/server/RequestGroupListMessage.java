package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.GroupNamesMessage;
import com.forgeessentials.api.APIRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestGroupListMessage extends AbstractServerMessage<RequestGroupListMessage> {

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestGroupListMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {

			List<String> groups = new ArrayList<>();

			for (String group : APIRegistry.perms.getServerZone().getGroups()) {
				groups.add(group);
			}

			NetworkManager.sendTo(new GroupNamesMessage(groups), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

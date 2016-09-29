package com.dyn.server.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.WarpNamesMessage;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.teleport.CommandWarp.Warp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestWarpListMessage extends AbstractServerMessage<RequestWarpListMessage> {

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestWarpListMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {

			List<String> warps = new ArrayList<String>();

			for (String warp : DataManager.getInstance().loadAll(Warp.class).keySet()) {
				warps.add(warp);
			}

			PacketDispatcher.sendTo(new WarpNamesMessage(warps), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

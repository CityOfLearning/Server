package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.network.NetworkDispatcher;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.ServerUserlistMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserlistMessage extends AbstractServerMessage<RequestUserlistMessage> {

	// this has no data since its a request

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserlistMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			NetworkDispatcher.sendTo(new ServerUserlistMessage(ServerMod.proxy.getServerUserlist()),
					(EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

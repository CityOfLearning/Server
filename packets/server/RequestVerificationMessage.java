package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.names.manager.NamesManager;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.CheckDynUsernameMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestVerificationMessage extends AbstractServerMessage<RequestVerificationMessage> {

	// this has no data since its a request

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestVerificationMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
//			PacketDispatcher.sendTo(
//					new CheckDynUsernameMessage(NamesManager.getDYNUsername(player.getName()),
//							DYNServerMod.frozenPlayers.contains(player.getDisplayNameString())),
//					(EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

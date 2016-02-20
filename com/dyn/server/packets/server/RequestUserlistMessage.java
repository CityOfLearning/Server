package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.TeacherSettingsMessage;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.relauncher.Side;

public class RequestUserlistMessage extends AbstractServerMessage<RequestUserlistMessage> {

	//this has no data since its a request
	
	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserlistMessage() {
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if(side.isServer()){
			PacketDispatcher.sendTo(new TeacherSettingsMessage(ServerMod.proxy.getServerUserlist(), true),
					(EntityPlayerMP) player);	
		}
	}
}

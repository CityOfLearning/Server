package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.PlayerStatusMessage;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.PlayerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserStatusMessage extends AbstractServerMessage<RequestUserStatusMessage> {

	private String username;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserStatusMessage() {
	}

	public RequestUserStatusMessage(String username) {
		this.username = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			EntityPlayerMP student = UserIdent.getPlayerByUsername(username);
			if (student != null) {
				PacketDispatcher.sendTo(new PlayerStatusMessage(DYNServerMod.frozenPlayers.contains(username),
						PlayerUtil.getPersistedTag(student, true).getBoolean("mute"),
						student.capabilities.isCreativeMode), (EntityPlayerMP) player);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		username = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(username);
	}
}

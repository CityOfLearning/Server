package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.teleport.CommandWarp.Warp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class WarpMentorMessage extends AbstractServerMessage<WarpMentorMessage> {

	private String warp_point;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public WarpMentorMessage() {
	}

	public WarpMentorMessage(String username) {
		warp_point = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			WarpPoint point = DataManager.getInstance().loadAll(Warp.class).get(warp_point);
			TeleportHelper.doTeleport((EntityPlayerMP) player, point);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		warp_point = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(warp_point);
	}
}

package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.achievements.achievement.RequirementType;
import com.dyn.server.ServerMod;
import com.dyn.server.network.NetworkDispatcher;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.SyncAchievementsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class MentorGivingAchievementMessage extends AbstractServerMessage<MentorGivingAchievementMessage> {
	private String player_name;
	private int ach_id;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public MentorGivingAchievementMessage() {
	}

	// if there are any class fields, be sure to provide a constructor that
	// allows
	// for them to be initialized, and use that constructor when sending the
	// packet
	public MentorGivingAchievementMessage(String username, int id) {
		player_name = username;
		ach_id = id;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			for (EntityPlayerMP p : ServerMod.proxy.getServerUsers()) {
				if (p.getDisplayNameString().equals(player_name)) {
					NetworkDispatcher.sendTo(
							new SyncAchievementsMessage("" + ach_id + " " + RequirementType.MENTOR + " 0", true), p);
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		player_name = buffer.readStringFromBuffer(buffer.readableBytes());
		ach_id = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(player_name);
		buffer.writeInt(ach_id);
	}
}

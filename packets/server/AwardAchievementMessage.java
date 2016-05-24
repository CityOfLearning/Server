package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.login.LoginGUI;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class AwardAchievementMessage extends AbstractServerMessage<AwardAchievementMessage> {
	private int id;
	private String uuid;
	private String player_name;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public AwardAchievementMessage() {
	}

	// if there are any class fields, be sure to provide a constructor that
	// allows
	// for them to be initialized, and use that constructor when sending the
	// packet
	public AwardAchievementMessage(int id, String uuid) {
		this.id = id;
		this.uuid = uuid;
		player_name = "";
	}

	public AwardAchievementMessage(int id, String uuid, String username) {
		this.id = id;
		this.uuid = uuid;
		player_name = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			LoginGUI.DYN_Username = uuid; // the UI is client side so we
											// set this each time server
											// side when awarding an
											// achievement
			if (player_name.isEmpty()) {
				AchievementManager.findAchievementById(id).awardAchievement(player);
			} else {
				for (EntityPlayerMP p : ServerMod.proxy.getServerUsers()) {
					if (p.getDisplayNameString().equals(player_name)) {
						AchievementManager.findAchievementById(id).awardAchievement(p);
					}
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		id = buffer.readInt();
		uuid = buffer.readStringFromBuffer(buffer.readableBytes());
		player_name = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeInt(id);
		buffer.writeString(uuid);
		buffer.writeString(player_name);
	}
}

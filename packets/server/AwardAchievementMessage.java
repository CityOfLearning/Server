package com.dyn.server.packets.server;

import java.io.IOException;
import java.util.UUID;

import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class AwardAchievementMessage extends AbstractServerMessage<AwardAchievementMessage> {
	private int id;
	private UUID fake_uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private UUID ccol_uuid;
	private String player_name;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public AwardAchievementMessage() {
	}

	// if there are any class fields, be sure to provide a constructor that
	// allows
	// for them to be initialized, and use that constructor when sending the
	// packet

	public AwardAchievementMessage(int id, UUID uuid, String username) {
		this.id = id;
		if (uuid != null) {
			ccol_uuid = uuid;
		} else {
			ccol_uuid = fake_uuid;
		}
		player_name = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			if (ccol_uuid != fake_uuid) {
				AchievementManager.findAchievementById(id).awardAchievement(player, ccol_uuid);
			} else {
				AchievementManager.findAchievementById(id).awardAchievement(player, null);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		player_name = buffer.readStringFromBuffer(buffer.readableBytes());
		id = buffer.readInt();
		ccol_uuid = buffer.readUuid();

	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeInt(id);
		buffer.writeUuid(ccol_uuid);
		buffer.writeString(player_name);
	}
}

package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.achievements.handlers.AchievementHandler;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;

public class HaveServerWriteAchievementsMessage extends AbstractServerMessage<HaveServerWriteAchievementsMessage> {

	// this has no data since its a request

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public HaveServerWriteAchievementsMessage() {
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			AchievementHandler.writeOutPlayerAchievements();
			player.addChatMessage(new ChatComponentText("Finished Writing out Achievements Files"));
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
	}
}

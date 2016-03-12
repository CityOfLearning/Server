package com.dyn.server.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dyn.achievements.achievement.AchievementType;
import com.dyn.achievements.achievement.Requirements;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementHandler;
import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.AchievementProgressMessage;
import com.dyn.server.packets.client.TeacherSettingsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserAchievementsProgressMessage
		extends AbstractServerMessage<RequestUserAchievementsProgressMessage> {

	private String username;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserAchievementsProgressMessage() {
	}

	public RequestUserAchievementsProgressMessage(String playerName) {
		this.username = playerName;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			//this method works but its extremely inefficent, we cant be sending huge data packets all the time
			//over the network we should probably just send the amount aquired
			//we already know the name of the achievement, all its requirements, and how many are needed, 
			//so we really just need the total aquired so far. We could even make a mock requirement set
			//on the client side to do all the mapping easier.
			
			
			/*Map<String, Requirements> achProgress = AchievementHandler.getPlayerAchievementProgress(this.username);
			if (achProgress != null) {
				List<String> achData = new ArrayList<String>();
				String achString = "";
				for (String achKeys : achProgress.keySet()) {
					achString += achKeys;
					// achData(" U: " +
					// configMan.getPlayerStatsFile(keys).hasAchievementUnlocked(findAchievementByName(achKeys)));
					Requirements reqs = achProgress.get(achKeys);
					boolean[] types = reqs.getRequirementTypes();
					for (int i = 0; i < 8; i++) {
						switch (i) {
						case 0:
							if (types[i]) {
								achString += "\tcraft";
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.CRAFT);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 1:
							if (types[i]) {
								achString += "\tsmelt";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.SMELT);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 2:
							if (types[i]) {
								achString += "\tpick_up";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.PICKUP);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 3:
							if (types[i]) {
								achString += "\tstat";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.STAT);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 4:
							if (types[i]) {
								achString += "\tkill";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.KILL);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 5:
							if (types[i]) {
								achString += "\tbrew";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.BREW);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 6:
							if (types[i]) {
								achString += "\tplace";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.PLACE);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 7:
							if (types[i]) {
								achString += "\tbreak";

								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(AchievementType.BREAK);
								for (BaseRequirement t : typeReq) {
									achString += "\nitem " + t.getRequirementEntityName();

									achString += "\namount " + t.getTotalNeeded();

									achString += "\ntotal " + t.getTotalAquired();

								}
							}
							break;
						case 8:
							if (types[i]) {
								reqs.getRequirementsByType(AchievementType.MENTOR);
								achString += "\tmentor";
							}
							break;
						default:
							break;
						}
					}
					achData.add(achString);
					achString = "";
				}

				PacketDispatcher.sendTo(new AchievementProgressMessage(achData), (EntityPlayerMP) player);
			}*/
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.username = buffer.readStringFromBuffer(100);
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(this.username);
	}
}

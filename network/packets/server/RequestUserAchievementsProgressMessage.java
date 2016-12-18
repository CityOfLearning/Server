package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dyn.achievements.achievement.RequirementType;
import com.dyn.achievements.achievement.Requirements;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.AchievementProgressMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.fml.relauncher.Side;

public class RequestUserAchievementsProgressMessage
		extends AbstractServerMessage<RequestUserAchievementsProgressMessage> {

	private String username;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestUserAchievementsProgressMessage() {
	}

	public RequestUserAchievementsProgressMessage(String playerName) {
		username = playerName;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			// we cant be sending huge data packets all the time
			// over the network we should probably just send the amount aquired
			// we already know the name of the achievement, all its
			// requirements, and how many are needed,
			// so we really just need the total aquired

			Map<String, Requirements> achProgress = AchievementManager.getPlayerAchievementProgress(username);
			if (achProgress != null) {
				List<String> achData = new ArrayList<>();
				ServerConfigurationManager configMan = MinecraftServer.getServer().getConfigurationManager();
				String achString = "";
				for (String achKeys : achProgress.keySet()) {
					achString += AchievementManager.findAchievementByName(achKeys).getId();
					achString += "^" + (configMan.getPlayerStatsFile(configMan.getPlayerByUsername(username))
							.hasAchievementUnlocked(AchievementManager.findAchievementByName(achKeys)) ? 1 : 0);
					achString += "?";// regex for the achievement info split and
										// requirements
					Requirements reqs = achProgress.get(achKeys);
					boolean[] types = reqs.getRequirementTypes();
					for (int i = 0; i < types.length; i++) {
						switch (i) {
						case 0:
							if (types[i]) {
								achString += "c"; // craft
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.CRAFT);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 1:
							if (types[i]) {
								achString += "s"; // smelt
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.SMELT);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 2:
							if (types[i]) {
								achString += "p"; // pickup
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.PICKUP);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 3:
							if (types[i]) {
								achString += "t"; // stat
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.STAT);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 4:
							if (types[i]) {
								achString += "k"; // kill
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.KILL);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 5:
							if (types[i]) {
								achString += "b"; // brew
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.BREW);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 6:
							if (types[i]) {
								achString += "e"; // place
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.PLACE);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 7:
							if (types[i]) {
								achString += "r"; // break
								ArrayList<BaseRequirement> typeReq = reqs.getRequirementsByType(RequirementType.BREAK);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						case 8:
							if (types[i]) { // mentor based achievements at this
											// point should only have 1
											// requirement
								reqs.getRequirementsByType(RequirementType.MENTOR);
								achString += "m";
								achString += "$";
							}
							break;
						case 9:
							if (types[i]) {
								achString += "l";
								ArrayList<BaseRequirement> typeReq = reqs
										.getRequirementsByType(RequirementType.LOCATION);
								for (BaseRequirement t : typeReq) {
									achString += t.getRequirementID();
									achString += "," + t.getTotalAquired();
									achString += "%"; // regex for each
														// requirement
								}
								achString += "$";
							}
							break;
						default:
							break;
						}
					}
					achData.add(achString);
					achString = "";
				}

				NetworkManager.sendTo(new AchievementProgressMessage(achData), (EntityPlayerMP) player);
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

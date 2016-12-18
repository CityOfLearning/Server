package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dyn.DYNServerMod;
import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.achievement.RequirementType;
import com.dyn.achievements.achievement.Requirements;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class AchievementProgressMessage extends AbstractClientMessage<AchievementProgressMessage> {

	// the info needed to increment a requirement
	private String data = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public AchievementProgressMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public AchievementProgressMessage(List<String> achData) {
		for (String s : achData) {
			data += s + "|";
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// we have to split a lot here, the pipe character is each achievement
		// tabs are titles and new lines are the items within each requirement
		// set
		if (side.isClient()) {
			DYNServerMod.userAchievementProgress.clear();
			for (String s : data.split("[|]")) {// splits the achievements
				String[] splitData = s.split("[?]");
				AchievementPlus achievement = AchievementManager
						.findAchievementById(Integer.parseInt(splitData[0].substring(0, splitData[0].indexOf('^'))));
				String achName = achievement.getName();
				if (splitData[0].substring(splitData[0].indexOf('^') + 1, splitData[0].length()).equals("1")) {
					// the achievement is unlocked no need to waste space
					DYNServerMod.userAchievementProgress.put(achName, null);
				} else { // we need to parse the requirements
					Requirements reqCopies = Requirements.getCopy(achievement.getRequirements());
					String[] reqData = splitData[1].split("[$]");
					List<BaseRequirement> baseCopies = new ArrayList<>();
					for (String req : reqData) {
						if (req.startsWith("c")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.CRAFT)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("s")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.SMELT)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("p")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.PICKUP)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("t")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.STAT)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("k")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.KILL)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("b")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.BREW)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("e")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.PLACE)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("r")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.BREAK)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						} else if (req.startsWith("m")) {
							// mentor achievements are special cases since they
							// should only ever occur once
							// per achievement and should always be by
							// themselves but just incase...
							baseCopies.addAll(reqCopies.getRequirementsByType(RequirementType.MENTOR));
						} else if (req.startsWith("l")) {
							req = req.substring(1, req.length());
							for (String sReq : req.split("[%]")) {
								for (BaseRequirement copy : reqCopies.getRequirementsByType(RequirementType.LOCATION)) {
									if (copy.getRequirementID() == Integer.parseInt("" + sReq.charAt(0))) {
										copy.setAquiredTo(Integer.parseInt(sReq.split("[,]")[1]));
										baseCopies.add(copy);
									}
								}
							}
						}
					}
					Requirements r = new Requirements();
					for (BaseRequirement br : baseCopies) {
						r.addRequirement(br);
					}
					DYNServerMod.userAchievementProgress.put(achName, r);
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// this could potentially be a giant file...
		data = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(data);
	}
}

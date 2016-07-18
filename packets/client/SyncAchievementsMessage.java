package com.dyn.server.packets.client;

import java.io.IOException;

import com.derimagia.forgeslack.slack.SlackSender;
import com.dyn.achievements.AchievementsMod;
import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.achievement.RequirementType;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.login.LoginGUI;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.AwardAchievementMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class SyncAchievementsMessage extends AbstractClientMessage<SyncAchievementsMessage> {

	// the info needed to increment a requirement
	private String data;
	private boolean mentorAwarded;

	// this packet should only be sent when a player is in the right dimension
	// so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public SyncAchievementsMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public SyncAchievementsMessage(String s) {
		data = s;
		mentorAwarded = false;
	}

	public SyncAchievementsMessage(String s, boolean b) {
		data = s;
		mentorAwarded = b;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {

			String[] values = data.split(" ");
			String description = "You ";
			int ach_id = Integer.parseInt(values[0]);
			RequirementType type = null;
			if (values[1].equals("CRAFT")) {
				type = RequirementType.CRAFT;
				description += "Crafted ";
			} else if (values[1].equals("SMELT")) {
				type = RequirementType.SMELT;
				description += "Smelted ";
			} else if (values[1].equals("PICKUP")) {
				type = RequirementType.PICKUP;
				description += "Picked up ";
			} else if (values[1].equals("KILL")) {
				type = RequirementType.KILL;
				description += "Killed ";
			} else if (values[1].equals("BREW")) {
				type = RequirementType.BREW;
				description += "Brewed ";
			} else if (values[1].equals("STAT")) {
				type = RequirementType.STAT;
			} else if (values[1].equals("PLACE")) {
				type = RequirementType.PLACE;
				description += "Placed ";
			} else if (values[1].equals("BREAK")) {
				type = RequirementType.BREAK;
				description += "Broke ";
			} else if (values[1].equals("MENTOR")) {
				type = RequirementType.MENTOR;
			} else if (values[1].equals("LOCATION")) {
				type = RequirementType.LOCATION;
				description += "Found ";
			}
			int req_id = Integer.parseInt(values[2]);

			AchievementPlus a = AchievementManager.findAchievementById(ach_id);
			if (!a.isAwarded()) {
				if (!mentorAwarded) {
					if (!a.hasParent()) {
						for (BaseRequirement r : a.getRequirements().getRequirementsByType(type)) {
							if (r.getRequirementID() == req_id) {
								if (r.getTotalAquired() < r.getTotalNeeded()) {
									r.incrementTotal();
									if (r.getTotalAquired() == r.getTotalNeeded()) {
										if (type != RequirementType.LOCATION) {
											description += r.getTotalNeeded() + " ";
										}
										description += r.getRequirementEntityName();
										AchievementsMod.proxy.getNotificationsManager()
												.addNotification("Requirement Met:", description);
									}
								}
							}
						}
						if (a.meetsRequirements()) {
							PacketDispatcher
									.sendToServer(new AwardAchievementMessage(a.getId(), LoginGUI.DYN_Username));
							a.setAwarded();
						}
					} else if (a.getParent().isAwarded()) {
						for (BaseRequirement r : a.getRequirements().getRequirementsByType(type)) {
							if (r.getRequirementID() == req_id) {
								if (r.getTotalAquired() < r.getTotalNeeded()) {
									r.incrementTotal();
									if (r.getTotalAquired() == r.getTotalNeeded()) {
										if (type != RequirementType.LOCATION) {
											description += r.getTotalNeeded() + " ";
										}
										description += r.getRequirementEntityName();
										AchievementsMod.proxy.getNotificationsManager()
												.addNotification("Requirement Met:", description);
									}
								}
							}
						}
						if (a.meetsRequirements()) {
							PacketDispatcher
									.sendToServer(new AwardAchievementMessage(a.getId(), LoginGUI.DYN_Username));
							a.setAwarded();
						}
					}
				} else {
					// System.out.println("Awarding Mentor Badge to " +
					// LoginGUI.DYN_Username);
					PacketDispatcher.sendToServer(new AwardAchievementMessage(a.getId(), LoginGUI.DYN_Username));
					a.setAwarded();
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		data = buffer.readStringFromBuffer(buffer.readableBytes());
		mentorAwarded = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(data);
		buffer.writeBoolean(mentorAwarded);
	}
}

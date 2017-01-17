package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.ZonePermissionsMessage;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.permissions.ModulePermissions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;

public class RequestZonePermissionsMessage extends AbstractServerMessage<RequestZonePermissionsMessage> {

	int zoneId;
	String groupName;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestZonePermissionsMessage() {
	}

	public RequestZonePermissionsMessage(int zone) {
		this(zone, "_ALL_");
	}

	public RequestZonePermissionsMessage(int zone, String group) {
		zoneId = zone;
		groupName = group;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			Zone zone = ModulePermissions.permissionHelper.getZoneById(zoneId);
			List<String> permissions = new ArrayList<>();
			if (zone instanceof AreaZone) {
				Zone rZone = zone.getParent();
				while (!(rZone instanceof WorldZone) && !(rZone instanceof ServerZone)) {
					rZone = rZone.getParent();
				}
				if (rZone instanceof WorldZone) {
					List<Zone> zones = APIRegistry.perms.getServerZone().getZonesAt(new WorldPoint(
							((WorldZone) rZone).getDimensionID(), ((AreaZone) zone).getArea().getCenter()));
					Collections.reverse(zones);
					for (Zone subZone : zones) {
						if (!(subZone instanceof RootZone) && !(subZone instanceof ServerZone)) {
							permissions.add(EnumChatFormatting.YELLOW + "Zone #" + subZone.getId()
									+ EnumChatFormatting.GOLD + " " + subZone.toString());
							if ((groupName != "_ALL_") && (subZone.getGroupPermissions(groupName) != null)) {
								permissions.add(EnumChatFormatting.YELLOW + "Group: " + EnumChatFormatting.DARK_AQUA
										+ " " + groupName);
								for (Entry<String, String> perm : subZone.getGroupPermissions(groupName).entrySet()) {
									permissions.add(
											EnumChatFormatting.LIGHT_PURPLE + perm.getKey() + EnumChatFormatting.RESET
													+ " = " + EnumChatFormatting.GREEN + perm.getValue());
								}
							} else {
								for (Entry<UserIdent, PermissionList> permList : subZone.getPlayerPermissions()
										.entrySet()) {
									permissions.add(EnumChatFormatting.YELLOW + "Player: "
											+ EnumChatFormatting.DARK_AQUA + " " + permList.getKey().getUsername());
									for (Entry<String, String> perm : permList.getValue().entrySet()) {
										permissions.add(EnumChatFormatting.LIGHT_PURPLE + perm.getKey()
												+ EnumChatFormatting.RESET + " = " + EnumChatFormatting.GREEN
												+ perm.getValue());
									}
								}
								for (Entry<String, PermissionList> permList : subZone.getGroupPermissions()
										.entrySet()) {
									permissions.add(EnumChatFormatting.YELLOW + "Group: " + EnumChatFormatting.DARK_AQUA
											+ " " + permList.getKey());
									for (Entry<String, String> perm : permList.getValue().entrySet()) {
										permissions.add(EnumChatFormatting.LIGHT_PURPLE + perm.getKey()
												+ EnumChatFormatting.RESET + " = " + EnumChatFormatting.GREEN
												+ perm.getValue());
									}
								}
							}
						}
					}
					NetworkManager.sendTo(new ZonePermissionsMessage(permissions), (EntityPlayerMP) player);
				} else {
					DYNServerMod.logger.error("Sub-Zone is not of correct type " + rZone);
				}
			} else {
				DYNServerMod.logger.error("Zone is not of correct type " + zone);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		groupName = buffer.readStringFromBuffer(buffer.readableBytes());
		zoneId = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(groupName);
		buffer.writeInt(zoneId);
	}
}

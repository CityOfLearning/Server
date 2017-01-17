package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.GroupPermissionsMessage;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.permissions.ModulePermissions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;

public class RequestGroupPermissionsMessage extends AbstractServerMessage<RequestGroupPermissionsMessage> {

	String groupName;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestGroupPermissionsMessage() {
	}

	public RequestGroupPermissionsMessage(String group) {
		groupName = group;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {

			List<String> permissions = new ArrayList<>();

			Map<Zone, Map<String, String>> groupPerms = ModulePermissions.permissionHelper
					.enumGroupPermissions(groupName, false);
			if (!groupPerms.isEmpty()) {
				for (Entry<Zone, Map<String, String>> zone : groupPerms.entrySet()) {
					permissions.add(EnumChatFormatting.YELLOW + "Zone #" + zone.getKey().getId()
							+ EnumChatFormatting.GOLD + " " + zone.getKey().toString());
					for (Entry<String, String> perm : zone.getValue().entrySet()) {
						if (perm.getKey().equals(FEPermissions.GROUP)
								|| perm.getKey().equals(FEPermissions.GROUP_PRIORITY)
								|| perm.getKey().equals(FEPermissions.PREFIX)
								|| perm.getKey().equals(FEPermissions.SUFFIX)) {
							continue;
						}
						permissions.add(EnumChatFormatting.LIGHT_PURPLE + perm.getKey() + EnumChatFormatting.RESET
								+ " = " + EnumChatFormatting.GREEN + perm.getValue());
					}
				}
			}
			NetworkManager.sendTo(new GroupPermissionsMessage(permissions), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		groupName = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(groupName);
	}
}

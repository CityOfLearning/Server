package com.dyn.server.network.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.dyn.DYNServerMod;
import com.dyn.server.network.NetworkDispatcher;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.network.packets.client.ZonePermissionsMessage;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.permissions.ModulePermissions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestZonePermissionsMessage extends AbstractServerMessage<RequestZonePermissionsMessage> {

	int zoneName;
	String groupName;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestZonePermissionsMessage() {
	}

	public RequestZonePermissionsMessage(int zone) {
		this(zone, "_ALL_");
	}

	public RequestZonePermissionsMessage(int zone, String group) {
		zoneName = zone;
		groupName = group;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			Zone zone = ModulePermissions.permissionHelper.getZoneById(zoneName);
			List<String> permissions = new ArrayList<String>();
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
							permissions.add("\u00a7eZone #" + subZone.getId() + "\u00a76 " + subZone.toString());
							if (subZone.getGroupPermissions(groupName) != null) {
								for (Entry<String, String> perm : subZone.getGroupPermissions(groupName).entrySet()) {
									permissions.add("\u00a7d" + perm.getKey() + "\u00a7r = \u00a7a" + perm.getValue());
								}
							}
						}
					}
				}
				for (String perms : permissions) {
					DYNServerMod.logger.info(perms);
				}
				NetworkDispatcher.sendTo(new ZonePermissionsMessage(permissions), (EntityPlayerMP) player);
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		groupName = buffer.readStringFromBuffer(buffer.readableBytes());
		zoneName = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(zoneName);
		buffer.writeString(groupName);
	}
}

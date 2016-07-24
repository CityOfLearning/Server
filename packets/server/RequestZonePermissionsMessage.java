package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.ZonePermissionsMessage;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
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

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestZonePermissionsMessage() {
	}

	public RequestZonePermissionsMessage(int zone) {
		zoneName = zone;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
//			APIRegistry.perms.getServerZone().getZonesAt(.toWorldPoint());
			Zone zone = ModulePermissions.permissionHelper.getZoneById(zoneName);
			if(zone instanceof AreaZone){
//				APIRegistry.perms.getServerZone().getZonesAt(new WorldPoint(zone.getParent().((AreaZone)zone).getArea().getCenter());
			}
			
			PacketDispatcher.sendTo(
					new ZonePermissionsMessage(
							ModulePermissions.permissionHelper.getZoneById(zoneName).enumRegisteredPermissions()),
					(EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		zoneName = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(zoneName);
	}
}

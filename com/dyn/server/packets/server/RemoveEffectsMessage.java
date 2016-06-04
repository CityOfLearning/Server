package com.dyn.server.packets.server;

import java.io.IOException;

import com.dyn.server.ServerMod;
import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RemoveEffectsMessage extends AbstractServerMessage<RemoveEffectsMessage> {

	private String player_name;

	// The basic, no-argument constructor MUST be included to use the new
	// automated handling
	public RemoveEffectsMessage() {
	}

	public RemoveEffectsMessage(String username) {
		player_name = username;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		// using the message instance gives access to 'this.id'
		if (side.isServer()) {
			for (EntityPlayerMP p : ServerMod.proxy.getServerUsers()) {
				if (p.getDisplayNameString().equals(player_name)) {
					p.curePotionEffects(new ItemStack(new ItemBucketMilk()));
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataInputStream
		player_name = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		// basic Input/Output operations, very much like DataOutputStream
		buffer.writeString(player_name);
	}
}

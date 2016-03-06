package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class ReturnFlagMessage extends AbstractClientMessage<ReturnFlagMessage> {

	// the info needed to increment a requirement
	private ItemStack data;

	// this packet should only be sent when a player is in the right dimension
	// so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public ReturnFlagMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public ReturnFlagMessage(ItemStack is) {
		this.data = is;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			if (!player.inventory.hasItem(this.data.getItem())) {
				player.inventory.addItemStackToInventory(this.data);
				/*
				 * for(int i=0;i<player.inventory.getSizeInventory();i++){
				 * if(player.inventory.getStackInSlot(i) == null){
				 * player.inventory.setInventorySlotContents(i, data); break; }
				 * }
				 */
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		this.data = buffer.readItemStackFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeItemStackToBuffer(this.data);
	}
}

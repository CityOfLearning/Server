package com.dyn.server.packets.client;

import java.io.IOException;
import com.dyn.achievements.achievement.AchievementPlus;
import com.dyn.achievements.achievement.AchievementType;
import com.dyn.achievements.achievement.Requirements.BaseRequirement;
import com.dyn.achievements.handlers.AchievementHandler;
import com.dyn.login.LoginGUI;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.server.AwardAchievementMessage;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.relauncher.Side;

public class ReturnFlagMessage extends AbstractClientMessage<ReturnFlagMessage> {

	// the info needed to increment a requirement
	private ItemStack data;
	
	//this packet should only be sent when a player is in the right dimension so we shouldnt have to check for it ever

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public ReturnFlagMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public ReturnFlagMessage(ItemStack is) {
		data = is;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		data = buffer.readItemStackFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeItemStackToBuffer(data);
	}

	@Override
	public void process(EntityPlayer player, Side side) {	
		if (side.isClient()) {
			if(!player.inventory.hasItem(data.getItem())){
				player.inventory.addItemStackToInventory(data);
				/*for(int i=0;i<player.inventory.getSizeInventory();i++){
					if(player.inventory.getStackInSlot(i) == null){
						player.inventory.setInventorySlotContents(i, data);
						break;
					}
				}*/
			}			
		}
	}
}

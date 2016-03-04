package com.dyn.server.proxy;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public interface Proxy {
        public void renderGUI();
        
        /**
    	 * Returns a side-appropriate EntityPlayer for use during message handling
    	 */
    	public EntityPlayer getPlayerEntity(MessageContext ctx);
    	
    	public void init();
    	
    	public String[] getServerUserlist();
    	
    	public List<EntityPlayerMP> getServerUsers();
}
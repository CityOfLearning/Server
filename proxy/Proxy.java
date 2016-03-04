package com.dyn.server.proxy;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;

public interface Proxy {
        public void renderGUI();
        
        /**
    	 * Returns a side-appropriate EntityPlayer for use during message handling
    	 */
    	public EntityPlayer getPlayerEntity(MessageContext ctx);
    	
    	public void init();
    	
    	public String[] getServerUserlist();
    	
    	public List<EntityPlayerMP> getServerUsers();
    	
    	public int getOpLevel(GameProfile profile);
    	
    	public IThreadListener getThreadFromContext(MessageContext ctx);
}
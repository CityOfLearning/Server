package com.dyn.server.proxy;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;

public class Client implements Proxy {

        /**
         * @see forge.reference.proxy.Proxy#renderGUI()
         */
        @Override
        public void renderGUI() {
                // Render GUI when on call from client
        } 
        
        @Override
    	public EntityPlayer getPlayerEntity(MessageContext ctx) {
    		// Note that if you simply return 'Minecraft.getMinecraft().thePlayer',
    		// your packets will not work as expected because you will be getting a
    		// client player even when you are on the server!
    		// Sounds absurd, but it's true.

    		// Solution is to double-check side before returning the player:
    		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : null);
    	}

		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String[] getServerUserlist() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<EntityPlayerMP> getServerUsers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getOpLevel(GameProfile profile) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public IThreadListener getThreadFromContext(MessageContext ctx) {
			return (ctx.side.isClient() ? Minecraft.getMinecraft() : null);
		}
}
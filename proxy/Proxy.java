package com.dyn.server.proxy;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface Proxy {

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx);

	public String[] getServerUserlist();

	public List<EntityPlayerMP> getServerUsers();

	public IThreadListener getThreadFromContext(MessageContext ctx);

	public void init();

	public void preInit();

	public void renderGUI();
}
package com.dyn.server.proxy;

import java.util.List;

import com.dyn.DYNServerMod;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Client implements Proxy, IMessageHandler<Packet1SelectionUpdate, IMessage> {

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
	public String[] getServerUserlist() {
		return null;
	}

	@Override
	public List<EntityPlayerMP> getServerUsers() {
		return null;
	}

	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		// this causes null pointers in single player...
		return (ctx.side.isClient() ? Minecraft.getMinecraft() : null);
	}

	@Override
	public void init() {

	}

	@Override
	public IMessage onMessage(Packet1SelectionUpdate message, MessageContext ctx) {
		DYNServerMod.selection = message.getSelection();
		return null;
	}

	@Override
	public void preInit() {

	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Render GUI when on call from client
	}
}
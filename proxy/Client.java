package com.dyn.server.proxy;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.dyn.server.ServerMod;
import com.dyn.server.database.DBManager;
import com.dyn.server.utils.PlayerLevel;
import com.dyn.student.StudentUI;
import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Client implements Proxy, IMessageHandler<Packet1SelectionUpdate, IMessage> {

	@SubscribeEvent
	public void connectionOpened(FMLNetworkEvent.ClientConnectedToServerEvent e) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				String playerStatus = DBManager
						.getPlayerStatus(Minecraft.getMinecraft().thePlayer.getDisplayNameString());

				if (playerStatus.contains("Admin")) {
					ServerMod.status = PlayerLevel.ADMIN;
				} else if (playerStatus.contains("Mentor")) {
					ServerMod.status = PlayerLevel.MENTOR;
				}
			}
		}, 6 * 1000);
	}

	@SubscribeEvent
	public void connectionOpened(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
		// let's close the client when we disconnect
		FMLCommonHandler.instance().exitJava(0, false);
	}

	@Override
	public int getOpLevel(GameProfile profile) {
		// TODO Auto-generated method stub
		return 0;
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
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft() : null);
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Render GUI when on call from client
	}

	@Override
	public IMessage onMessage(Packet1SelectionUpdate message, MessageContext ctx) {
		ServerMod.selection = message.getSelection();
		return null;
	}
}
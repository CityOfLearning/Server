package com.dyn.server.proxy;

import java.util.List;

import com.dyn.DYNServerMod;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.server.commands.CommandFreeze;
import com.dyn.server.database.DBManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.ServerUserlistMessage;
import com.dyn.utils.PlayerLevel;

import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Server implements Proxy {

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	@Override
	public String[] getServerUserlist() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public List<EntityPlayerMP> getServerUsers() {
		return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
	}

	/**
	 * Returns the current thread based on side during message handling, used
	 * for ensuring that the message is being handled by the main thread
	 */
	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity.getServerForPlayer();
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(new CommandFreeze());
	}

	@SubscribeEvent
	public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		String playerStatus = DBManager.getPlayerStatus(event.player.getDisplayNameString());
		PlayerLevel status = PlayerLevel.STUDENT;
		if (playerStatus.contains("Admin")) {
			status = PlayerLevel.ADMIN;
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group add _OPS_");
		} else if (playerStatus.contains("Mentor")) {
			status = PlayerLevel.MENTOR;
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group remove _STUDENTS_");
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group add _MENTORS_");
		} else {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group remove _MENTORS_");
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group add _STUDENTS_");
		}

		if (!DYNServerMod.frozenPlayers.contains(event.player)) {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getDisplayNameString() + " group remove _FROZEN_");
		}

		if (status == PlayerLevel.ADMIN) {
			PacketDispatcher.sendTo(new ServerUserlistMessage(MinecraftServer.getServer().getAllUsernames()),
					(EntityPlayerMP) event.player);
		}

		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
				String.format("/nick %s %s", event.player.getDisplayNameString(),
						DBManager.getDisplayNameFromMCUsername(event.player.getDisplayNameString())));

		// this has to do with verification but we are not doing that till later
		// PacketDispatcher.sendTo(
		// new
		// CheckDynUsernameMessage(NamesManager.getDYNUsername(event.player.getName()),
		// DYNServerMod.frozenPlayers.contains(event.player.getDisplayNameString())),
		// (EntityPlayerMP) event.player);
		AchievementManager.setupPlayerAchievements(event.player);
	}

	@Override
	public void preInit() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Actions on render GUI for the server (logging)

	}

}
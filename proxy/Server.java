package com.dyn.server.proxy;

import java.util.List;

import com.dyn.DYNServerMod;
import com.dyn.achievements.handlers.AchievementManager;
import com.dyn.robot.RobotMod;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.commands.CommandFreeze;
import com.dyn.server.database.DBManager;
import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.messages.RawErrorMessage;
import com.dyn.server.network.packets.client.ServerUserlistMessage;
import com.dyn.utils.CCOLPlayerInfo;
import com.dyn.utils.PlayerAccessLevel;

import mobi.omegacentauri.raspberryjammod.RaspberryJamMod;
import mobi.omegacentauri.raspberryjammod.network.CodeEvent;
import mobi.omegacentauri.raspberryjammod.network.CodeEvent.RobotErrorEvent;
import mobi.omegacentauri.raspberryjammod.network.SocketEvent;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Server implements Proxy {

	@Override
	public void addScheduledTask(Runnable runnable) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void chatEvent(ServerChatEvent event) {
		if (event.player != null) {
			event.setCanceled(true);
			// only send to players in the same dimension
			for (EntityPlayer player : event.player.worldObj.playerEntities) {
				player.addChatComponentMessage(event.getComponent());
			}
		}
	}

	@SubscribeEvent
	public void codeError(CodeEvent.ErrorEvent event) {
		if (event instanceof RobotErrorEvent) {
			EntityPlayer player = event.getPlayer();
			World world = player.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(((RobotErrorEvent) event).getEntityId());
			robot.stopExecutingCode();
		}
		NetworkManager.sendTo(new RawErrorMessage(event.getCode(), event.getError(), event.getLine()),
				(EntityPlayerMP) event.getPlayer());
	}

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
		RaspberryJamMod.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(this);
		((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(new CommandFreeze());
	}

	@SubscribeEvent
	public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		if (DYNServerMod.developmentEnvironment) {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/op " + event.player.getName());
		}

		String playerStatus = DBManager.getPlayerStatus(event.player.getName());
		PlayerAccessLevel status = PlayerAccessLevel.STUDENT;
		if (playerStatus.contains("Admin")) {
			status = PlayerAccessLevel.ADMIN;
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group add _OPS_");
		} else if (playerStatus.contains("Mentor")) {
			status = PlayerAccessLevel.MENTOR;
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group remove _STUDENTS_");
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group add _MENTORS_");
		} else {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group remove _MENTORS_");
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group add _STUDENTS_");
		}

		if (!DYNServerMod.frozenPlayers.contains(event.player)) {
			MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
					"/p user " + event.player.getName() + " group remove _FROZEN_");
		}

		if (status == PlayerAccessLevel.ADMIN) {
			NetworkManager.sendTo(new ServerUserlistMessage(MinecraftServer.getServer().getAllUsernames()),
					(EntityPlayerMP) event.player);
		}

		MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), String.format(
				"/nick %s %s", event.player.getName(), DBManager.getDisplayNameFromMCUsername(event.player.getName())));

		AchievementManager.setupPlayerAchievements(event.player);

		CCOLPlayerInfo ccolInfo = new CCOLPlayerInfo(event.player.getName());
		if ((ccolInfo != null) && (ccolInfo.getCCOLid() != null)) {
			if (!CCOLPlayerInfo.isReturningCcolUser(ccolInfo)) {
				CCOLPlayerInfo.writeCCOLInfoToJson(ccolInfo, event.player);
			} else {
				CCOLPlayerInfo.readCCOLInfo(ccolInfo, true);
			}
			DYNServerMod.playersCcolInfo.put(event.player, ccolInfo);
		} else {
			if (!CCOLPlayerInfo.isReturningPlayer(event.player)) {
				CCOLPlayerInfo.writePlayerInfoToJson(event.player);
			} else {
				CCOLPlayerInfo.readPlayerInfo(event.player, true);
			}
		}
	}

	@SubscribeEvent
	public void logoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		CCOLPlayerInfo.writeDataToJson(event.player, DYNServerMod.playersCcolInfo.remove(event.player));
	}

	// @SubscribeEvent
	// public void onZoneChange(PlayerChangedZone event) {
	//
	// }

	@Override
	public void preInit() {
		CCOLPlayerInfo.setCCOLDataFolder(MinecraftServer.getServer().getDataDirectory());
	}

	/**
	 * @see forge.reference.proxy.Proxy#renderGUI()
	 */
	@Override
	public void renderGUI() {
		// Actions on render GUI for the server (logging)

	}

	// should be fine from server side
	@SubscribeEvent
	public void socketClose(SocketEvent.Close event) {
		if (RobotMod.robotid2player.inverse().containsKey(event.getPlayer())) {
			World world = event.getPlayer().worldObj;
			EntityRobot robot = (EntityRobot) world
					.getEntityByID(RobotMod.robotid2player.inverse().get(event.getPlayer()));
			DYNServerMod.logger
					.info("Stop Executing Code from Socket Message for Player: " + event.getPlayer().getName());
			if (robot != null) {
				robot.stopExecutingCode();
			}
		}
	}

}
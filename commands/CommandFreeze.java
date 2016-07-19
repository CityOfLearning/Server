package com.dyn.server.commands;

import com.dyn.DYNServerMod;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.FreezePlayerMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandFreeze extends CommandBase {

	@Override
	public String getCommandName() {
		return "freeze";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/freeze add|remove [playerName]: Freeze or Thaw a player";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			if (args[0].equals("add")) {
				for(String arg: args){
					try {
						EntityPlayer entityplayer = getPlayer(sender, arg);
						DYNServerMod.frozenPlayers.add(entityplayer.getDisplayNameString());
						//its a little weird to have a command call a command but lets let FE handle its business
						MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
								"/p user " + entityplayer.getDisplayNameString() + " group add _FROZEN_");
						PacketDispatcher.sendTo(new FreezePlayerMessage(true),
								(EntityPlayerMP) entityplayer);
						entityplayer.capabilities.allowEdit = false;
						notifyOperators(sender, this, "You %s player %s",
								new Object[] { "froze", entityplayer.getDisplayNameString() });
					} catch (PlayerNotFoundException e){
						notifyOperators(sender, this, "Could not find player %s",
								new Object[] { arg });
					}
				}
				return;
			}

			if (args[0].equals("remove")) {
				for(String arg: args){
					try {
						EntityPlayer entityplayer = getPlayer(sender, arg);
						DYNServerMod.frozenPlayers.remove(entityplayer.getDisplayNameString());
						MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
								"/p user " + entityplayer.getDisplayNameString() + " group remove _FROZEN_");
						PacketDispatcher.sendTo(new FreezePlayerMessage(false),
								(EntityPlayerMP) entityplayer);
						entityplayer.capabilities.allowEdit = true;
						notifyOperators(sender, this, "You %s player %s",
								new Object[] { "unfroze", entityplayer.getDisplayNameString() });
					} catch (PlayerNotFoundException e){
						notifyOperators(sender, this, "Could not find player %s",
								new Object[] { arg });
					}
				}
				return;
			}
		}

		throw new WrongUsageException(getCommandUsage(sender), new Object[0]);

	}

}

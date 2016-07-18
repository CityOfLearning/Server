package com.dyn.server.commands;

import com.dyn.DYNServerMod;
import com.dyn.student.StudentUI;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

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
		if (args.length > 2) {
			EntityPlayer entityplayer = getPlayer(sender, args[1]);
			if (args[0].equals("add")) {
				DYNServerMod.frozenPlayers.add(entityplayer.getDisplayNameString());
				MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
						"/p user " + entityplayer.getDisplayNameString() + " group add _FROZEN_");
				entityplayer.addChatMessage(new ChatComponentText("You were unfrozen by the teacher"));
				StudentUI.frozen = false;
				entityplayer.capabilities.allowEdit = true;
				notifyOperators(sender, this, "You %s player %s",
						new Object[] { "froze", entityplayer.getDisplayNameString() });
				return;
			}

			if (args[0].equals("remove")) {
				DYNServerMod.frozenPlayers.remove(entityplayer.getDisplayNameString());
				MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(),
						"/p user " + entityplayer.getDisplayNameString() + " group remove _FROZEN_");
				entityplayer.addChatMessage(new ChatComponentText("You were unfrozen by the teacher"));
				StudentUI.frozen = false;
				entityplayer.capabilities.allowEdit = true;
				notifyOperators(sender, this, "You %s player %s",
						new Object[] { "unfroze", entityplayer.getDisplayNameString() });
				return;
			}
		}

		throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);

	}

}

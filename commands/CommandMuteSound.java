package com.dyn.server.commands;

import com.dyn.server.network.NetworkManager;
import com.dyn.server.network.packets.client.MutePlayerAudioMessage;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandMuteSound extends CommandBase {

	@Override
	public String getCommandName() {
		return "muteSound";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/muteSound [playerName]";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
			try {
				EntityPlayer entityplayer = getPlayer(sender, args[0]);

				NetworkManager.sendTo(new MutePlayerAudioMessage(true), (EntityPlayerMP) entityplayer);
			} catch (PlayerNotFoundException e) {
				throw new CommandException("Could not find player %s", new Object[] { args[0] });
			}
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender), new Object[0]);

	}

}

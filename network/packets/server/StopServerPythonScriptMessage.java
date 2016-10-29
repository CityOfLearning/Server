package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;

import mobi.omegacentauri.raspberryjammod.RaspberryJamMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;

public class StopServerPythonScriptMessage extends AbstractServerMessage<StopServerPythonScriptMessage> {

	private static boolean isProcessAlive(Process proc) {
		try {
			proc.exitValue();
			return false;
		} catch (Exception e) {
			return true;
		}

	}

	private String playerName;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public StopServerPythonScriptMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public StopServerPythonScriptMessage(String player) {
		playerName = player;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			EntityPlayerMP sPlayer = MinecraftServer.getServer().getConfigurationManager()
					.getPlayerByUsername(playerName);
			if (sPlayer != null) {
				if (RaspberryJamMod.playerProcesses.containsKey(playerName)) {
					Process pro = RaspberryJamMod.playerProcesses.get(playerName);
					if (isProcessAlive(pro)) {
						pro.destroy();
					}
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		playerName = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeString(playerName);
	}
}

package com.dyn.server.packets.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dyn.instructor.TeacherMod;
import com.dyn.login.LoginGUI;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class CheckDynUsernameMessage extends AbstractClientMessage<CheckDynUsernameMessage> {

	// the info needed to increment a requirement
	private boolean freeze = false;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public CheckDynUsernameMessage() {
	}

	public CheckDynUsernameMessage(boolean frozen) {
		freeze = frozen;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {

			TeacherMod.frozen = freeze;

			String lines = "";

			List<String> dyn_usernames = new ArrayList<String>();
			List<String> dyn_passwords = new ArrayList<String>();
			Map<String, String> minecraft_usernames = new HashMap<String, String>();
			Map<String, String> minecraft_passwords = new HashMap<String, String>();

			try {
				URL url = new URL("https://dl.dropboxusercontent.com/u/33377940/MinecraftAccounts.csv");

				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				while ((lines = br.readLine()) != null) {

					// use comma as separator
					String[] line = lines.split(",");

					if ((line.length > 4) && (line[0] != null) && !line[0].isEmpty()) {
						dyn_usernames.add(line[0]);
						dyn_passwords.add(line[1]);
						minecraft_usernames.put(line[0], line[4]);
						minecraft_passwords.put(line[0], line[3]);
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (minecraft_usernames.containsKey(player.getDisplayNameString())) {
				LoginGUI.DYN_Username = minecraft_usernames
						.get(Minecraft.getMinecraft().thePlayer.getDisplayNameString());
				LoginGUI.DYN_Password = minecraft_passwords
						.get(Minecraft.getMinecraft().thePlayer.getDisplayNameString());
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		freeze = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(freeze);
	}

}

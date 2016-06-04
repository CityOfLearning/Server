package com.dyn.server.packets.client;

import java.io.IOException;

import com.dyn.login.LoginGUI;
import com.dyn.server.packets.AbstractMessage.AbstractClientMessage;
import com.dyn.student.StudentUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class CheckDynUsernameMessage extends AbstractClientMessage<CheckDynUsernameMessage> {

	// the info needed to increment a requirement
	private boolean freeze = false;

	private String dynName = "";

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public CheckDynUsernameMessage() {
	}

	public CheckDynUsernameMessage(String dyn, boolean frozen) {
		freeze = frozen;
		if (dyn != null) {
			dynName = dyn;
		}
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {

			StudentUI.frozen = freeze;
			LoginGUI.needsVerification = false;
			// needed for fidelity checks later on
			/*
			 * if (LoginGUI.DYN_Username.isEmpty()) {// they must have logged in
			 * // already from the client, // ignore if ((dynName != null) &&
			 * !dynName.isEmpty()) { LoginGUI.DYN_Username = dynName; } else {
			 * // the player is not mapped on our end, freeze them and ask //
			 * them // to log in. LoginGUI.needsVerification = true;
			 * TeacherMod.frozen = true; LoginGUI.proxy.checkVerification(); } }
			 */
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		freeze = buffer.readBoolean();
		dynName = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(freeze);
		buffer.writeString(dynName);
	}

}

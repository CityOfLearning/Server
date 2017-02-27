package com.dyn.server.network.packets.server;

import java.io.IOException;

import com.dyn.DYNServerMod;
import com.dyn.fixins.blocks.cmdblock.StudentCommandBlockLogic;
import com.dyn.fixins.blocks.cmdblock.StudentCommandBlockTileEntity;
import com.dyn.server.network.packets.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;

public class StudentCommandBlockMessage extends AbstractServerMessage<StudentCommandBlockMessage> {

	boolean showOutput;
	String command;
	BlockPos pos;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public StudentCommandBlockMessage() {
	}

	public StudentCommandBlockMessage(boolean show, BlockPos bpos, String cmd) {
		pos = bpos;
		showOutput = show;
		command = cmd;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			if (!net.minecraft.server.MinecraftServer.getServer().isCommandBlockEnabled()) {
				player.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
			} else {
				try {

					TileEntity tileentity = player.worldObj.getTileEntity(pos);

					if (tileentity instanceof StudentCommandBlockTileEntity) {
						StudentCommandBlockLogic commandblocklogic = ((StudentCommandBlockTileEntity) tileentity)
								.getCommandBlockLogic();

						if (commandblocklogic != null) {
							commandblocklogic.setCommand(command);
							commandblocklogic.setTrackOutput(showOutput);

							if (!showOutput) {
								commandblocklogic.setLastOutput((IChatComponent) null);
							}

							commandblocklogic.updateCommand();
							player.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success",
									new Object[] { command }));
						}
					}
				} catch (Exception exception1) {
					DYNServerMod.logger.error("Couldn\'t set command block", exception1);
				}
			}
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		showOutput = buffer.readBoolean();
		command = buffer.readStringFromBuffer(buffer.readableBytes());
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(showOutput);
		buffer.writeString(command);
	}
}

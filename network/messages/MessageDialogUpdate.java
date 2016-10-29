package com.dyn.server.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageDialogUpdate implements IMessage {

	private BlockPos pos;
	private String text;
	private int blockX;
	private int blockY;
	private int blockZ;

	public MessageDialogUpdate() {
	}

	public MessageDialogUpdate(BlockPos pos, String text, int xradius, int yradius, int zradius) {
		this.pos = pos;
		this.text = text;
		blockX = xradius;
		blockY = yradius;
		blockZ = zradius;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		text = ByteBufUtils.readUTF8String(buf);
		blockX = buf.readInt();
		blockY = buf.readInt();
		blockZ = buf.readInt();
	}

	public BlockPos getPos() {
		return pos;
	}

	public String getText() {
		return text;
	}

	public int getXRadius() {
		return blockX;
	}

	public int getYRadius() {
		return blockY;
	}

	public int getZRadius() {
		return blockZ;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, text);
		buf.writeInt(blockX);
		buf.writeInt(blockY);
		buf.writeInt(blockZ);
	}
}

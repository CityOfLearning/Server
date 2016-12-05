package com.dyn.server.network.messages;

import com.dyn.fixins.blocks.redstone.timer.TimerBlockTileEntity;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTimerBlockUpdate implements IMessage {

	public static class Handler implements IMessageHandler<MessageTimerBlockUpdate, IMessage> {
		@Override
		public IMessage onMessage(final MessageTimerBlockUpdate message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				if (tileEntity instanceof TimerBlockTileEntity) {
					((TimerBlockTileEntity) tileEntity).setTimerTime(message.getTime());
					((TimerBlockTileEntity) tileEntity).markForUpdate();
				}
			});
			return null;
		}
	}

	private int time;
	private BlockPos pos;

	public MessageTimerBlockUpdate() {
	}

	public MessageTimerBlockUpdate(BlockPos pos, int time) {
		this.time = time;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		time = buf.readInt();
		pos = BlockPos.fromLong(buf.readLong());
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getTime() {
		return time;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(time);
		buf.writeLong(pos.toLong());
	}
}

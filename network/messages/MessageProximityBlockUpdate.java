package com.dyn.server.network.messages;

import com.dyn.fixins.blocks.redstone.proximity.ProximityBlockTileEntity;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageProximityBlockUpdate implements IMessage {

	public static class Handler implements IMessageHandler<MessageProximityBlockUpdate, IMessage> {
		@Override
		public IMessage onMessage(final MessageProximityBlockUpdate message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				if (tileEntity instanceof ProximityBlockTileEntity) {
					((ProximityBlockTileEntity) tileEntity).setCorners(message.getCorner1(), message.getCorner2());
					((ProximityBlockTileEntity) tileEntity).markForUpdate();
				}
			});
			return null;
		}
	}

	private BlockPos pos;
	private BlockPos corner1;
	private BlockPos corner2;

	public MessageProximityBlockUpdate() {
	}

	public MessageProximityBlockUpdate(BlockPos pos, BlockPos corner1, BlockPos corner2) {
		this.pos = pos;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		corner1 = BlockPos.fromLong(buf.readLong());
		corner2 = BlockPos.fromLong(buf.readLong());
	}

	public BlockPos getCorner1() {
		return corner1;
	}

	public BlockPos getCorner2() {
		return corner2;
	}

	public BlockPos getPos() {
		return pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeLong(corner1.toLong());
		buf.writeLong(corner2.toLong());
	}
}

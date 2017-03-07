package com.dyn.server.network.messages;

import com.dyn.fixins.blocks.decision.DecisionBlock;
import com.dyn.fixins.blocks.decision.DecisionBlockTileEntity;
import com.dyn.fixins.blocks.redstone.proximity.ProximityBlock;
import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBlockRedstoneSignalUpdate implements IMessage {

	public static class Handler implements IMessageHandler<MessageBlockRedstoneSignalUpdate, IMessage> {
		@Override
		public IMessage onMessage(final MessageBlockRedstoneSignalUpdate message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				if (tileEntity instanceof DecisionBlockTileEntity) {
					world.setBlockState(message.getPos(),
							((DecisionBlockTileEntity) tileEntity).getBlockType().getDefaultState()
									.withProperty(ProximityBlock.POWERED, Boolean.valueOf(message.getState())),
							3);
					((DecisionBlock) ((DecisionBlockTileEntity) tileEntity).getBlockType()).notifyNeighbors(world,
							message.getPos());
					((DecisionBlockTileEntity) tileEntity).markForUpdate();
				}
			});
			return null;
		}
	}

	private boolean state;
	private BlockPos pos;

	public MessageBlockRedstoneSignalUpdate() {
	}

	public MessageBlockRedstoneSignalUpdate(BlockPos pos, boolean time) {
		state = time;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		state = buf.readBoolean();
		pos = BlockPos.fromLong(buf.readLong());
	}

	public BlockPos getPos() {
		return pos;
	}

	public boolean getState() {
		return state;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(state);
		buf.writeLong(pos.toLong());
	}
}

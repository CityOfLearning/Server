package com.dyn.server.network.messages;

import com.dyn.server.ServerMod;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientUpdateTileEntity implements IMessage {

	public static class Handler implements IMessageHandler<MessageClientUpdateTileEntity, IMessage> {
		@Override
		public IMessage onMessage(final MessageClientUpdateTileEntity message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				tileEntity.readFromNBT(message.getNbt());
				tileEntity.markDirty();
			});
			return null;
		}
	}

	private NBTTagCompound nbt;
	private BlockPos pos;

	public MessageClientUpdateTileEntity() {
	}

	public MessageClientUpdateTileEntity(BlockPos pos, NBTTagCompound nbt) {
		this.nbt = nbt;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
		pos = BlockPos.fromLong(buf.readLong());
	}

	/**
	 * @return the nbt
	 */
	public NBTTagCompound getNbt() {
		return nbt;
	}

	/**
	 * @return the pos
	 */
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeLong(pos.toLong());
	}

}

package com.dyn.server.network.messages;

import com.dyn.fixins.blocks.dialog.DialogBlockTileEntity;
import com.dyn.server.ServerMod;
import com.rabbit.gui.component.display.entity.DisplayEntity;
import com.rabbit.gui.component.display.entity.DisplayEntityHead;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDialogUpdate implements IMessage {

	public static class Handler implements IMessageHandler<MessageDialogUpdate, IMessage> {
		@Override
		public IMessage onMessage(final MessageDialogUpdate message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				if (tileEntity instanceof DialogBlockTileEntity) {
					((DialogBlockTileEntity) tileEntity).setData(message.getText(), message.getCorner1(),
							message.getCorner2());
					if (!message.getEntity().isEmpty()) {
						if (message.getEntity().equals("DisplayHead")) {
							((DialogBlockTileEntity) tileEntity).setEntity(new DisplayEntityHead(tileEntity.getWorld()),
									90);
						} else if (message.getEntity().equals("DisplayEntity")) {
							((DialogBlockTileEntity) tileEntity).setEntity(new DisplayEntity(tileEntity.getWorld()),
									90);
						} else {
							((DialogBlockTileEntity) tileEntity)
									.setEntity(
											(EntityLiving) EntityList.createEntityByName(message.getEntity(),
													tileEntity.getWorld()),
											EntityList.getIDFromString(message.getEntity()));
						}
					}
					if ((((DialogBlockTileEntity) tileEntity).getEntity() instanceof DisplayEntity)
							&& !message.getSkin().isEmpty()) {
						((DisplayEntity) ((DialogBlockTileEntity) tileEntity).getEntity())
								.setTexture(new ResourceLocation(message.getSkin()));
					}
					((DialogBlockTileEntity) tileEntity).setInterruptible(message.getInterupt());
					((DialogBlockTileEntity) tileEntity).markForUpdate();
				}
			});
			return null;
		}
	}

	private BlockPos pos;
	private String text;
	private BlockPos corner1;
	private BlockPos corner2;
	private String entity;
	private String skin;
	private boolean interupt;

	public MessageDialogUpdate() {
	}

	public MessageDialogUpdate(String entity, String skin, BlockPos pos, String text, BlockPos corner1,
			BlockPos corner2, boolean interupt) {
		this.entity = entity;
		this.pos = pos;
		this.text = text;
		this.skin = skin;
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.interupt = interupt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entity = ByteBufUtils.readUTF8String(buf);
		pos = BlockPos.fromLong(buf.readLong());
		text = ByteBufUtils.readUTF8String(buf);
		corner1 = BlockPos.fromLong(buf.readLong());
		corner2 = BlockPos.fromLong(buf.readLong());
		skin = ByteBufUtils.readUTF8String(buf);
		interupt = buf.readBoolean();
	}

	public BlockPos getCorner1() {
		return corner1;
	}

	public BlockPos getCorner2() {
		return corner2;
	}

	public String getEntity() {
		return entity;
	}

	public boolean getInterupt() {
		return interupt;
	}

	public BlockPos getPos() {
		return pos;
	}

	public String getSkin() {
		return skin;
	}

	public String getText() {
		return text;
	}

	public void setInterupt(boolean interupt) {
		this.interupt = interupt;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, entity);
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, text);
		buf.writeLong(corner1.toLong());
		buf.writeLong(corner2.toLong());
		ByteBufUtils.writeUTF8String(buf, skin);
		buf.writeBoolean(interupt);
	}
}

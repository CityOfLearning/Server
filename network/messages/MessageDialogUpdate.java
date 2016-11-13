package com.dyn.server.network.messages;

import com.dyn.fixins.blocks.dialog.DialogBlockTileEntity;
import com.dyn.server.ServerMod;
import com.rabbit.gui.component.display.entity.DisplayEntity;
import com.rabbit.gui.component.display.entity.DisplayEntityHead;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
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
					((DialogBlockTileEntity) tileEntity).setData(message.getText(), message.getXRadius(),
							message.getYRadius(), message.getZRadius());
					if (!message.getEntity().isEmpty()) {
						System.out.println(message.getEntity());
						if(message.getEntity().equals("DisplayHead")){
							((DialogBlockTileEntity) tileEntity).setEntity(new DisplayEntityHead(tileEntity.getWorld()));
						} else if(message.getEntity().equals("DisplayEntity")){
							((DialogBlockTileEntity) tileEntity).setEntity(new DisplayEntity(tileEntity.getWorld()));
						} else {
							((DialogBlockTileEntity) tileEntity).setEntity((EntityLivingBase) EntityList
								.createEntityByName(message.getEntity(), tileEntity.getWorld()));
						}
						
					}
					if(((DialogBlockTileEntity) tileEntity).getEntity() instanceof DisplayEntity && !message.getSkin().isEmpty()){
						((DisplayEntity) ((DialogBlockTileEntity) tileEntity).getEntity()).setTexture(new ResourceLocation(message.getSkin()));
					}
					((DialogBlockTileEntity) tileEntity).markForUpdate();
				}
			});
			return null;
		}
	}

	private BlockPos pos;
	private String text;
	private int blockX;
	private int blockY;

	private int blockZ;
	private String entity;
	private String skin;

	public MessageDialogUpdate() {
	}

	public String getSkin() {
		return skin;
	}

	public MessageDialogUpdate(String entity, String skin, BlockPos pos, String text, int xradius, int yradius, int zradius) {
		this.entity = entity;
		this.pos = pos;
		this.text = text;
		this.skin = skin;
		blockX = xradius;
		blockY = yradius;
		blockZ = zradius;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entity = ByteBufUtils.readUTF8String(buf);
		pos = BlockPos.fromLong(buf.readLong());
		text = ByteBufUtils.readUTF8String(buf);
		blockX = buf.readInt();
		blockY = buf.readInt();
		blockZ = buf.readInt();
		skin = ByteBufUtils.readUTF8String(buf);
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

	public String getEntity() {
		return entity;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, entity);
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, text);
		buf.writeInt(blockX);
		buf.writeInt(blockY);
		buf.writeInt(blockZ);
		ByteBufUtils.writeUTF8String(buf, skin);
	}
}

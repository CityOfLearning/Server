package com.dyn.server.network.messages;

import java.util.Map;
import java.util.regex.Pattern;

import com.dyn.fixins.blocks.decision.DecisionBlockTileEntity;
import com.dyn.fixins.blocks.decision.DecisionBlockTileEntity.Choice;
import com.dyn.server.ServerMod;
import com.google.common.collect.Maps;
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

public class MessageDecisionUpdate implements IMessage {

	public static class Handler implements IMessageHandler<MessageDecisionUpdate, IMessage> {
		@Override
		public IMessage onMessage(final MessageDecisionUpdate message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				World world = ctx.getServerHandler().playerEntity.getEntityWorld();
				TileEntity tileEntity = world.getTileEntity(message.getPos());
				if (tileEntity instanceof DecisionBlockTileEntity) {
					((DecisionBlockTileEntity) tileEntity).setData(message.getText(), message.getCorner1(),
							message.getCorner2());
					if (!message.getEntity().isEmpty()) {
						if (message.getEntity().equals("DisplayHead")) {
							((DecisionBlockTileEntity) tileEntity)
									.setEntity(new DisplayEntityHead(tileEntity.getWorld()), 90);
						} else if (message.getEntity().equals("DisplayEntity")) {
							((DecisionBlockTileEntity) tileEntity).setEntity(new DisplayEntity(tileEntity.getWorld()),
									90);
						} else {
							((DecisionBlockTileEntity) tileEntity)
									.setEntity(
											(EntityLiving) EntityList.createEntityByName(message.getEntity(),
													tileEntity.getWorld()),
											EntityList.getIDFromString(message.getEntity()));
						}
					}
					if ((((DecisionBlockTileEntity) tileEntity).getEntity() instanceof DisplayEntity)
							&& !message.getSkin().isEmpty()) {
						((DisplayEntity) ((DecisionBlockTileEntity) tileEntity).getEntity())
								.setTexture(new ResourceLocation(message.getSkin()));
					}
					((DecisionBlockTileEntity) tileEntity).setChoices(message.getChoices());
					((DecisionBlockTileEntity) tileEntity).markForUpdate();
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
	private Map<String, Choice> choices = Maps.newHashMap();

	Pattern p1 = Pattern.compile("\u2021", Pattern.LITERAL);
	Pattern p2 = Pattern.compile("\u2020", Pattern.LITERAL);

	public MessageDecisionUpdate() {
	}

	public MessageDecisionUpdate(String entity, String skin, BlockPos pos, String text, BlockPos corner1,
			BlockPos corner2, Map<String, Choice> choices) {
		this.entity = entity;
		this.pos = pos;
		this.text = text;
		this.skin = skin;
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.choices = choices;
	}

	private String encodeChoices() {
		String choiceString = "";
		for (String key : choices.keySet()) {
			choiceString += key + String.valueOf('\u2020') + choices.get(key).toString() + String.valueOf('\u2021');
		}
		return choiceString;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entity = ByteBufUtils.readUTF8String(buf);
		pos = BlockPos.fromLong(buf.readLong());
		text = ByteBufUtils.readUTF8String(buf);
		corner1 = BlockPos.fromLong(buf.readLong());
		corner2 = BlockPos.fromLong(buf.readLong());
		skin = ByteBufUtils.readUTF8String(buf);
		parseChoices(ByteBufUtils.readUTF8String(buf));
	}

	public Map<String, Choice> getChoices() {
		return choices;
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

	public BlockPos getPos() {
		return pos;
	}

	public String getSkin() {
		return skin;
	}

	public String getText() {
		return text;
	}

	private void parseChoices(String stringMap) {
		choices.clear();
		for (String key : p1.split(stringMap)) {
			choices.put(p2.split(key)[0], Choice.parse(p2.split(key)[1]));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, entity);
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, text);
		buf.writeLong(corner1.toLong());
		buf.writeLong(corner2.toLong());
		ByteBufUtils.writeUTF8String(buf, skin);
		ByteBufUtils.writeUTF8String(buf, encodeChoices());
	}

}

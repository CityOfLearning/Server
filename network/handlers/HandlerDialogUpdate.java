package com.dyn.server.network.handlers;

import com.dyn.fixins.blocks.dialog.DialogBlockTileEntity;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageDialogUpdate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerDialogUpdate implements IMessageHandler<MessageDialogUpdate, IMessage> {
	@Override
	public IMessage onMessage(final MessageDialogUpdate message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> {
			World world = ctx.getServerHandler().playerEntity.getEntityWorld();
			TileEntity tileEntity = world.getTileEntity(message.getPos());
			if (tileEntity instanceof DialogBlockTileEntity) {
				((DialogBlockTileEntity) tileEntity).setData(message.getText(), message.getXRadius(),
						message.getYRadius(), message.getZRadius());
				((DialogBlockTileEntity) tileEntity).markForUpdate();
			}
		});
		return null;
	}
}

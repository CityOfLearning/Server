package com.dyn.server.network.handlers;

import java.util.List;

import com.dyn.robot.RobotMod;
import com.dyn.robot.entity.DynRobotEntity;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageActivateRobot;
import com.forgeessentials.multiworld.ModuleMultiworld;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerActivateRobot implements IMessageHandler<MessageActivateRobot, IMessage> {
	@Override
	public IMessage onMessage(final MessageActivateRobot message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> {
			if (message.isActivating()) {
				WorldServer world1 = ModuleMultiworld.getMultiworldManager()
						.getWorld(ModuleMultiworld.getMultiworldManager().getWorldName(message.getDimension()));
				world1.setBlockToAir(message.getPosition());
				DynRobotEntity new_robot = (DynRobotEntity) ItemMonsterPlacer.spawnCreature(world1,
						EntityList.classToStringMapping.get(DynRobotEntity.class), message.getPosition().getX() + 0.5,
						message.getPosition().getY(), message.getPosition().getZ() + 0.5);
				new_robot.setOwner(ctx.getServerHandler().playerEntity);
				new_robot.setRobotName(message.getName());
			} else {
				List<EntityRobot> robots = ctx.getServerHandler().playerEntity.worldObj.getEntitiesWithinAABB(
						EntityRobot.class,
						AxisAlignedBB.fromBounds(message.getPosition().getX(), message.getPosition().getY(),
								message.getPosition().getZ(), message.getPosition().getX() + 1,
								message.getPosition().getY() + 1, message.getPosition().getZ() + 1));
				for (EntityRobot robot : robots) {
					if (robot.isOwner(ctx.getServerHandler().playerEntity)) {
						robot.setDead();
					}
				}
				WorldServer world2 = ModuleMultiworld.getMultiworldManager()
						.getWorld(ModuleMultiworld.getMultiworldManager().getWorldName(message.getDimension()));
				world2.setBlockToAir(message.getPosition());
				world2.setBlockState(message.getPosition(), safeGetStateFromMeta(RobotMod.dynRobot, 0), 3);
			}
		});
		return null;
	}

	private IBlockState safeGetStateFromMeta(Block b, int meta) {
		try {
			return b.getStateFromMeta(meta);
		} catch (Exception e) {
			return b.getStateFromMeta(0);
		}
	}
}

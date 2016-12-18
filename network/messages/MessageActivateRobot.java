package com.dyn.server.network.messages;

import java.util.List;

import com.dyn.robot.RobotMod;
import com.dyn.robot.entity.DynRobotEntity;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.forgeessentials.chat.Censor;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageActivateRobot implements IMessage {

	public static class Handler implements IMessageHandler<MessageActivateRobot, IMessage> {
		@Override
		public IMessage onMessage(final MessageActivateRobot message, final MessageContext ctx) {
			ServerMod.proxy.addScheduledTask(() -> {
				if (message.isActivating()) {
					World world1 = ctx.getServerHandler().playerEntity.worldObj;
					world1.setBlockToAir(message.getPosition());
					DynRobotEntity new_robot = (DynRobotEntity) ItemMonsterPlacer.spawnCreature(world1,
							EntityList.classToStringMapping.get(DynRobotEntity.class),
							message.getPosition().getX() + 0.5, message.getPosition().getY(),
							message.getPosition().getZ() + 0.5);
					new_robot.setOwner(ctx.getServerHandler().playerEntity);
					new_robot.setRobotName(Censor.filter(message.getName()));
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
					World world2 = ctx.getServerHandler().playerEntity.worldObj;
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

	private String robotName;
	private boolean activate;
	private BlockPos pos;

	private int dim;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public MessageActivateRobot() {
	}

	public MessageActivateRobot(String robotName, BlockPos pos, int dim, boolean activate) {
		this.robotName = robotName;
		this.activate = activate;
		this.pos = pos;
		this.dim = dim;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		activate = buf.readBoolean();
		pos = BlockPos.fromLong(buf.readLong());
		robotName = ByteBufUtils.readUTF8String(buf);
		dim = buf.readInt();
	}

	public int getDimension() {
		return dim;
	}

	public String getName() {
		return robotName;
	}

	public BlockPos getPosition() {
		return pos;
	}

	public boolean isActivating() {
		return activate;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(activate);
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, robotName);
		buf.writeInt(dim);

	}
}

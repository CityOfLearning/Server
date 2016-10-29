package com.dyn.server.network.handlers;

import com.dyn.DYNServerMod;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.ServerMod;
import com.dyn.server.network.messages.MessageDebugRobot;

import mobi.omegacentauri.raspberryjammod.util.Location;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerDebugRobot implements IMessageHandler<MessageDebugRobot, IMessage> {
	private float getAngleFromFacing(EnumFacing dir) {
		switch (dir) {
		case SOUTH:
			return 0;
		case NORTH:
			return 180;
		case EAST:
			return 270;
		case WEST:
			return 90;
		default:
			return 0;
		}
	}

	@Override
	public IMessage onMessage(final MessageDebugRobot message, final MessageContext ctx) {
		ServerMod.proxy.addScheduledTask(() -> {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			World world = player.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(message.getRobotId());
			robot.setIsFollowing(false);
			robot.removeNonEssentialAI();
			// snap the robot to the center of the block and set its facing to
			// the current direction
			if ((robot.rotationYaw % 90) != 0) {
				BlockPos loc = robot.getPosition();
				robot.setPositionAndRotation(loc.getX() + .5, loc.getY(), loc.getZ() + .5,
						getAngleFromFacing(robot.getHorizontalFacing()), robot.rotationPitch);
			}
			switch (message.getCommand()) {
			case FORWARD:
				robot.moveForward(message.getAmount());
				break;
			case BACK:
				robot.moveBackward(message.getAmount());
				break;
			case RIGHT: {
				float newYaw = MathHelper.wrapAngleTo180_float(robot.rotationYaw + message.getAmount());
				robot.rotationYaw = newYaw;
				robot.setRotationYawHead(newYaw);
				robot.setRenderYawOffset(newYaw);
				break;
			}
			case LEFT: {
				float newYaw = MathHelper.wrapAngleTo180_float(robot.rotationYaw - message.getAmount());
				robot.rotationYaw = newYaw;
				robot.setRotationYawHead(newYaw);
				robot.setRenderYawOffset(newYaw);
				break;
			}
			case RUN:
				robot.startExecutingCode();
				break;
			case INTERACT: {
				BlockPos curLoc = robot.getPosition();
				BlockPos interactBlock = null;
				if (Block.isEqualTo(robot.worldObj.getBlockState(curLoc).getBlock(), Blocks.lever)
						|| Block.isEqualTo(robot.worldObj.getBlockState(curLoc).getBlock(), Blocks.stone_button)
						|| Block.isEqualTo(robot.worldObj.getBlockState(curLoc).getBlock(), Blocks.wooden_button)) {
					interactBlock = curLoc;
				} else {
					// when using left right commands
					// robot.rotationYaw = robot.rotationYawHead;
					switch (robot.getHorizontalFacing()) {
					case NORTH:
						interactBlock = curLoc.north();
						break;
					case SOUTH:
						interactBlock = curLoc.south();
						break;
					case EAST:
						interactBlock = curLoc.east();
						break;
					case WEST:
						interactBlock = curLoc.west();
						break;
					default:
						break;
					}
				}
				if (robot.worldObj.getBlockState(interactBlock).getBlock() != Blocks.air) {
					robot.worldObj.getBlockState(interactBlock).getBlock().onBlockActivated(robot.worldObj,
							interactBlock, robot.worldObj.getBlockState(interactBlock), robot.getOwner(),
							robot.getHorizontalFacing().getOpposite(), 0, 0, 0);
				}
				break;
			}
			case PLACE: {
				BlockPos curLoc = robot.getPosition();
				BlockPos placeBlock = null;
				switch (robot.getHorizontalFacing()) {
				case NORTH:
					placeBlock = curLoc.north();
					break;
				case SOUTH:
					placeBlock = curLoc.south();
					break;
				case EAST:
					placeBlock = curLoc.east();
					break;
				case WEST:
					placeBlock = curLoc.west();
					break;
				default:
					break;
				}
				// only place the block if the block is air
				if (robot.worldObj.getBlockState(placeBlock).getBlock() == Blocks.air) {
					Location pos = new Location(robot.worldObj, placeBlock.getX(), placeBlock.getY(),
							placeBlock.getZ());
					if (!robot.isInventoryEmpty()) {
						for (int i = 0; i < robot.m_inventory.getSizeInventory(); i++) {
							ItemStack slot = robot.m_inventory.getStackInSlot(i);
							if (slot != null) {
								Block inventoryBlock = Block.getBlockFromItem(slot.getItem());
								if ((inventoryBlock != null) && inventoryBlock.canPlaceBlockAt(robot.worldObj, pos)) {
									robot.m_inventory.decrStackSize(i, 1);
									pos.getWorld().setBlockState(pos, inventoryBlock.getBlockState().getBaseState(), 3);
									break;
								}
							}
						}
					} else {
						pos.getWorld().setBlockState(pos, Blocks.dirt.getDefaultState(), 3);
					}
				}
				break;
			}
			case BREAK: {
				BlockPos curLoc = robot.getPosition();
				BlockPos breakBlock = null;
				switch (robot.getHorizontalFacing()) {
				case NORTH:
					breakBlock = curLoc.north();
					break;
				case SOUTH:
					breakBlock = curLoc.south();
					break;
				case EAST:
					breakBlock = curLoc.east();
					break;
				case WEST:
					breakBlock = curLoc.west();
					break;
				default:
					break;
				}
				if (robot.worldObj.getBlockState(breakBlock).getBlock() != Blocks.air) {
					if (!robot.isInventoryFull()) {
						robot.addItemStackToInventory(
								new ItemStack(robot.worldObj.getBlockState(breakBlock).getBlock(), 1));
					} else {
						robot.worldObj.getBlockState(breakBlock).getBlock().dropBlockAsItem(robot.worldObj, breakBlock,
								robot.worldObj.getBlockState(breakBlock), 1);
					}
					robot.worldObj.setBlockToAir(breakBlock);
				}
				break;
			}
			case JUMP:
				robot.setShouldJump(true);
				;
				break;
			case SAY:
				robot.addMessage("Hello World");
				break;
			case CLIMB:
				robot.climb(1);
				break;
			default:
				DYNServerMod.logger.error("Command not recognized");
				break;
			}
		});
		return null;
	}
}

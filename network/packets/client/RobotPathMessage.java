package com.dyn.server.network.packets.client;

import java.io.IOException;
import java.util.List;

import com.dyn.render.hud.path.EntityPathRenderer;
import com.dyn.robot.entity.EntityRobot;
import com.dyn.server.network.packets.AbstractMessage.AbstractClientMessage;
import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class RobotPathMessage extends AbstractClientMessage<RobotPathMessage> {

	private PathEntity path;
	private int robotId;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RobotPathMessage() {
	}

	// We need to initialize our data, so provide a suitable constructor:
	public RobotPathMessage(PathEntity path, int robotid) {
		this.path = path;
		robotId = robotid;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
			World world = player.worldObj;
			EntityRobot robot = (EntityRobot) world.getEntityByID(robotId);
			EntityPathRenderer.addEntityForPathRendering(robot, path);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		robotId = buffer.readInt();
		int numPoints = buffer.readInt();
		List<PathPoint> points = Lists.newArrayList();
		for (int i = 0; i < numPoints; i++) {
			BlockPos pos = buffer.readBlockPos();
			points.add(new PathPoint(pos.getX(), pos.getY(), pos.getZ()));
		}
		path = new PathEntity(points.toArray(new PathPoint[0]));
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(robotId);
		buffer.writeInt(path.getCurrentPathLength());
		for (int i = 0; i < path.getCurrentPathLength(); i++) {
			PathPoint point = path.getPathPointFromIndex(i);
			buffer.writeBlockPos(new BlockPos(point.xCoord, point.yCoord, point.zCoord));
		}
	}
}

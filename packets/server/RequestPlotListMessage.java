package com.dyn.server.packets.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.dyn.server.packets.AbstractMessage.AbstractServerMessage;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.packets.client.PlotNamesMessage;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.command.CommandPlot.PlotListingType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class RequestPlotListMessage extends AbstractServerMessage<RequestPlotListMessage> {

	private boolean type;

	// The basic, no-argument constructor MUST be included for
	// automated handling
	public RequestPlotListMessage() {
	}

	public RequestPlotListMessage(boolean plotType) {
		type = plotType;
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
			PlotListingType listType = PlotListingType.OWN;
			if (type) {
				listType = PlotListingType.SALE;
			}

			final WorldPoint playerRef = new WorldPoint(player.getEntityWorld(), player.getPosition());
			SortedSet<Plot> plots = new TreeSet<Plot>((a, b) -> {
				if (a.getDimension() != playerRef.getDimension()) {
					if (b.getDimension() == playerRef.getDimension()) {
						return 1;
					}
				} else {
					if (b.getDimension() != playerRef.getDimension()) {
						return -1;
					}
				}
				double aDist = a.getZone().getArea().getCenter().setY(0).distance(playerRef);
				double bDist = b.getZone().getArea().getCenter().setY(0).distance(playerRef);
				return (int) Math.signum(aDist - bDist);
			});
			for (Plot plot : Plot.getPlots()) {
				if (listType.check(player, plot)) {
					plots.add(plot);
				}
			}
			List<String> sPlots = new ArrayList<String>();
			int plotNum = 1;
			for (Plot plot : plots) {
				sPlots.add(String.format("#%d: \"%s\"", plotNum++,
						plot.getName().substring(0, plot.getName().length() > 8 ? 8 : plot.getName().length()),
						plot.getCenter().toString()));
			}

			PacketDispatcher.sendTo(new PlotNamesMessage(sPlots), (EntityPlayerMP) player);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		type = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(type);
	}
}

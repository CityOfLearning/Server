package com.dyn.server.packets;

import com.dyn.server.packets.client.AchievementProgressMessage;
import com.dyn.server.packets.client.FreezePlayerMessage;
import com.dyn.server.packets.client.GroupNamesMessage;
import com.dyn.server.packets.client.GroupPermissionsMessage;
import com.dyn.server.packets.client.PlayerStatusMessage;
import com.dyn.server.packets.client.PlotNamesMessage;
import com.dyn.server.packets.client.ReturnFlagMessage;
import com.dyn.server.packets.client.ServerUserlistMessage;
import com.dyn.server.packets.client.SyncAchievementsMessage;
import com.dyn.server.packets.client.SyncNamesMessage;
import com.dyn.server.packets.client.SyncSkinsMessage;
import com.dyn.server.packets.client.WorldNamesMessage;
import com.dyn.server.packets.client.WorldZonesMessage;
import com.dyn.server.packets.client.ZonePermissionsMessage;
import com.dyn.server.packets.server.AwardAchievementMessage;
import com.dyn.server.packets.server.FeedPlayerMessage;
import com.dyn.server.packets.server.HaveServerWriteAchievementsMessage;
import com.dyn.server.packets.server.MentorGivingAchievementMessage;
import com.dyn.server.packets.server.RemoveEffectsMessage;
import com.dyn.server.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.packets.server.RequestGroupListMessage;
import com.dyn.server.packets.server.RequestGroupPermissionsMessage;
import com.dyn.server.packets.server.RequestPlotListMessage;
import com.dyn.server.packets.server.RequestUserAchievementsProgressMessage;
import com.dyn.server.packets.server.RequestUserStatusMessage;
import com.dyn.server.packets.server.RequestUserlistMessage;
import com.dyn.server.packets.server.RequestVerificationMessage;
import com.dyn.server.packets.server.RequestWorldListMessage;
import com.dyn.server.packets.server.RequestWorldZonesMessage;
import com.dyn.server.packets.server.RequestZonePermissionsMessage;
import com.dyn.server.packets.server.ServerCommandMessage;
import com.dyn.server.packets.server.StudentCommandBlockMessage;
import com.dyn.server.packets.server.SyncNamesServerMessage;
import com.dyn.server.packets.server.SyncSkinsServerMessage;
import com.dyn.server.reference.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 *
 * This class will house the SimpleNetworkWrapper instance, which I will name
 * 'dispatcher', as well as give us a logical place from which to register our
 * packets. These two things could be done anywhere, however, even in your Main
 * class, but I will be adding other functionality (see below) that gives this
 * class a bit more utility.
 *
 * While unnecessary, I'm going to turn this class into a 'wrapper' for
 * SimpleNetworkWrapper so that instead of writing
 * "PacketDispatcher.dispatcher.{method}" I can simply write
 * "PacketDispatcher.{method}" All this does is make it quicker to type and
 * slightly shorter; if you do not care about that, then make the 'dispatcher'
 * field public instead of private, or, if you do not want to add a new class
 * just for one field and one static method that you could put anywhere, feel
 * free to put them wherever.
 *
 * For further convenience, I have also added two extra sendToAllAround methods:
 * one which takes an EntityPlayer and one which takes coordinates.
 *
 */
public class PacketDispatcher {
	// a simple counter will allow us to get rid of 'magic' numbers used during
	// packet registration
	private static byte packetId = 0;

	/**
	 * The SimpleNetworkWrapper instance is used both to register and send
	 * packets. Since I will be adding wrapper methods, this field is private,
	 * but you should make it public if you plan on using it directly.
	 */
	private static SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

	/**
	 * Registers an {@link AbstractMessage} to the appropriate side(s)
	 */
	private static <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
		// We can tell by the message class which side to register it on by
		// using #isAssignableFrom (google it)

		// Also, one can see the convenience of using a static counter
		// 'packetId' to keep
		// track of the current index, rather than hard-coding them all, plus
		// it's one less
		// parameter to pass.
		if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} else {
			// hopefully you didn't forget to extend the right class, or you
			// will get registered on both sides
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			PacketDispatcher.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	/**
	 * Call this during pre-init or loading and register all of your packets
	 * (messages) here
	 */
	public static void registerPackets() {
		// Packets handled on CLIENT
		registerMessage(AchievementProgressMessage.class);
		registerMessage(FreezePlayerMessage.class);
		registerMessage(PlayerStatusMessage.class);
		registerMessage(PlotNamesMessage.class);
		registerMessage(GroupNamesMessage.class);
		registerMessage(WorldNamesMessage.class);
		registerMessage(WorldZonesMessage.class);
		registerMessage(ReturnFlagMessage.class);
		registerMessage(ServerUserlistMessage.class);
		registerMessage(SyncAchievementsMessage.class);
		registerMessage(GroupPermissionsMessage.class);
		registerMessage(ZonePermissionsMessage.class);
		// Packets ment for All players
		registerMessage(SyncSkinsMessage.class);
		registerMessage(SyncNamesMessage.class);

		// Packets handled on SERVER
		registerMessage(AwardAchievementMessage.class);
		registerMessage(FeedPlayerMessage.class);
		registerMessage(HaveServerWriteAchievementsMessage.class);
		registerMessage(MentorGivingAchievementMessage.class);
		registerMessage(RemoveEffectsMessage.class);
		registerMessage(RequestFreezePlayerMessage.class);
		registerMessage(RequestPlotListMessage.class);
		registerMessage(RequestGroupListMessage.class);
		registerMessage(RequestWorldListMessage.class);
		registerMessage(RequestWorldZonesMessage.class);
		registerMessage(RequestGroupPermissionsMessage.class);
		registerMessage(RequestZonePermissionsMessage.class);
		registerMessage(RequestUserAchievementsProgressMessage.class);
		registerMessage(RequestUserlistMessage.class);
		registerMessage(RequestUserStatusMessage.class);
		registerMessage(RequestVerificationMessage.class);
		registerMessage(ServerCommandMessage.class);
		registerMessage(StudentCommandBlockMessage.class);
		registerMessage(SyncSkinsServerMessage.class);
		registerMessage(SyncNamesServerMessage.class);

	}

	// ========================================================//
	// The following methods are the 'wrapper' methods; again,
	// this just makes sending a message slightly more compact
	// and is purely a matter of stylistic preference
	// ========================================================//

	/**
	 * Send this message to the specified player's client-side counterpart. See
	 * {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static void sendTo(IMessage message, EntityPlayerMP player) {
		PacketDispatcher.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone. See
	 * {@link SimpleNetworkWrapper#sendToAll(IMessage)}
	 */
	public static void sendToAll(IMessage message) {
		PacketDispatcher.dispatcher.sendToAll(message);
	}

	/**
	 * Sends a message to everyone within a certain range of the player
	 * provided. Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		PacketDispatcher.sendToAllAround(message, player.worldObj.provider.getDimensionId(), player.posX, player.posY,
				player.posZ, range);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in
	 * the same dimension. Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Send this message to everyone within a certain range of a point. See
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		PacketDispatcher.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Send this message to everyone within the supplied dimension. See
	 * {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static void sendToDimension(IMessage message, int dimensionId) {
		PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server. See
	 * {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static void sendToServer(IMessage message) {
		PacketDispatcher.dispatcher.sendToServer(message);
	}
}

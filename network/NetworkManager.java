package com.dyn.server.network;

import com.dyn.server.network.messages.MessageActivateRobot;
import com.dyn.server.network.messages.MessageDebugRobot;
import com.dyn.server.network.messages.MessageDialogUpdate;
import com.dyn.server.network.messages.MessageOpenRobotInventory;
import com.dyn.server.network.messages.MessageRunPythonScript;
import com.dyn.server.network.messages.MessageRunRobotScript;
import com.dyn.server.network.messages.MessageToggleRobotFollow;
import com.dyn.server.network.packets.AbstractMessage;
import com.dyn.server.network.packets.bidirectional.MentorRequstScriptMessage;
import com.dyn.server.network.packets.bidirectional.RequestStudentScriptMessage;
import com.dyn.server.network.packets.client.AchievementProgressMessage;
import com.dyn.server.network.packets.client.FreezePlayerMessage;
import com.dyn.server.network.packets.client.GroupNamesMessage;
import com.dyn.server.network.packets.client.GroupPermissionsMessage;
import com.dyn.server.network.packets.client.PlayerStatusMessage;
import com.dyn.server.network.packets.client.PlotNamesMessage;
import com.dyn.server.network.packets.client.ReturnFlagMessage;
import com.dyn.server.network.packets.client.RobotSpeakMessage;
import com.dyn.server.network.packets.client.ServerUserlistMessage;
import com.dyn.server.network.packets.client.SyncAchievementsMessage;
import com.dyn.server.network.packets.client.SyncNamesMessage;
import com.dyn.server.network.packets.client.SyncSkinsMessage;
import com.dyn.server.network.packets.client.WorldNamesMessage;
import com.dyn.server.network.packets.client.WorldZonesMessage;
import com.dyn.server.network.packets.client.ZonePermissionsMessage;
import com.dyn.server.network.packets.server.AwardAchievementMessage;
import com.dyn.server.network.packets.server.FeedPlayerMessage;
import com.dyn.server.network.packets.server.HaveServerWriteAchievementsMessage;
import com.dyn.server.network.packets.server.MentorGivingAchievementMessage;
import com.dyn.server.network.packets.server.RemoveEffectsMessage;
import com.dyn.server.network.packets.server.RequestFreezePlayerMessage;
import com.dyn.server.network.packets.server.RequestGroupListMessage;
import com.dyn.server.network.packets.server.RequestGroupPermissionsMessage;
import com.dyn.server.network.packets.server.RequestPlotListMessage;
import com.dyn.server.network.packets.server.RequestUserAchievementsProgressMessage;
import com.dyn.server.network.packets.server.RequestUserStatusMessage;
import com.dyn.server.network.packets.server.RequestUserlistMessage;
import com.dyn.server.network.packets.server.RequestVerificationMessage;
import com.dyn.server.network.packets.server.RequestWorldListMessage;
import com.dyn.server.network.packets.server.RequestWorldZonesMessage;
import com.dyn.server.network.packets.server.RequestZonePermissionsMessage;
import com.dyn.server.network.packets.server.ServerCommandMessage;
import com.dyn.server.network.packets.server.StopServerPythonScriptMessage;
import com.dyn.server.network.packets.server.StudentCommandBlockMessage;
import com.dyn.server.network.packets.server.SyncNamesServerMessage;
import com.dyn.server.network.packets.server.SyncSkinsServerMessage;
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
public class NetworkManager {
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
			NetworkManager.dispatcher.registerMessage(clazz, clazz, packetId++, Side.CLIENT);
		} else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
			NetworkManager.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		} else {
			// hopefully you didn't forget to extend the right class, or you
			// will get registered on both sides
			NetworkManager.dispatcher.registerMessage(clazz, clazz, packetId, Side.CLIENT);
			NetworkManager.dispatcher.registerMessage(clazz, clazz, packetId++, Side.SERVER);
		}
	}

	// this is built so that message and handler can be separate classes
	private static <T extends IMessage, U extends IMessageHandler<T, IMessage>> void registerMessage(
			Class<T> message_clazz, Class<U> handler_clazz, Side side) {
		if (side != null) {
			NetworkManager.dispatcher.registerMessage(handler_clazz, message_clazz, packetId++, side);
		}
	}

	public static void registerMessages() {
		// Server
		registerMessage(MessageDialogUpdate.class, MessageDialogUpdate.Handler.class, Side.SERVER);
		registerMessage(MessageRunPythonScript.class, MessageRunPythonScript.Handler.class, Side.SERVER);
		registerMessage(MessageRunRobotScript.class, MessageRunRobotScript.Handler.class, Side.SERVER);
		registerMessage(MessageActivateRobot.class, MessageActivateRobot.Handler.class, Side.SERVER);
		registerMessage(MessageToggleRobotFollow.class, MessageToggleRobotFollow.Handler.class, Side.SERVER);
		registerMessage(MessageDebugRobot.class, MessageDebugRobot.Handler.class, Side.SERVER);
		registerMessage(MessageOpenRobotInventory.class, MessageOpenRobotInventory.Handler.class, Side.SERVER);
	}
	// ========================================================//
	// The following methods are the 'wrapper' methods; again,
	// this just makes sending a message slightly more compact
	// and is purely a matter of stylistic preference
	// ========================================================//

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
		// Packets meant for All players
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
		registerMessage(RobotSpeakMessage.class);
		registerMessage(ServerCommandMessage.class);
		registerMessage(StopServerPythonScriptMessage.class);
		registerMessage(StudentCommandBlockMessage.class);
		registerMessage(SyncSkinsServerMessage.class);
		registerMessage(SyncNamesServerMessage.class);
		
		// Bidirectional Packets
		registerMessage(RequestStudentScriptMessage.class);
		registerMessage(MentorRequstScriptMessage.class);
	}

	/**
	 * Send this message to the specified player's client-side counterpart. See
	 * {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
	 */
	public static void sendTo(IMessage message, EntityPlayerMP player) {
		NetworkManager.dispatcher.sendTo(message, player);
	}

	/**
	 * Send this message to everyone. See
	 * {@link SimpleNetworkWrapper#sendToAll(IMessage)}
	 */
	public static void sendToAll(IMessage message) {
		NetworkManager.dispatcher.sendToAll(message);
	}

	/**
	 * Sends a message to everyone within a certain range of the player
	 * provided. Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, EntityPlayer player, double range) {
		NetworkManager.sendToAllAround(message, player.worldObj.provider.getDimensionId(), player.posX, player.posY,
				player.posZ, range);
	}

	/**
	 * Sends a message to everyone within a certain range of the coordinates in
	 * the same dimension. Shortcut to
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
		NetworkManager.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}

	/**
	 * Send this message to everyone within a certain range of a point. See
	 * {@link SimpleNetworkWrapper#sendToAllAround(IMessage, NetworkRegistry.TargetPoint)}
	 */
	public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		NetworkManager.dispatcher.sendToAllAround(message, point);
	}

	/**
	 * Send this message to everyone within the supplied dimension. See
	 * {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
	 */
	public static void sendToDimension(IMessage message, int dimensionId) {
		NetworkManager.dispatcher.sendToDimension(message, dimensionId);
	}

	/**
	 * Send this message to the server. See
	 * {@link SimpleNetworkWrapper#sendToServer(IMessage)}
	 */
	public static void sendToServer(IMessage message) {
		NetworkManager.dispatcher.sendToServer(message);
	}
}

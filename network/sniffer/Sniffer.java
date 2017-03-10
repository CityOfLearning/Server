package com.dyn.server.network.sniffer;

import java.util.NoSuchElementException;

import com.dyn.DYNServerMod;
import com.stormister.rediscovered.entity.EntitySkyChicken;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class Sniffer {

	public static class PacketInterceptor extends ChannelDuplexHandler {

		EntityPlayerMP player;

		public PacketInterceptor(EntityPlayerMP player) {
			this.player = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			if (packet == null) {
				return;
			}

			if (C0CPacketInput.class.isInstance(packet)) {
				C0CPacketInput steerPacket = (C0CPacketInput) packet;
				// TODO: Should fire a steering event but this is ok for now
				if ((player.ridingEntity != null) && (player.ridingEntity instanceof EntitySkyChicken)) {
					// this doesnt work as intended
					// if(steerPacket.getForwardSpeed() != 0 ||
					// steerPacket.getStrafeSpeed() != 0){
					// ((EntityLivingBase)
					// player.ridingEntity).moveEntityWithHeading(steerPacket.getStrafeSpeed(),
					// steerPacket.getForwardSpeed());
					// }
					if (steerPacket.isJumping()) {
						((EntitySkyChicken) player.ridingEntity).makeJump();
					}
				}
			}

			super.channelRead(ctx, packet);
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP mcPlayer = ((EntityPlayerMP) event.player);
		PacketInterceptor interceptor = new PacketInterceptor(mcPlayer);
		mcPlayer.playerNetServerHandler.getNetworkManager().channel().pipeline().addBefore("packet_handler",
				"dyn_packet_sniffer", interceptor);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
		EntityPlayerMP mcPlayer = ((EntityPlayerMP) event.player);
		try {
			mcPlayer.playerNetServerHandler.getNetworkManager().channel().pipeline().remove("dyn_packet_sniffer");
		} catch (NoSuchElementException nsee) {
			DYNServerMod.logger.warn("Could not remove Packet Sniffer from pipeline");
		}
	}

}

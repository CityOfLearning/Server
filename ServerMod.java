package com.dyn.server;

import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.proxy.Proxy;
import com.dyn.server.reference.MetaData;
import com.dyn.server.reference.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:dyn")
public class ServerMod {

	@Mod.Instance(Reference.MOD_ID)
	public static ServerMod instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

//	@Mod.Metadata(Reference.MOD_ID)
//	public ModMetadata metadata;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
//		metadata = MetaData.init(metadata);
		MetaData.init(event.getModMetadata());

		PacketDispatcher.registerPackets();
	}
}

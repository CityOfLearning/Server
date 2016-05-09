package com.dyn.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.dyn.server.database.DBManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.proxy.Proxy;
import com.dyn.server.reference.MetaData;
import com.dyn.server.reference.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class ServerMod {

	public static List<String> usernames = new ArrayList<String>();
	public static List<String> frozenPlayers = new ArrayList<String>();
	public static boolean opped = false;

	@Mod.Instance(Reference.MOD_ID)
	public static ServerMod instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;
	
	public static Logger logger;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		DBManager.init();
		
		metadata = MetaData.init(metadata);

		logger = event.getModLog();
		
		PacketDispatcher.registerPackets();
		proxy.init();
	}
}

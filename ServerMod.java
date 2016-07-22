package com.dyn.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;

import com.dyn.DYNServerMod;
import com.dyn.server.keys.KeyManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.proxy.Proxy;
import com.dyn.server.reference.MetaData;
import com.dyn.server.reference.Reference;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:dyn")
public class ServerMod {

	@Mod.Instance(Reference.MOD_ID)
	public static ServerMod instance;

	@SidedProxy(modId = Reference.MOD_ID, clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MetaData.init(event.getModMetadata());

		PacketDispatcher.registerPackets();
	}
}

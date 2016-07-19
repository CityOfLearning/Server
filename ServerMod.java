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

	// on the client side this also is done by the launcher but since it cleans
	// up after itself
	// it should prevent duplicates
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		String path = MinecraftServer.getServer().getDataDirectory().getAbsolutePath();
		File crashfiles = new File(path, "/crash-reports");
		File logfiles = new File(path, "/logs");

		FTPClient client = new FTPClient();
		FileInputStream fis = null;

		try {
			client.connect(KeyManager.getFtpKeys().getLeft());
			client.login(KeyManager.getFtpKeys().getMiddle(), KeyManager.getFtpKeys().getRight());

			for (File f : crashfiles.listFiles()) {

				// Create an InputStream of the file to be uploaded
				fis = new FileInputStream(f);
				// Store file to server
				DYNServerMod.logger.info(
						"Uploading " + f.getName() + " to FTP server at /Minecraft/CrashReports/Server/" + f.getName());

				if (!client.storeFile("/Minecraft/CrashReports/Server/" + f.getName(), fis)) {
					DYNServerMod.logger.info("Failed to upload file");
				}
				fis.close();
			}
			for (File f : logfiles.listFiles()) {
				// Create an InputStream of the file to be uploaded
				fis = new FileInputStream(f);
				// Store file to server
				DYNServerMod.logger.info(
						"Uploading " + f.getName() + " to FTP server at /Minecraft/ChatLogs/Server/" + f.getName());

				if (!client.storeFile(
						"/Minecraft/ChatLogs/Server/" + LocalDateTime.now().toLocalTime() + "-" + f.getName(), fis)) {
					DYNServerMod.logger.info("Failed to upload file");
				}
				fis.close();
			}
			client.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				client.disconnect();
				for (File f : crashfiles.listFiles()) {
					try {
						DYNServerMod.logger.info("Deleting " + f.getName());
						FileUtils.forceDelete(f);
					} catch (IOException e) {
						DYNServerMod.logger.info("Failed to delete " + f.getName() + ". Attempting to delete on close");
						try {
							FileUtils.forceDeleteOnExit(f);
						} catch (Exception e1) {
							DYNServerMod.logger.info("Failed to delete " + f.getName() + "on exit as well");
							e1.printStackTrace();
						}
					}
				}
				for (File f : logfiles.listFiles()) {
					try {
						DYNServerMod.logger.info("Deleting " + f.getName());
						FileUtils.forceDelete(f);
					} catch (IOException e) {
						DYNServerMod.logger.info("Failed to delete " + f.getName() + ". Attempting to delete on close");
						try {
							FileUtils.forceDeleteOnExit(f);
						} catch (Exception e1) {
							DYNServerMod.logger.info("Failed to delete " + f.getName() + "on exit as well");
							e1.printStackTrace();
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

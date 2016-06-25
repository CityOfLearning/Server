package com.dyn.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.dyn.DYNServerMod;
import com.dyn.server.database.DBManager;
import com.dyn.server.keys.KeyManager;
import com.dyn.server.packets.PacketDispatcher;
import com.dyn.server.proxy.Proxy;
import com.dyn.server.reference.MetaData;
import com.dyn.server.reference.Reference;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class ServerMod {

	@Mod.Instance(Reference.MOD_ID)
	public static ServerMod instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static Proxy proxy;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		try {
			URL url = new URL("https://dl.dropboxusercontent.com/u/33377940/keys.json");

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(new JsonReader(new InputStreamReader(url.openStream())));
			JsonObject json = element.getAsJsonObject();

			DBManager.init(json.get("db_url").getAsString(), json.get("db_un").getAsString(),
					json.get("db_pw").getAsString());

			proxy.preInit();

			for (JsonElement jElement : json.get("org_keys").getAsJsonArray()) {
				JsonObject jobj = jElement.getAsJsonObject();
				KeyManager.setOrgKey(jobj.get("org_id").getAsInt(), jobj.get("org_key").getAsString());
				KeyManager.setSecretKey(jobj.get("org_id").getAsInt(), jobj.get("secret_key").getAsString());
			}

		} catch (MalformedURLException e) {
			DYNServerMod.logger.error("Failed to get keys");
			e.printStackTrace();
		} catch (JsonIOException e) {
			DYNServerMod.logger.error("Failed to intialize json");
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			DYNServerMod.logger.error("Failed to parse json");
			e.printStackTrace();
		} catch (IOException e) {
			DYNServerMod.logger.error("Failed to read stream");
			e.printStackTrace();
		}
		metadata = MetaData.init(metadata);

		PacketDispatcher.registerPackets();
	}
}

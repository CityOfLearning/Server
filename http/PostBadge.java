
package com.dyn.server.http;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.dyn.DYNServerMod;
import com.dyn.achievements.achievement.AchievementPlus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

public class PostBadge extends Thread {

	public static JsonElement jsonResponse;
	public static String response;
	private String CCOL_UUID;
	private String secretKey;
	private String orgKey;
	private int badgeID;
	private EntityPlayerMP player;
	private AchievementPlus achievement;

	public PostBadge(int badgeId, String uuid, String secret, String key, EntityPlayer player, AchievementPlus ach) {
		if (uuid.isEmpty() || secret.isEmpty() || key.isEmpty()) {
			return;
		}
		CCOL_UUID = uuid;
		secretKey = secret;
		orgKey = key;
		badgeID = badgeId;
		this.player = (EntityPlayerMP) player;
		achievement = ach;
		setName("Server Mod HTTP Post");
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		try {
			HttpClient httpclient = HttpClients.createDefault();

			// decode the base64 encoded string
			byte[] decodedKey = secretKey.getBytes();
			// rebuild key using SecretKeySpec
			SecretKey theSecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

			HmacSHA256Signer signer = new HmacSHA256Signer(null, null, theSecretKey.getEncoded());

			// Configure JSON token with signer and SystemClock
			JsonToken token = new JsonToken(signer);
			token.setExpiration(Instant.now().plusSeconds(300)); // 5 Minutes
			token.setParam("version", "v1");
			token.setSubject("issued_badge");
			JsonObject sPayload = new JsonObject();
			sPayload.addProperty("badge_id", badgeID);
			if (CCOL_UUID.contains("@")) {
				sPayload.addProperty("user_identifier_type", 2);
			} else {
				try {
					UUID.fromString(CCOL_UUID);
				} catch (IllegalArgumentException iae) {
					sPayload.addProperty("user_identifier_type", 1);
				}
			}
			sPayload.addProperty("recipient", CCOL_UUID);
			token.addJsonObject("payload", sPayload);

			HttpPost postReq = new HttpPost("http://chicago.col-engine.com/partner_organizations/api.json");

			postReq.setHeader("Accept", "application/json");
			postReq.setHeader("Authorization", "JWT token=" + orgKey);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("jwt", token.serializeAndSign()));

			postReq.setEntity(new UrlEncodedFormEntity(pairs));

			// Execute and get the response.
			HttpResponse reply = httpclient.execute(postReq);
			HttpEntity entity = reply.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				try {
					response = "";
					int data = instream.read();
					while (data != -1) {
						char theChar = (char) data;
						response = response + theChar;
						data = instream.read();
					}
					JsonParser jParse = new JsonParser();
					jsonResponse = jParse.parse(response);
					JsonObject statusCheck = jsonResponse.getAsJsonObject();
					if (statusCheck.has("status") && (statusCheck.get("status").getAsInt() == 201)) {
						achievement.setAwarded(player);
						player.addChatMessage(
								new ChatComponentText("Player " + player.getDisplayName() + " has earned the badge"));
					} else {
						if (statusCheck.has("status") && (statusCheck.get("status").getAsInt() != 200)) {
							player.addChatMessage(new ChatComponentText(
									"Error: Returned Status: " + statusCheck.get("status").getAsInt()));
						} else {
							player.addChatMessage(new ChatComponentText("You have already earned this badge"));
						}
					}
				} finally {
					instream.close();
				}
			}
		} catch (Exception e) {
			DYNServerMod.logger.error("Could not get complete Badge post request", e);
		}
	}
}

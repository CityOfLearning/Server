package com.dyn.server.http;

import java.io.InputStream;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

public class GetBadge extends Thread {

	public static JsonElement jsonResponse;
	public static String response;
	private String secretKey;
	private String orgKey;

	public GetBadge(String secret, String key) {
		secretKey = secret;
		orgKey = key;
		setName("Server Mod HTTP Get");
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
			token.setSubject("badges");
			JsonObject sPayload = new JsonObject();
			sPayload.addProperty("key", orgKey);
			token.addJsonObject("payload", sPayload);

			HttpGet getReq = new HttpGet(
					"http://chicago.col-engine.com/partner_organizations/api.json?jwt=" + token.serializeAndSign());
			getReq.setHeader("Accept", "application/json");
			getReq.setHeader("Authorization", "JWT token=" + orgKey);
			getReq.addHeader("jwt", token.serializeAndSign());

			// Execute and get the response.
			HttpResponse reply = httpclient.execute(getReq);
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
				} finally {
					instream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

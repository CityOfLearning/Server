package com.dyn.server.http;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dyn.utils.BooleanListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

public class GetPrograms extends Thread {

	public JsonElement jsonResponse;
	public String response;
	private String orgId;
	private String secretKey;
	private String orgKey;
	public BooleanListener responseReceived = new BooleanListener(false);

	public GetPrograms(int ord_id, String secret, String key) {
		orgId = "" + ord_id;
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
			JsonObject sPayload = new JsonObject();
			sPayload.addProperty("org_id", orgId);
			token.addJsonObject("payload", sPayload);

			// https request still dont work because of ssl handshake issue
			HttpGet getReq = new HttpGet(
					String.format("http://chicago.col-engine.com/partner_api/v1/orgs/%s/programs.json?jwt=%s", orgId,
							token.serializeAndSign()));

			getReq.setHeader("Accept", "application/json");
			getReq.setHeader("Authorization", "JWT token=" + orgKey);
			getReq.addHeader("jwt", token.serializeAndSign());

			HttpResponse reply = httpclient.execute(getReq);

			HttpEntity entity = reply.getEntity();

			if (entity != null) {
				response = EntityUtils.toString(entity);
				JsonParser jParse = new JsonParser();
				jsonResponse = jParse.parse(response);
				responseReceived.setFlag(true);
			}
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.dyn.server.keys;

import java.util.HashMap;
import java.util.Map;

public class KeyManager {
	private static Map<Integer, String> secretKey = new HashMap<Integer, String>();
	private static Map<Integer, String> orgKey = new HashMap<Integer, String>();
	
	public static String getSecretKey(int id) {
		return secretKey.get(id);
	}
	public static void setSecretKey(int id, String secretKey) {
		KeyManager.secretKey.put(id, secretKey);
	}
	public static String getOrgKey(int id) {
		return orgKey.get(id);
	}
	public static void setOrgKey(int id, String orgKey) {
		KeyManager.orgKey.put(id, orgKey);
	}
	
}

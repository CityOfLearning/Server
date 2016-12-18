package com.dyn.server.keys;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

public class KeyManager {
	private static Map<Integer, String> secretKey = new HashMap<>();
	private static Map<Integer, String> orgKey = new HashMap<>();
	private static Triple<String, String, String> ftpKey = new MutableTriple<>();

	public static void addFtpKey(String url, String un, String pw) {
		KeyManager.ftpKey = new MutableTriple<>(url, un, pw);
	}

	public static Triple<String, String, String> getFtpKeys() {
		return ftpKey;
	}

	public static String getOrgKey(int id) {
		return orgKey.get(id);
	}

	public static String getSecretKey(int id) {
		return secretKey.get(id);
	}

	public static void setOrgKey(int id, String orgKey) {
		KeyManager.orgKey.put(id, orgKey);
	}

	public static void setSecretKey(int id, String secretKey) {
		KeyManager.secretKey.put(id, secretKey);
	}

}

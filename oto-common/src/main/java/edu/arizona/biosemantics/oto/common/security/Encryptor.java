package edu.arizona.biosemantics.oto.common.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public final class Encryptor {
	private static Encryptor instance;

	public static synchronized Encryptor getInstance() {
		if (instance == null) {
			instance = new Encryptor();
		}
		return instance;
	}

	private Encryptor() {
	}

	public synchronized String encrypt(String plaintext) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getStackTrace());
		}
		try {
			md.update(plaintext.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getStackTrace());
		}

		byte raw[] = md.digest();
		String hash = (new BASE64Encoder()).encode(raw);

		// since this key will be used in URL, cannot have special characters
		// replace special characters with 0
		hash = hash.replaceAll("[^0-9A-Za-z]", "0");
		return hash;
	}
}
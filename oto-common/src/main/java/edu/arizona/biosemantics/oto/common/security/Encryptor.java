package edu.arizona.biosemantics.oto.common.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;

import javax.xml.bind.DatatypeConverter;

public final class Encryptor {
	private static Encryptor instance;

	public static Encryptor getInstance() {
		if (instance == null) {
			instance = new Encryptor();
		}
		return instance;
	}

	private Encryptor() {
	}
	
	public static void main(String[] args) {
		Encryptor enc = new Encryptor();
		enc.encrypt("OTOdemopass");
	}

	public String encrypt(String plaintext) {
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
		
		String hash = DatatypeConverter.printBase64Binary(raw);
		return hash;
	}
}
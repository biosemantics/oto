package edu.arizona.biosemantics.oto.oto.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public final class Encryptor_OTO {
	private static Encryptor_OTO instance;

	public static Encryptor_OTO getInstance() {
		System.out.println("TEST: instance 1");
		if (instance == null) {
			System.out.println("TEST: instance 2");
			instance = new Encryptor_OTO();
		}
		System.out.println("TEST: instance 3");
		return instance;
	}

	private Encryptor_OTO() {
	}
	
	public static void main(String[] args) {
		Encryptor_OTO enc = new Encryptor_OTO();
		enc.encrypt("OTOdemopass");
	}

	public String encrypt(String plaintext) {
		if (plaintext == null) {
			return "";
		}
		
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
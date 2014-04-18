package edu.arizona.biosemantics.util;

//import java.security.*;

/**
 * This class will handle the encryption and decryption algorithms for passwords
 * 
 * @author Partha
 * 
 */
/*
 public class EncryptDecrypt {

 public String encrypt(String word) throws NoSuchAlgorithmException {
 MessageDigest md = MessageDigest.getInstance("SHA-1");
 md.update(word.getBytes());
 byte[] output = md.digest();
 System.out.println(word + " -> " + output.toString());
 return output.toString();

 //return word;
 }

 public String decrypt(String word) {

 return word;
 }
 }
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public final class EncryptDecrypt {
	private static EncryptDecrypt instance;

	private EncryptDecrypt() {
	}

	public synchronized String encrypt(String plaintext) {
		if (plaintext == null) {
			return "";
		}
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA"); // step 2
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getStackTrace());
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getStackTrace());
		}

		byte raw[] = md.digest(); // step 4
		String hash = (new BASE64Encoder()).encode(raw); // step 5
		return hash; // step 6
	}

	public static synchronized EncryptDecrypt getInstance() // step 1
	{
		if (instance == null) {
			instance = new EncryptDecrypt();
		}
		return instance;
	}
}
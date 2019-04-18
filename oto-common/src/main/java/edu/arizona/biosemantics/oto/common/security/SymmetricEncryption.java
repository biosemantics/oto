package edu.arizona.biosemantics.oto.common.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.Configuration;
import javax.xml.bind.DatatypeConverter;

public class SymmetricEncryption {

	private Cipher cipher;
	private SecretKeySpec keySpec;
	private static SymmetricEncryption instance;
		
	public SymmetricEncryption(String secret) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {	    
		cipher = Cipher.getInstance("AES");
		byte key[] = Arrays.copyOfRange(secret.getBytes(), 0, 16);
		keySpec = new SecretKeySpec(key, "AES");	
	}
	
	public byte[] encrypt(String input) throws ShortBufferException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		return cipher.doFinal(input.getBytes());
	}

	public String decrypt(byte[] input) throws ShortBufferException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decrypted = cipher.doFinal(input);
		return new String(decrypted);
	}
	
	public static void main(String[] args) throws Exception {
			SymmetricEncryption symmetricEncryption = new SymmetricEncryption("secret");
			String orig = "test";
			byte[] enc = symmetricEncryption.encrypt(orig);
			
			String hash = DatatypeConverter.printBase64Binary(enc);
			//System.out.println(hash);
			byte[] d = DatatypeConverter.parseBase64Binary(hash);
			
			String decr = symmetricEncryption.decrypt(d);
			//System.out.println(orig);
			//System.out.println(enc);
			//System.out.println(decr);
	}
}

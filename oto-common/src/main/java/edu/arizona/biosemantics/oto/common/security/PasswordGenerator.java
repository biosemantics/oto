package edu.arizona.biosemantics.oto.common.security;

public class PasswordGenerator {
	public static final int MIN_LENGTH = 10;

	protected static java.util.Random r = new java.util.Random();

	protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '!', '@', '#', '$', '%', '&', '*', '(', ')', '+' };

	public static String getNext(int length) {
		if (length < 7) {
			throw new IllegalArgumentException("password length must be over 6: "
					+ length);
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(goodChar[r.nextInt(goodChar.length)]);
		}
		return sb.toString();
	}

	public String generatePassword() {
		return getNext(MIN_LENGTH);
	}
}

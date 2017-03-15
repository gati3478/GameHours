package gh.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public static final String ATTRIBUTE_NAME = "hasher";

	/**
	 * Hashes given text with SHA encryption algorithm.
	 * 
	 * @param text
	 *            Any String.
	 * @return Returns hashed text.
	 * @throws NoSuchAlgorithmException
	 */
	public static String hashText(String text) throws NoSuchAlgorithmException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA");
		md.update(text.getBytes());
		return hexToString(md.digest());
	}

	/*
	 * Given a byte[] array, produces a hex String, such as "234a6f". with 2
	 * chars for each byte in the array. (provided code)
	 */
	private static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff; // remove higher bits, sign
			if (val < 16)
				buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}

}

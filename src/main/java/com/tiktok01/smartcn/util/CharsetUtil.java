package com.tiktok01.smartcn.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CharsetUtil {

	public static final int GB2312_FIRST_CHAR = 1410;
	public static final int GB2312_CHAR_NUM = 87 * 94;
	public static final int CHAR_NUM_IN_FILE = 6768;

	public final static byte[] intToLEBytes(int i) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(i);
		return buffer.array();
	}
	
	public final static int intFromLEBytes(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.getInt();
	}

	public final static String getCCByGB2312Id(int ccid) {
		if (ccid < 0 || ccid > GB2312_CHAR_NUM)
			return "";
		int cc1 = ccid / 94 + 161;
		int cc2 = ccid % 94 + 161;
		byte[] buffer = new byte[2];
		buffer[0] = (byte) cc1;
		buffer[1] = (byte) cc2;
		try {
			String cchar = new String(buffer, "GB2312");
			return cchar;
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public final static short getGB2312Id(char ch) {
		try {
			byte[] buffer = Character.toString(ch).getBytes("GB2312");
			if (buffer.length != 2) {
				return -1;
			}
			int b0 = (buffer[0] & 0x0FF) - 161;
			int b1 = (buffer[1] & 0x0FF) - 161;
			return (short) (b0 * 94 + b1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final static byte[] toGB2312Bytes(String s) throws Exception {
		return s.getBytes("GB2312");
	}
}

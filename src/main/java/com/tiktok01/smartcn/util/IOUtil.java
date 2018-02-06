package com.tiktok01.smartcn.util;

import java.io.Closeable;

public class IOUtil {

	public final static void closeMulti(Closeable...closeables) {
		if(closeables == null || closeables.length <= 0) {
			return;
		}
		for(Closeable closeable : closeables) {
			closeSingle(closeable);
		}
	}
	
	public final static void closeSingle(Closeable closeable) {
		if(closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

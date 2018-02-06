package org.apache.lucene.analysis.cn.smart.hhmm;

import java.lang.reflect.Method;

public class DictionaryReloader {

	public final static void reload(String dctroot) {
		reloadCoreMem(dctroot);
		reloadBigramMem(dctroot);
	}
	
	private final static void reloadCoreMem(String dctroot) {
		WordDictionary dict = WordDictionary.getInstance();
		dict.load(dctroot);
	}
	
	private final static void reloadBigramMem(String dctroot) {
		BigramDictionary dict = BigramDictionary.getInstance();
		try {
			Method method = BigramDictionary.class.getDeclaredMethod("load", String.class);
			method.setAccessible(true);
			method.invoke(dict, dctroot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

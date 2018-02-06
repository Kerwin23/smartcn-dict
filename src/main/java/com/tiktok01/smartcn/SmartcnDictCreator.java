package com.tiktok01.smartcn;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tiktok01.smartcn.util.CharsetUtil;
import com.tiktok01.smartcn.util.IOUtil;

public class SmartcnDictCreator {

	public final static String TYPE_CORE = "core";
	public final static String TYPE_BIGRAM = "bigram";
	private final static String CORE_DICT_NAME = "new_coredict.dct";
	private final static String BIGRAM_DICT_NAME = "new_bigramdict.dct";
	
	private String type;
	
	public SmartcnDictCreator(String type) {
		super();
		this.type = type;
	}
	
	public void create(String dir, Map<String, List<TermFrequency>> charTFsMap, Map<String, Integer> delimiterFreqsMap) {
		String filepath = getFileName(dir);
		OutputStream oStream = null;
		try {
			oStream = new FileOutputStream(filepath);
			for (int i = CharsetUtil.GB2312_FIRST_CHAR; i < CharsetUtil.GB2312_FIRST_CHAR + CharsetUtil.CHAR_NUM_IN_FILE; i++) {
				if(i == 3755 + CharsetUtil.GB2312_FIRST_CHAR && TYPE_CORE.equals(type)) {
					writeDelimiters(oStream, delimiterFreqsMap);
					continue;
				}
				String cc = CharsetUtil.getCCByGB2312Id(i);
				List<TermFrequency> tfs = charTFsMap.get(cc);
				if(tfs == null || tfs.isEmpty()) {
					writeEmpty(oStream);
				} else {
					writeTFs(oStream, tfs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeSingle(oStream);
		}
	}
	
	private String getFileName(String dir) {
		String filename = null;
		if(TYPE_CORE.equals(type)) {
			filename = CORE_DICT_NAME;
		} else {
			filename = BIGRAM_DICT_NAME;
		}
		return dir + "/" + filename;
	}
	
	private void writeDelimiters(OutputStream oStream, Map<String, Integer> delimiterFreqsMap) throws Exception {
		int cnt = delimiterFreqsMap.size();
		writeInt(oStream, cnt);
		Set<String> keys = delimiterFreqsMap.keySet();
		for(String key : keys) {
			int freq = delimiterFreqsMap.get(key);
			writeInt(oStream, freq);
			byte[] deliBs = CharsetUtil.toGB2312Bytes(key);
			int len = deliBs.length;
			writeInt(oStream, len);
			writeInt(oStream, 0);
			oStream.write(deliBs);
		}
	}
	
	private void writeTFs(OutputStream oStream, List<TermFrequency> tfs) throws Exception {
		int cnt = tfs.size();
		writeInt(oStream, cnt);
		for(TermFrequency tf : tfs) {
			writeTF(oStream, tf);
		}
	}
	
	private void writeTF(OutputStream oStream, TermFrequency tf) throws Exception {
		int freq = tf.getFrequency();
		writeInt(oStream, freq);
		String term = tf.getTerm();
		byte[] termBs = CharsetUtil.toGB2312Bytes(term);
		int len = termBs.length;
		writeInt(oStream, len);
		writeInt(oStream, 0);
		oStream.write(termBs);
	}
	
	private void writeEmpty(OutputStream oStream) throws Exception {
		writeInt(oStream, 0);
	}
	
	private void writeInt(OutputStream oStream, int i) throws Exception {
		byte[] bytes = CharsetUtil.intToLEBytes(i);
		oStream.write(bytes);
	}
}

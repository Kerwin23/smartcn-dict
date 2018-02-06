package com.tiktok01.smartcn.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;

import com.tiktok01.smartcn.TermFrequency;

/**
 * 原始dict文件工具类
 * 
 * @author Kerwin
 *
 */
public class SourceDictUtil {

	public final static String KEY_DELIMITER_FREQ = "delimiterFreqs";
	public final static String KEY_TERM_FREQ = "termFreqs";
	private final static ThreadLocalRandom random = ThreadLocalRandom.current();
	public final static Map<String, Integer> defaultDelimiterFreqsMap = new HashMap<String, Integer>();

	static {
		defaultDelimiterFreqsMap.put("，", 20);
		defaultDelimiterFreqsMap.put("“", 20);
		defaultDelimiterFreqsMap.put("”", 20);
		defaultDelimiterFreqsMap.put("：", 20);
		defaultDelimiterFreqsMap.put("。", 20);
	}

	/**
	 * 读取自定义字典文件
	 * 
	 * @param filepath
	 * @return
	 */
	public final static Map<String, List<TermFrequency>> readCustomDict(String filepath) {
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new FileReader(filepath));
			return readCustomDict(bReader);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, List<TermFrequency>>();
		} finally {
			IOUtil.closeMulti(bReader);
		}
	}

	private final static Map<String, List<TermFrequency>> readCustomDict(BufferedReader bReader) throws Exception {
		Map<String, Integer> sourceDatas = readAndMergeSameTerm(bReader);
		return transSource(sourceDatas);
	}

	private final static Map<String, Integer> readAndMergeSameTerm(BufferedReader bReader) throws Exception {
		String line = null;
		Map<String, Integer> termWithFreqMap = new HashMap<String, Integer>();
		while ((line = bReader.readLine()) != null) {
			int index = line.lastIndexOf(" ");
			String term = null;
			String sFreq = null;
			if(index < 0) {
				term = line.trim();
			} else {
				term = line.substring(0, index);
				sFreq = line.substring(index + 1);
			}
			term = fetchCnTerm(term);
			if(StringUtils.isBlank(term)) {
				continue;
			}
			Integer freq = termWithFreqMap.get(term);
			if (freq == null) {
				freq = 0;
			}
			if (StringUtils.isNotBlank(sFreq) && StringUtils.isNumeric(sFreq)) {
				freq += Integer.parseInt(sFreq);
			} else {
				freq += random.nextInt(100);
			}
			termWithFreqMap.put(term, freq);
		}
		return termWithFreqMap;
	}
	
	private final static String fetchCnTerm(String src) {
		if(StringUtils.isBlank(src)) {
			return "";
		}
		String tmpTerm = src.replaceAll(" ", "");
		int subIndex = -1;
		int len = tmpTerm.length();
		int firstCcid = CharsetUtil.GB2312_FIRST_CHAR;
		int lastCcid = CharsetUtil.GB2312_FIRST_CHAR + CharsetUtil.GB2312_CHAR_NUM;
		for(int i = 0; i < len; i++) {
			char ch = tmpTerm.charAt(i);
			int ccid = CharsetUtil.getGB2312Id(ch);
			if(ccid >= firstCcid && ccid <= lastCcid) {
				subIndex = i;
				break;
			}
		}
		if(subIndex != -1) {
			return tmpTerm.substring(subIndex);
		}
		return "";
	}
	
	private final static Map<String, List<TermFrequency>> transSource(Map<String, Integer> source) {
		Set<String> keys = source.keySet();
		Map<String, List<TermFrequency>> charTermFreqsMap = new HashMap<String, List<TermFrequency>>();
		for (String key : keys) {
			Integer freq = source.get(key);
			String ch = key.substring(0, 1);
			List<TermFrequency> tfs = charTermFreqsMap.get(ch);
			if (tfs == null) {
				tfs = new ArrayList<TermFrequency>();
			}
			TermFrequency tf = new TermFrequency(key.substring(1), freq);
			tfs.add(tf);
			charTermFreqsMap.put(ch, tfs);
		}
		return charTermFreqsMap;
	}

	/**
	 * 从coredict.mem文件格式中反解
	 * 
	 * @param filepath
	 * @return
	 */
	public final static Map<String, Map<String, ?>> reverseFromCoreMem(String filepath) {
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(filepath);
			return readFromCoreMem(iStream);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, Map<String, ?>>();
		} finally {
			IOUtil.closeMulti(iStream);
		}
	}

	private final static Map<String, Map<String, ?>> readFromCoreMem(InputStream iStream) throws Exception {
		ObjectInputStream oiStream = null;
		Map<String, List<TermFrequency>> charTermFreqsMap = new HashMap<String, List<TermFrequency>>();
		Map<String, Integer> delimiterFreqsMap = new HashMap<String, Integer>();
		try {
			oiStream = new ObjectInputStream(iStream);
			oiStream.readObject();
			oiStream.readObject();
			char[][][] wordItemCharArrayTable = (char[][][]) oiStream.readObject();
			int[][] wordItemFrequencyTable = (int[][]) oiStream.readObject();
			readCoreMemFromArrays(wordItemCharArrayTable, wordItemFrequencyTable, charTermFreqsMap, delimiterFreqsMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtil.closeMulti(oiStream);
		}
		 mergeSameTerms(charTermFreqsMap);
		 Map<String, Map<String, ?>> resultMap = new HashMap<String, Map<String,?>>();
		 resultMap.put(KEY_DELIMITER_FREQ, delimiterFreqsMap);
		 resultMap.put(KEY_TERM_FREQ, charTermFreqsMap);
		 return resultMap;
	}
	
	private final static void readCoreMemFromArrays(char[][][] wordItemCharArrayTable, int[][] wordItemFrequencyTable, Map<String, List<TermFrequency>> charTermFreqsMap, Map<String, Integer> delimiterFreqsMap) {
		int length = wordItemCharArrayTable.length;
		for(int i = 0; i < length; i++) {
			char[][] termsArray = wordItemCharArrayTable[i];
			if(termsArray == null) {
				continue;
			}
			String cc = CharsetUtil.getCCByGB2312Id(i);
			if(i < CharsetUtil.GB2312_FIRST_CHAR) {
				readCoreMemDelimeter(cc, termsArray, wordItemFrequencyTable[i], delimiterFreqsMap);
			} else {
				readCoreMemTerms(cc, termsArray, wordItemFrequencyTable[i], charTermFreqsMap);
			}
		}
	}
	
	private final static void readCoreMemTerms(String cc, char[][] termsArray, int[] freqArray, Map<String, List<TermFrequency>> charTermFreqsMap) {
		List<TermFrequency> tfs = new ArrayList<TermFrequency>();
		int cnt = termsArray.length;
		for(int i = 0; i < cnt; i++) {
			char[] termArray = termsArray[i];
			if(termArray == null) {
				continue;
			}
			String term = new String(termArray);
			int freq = freqArray[i];
			TermFrequency tf = new TermFrequency(term, freq);
			tfs.add(tf);
		}
		charTermFreqsMap.put(cc, tfs);
	}
	
	private final static void readCoreMemDelimeter(String cc, char[][] termsArray, int[] freqArray, Map<String, Integer> delimiterFreqsMap) {
		int freq = 0;
		int cnt = termsArray.length;
		for(int i = 0; i < cnt; i++) {
			char[] termArray = termsArray[i];
			if(termArray == null) {
				continue;
			}
			freq += freqArray[i];
		}
		delimiterFreqsMap.put(cc, freq);
	}
	

	public final static void mergeSameTerms(Map<String, List<TermFrequency>> charTFsMap) {
		Set<String> keys = charTFsMap.keySet();
		for (String key : keys) {
			List<TermFrequency> tfs = charTFsMap.get(key);
			tfs = mergeSameTerms(tfs);
			charTFsMap.put(key, tfs);
		}
	}

	private final static List<TermFrequency> mergeSameTerms(List<TermFrequency> tfs) {
		Map<String, TermFrequency> tfMap = new HashMap<String, TermFrequency>();
		for (TermFrequency tf : tfs) {
			String term = tf.getTerm();
			TermFrequency oldTF = tfMap.get(term);
			if (oldTF != null) {
				int freq = oldTF.getFrequency() + tf.getFrequency();
				oldTF.setFrequency(freq);
			} else {
				tfMap.put(term, tf);
			}
		}
		return new ArrayList<TermFrequency>(tfMap.values());
	}

	public final static Map<String, List<TermFrequency>> mergeTFsMap(Map<String, List<TermFrequency>> tfsMap1,
			Map<String, List<TermFrequency>> tfsMap2) {
		Map<String, List<TermFrequency>> newTFsMap = new HashMap<String, List<TermFrequency>>();
		Set<String> keys = new HashSet<String>();
		keys.addAll(tfsMap1.keySet());
		keys.addAll(tfsMap2.keySet());
		for (String key : keys) {
			List<TermFrequency> tfs1 = tfsMap1.get(key);
			List<TermFrequency> tfs2 = tfsMap2.get(key);
			List<TermFrequency> tfs = new ArrayList<TermFrequency>();
			if (tfs1 != null) {
				tfs.addAll(tfs1);
			}
			if (tfs2 != null) {
				tfs.addAll(tfs2);
			}
			newTFsMap.put(key, tfs);
		}
		mergeSameTerms(newTFsMap);
		return newTFsMap;
	}
}

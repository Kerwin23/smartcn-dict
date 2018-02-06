package com.tiktok01.smartcn;

import java.util.List;
import java.util.Map;

import com.tiktok01.smartcn.util.SourceDictUtil;

public class DictCreatorTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		try {
			Map<String, Map<String, ?>> resultMap = SourceDictUtil.reverseFromCoreMem("E:/test/lucene/src_coredict.mem");
			System.out.println("read src complete");
			String dir = "E:/test/lucene";
			Map<String, List<TermFrequency>> srcCharTFsMap = (Map<String, List<TermFrequency>>) resultMap.get(SourceDictUtil.KEY_TERM_FREQ);
			Map<String, List<TermFrequency>> charTFsMap = SourceDictUtil.readCustomDict("E:/test/lucene/src.dct");
			charTFsMap = SourceDictUtil.mergeTFsMap(charTFsMap, srcCharTFsMap);
			System.out.println(charTFsMap.size());
			Map<String, Integer> deliFreqsMap = (Map<String, Integer>) resultMap.get(SourceDictUtil.KEY_DELIMITER_FREQ);
			if(deliFreqsMap == null) {
				deliFreqsMap = SourceDictUtil.defaultDelimiterFreqsMap;
			} else {
				deliFreqsMap.putAll(SourceDictUtil.defaultDelimiterFreqsMap);
			}
			SmartcnDictCreator coreDictCreator = new SmartcnDictCreator(SmartcnDictCreator.TYPE_CORE);
			coreDictCreator.create(dir, charTFsMap, deliFreqsMap);
			SmartcnDictCreator bigramDictCreator = new SmartcnDictCreator(SmartcnDictCreator.TYPE_BIGRAM);
			bigramDictCreator.create(dir, charTFsMap, deliFreqsMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

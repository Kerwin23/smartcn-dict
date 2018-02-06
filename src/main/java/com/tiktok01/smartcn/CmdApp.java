package com.tiktok01.smartcn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.tiktok01.smartcn.cmd.CmdParser;
import com.tiktok01.smartcn.cmd.Command;
import com.tiktok01.smartcn.util.SourceDictUtil;

public class CmdApp {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Command command = CmdParser.parse(args);
		Map<String, List<TermFrequency>> cnTFsMap = new HashMap<String, List<TermFrequency>>();
		Map<String, Integer> deliFreqsMap = SourceDictUtil.defaultDelimiterFreqsMap;
		if(StringUtils.isNotBlank(command.getSrcCoreMem())) {
			Map<String, Map<String, ?>> memDataMap = SourceDictUtil.reverseFromCoreMem(command.getSrcCoreMem());
			Map<String, List<TermFrequency>> srcCnTFsMap = (Map<String, List<TermFrequency>>) memDataMap.get(SourceDictUtil.KEY_TERM_FREQ);
			if(srcCnTFsMap != null) {
				cnTFsMap = srcCnTFsMap;
			}
			Map<String, Integer> srcDeliFreqsMap = (Map<String, Integer>) memDataMap.get(SourceDictUtil.KEY_DELIMITER_FREQ);
			if(srcDeliFreqsMap != null) {
				deliFreqsMap.putAll(srcDeliFreqsMap);
			}
		}
		if(StringUtils.isNotBlank(command.getSrcDict())) {
			Map<String, List<TermFrequency>> dctTFsMap = SourceDictUtil.readCustomDict(command.getSrcDict());
			if(dctTFsMap != null) {
				cnTFsMap = SourceDictUtil.mergeTFsMap(cnTFsMap, dctTFsMap);
			}
		}
		SmartcnDictCreator coreDictCreator = new SmartcnDictCreator(SmartcnDictCreator.TYPE_CORE);
		coreDictCreator.create(command.getTargetDir(), cnTFsMap, deliFreqsMap);
		SmartcnDictCreator bigramDictCreator = new SmartcnDictCreator(SmartcnDictCreator.TYPE_BIGRAM);
		bigramDictCreator.create(command.getTargetDir(), cnTFsMap, deliFreqsMap);
	}
}

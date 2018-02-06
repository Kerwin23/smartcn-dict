package com.tiktok01.smartcn;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.ExtSmartcnAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerTest {

	public static void main(String[] args) {
	    TokenStream tokenStream = null;
	    String analyzeStr = "《呐喊》收录了鲁迅从1918年至1922年所创作的14篇短篇小说，其中包括《狂人日记》、《孔乙己》、《药》、《明天》、《一件小事》、《头发的故事》、《风波》、《故乡》、《阿Q正传》、《端午节》、《白光》、《兔和猫》、《鸭的喜剧》以及《社戏》。其中《狂人日记》、《阿Q正传》、《孔乙己》、《故乡》等都已成为中国散文的名篇，艺术价值极高。这部短篇集诞生于五四运动及新文化运动的大背景之下。《呐喊》不仅是新文化运动的一面旗帜，更是鲁迅对封建旧礼教、旧思想开战的有力宣言";
	    List<String> response = new ArrayList<String>();
	    try {  
	    	Analyzer analyzer = new ExtSmartcnAnalyzer();
	        tokenStream = analyzer.tokenStream("content", new StringReader(analyzeStr));  
	        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);  
	        tokenStream.reset();  
	        while (tokenStream.incrementToken()) {  
	            response.add(attr.toString());  
	        }  
	        System.out.println(response);
	        analyzer.close();
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (tokenStream != null) {  
	            try {  
	                tokenStream.close();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	}
}

package org.apache.lucene.analysis.cn.smart;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.cn.smart.hhmm.DictionaryReloader;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.util.IOUtils;

public class ExtSmartcnAnalyzer extends Analyzer {
	private final CharArraySet stopWords;

	private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";

	private static final String STOPWORD_FILE_COMMENT = "//";

	public static CharArraySet getDefaultStopSet() {
		return DefaultSetHolder.DEFAULT_STOP_SET;
	}

	private static class DefaultSetHolder {
		static final CharArraySet DEFAULT_STOP_SET;

		static {
			try {
				DEFAULT_STOP_SET = loadDefaultStopWordSet();
			} catch (IOException ex) {
				throw new RuntimeException("Unable to load default stopword set");
			}
		}

		static CharArraySet loadDefaultStopWordSet() throws IOException {
			return CharArraySet
					.unmodifiableSet(WordlistLoader.getWordSet(IOUtils.getDecodingReader(SmartChineseAnalyzer.class,
							DEFAULT_STOPWORD_FILE, StandardCharsets.UTF_8), STOPWORD_FILE_COMMENT));
		}
	}

	public ExtSmartcnAnalyzer() {
		this(true);
	}

	public ExtSmartcnAnalyzer(boolean useDefaultStopWords) {
		stopWords = useDefaultStopWords ? DefaultSetHolder.DEFAULT_STOP_SET : CharArraySet.EMPTY_SET;
	}

	public ExtSmartcnAnalyzer(CharArraySet stopWords) {
		this.stopWords = stopWords == null ? CharArraySet.EMPTY_SET : stopWords;
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName) {
		DictionaryReloader.reload("E:/test/lucene");
		final Tokenizer tokenizer = new HMMChineseTokenizer();
		TokenStream result = tokenizer;
		result = new PorterStemFilter(result);
		if (!stopWords.isEmpty()) {
			result = new StopFilter(result, stopWords);
		}
		return new TokenStreamComponents(tokenizer, result);
	}

	@Override
	protected TokenStream normalize(String fieldName, TokenStream in) {
		return new LowerCaseFilter(in);
	}
}

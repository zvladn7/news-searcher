package ru.spbstu.news.searcher.indexes;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.LengthFilterFactory;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.TokenizerChain;

import java.util.HashMap;
import java.util.Map;

public class AnalyzerProvider {

    public static Analyzer provide() {
        Map<String, String> factoryParams = new HashMap<>();
        factoryParams.put(AbstractAnalysisFactory.LUCENE_MATCH_VERSION_PARAM, Version.LUCENE_8_10_0.toString());

        final HashMap<String, String> lengthFilterConfig = new HashMap<>();
        lengthFilterConfig.put("min",   "10");
        lengthFilterConfig.put("max", "100000");
        lengthFilterConfig.put(AbstractAnalysisFactory.LUCENE_MATCH_VERSION_PARAM, Version.LUCENE_8_10_0.toString());
        return new TokenizerChain(
                new StandardTokenizerFactory(factoryParams),
                new TokenFilterFactory[] {
                        new LengthFilterFactory(lengthFilterConfig),
                        new WordDelimiterGraphFilterFactory(factoryParams),
                        new LowerCaseFilterFactory(factoryParams),
                        new RemoveDuplicatesTokenFilterFactory(factoryParams)
                }
        );
    }

}

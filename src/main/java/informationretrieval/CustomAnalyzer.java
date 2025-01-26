package informationretrieval;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.CharArraySet;
import java.io.IOException;
import java.util.Arrays;

public class CustomAnalyzer extends Analyzer {

    private static final CharArraySet STOP_WORDS_SET = new CharArraySet(
        Arrays.asList("a", "about", "above", "after", "again", "against", "all", "am", "an", 
                      "the", "to", "and", "in", "that", "it", "is", "was", "for", "on", 
                      "are", "with", "as", "by", "at", "from"), true
    );

    private SynonymMap synonymMap;

    public CustomAnalyzer() {
        try {
            SynonymMap.Builder builder = new SynonymMap.Builder(true);
            builder.add(new CharsRef("conflict"), new CharsRef("war"), true);
            synonymMap = builder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        tokenStream = new StopFilter(tokenStream, STOP_WORDS_SET);
        tokenStream = new SynonymFilter(tokenStream, synonymMap, true);
        tokenStream = new LengthFilter(tokenStream, 2, 20);
        tokenStream = new PorterStemFilter(tokenStream);

        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}

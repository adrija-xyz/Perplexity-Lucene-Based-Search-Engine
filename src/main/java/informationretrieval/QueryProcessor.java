package informationretrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

// Lucene analyzers
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

// Lucene scoring
import org.apache.lucene.search.similarities.BM25Similarity;

// Lucene queries and search
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

// Other Lucene imports
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class QueryProcessor {

    private QueryProcessor() {
        // Private constructor to prevent instantiation
    }

    public static void main(String[] args) throws Exception {

        //-------------- Initialize reader, writer, and searcher --------------

        String indexDirectory = "./index-files";
        String outputFilePath = "../results_100_custom.txt";
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectory)));
        PrintWriter resultWriter = new PrintWriter(outputFilePath, StandardCharsets.UTF_8);
        IndexSearcher searchEngine = new IndexSearcher(indexReader);

        //-------------- Choose Analyzer --------------

//        Analyzer analyzer = new EnglishAnalyzer();
        Analyzer analyzer = new CustomAnalyzer();

        //-------------- Choose Similarity/Scoring Model --------------

        searchEngine.setSimilarity(new BM25Similarity());

        //-------------- Reading and Parsing Queries --------------

        String queryFilePath = "C:\\Users\\Subhayan Das\\Desktop\\Course\\Information Retrieval\\Assignment 2\\Assignment Two\\parsed_topics_new.txt";
        BufferedReader queryReader = Files.newBufferedReader(Paths.get(queryFilePath), StandardCharsets.UTF_8);

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[]{"title", "content"}, analyzer);  // Adjusted fields to match indexed fields

        String header = "", description = "", narrative = "";
        int queryID = 0;
        String currentLine;

        System.out.println("Processing queries and generating search results.");

        while ((currentLine = queryReader.readLine()) != null) {
            currentLine = currentLine.trim();

            if (currentLine.startsWith(".T")) {
                if (queryID > 0) { // Process the previous query
                    processQuery(queryParser, searchEngine, resultWriter, queryID, header, description, narrative);
                }

                // Extract query ID from the current line
                queryID = Integer.parseInt(currentLine.substring(2).trim());
                header = "";
                description = "";
                narrative = "";
            } 
            else if (currentLine.startsWith(".H")) {
                header = currentLine.substring(2).trim();
            } 
            else if (currentLine.startsWith(".D")) {
                description += currentLine.substring(2).trim() + " ";
            } 
            else if (currentLine.startsWith(".N")) {
                narrative += currentLine.substring(2).trim() + " ";
            }
        }

        // Process the final query
        if (queryID > 0) {
            processQuery(queryParser, searchEngine, resultWriter, queryID, header, description, narrative);
        }

        System.out.println("Processing Completed!");
        resultWriter.close();
        indexReader.close();
    }

    private static void processQuery(MultiFieldQueryParser queryParser, IndexSearcher searchEngine,
                                     PrintWriter resultWriter, int queryID, String header,
                                     String description, String narrative) throws Exception {
        String queryText = String.format("title:(%s) content:(%s)",  // Adjusted to match the fields
                QueryParser.escape(header), QueryParser.escape(description));
        Query luceneQuery = queryParser.parse(queryText);
        executeSearch(searchEngine, resultWriter, queryID, luceneQuery);
    }

    private static void executeSearch(IndexSearcher searchEngine, PrintWriter resultWriter,
                                      int queryID, Query query) throws IOException {
        // Adjust the number of results to retrieve
        TopDocs searchResults = searchEngine.search(query, 100); // Can be adjusted as needed
        ScoreDoc[] hitDocs = searchResults.scoreDocs;

        System.out.println("Query id: " + queryID + " - Total hits: " + hitDocs.length);

        for (int rank = 0; rank < hitDocs.length; rank++) { // Start at rank 0 to include all results
            Document doc = searchEngine.doc(hitDocs[rank].doc);
            resultWriter.println(queryID + " 0 " + doc.get("id") + " " + (rank + 1) + " " +
                    hitDocs[rank].score + " EXP");
        }
    }
}

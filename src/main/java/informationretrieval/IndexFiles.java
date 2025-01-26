package informationretrieval;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import loader.LoaderForAll;
import java.io.IOException;
import java.nio.file.Paths;

public class IndexFiles {

    private IndexWriter writer;

    public IndexFiles(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));

        Analyzer analyzer = new CustomAnalyzer(); //Using CustomAnalyzer
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, config);
    }

    public void addDocument(Document doc) throws IOException {
        writer.addDocument(doc);
    }

    public void close() throws IOException {
        writer.close();
    }

    public static void main(String[] args) {
        try {
            IndexFiles indexer = new IndexFiles("./index-files"); //output

            // Assuming you want to load all files from the 'all_parsed' directory
            String directoryPath = "./all_parsed";
            
            // Now we call the method to load and index all documents from the directory
            LoaderForAll.createDocumentsFromText(directoryPath, indexer);

            indexer.close();
            System.out.println("Indexing complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

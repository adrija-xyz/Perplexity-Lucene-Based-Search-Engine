package loader;

import informationretrieval.IndexFiles; // Add this import
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoaderForAll {

    public static void createDocumentsFromText(String directoryPath, IndexFiles indexer) throws IOException {
        Path dirPath = Paths.get(directoryPath);

        // Iterate over each .txt file in the directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
            for (Path entry : stream) {
                String content = new String(Files.readAllBytes(entry)); // Read the file content
                Document doc = createDocumentFromFileContent(content);
                indexer.addDocument(doc);  // Add document to the index
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading files from the directory.", e);
        }
    }

    private static Document createDocumentFromFileContent(String content) {
        Document doc = new Document();

        // Extract and store document data
        String docNo = extract(content, "Document Number:", "\n");
        String title = extract(content, "Title:", "\n");
        String documentContent = extractFullContent(content);

        // Store the docNo as the unique ID of the document
        doc.add(new StringField("id", docNo, Field.Store.YES)); // Set 'docNo' as the ID field

        // Add fields to the document
        doc.add(new TextField("title", title, Field.Store.YES));  // Store the title
        doc.add(new TextField("content", documentContent, Field.Store.YES));  // Store the content of the document

        // Print out the document (optional)
        System.out.println(doc);

        return doc;
    }

    private static String extract(String content, String startDelimiter, String endDelimiter) {
        int startIndex = content.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = content.indexOf(endDelimiter, startIndex);
        if (startIndex < endIndex && startIndex != -1) {
            return content.substring(startIndex, endIndex).trim();
        }
        return "";
    }

    private static String extractFullContent(String content) {
        // Assuming "Content:" is the start of the detailed content
        int contentStartIndex = content.indexOf("Content:") + "Content:".length();
        return content.substring(contentStartIndex).trim();
    }
}

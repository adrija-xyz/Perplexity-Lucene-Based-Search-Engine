package loader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoadLATimes {

    public static Document createDocumentFromText(String directoryPath) throws IOException {
        Path dirPath = Paths.get(directoryPath);
        List<String> fileContents = new ArrayList<>();

        // Walk through the directory and read all .txt files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
            for (Path entry : stream) {
                String content = new String(Files.readAllBytes(entry)); // Read the file content
                fileContents.add(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading files from the directory.", e);
        }

        // Combine the contents into a single string
        String combinedContent = String.join("\n\n", fileContents); // Add a blank line between documents

        Document doc = new Document();

        // Extract and store document data
        String docNo = extract(combinedContent, "Document Number:", "\n");
        String title = extract(combinedContent, "Title:", "\n");
        String content = extractFullContent(combinedContent);

        // Store the docNo as the unique ID of the document
        doc.add(new StringField("id", docNo, Field.Store.YES)); // Set 'docNo' as the ID field

        // Add fields to the document
        if (!title.isEmpty()) {
            doc.add(new TextField("title", title, Field.Store.YES));  // Store the title
        }
        if (!content.isEmpty()) {
            doc.add(new TextField("content", content, Field.Store.YES));  // Store the content of the document
        }

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
        if (contentStartIndex != -1) {
            return content.substring(contentStartIndex).trim();
        }
        return "";
    }

}

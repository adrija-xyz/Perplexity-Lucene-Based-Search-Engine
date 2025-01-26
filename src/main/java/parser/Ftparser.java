package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Ftparser {
	public static void main(String[] args) throws IOException {
        // Path to the FBIS files and the output directory for individual documents
        String fbisDirPath = "./ft/ft944";
        String outputDirPath = "./ft_parsed/ft944/";

        // Create the output directory if it doesn't exist
        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Parsing each FBIS file in the directory
        File fbisDir = new File(fbisDirPath);
        File[] files = fbisDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(file, "UTF-8");
                    Elements docs = jsoupDoc.select("DOC");
                    
                    for (Element docElement : docs) {
                        // Extracting fields from each <DOC> with null checks
                        String docNo = (docElement.selectFirst("DOCNO") != null) ? docElement.selectFirst("DOCNO").text() : "NoDocno";
                        String header = (docElement.selectFirst("HEADLINE") != null) ? docElement.selectFirst("HEADLINE").text() : "NoTitle";
                        String text = (docElement.selectFirst("TEXT") != null) ? docElement.selectFirst("TEXT").text() : "NoContent";

                        // Creating a unique file for each document using the docNo as filename
                        File outputFile = new File(outputDirPath + docNo + ".txt");

                        // Writing the document content to the separate file
                        try (FileWriter writer = new FileWriter(outputFile)) {
                            writer.write("Document Number: " + docNo + "\n");
                            writer.write("Title: " + header + "\n");
                            writer.write("Content:\n" + text + "\n");
                            writer.write("---------------------------\n");
                        } catch (IOException e) {
                            System.err.println("Error writing to file: " + outputFile.getAbsolutePath());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        System.out.println("Parsing completed successfully. Files saved in: " + outputDirPath);
    }
}

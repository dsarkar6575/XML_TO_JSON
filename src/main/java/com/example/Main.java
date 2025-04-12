package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        String inputFile = "src/main/resources/input.xml";
        String outputFile = "src/main/resources/output.json";
        try {
            String xmlInput = new String(Files.readAllBytes(Paths.get(inputFile)));
            XmlToJsonConverter converter = new XmlToJsonConverter();
            String jsonOutput = converter.convertXmlToJsonWithMatchSummary(xmlInput);
            Files.write(Paths.get(outputFile), jsonOutput.getBytes());
            System.out.println("Conversion complete. Output written to: " + outputFile);
        } catch (IOException | XmlToJsonConverter.XmlConversionException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

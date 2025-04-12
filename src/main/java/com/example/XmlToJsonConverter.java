package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;

public class XmlToJsonConverter {
    private static final Logger logger = LoggerFactory.getLogger(XmlToJsonConverter.class);
    private static final String MATCH_SUMMARY_FIELD = "MatchSummary";
    private static final String TOTAL_MATCH_SCORE_FIELD = "TotalMatchScore";
    private static final String MATCH_DETAILS_PATH = "/Response/ResultBlock/MatchDetails/Match";
    private static final String SCORE_FIELD = "Score";

    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    public XmlToJsonConverter() {
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
    }

    public String convertXmlToJsonWithMatchSummary(String xmlString) throws XmlConversionException {
        if (xmlString == null || xmlString.isBlank()) {
            throw new XmlConversionException("Empty or null XML input");
        }

        try {
            JsonNode rootNode = xmlMapper.readTree(xmlString.getBytes());
            if (!rootNode.has("Response")) {
                ObjectNode wrapped = jsonMapper.createObjectNode();
                wrapped.set("Response", rootNode);
                rootNode = wrapped;
            }
            BigDecimal totalScore = calculateTotalMatchScore(rootNode);
            JsonNode updatedJson = addMatchSummary(rootNode, totalScore);
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(updatedJson);
        } catch (IOException e) {
            throw new XmlConversionException("Error parsing XML", e);
        }
    }

    private BigDecimal calculateTotalMatchScore(JsonNode rootNode) {
        BigDecimal totalScore = BigDecimal.ZERO;
        JsonNode matches = rootNode.at(MATCH_DETAILS_PATH);
        if (matches.isMissingNode()) {
            logger.warn("Match nodes not found in XML");
            return totalScore;
        }

        if (matches.isArray()) {
            for (JsonNode match : matches) {
                totalScore = totalScore.add(parseScore(match));
            }
        } else {
            totalScore = totalScore.add(parseScore(matches));
        }
        return totalScore;
    }

    private BigDecimal parseScore(JsonNode matchNode) {
        try {
            String scoreText = matchNode.path(SCORE_FIELD).asText();
            return scoreText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(scoreText);
        } catch (Exception e) {
            logger.warn("Error parsing score: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private JsonNode addMatchSummary(JsonNode rootNode, BigDecimal totalScore) {
        ObjectNode modifiedRoot = rootNode.deepCopy();
        JsonNode responseNodeRaw = modifiedRoot.get("Response");
        if (!(responseNodeRaw instanceof ObjectNode)) {
            logger.warn("Response node not found or not an object");
            return rootNode;
        }
        ObjectNode responseNode = (ObjectNode) responseNodeRaw;
        JsonNode resultBlockNodeRaw = responseNode.get("ResultBlock");
        if (!(resultBlockNodeRaw instanceof ObjectNode)) {
            logger.warn("ResultBlock node not found or not an object");
            return rootNode;
        }
        ObjectNode resultBlockNode = (ObjectNode) resultBlockNodeRaw;
        ObjectNode matchSummary = jsonMapper.createObjectNode();
        matchSummary.put(TOTAL_MATCH_SCORE_FIELD, totalScore.toString());

        ObjectNode newResultBlock = jsonMapper.createObjectNode();
        newResultBlock.set(MATCH_SUMMARY_FIELD, matchSummary);

        Iterator<String> fields = resultBlockNode.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            newResultBlock.set(field, resultBlockNode.get(field));
        }

        responseNode.set("ResultBlock", newResultBlock);
        return modifiedRoot;
    }

    public static class XmlConversionException extends Exception {
        public XmlConversionException(String msg) {
            super(msg);
        }

        public XmlConversionException(String msg, Throwable t) {
            super(msg, t);
        }
    }
}

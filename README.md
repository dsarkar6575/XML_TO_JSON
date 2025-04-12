# XML to JSON Converter with Match Summary

This project is a Java-based utility that converts an XML input file into a structured JSON format, enriching the result by computing and injecting a `MatchSummary` field containing the total match score.

## Features

- Converts XML input into well-formatted JSON.
- Parses and sums up `Score` fields under `Match` nodes.
- Adds a new JSON field `MatchSummary` inside `ResultBlock`, which includes `TotalMatchScore`.
- Uses Jackson for XML and JSON parsing.
- Provides clear exception handling and logging via SLF4J.

---

## Project Structure

xml-to-json-converter/
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           ├── Main.java
        │           └── XmlToJsonConverter.java
        └── resources
            └── input.xml


### 🛠 Requirements

- Java 24
- Maven build system to handle dependencies
- IDE like  Eclipse 

### Dependencies

This project uses the following libraries:
- `jackson-databind`
- `jackson-dataformat-xml`
- `slf4j-api`
- `slf4j-simple` 

> Add them in your `pom.xml` if using Maven.



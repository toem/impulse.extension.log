<!---
title: "Generic Log Reader Extension"
author: "Thomas Haber"
keywords: [log, impulse, pattern, XML, JSON, YAML, CSV, log analysis, visualization, extension, signal processing]
description: "Provides an extension for the impulse framework to import and analyze generic log data, supporting formats such as Pattern-based, XML, JSON, YAML, and CSV. Enables engineers to visualize and process log and data files from various sources within impulse."
category: "impulse-extension"
tags: [extension, log ]
docID: 1227
hash: 284f875d71cf703336c2e44b945bdb7b
--->
# Generic Log Reader Extension

This package provides an extension for the [impulse](https://www.toem.io/products/impulse) framework, focusing on the import and (optionally) export of generic log and data files.

## About impulse

**impulse** is a powerful visualization and analysis workbench designed to help engineers understand, analyze, and debug complex semiconductor and multi-core software systems. It provides seamless integration into IDE frameworks and enables interoperability with a growing set of extensions, such as language IDEs and reporting tools.

**impulse** is based on a modular framework designed to unify the handling of signal and measurement data from diverse sources. It provides a robust infrastructure for reading, writing, analyzing, and visualizing signals, supporting both simple and highly structured data. impulse's extensible architecture allows developers to add support for new data formats by implementing custom readers and writers, making it a versatile tool for engineers, scientists, and developers.

## About Logging

Engineers add logging to embedded systems to observe behavior, debug problems, and understand what went wrong. They face a fundamental trade-off: logging adds visibility but consumes resources—CPU, memory, storage, power. On resource-constrained devices, engineers choose minimal logging patterns (sprintf to UART); on networked systems, they prefer structured formats for easier cloud analysis. The reality: most systems combine multiple approaches—each module chooses what works for its constraints and workflow. Engineers then struggle to correlate events across these heterogeneous sources, making unified log analysis the missing link for efficient debugging.

## Purpose of this Extension

The Generic Log Reader Extension integrates support for standard log and data formats into impulse, enabling users to seamlessly import, analyze, and visualize log and measurement results from a wide variety of sources. This extension is essential for anyone working with log or structured data, as it bridges the gap between raw log outputs and the advanced analysis capabilities of impulse.

## Supported and Planned Formats

This package currently includes, or is planned to include, readers (and optionally writers) for the following log and data formats:

- **Pattern Log Reader:**  
  Reads line-based logs containing multiple line patterns. Using regular expressions, all relevant information can be extracted and read into struct signals. Comes with pre-defined Log4J pattern support. A powerful UI helps to define and extend reader configurations.

- **XML Log Reader:**  
  A configurable log reader for all kinds of XML (Extensible Markup Language) based log formats. Comes with pre-defined Log4J (1.2 and 2) and Java logging configurations. A powerful UI helps to define and extend reader configurations.

- **JSON Log Reader:**  
  A configurable log reader for all kinds of JSON (JavaScript Object Notation) based log formats. Comes with a pre-defined Log4J configuration. A powerful UI helps to define and extend reader configurations.

- **YAML Log Reader:**  
  A configurable log reader for all kinds of YAML (Yet Another Markup Language) based log formats. Comes with a pre-defined Log4J configuration. A powerful UI helps to define and extend reader configurations.

- CSV Log Reader: 
  CSV Log Reader functionality is part of the core CSV Reader, that can also non-log CSV input.

Additional formats may be added in the future as the needs of the impulse community evolve.

## LLM Tools Included (MCP)

This extension provides an integrated AI-assisted tool for creating and configuring log readers efficiently through the Model Context Protocol (MCP).

### createLogReader Tool (Prompt)

The **createLogReader** tool is an intelligent assistant that guides you through the process of creating a custom log reader configuration tailored to your specific log format.

**What it does:**
- Analyzes your log source file or format information
- Automatically detects the appropriate reader type (Pattern, CSV, XML, JSON, or YAML)
- Generates a pre-configured log reader based on your requirements
- Inserts the configuration directly into your impulse Serializer Preferences

**How it works:**

1. **Source Analysis**: Provide your log file or describe its format. The tool asks clarifying questions if needed to understand the structure.

2. **Format Detection**: The tool identifies which reader type best matches your log format and suggests the most appropriate one.

3. **Documentation Review**: It consults the comprehensive documentation for the selected reader type, including built-in examples and reference configurations.

4. **Configuration Generation**: Based on your log source, the tool generates an XML-based reader configuration and shows it to you for review before insertion.

5. **Direct Integration**: With your approval, the configuration is inserted directly into impulse's Serializer Preferences (requires an active impulse editor window).

6. **Interactive Adjustment**: The reader opens as a dialog in impulse where you can review and adjust settings before using. 

**Usage Steps:** (Availability and detailed usage depends on your platform's LM integration)

1. Open the log file or content in impulse that you want to create a reader for
2. Ensure your AI chat tool has access to the content (file and/or description)
3. Send the `createLogReader` prompt from the MCP server (e.g. in vscode type /createLogReader)
4. Interact with the AI assistant—provide details about your log format and answer clarifying questions
5. Once the reader dialog opens with the prepared configuration:
   - Review the settings
   - Select the reader in the source selection UI
   - Test it with your log data
6. Fine-tune the configuration as needed
7. Close the dialog to save your changes, or press **Discard** to cancel and revert



## Quick Start (no LLM)

Here's how to get started with the Generic Log Reader Extension using the Pattern Log Reader:

1. **Create a sample log file** `application.log` with this content:
   ```
   [INFO] 04-15-2020 10:44:13 com.example.Application main - Application started
   [WARN] 04-15-2020 10:44:14 com.example.Service processRequest - Request timeout detected
   [ERROR] 04-15-2020 10:44:15 com.example.Database connect - Connection failed
   [INFO] 04-15-2020 10:44:16 com.example.Application main - Retrying connection
   [INFO] 04-15-2020 10:44:17 com.example.Database connect - Connection established
   ```

2. **Configure the Pattern Log Reader**:
   * Go to **impulse Preferences → Serializers**
   * Right-click on the root element and select **Add Pattern Log Reader**
   * Choose the predefined configuration: **Log4j [%p] %d{MM-dd-yyyy HH:mm:ss} %c %M - %m%n**
   * Enable the configuration and save

3. **Open and view your log file**:
   * Right-click on `application.log` and select **Open with → impulse Viewer** (you may select impulse Viewer as the default option at this point)
   * On activation, it may take a while (a few seconds) to load the backend Java server. The OS may ask you for approval
   * Accept the license when prompted
   * The log entries will be organized hierarchically by logger (com.example.Application, com.example.Service, etc.)
   * Create a new view
   * Drag and drop signals from the signal tree into the view
   * Observe the timeline with color-coded severity levels (INFO, WARN, ERROR)
   * Hover over entries to see timestamps, methods, and messages
   * Have fun!

For more complex log formats, see the [Pattern Log Reader documentation](doc/pattern-log-reader.md) for details on creating custom patterns.

## License

**Our guiding principle for this and all subsequent versions is:**

* Non-commercial use is free.
* Commercial use requires a commercial license (including a free plan).
* see [LICENSE.md](LICENSE.md)
* see [Plans](https://toem.io/index.php/pricing)

## Documentation / Videos
 
Enter [https://toem.io/resources/](https://toem.io/resources/) for more information about impulse. 

## Status

- **Pattern Log Reader:** Supported (reader implemented "beta")
- **XML Log Reader:** Supported (reader implemented "beta")
- **JSON Log Reader:** Supported (reader implemented "beta")
- **YAML Log Reader:** Supported (reader implemented "beta")

Contributions and feedback are welcome as this extension continues to evolve.

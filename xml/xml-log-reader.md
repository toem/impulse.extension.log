<!---
title: "XML Log Reader"
author: "Thomas Haber"
keywords: [log, xml, impulse, text log, parsing, serializer, extension, configuration, analysis, multi-line, hierarchy, tagging, domain, timestamp, Log4j, syslog]
description: "The XML Log Reader extension for impulse enables flexible import and analysis of XML-based logs using user-defined element and attribute mappings. Supports multi-format logs, hierarchical organization, timestamp extraction, multi-line handling, severity tagging, and advanced configuration for uniform visualization and processing across diverse XML log sources."
category: "impulse-extension"
tags:
  - reference
  - serializer
--->
# XML Log Reader

The XML Log Reader is designed to handle a wide variety of XML-based log formats using user-defined element and attribute mappings. It is highly configurable and supports complex log parsing scenarios, but requires careful setup for best results.

The XML Log Reader lets you import, parse, and analyze XML log files in impulse by mapping XML elements and attributes to log fields. It enables uniform visualization and processing of logs from diverse sources, even when XML schemas differ due to historical or toolchain reasons.

With the XML Log Reader, you can:
- Parse mixed-format XML logs using multiple element/attribute patterns
- Extract log level, timestamp, logger/location, method, and message fields from XML attributes or text
- Organize logs hierarchically by logger or scope
- Tag log entries by severity (error, warning, info, etc.)
- Filter, ignore, or combine log entries using pattern actions
- Test and refine mappings interactively before import

## Supporting

This serializer supports:
- PROPERTIES: Provides options to customize serialisation behavior, filtering, and output attributes for serializers.
- CONFIGURATION: The serializer supports configuration management, allowing users to add and select configurations to override default name patterns and properties. 

![](images/ss_xml-log_dialog1.png)

## Dialog Sections and Fields

The XML Log Reader dialog allows you to define how XML log files are parsed and mapped into impulse signals. It is divided into several sections for flexible setup and testing.

### General Section

This section contains the main identification and categorization properties of the XML log reader.

- **Name**: The unique name of this reader. Choose a descriptive name to clarify the log format.
- **Enable**: Checkbox to activate or deactivate the reader.
- **Description**: Free-form text area for documenting the configuration’s purpose or notes.
- **Icon**: Select or display an icon for the reader.
- **Tags**: Keywords or labels for categorizing and filtering.
- **Help**: Optional help text for documentation.
- **Name pattern**: Pattern for naming imported signals.

### Default Properties Section

This section provides properties for this log reader that can be overwritten using serializer configurations.

**Signal Selection Properties**
- **Include**: Regular expression pattern to include specific signals during import.
- **Exclude**: Regular expression pattern to exclude specific signals during import.

**Domain Range and Transformation Properties**
- **Domain base**: The time base unit (e.g., `1us`).
- **Relative**: Whether timestamps are relative (`true`/`false`).
- **Start**: Start time position for importing samples.
- **End**: End time position for importing samples.
- **Delay**: Time offset to shift all timestamps during import.
- **Dilate**: Time scaling factor to stretch or compress the temporal dimension of the waveform.

**Logging and Diagnostics Properties**
- **Char Set**: Character encoding for the log file.
- **Add signal with raw lines included**: Option to include a signal with raw XML lines.
- **Skip Lines**: Number of lines to skip at the beginning.
- **Stop After Lines**: Maximum number of lines to process.

#### Log Xml Section

This section displays a table listing all defined XML log patterns. Each row represents a mapping used to parse XML log entries.

**Columns:**
- **Name**: The name of the pattern (e.g., "Event").
- **Type**: The type of pattern (e.g., Log Xml).
- **Description**: Short description of the pattern.
- **Location**: Optional location or source info.

**Controls (right side):**
- **Add**: Create a new pattern entry.
- **Insert**: Insert a pattern above the selected row.
- **Delete**: Remove the selected pattern.
- **View/Edit**: Open the selected pattern for detailed editing and testing.
- **Up/Down**: Move the selected pattern up or down to change evaluation order.

Patterns are evaluated top-to-bottom; the first matching pattern is applied. Use View/Edit to test mappings with sample XML log entries and configure field mappings.

### Serializer Configurations Section

This section displays a table of serializer configuration profiles for the reader. Configurations allow you to override default properties and pattern enablement for different import scenarios.

---

## Log Xml Pattern Dialog

This dialog allows you to define and test a single XML log pattern for the XML Log Reader.

![](images/ss_xml-log-option_dialog1.png)

- **Name**: Enter a name for the pattern (e.g., "Event").
- **Description**: Short description of the pattern's purpose.
- **Enable**: Checkbox to activate or deactivate the pattern.
- **Element**: The XML element name to match (e.g., `log4j:event`).
- **Attributes**: Comma-separated list of XML attributes to extract (e.g., `logger,timestamp,sequenceNumber,level,thread`).
- **Action**: Select what happens when the pattern matches:
  - Ignore
  - Start new log sample
  - Add to previous sample (Multi-line pattern)
  - Finish sample (Multi-line pattern)

### Members

Map XML attributes or text to log fields:
- **Source**: The XML attribute or text node (e.g., `logger`, `level`, `timestamp`, `thread`).
- **Name**: Assign a label (Logger, Level, Timestamp, Thread, etc.).
- **Type**: Choose data type:
  - **None** (0)
  - **Integer** (1)
  - **Float** (2)
  - **Text** (3)
  - **Enumeration** (4)

### Signal/Scope Name

Configure how signals/scopes are named:

- **Mode**: Select naming mode:
  - **Name from source value** (1): Use the value from a selected attribute as the signal name.
  - **Hierarchy from source value** (2): Build a hierarchy from an attribute value (e.g., dot-separated logger path).
  - **Explicit name** (3): Enter a fixed name manually.
  - **Explicit hierarchy** (4): Enter a fixed hierarchy manually.
- **Source**: Choose attribute for signal/scope name.
- **Extension Mode**: 
  - **Name extension from source value** (1): Use an attribute value as an extension to the name.

If **Hierarchy from source value** (2) or **Explicit hierarchy** (4) is chosen, additional fields appear:
- **Separator**: Specify how hierarchy levels are split (e.g., `.` for dot-separated names).
- **Prefix**: Add a prefix to distinguish between hierarchical nodes and actual signal names.

### Domain Value (e.g., time-stamp)

Configure timestamp parsing:

- **Mode**: Select domain value mode:
  - **Float value (e.g. 0,033ms, 0.4)** (1)
  - **Integer value (e.g. 100 us, 50)** (2)
  - **Date (e.g. yyyy-MM-dd HH:mm:ss,SSS)** (3)
  - **Same as previous** (4)
  - **Same as previous (per Signal)** (5)
  - **Incrementing** (6)
  - **Incrementing (per Signal)** (7)
  - **Reception time** (8)
- **Source**: Choose attribute for timestamp.
- **Domain unit**: Specify the time unit (e.g., `1ms`).
- **Extension Mode**: Select extension mode for additional value combination:
  - **Undefined** (0)
  - **Float value** (1)
  - **Integer value** (2)
- **Extension Source**: Select additional attribute whose value will be added to the main domain value.

### Tag

Configure tagging for severity/status using regular expression patterns:

- **Source**: Select attribute for severity matching.
- **Error pattern**: Regex pattern for error entries (e.g., `ERROR|FATAL`).
- **Warning pattern**: Regex pattern for warnings (e.g., `WARN|WARNING`).
- **Info pattern**: Regex pattern for info entries (e.g., `INFO|NOTE`).
- **Debug pattern**: Regex pattern for debug entries (e.g., `DEBUG|TRACE`).
- **Fatal pattern**: Regex pattern for fatal entries (e.g., `FATAL|CRITICAL`).
- **Success/Trace pattern**: Optional regex patterns for other tags.

*Note*: Tag patterns are matched as regular expressions against the selected attribute value. Matching is case-sensitive unless you use regex flags like `(?i)ERROR` for case-insensitive matching.

---

## Multi-line Log Handling

The XML Log Reader supports multi-line log entries through pattern actions:

- **Add to previous sample**: Appends the current entry's extracted content to the most recent log sample.
- **Finish sample**: Marks the end of a multi-line log entry.

---

## Error Handling and Troubleshooting

- Patterns are evaluated in table order (top to bottom).
- The first enabled pattern that matches an element is applied.
- If no pattern matches, the entry is ignored (no error is thrown).
- Use an "Ignore" pattern as the last pattern to catch unmatched entries.

---

## Example

Suppose you have an XML log file with entries like:

```xml
<log4j:event logger="my.logger" timestamp="1680000000" level="ERROR" thread="main">
  <log4j:message>Something failed</log4j:message>
</log4j:event>
```

### Step 1: Create an XML Log Reader

- Go to **impulse Preferences → Serializers**, then right click on the root element and add an **XML Log Reader**.
- Set a name, select the character set, and set the time base (e.g., `1ms`).

### Step 2: Add Log Patterns

- Add a pattern for the main log event element:
  - **Element**: `log4j:event`
  - **Attributes**: `logger,timestamp,level,thread`
  - Map `logger` to Logger, `timestamp` to Timestamp, `level` to Level, `thread` to Thread.
  - Set **Action** to "Start new log sample".
  - For the **Signal name**, select **Name from source value** and use `logger`.
  - For the **Domain value**, select **Integer value** and use `timestamp` with domain unit `1ms`.
  - For **Tag**, use `level` as source and set patterns for ERROR, WARN, FATAL, etc.

- Add additional patterns for other XML elements or to ignore irrelevant entries.

### Step 3: Test and Import

- Use the **View/Edit** dialog to test your mappings with sample XML log entries.
- Adjust your element/attribute mappings and tag patterns as needed.
- Import your XML log file and analyze the structured signals in impulse.

---

## Known Limitations

- Requires careful mapping setup for complex/mixed XML logs
- Multi-line logs need explicit pattern configuration
- Timestamp parsing depends on correct format and attribute mapping
- Not all XML log formats can be parsed automatically; user intervention may be needed

---

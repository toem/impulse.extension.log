<!---
title: "XML Log Reader"
author: "Thomas Haber"
keywords: [log, xml, impulse, text log, parsing, serializer, extension, configuration, analysis, multi-element, hierarchy, tagging, domain, timestamp, Log4j, syslog]
description: "The XML Log Reader extension for impulse enables flexible import and analysis of XML-based logs using user-defined element and attribute mappings. Supports multi-format logs, hierarchical organization, timestamp extraction, multi-element handling, severity tagging, and advanced configuration for uniform visualization and processing across diverse XML log sources."
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
- Filter, ignore, or combine log entries using actions

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

### Properties Section

This section provides default properties for this reader that can be overwritten using serializer configurations.

- **Domain base**: The time base unit (e.g., `us`).
- **Char Set**: Character encoding for the log file.
- **Relative**: Whether timestamps are relative (`true`/`false`).
- **Xml Fragment**: When xmlFragment property is true, the method automatically wraps the input stream with dummy root elements to create valid XML structure for parsing.

**Additional Configuration Properties**

These properties can be additionally set in configurations.

- **Include**: Regular expression pattern to include specific signals during import. Only signals matching this pattern will be imported into the waveform viewer.
- **Exclude**: Regular expression pattern to exclude specific signals during import. Signals matching this pattern will be filtered out and not imported.
- **Start**: Start time position for importing samples. Only value changes at or after this time will be imported (specified in domain units like ns, us, ms).
- **End**: End time position for importing samples. Only value changes before or at this time will be imported (specified in domain units like ns, us, ms).
- **Delay**: Time offset to shift all timestamps during import. Positive values delay the waveform, negative values advance it (specified in domain units). Applied before dilation.
- **Dilate**: Time scaling factor to stretch or compress the temporal dimension of the waveform. Values > 1.0 slow down time, values < 1.0 speed up time. Applied after delay transformation using formula: (time + delay) * dilate.

**Logging and Diagnostics Properties**
The parser integrates with impulse's console logging system, providing configurable verbosity levels for diagnostic output during the import process. Console properties control the level of detail in parsing progress reports, timing statistics, and error information.

#### Log Xml Section

This section displays a table listing all defined XML log entries.

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

Xml Log entries are evaluated top-to-bottom; the first matching pattern is applied.

### Serializer Configurations Section

This section displays a table of serializer configuration profiles for the reader. Configurations allow you to override default properties and pattern enablement for different import scenarios.

---

## Log Xml Dialog

This dialog allows you to define a single XML log entry for the XML Log Reader.

![](images/ss_xml-log-option_dialog1.png)

- **Name**: Enter a name for the pattern (e.g., "Event").
- **Description**: Short description of the pattern's purpose. When a Signal/Scope Name is defined, this description is applied to the created signal.
- **Icon**: Select or display an icon for the pattern. When a Signal/Scope Name is defined, this icon is applied to the created signal.
- **Tags**: Keywords or labels for categorizing. When a Signal/Scope Name is defined, these tags are applied to the created signal.
- **Enable**: Checkbox to activate or deactivate the pattern.
- **Element**: The XML element name/path to match (e.g., `log4j:event`).
- **Attributes**: Comma-separated list of XML attributes to extract (e.g., `logger,timestamp,sequenceNumber,level,thread`).
- **Action**: Select what happens when the pattern matches:
  - Ignore
  - Start new log sample
  - Add to previous sample (Multi-element pattern)
  - Finish sample (Multi-element pattern)

### Members

Map XML attributes or text to log fields:
- **Source**: The XML attributes  (e.g., `logger`, `level`, `timestamp`, `thread`).
- **Name**: Assign a label (Logger, Level, Timestamp, Thread, etc.).
- **Type**: Choose data type:
  - **None** (0)
  - **Integer** (1)
  - **Float** (2)
  - **Text** (3)
  - **Enumeration** (4)

  *Note on Text vs Enumeration*: Both **Text** and **Enumeration** types display text values. Use **Text** only for volatile sequences where each value is likely unique (e.g., unique error messages, stack traces). Use **Enumeration** when the same text values occur repeatedly (e.g., log levels like "ERROR", "WARN", "INFO"). With Enumeration, text values are stored once and referenced, significantly reducing memory usage. With Text, each occurrence is copied into the signal.

- **Format**: Specify how the value should be interpreted or displayed (e.g.):
  - `hex`: Interpret as hexadecimal number (e.g., `0xFF` → 255).
  - `dec`: Interpret as decimal number (standard for integers/floats).
  - Other format descriptors for specialized parsing.
- **Tags**: Comma-separated keywords or labels to categorize this member (e.g., `important,error-level,verbose`). Tags help with filtering and organization.

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
- **Separator**: Specify how hierarchy levels are split (e.g., `\.` for dot-separated names). Enter a regular expression.
- **Prefix**: Add a prefix to distinguish between a scope and a signal of the same name. This is only required if both parent node and child node have messages.

*Example*: For a logger value `top.CPU.cache` with Separator `\.`:

**Without prefix (empty):**
- If only `cache` has messages: Scopes `top/CPU` contain signal `cache`
- If both `CPU` and `cache` have messages: Not possible without prefix - would create name conflict between CPU scope and CPU signal

**With prefix `#`:**
- If only `cache` has messages: Scopes `top/CPU` contain signal `#cache`  
- If both `CPU` and `cache` have messages: Scope `top` contains both scope `CPU` and signal `#CPU` (for CPU's own messages); scope `CPU` contains signal `#cache` (for cache's messages)

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
- **Domain unit**: Specify the time unit (e.g., `ms`).
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

## Example

Suppose you have an XML log file with entries like:

```xml
<log4j:event logger="my.logger" timestamp="1680000000" level="ERROR" thread="main">
  <log4j:message>Something failed</log4j:message>
</log4j:event>
```

### Step 1: Create an XML Log Reader

- Go to **impulse Preferences → Serializers**, then right click on the root element and add an **XML Log Reader**.
- Set a name, select the character set, and set the time base (e.g., `ms`).

### Step 2: Add Xml Log entry

- Add a pattern for the main log event element:
  - **Element**: `log4j:event`
  - **Attributes**: `logger,timestamp,level,thread`
  - Map `logger` to Logger, `timestamp` to Timestamp, `level` to Level, `thread` to Thread.
  - Set **Action** to "Start new log sample".
  - For the **Signal name**, select **Name from source value** and use `logger`.
  - For the **Domain value**, select **Integer value** and use `timestamp` with domain unit `1ms`.
  - For **Tag**, use `level` as source and set patterns for ERROR, WARN, FATAL, etc.

### Step 3: Test and Import

- Adjust your element/attribute mappings and tag patterns as needed.
- Import your XML log file and analyze the structured signals in impulse.

---

## Known Limitations

---

## Data Structure

This section documents the concrete XML Log Reader (type 'reader.log.xml') data model.

### Xml Log Reader 'reader.log.xml' 

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Unique name of the xml log reader. |
| iconId | string | null | Optional icon identifier. |
| helpUrl | string | null | Optional URL for help documentation. |
| description | string | null | Optional description of the reader's purpose. |
| enabled | boolean | true | Whether this reader is active. |
| tags | string | null | Optional comma-separated tags for categorization. |
| properties | string[][] |   | Properties as described below. |

#### `properties` Field Details

Configuration properties that apply to the entire reader:

| Label | Default | Identifier | Description |
|---|---|---|---|
| Include |  | include | Regular expression pattern to include specific signals during import. Only signals matching this pattern will be imported into the waveform viewer. |
| Exclude |  | exclude | Regular expression pattern to exclude specific signals during import. Signals matching this pattern will be filtered out and not imported. |
| Start |  | start | Start time position for importing samples. Only value changes at or after this time will be imported (specified in domain units like ns, us, ms). |
| End |  | end | End time position for importing samples. Only value changes before or at this time will be imported (specified in domain units like ns, us, ms). |
| Delay |  | delay | Time offset to shift all timestamps during import. Positive values delay the waveform, negative values advance it (specified in domain units). Applied before dilation. |
| Dilate |  | dilate | Time scaling factor to stretch or compress the temporal dimension of the waveform. Values > 1.0 slow down time, values < 1.0 speed up time. Applied after delay transformation using formula: (time + delay) * dilate. |
| Domain base | us (1us) | domainBase | The minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps). Defines the granularity of the signal's domain. |
| Char Set |  | charSet | Char Set |
| Relative Domain | false | relativeDomain | Apply relative domain values with a zero-indexed signal. This configuration is intended for relative domain bases (e.g., time) rather than absolute domain bases like Date. |
| Enable Logging | 4 (From majors onwards) | enableLogging | Enables console logging and sets the verbosity level for diagnostic output. |
| Show Log Output | 1280 (For errors only) | showLogOutput | Configures the threshold for displaying the output console. |
| xmlFragment | true | xmlFragment |  |

#### Children

| Child | Type | Cardinality | Notes |
|---|---|---|---|
| Log XML | reader.log.xml.option | 0..n | Each `reader.log.xml.option` entry defines a single log parsing xml within the reader. |

### Log Pattern `reader.log.xml.option`

#### Core Pattern Fields

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Name of this log xml (e.g., "Event", "ISR"). |
| description | string | null | Description applied to created signals. |
| iconId | string | null | Icon applied to created signals. |
| tags | string | null | Tags applied to created signals. |
| enabled | boolean | true | Whether this xml is active. |
| action | integer | 0 | Action when xml matches: 0=Ignore, 1=Start new sample, 2=Add to previous, 3=Finish sample. |
| path | string | null | XML element path pattern for selection. |
| attributes | string | null | Comma-separated list of attribute names to extract. All attribute references below are 2-based (!!) while the xml text is at index 1 (index 0 means no source).|

#### Domain/Time Fields

| Field | Type | Default | Notes |
|---|---|---|---|
| domainMode | integer | 1 | Time parsing mode: 1=Float, 2=Integer, 3=Date, 4=Previous, 5=Per-signal, 6=Incrementing, 7=Per-signal increment, 8=Reception time. |
| domainSource | integer | 0 | Attribute for domain value. |
| domain2Mode | integer | 0 | Extension mode: 0=Undefined, 1=Float, 2=Integer. |
| domain2Source | integer | 0 | Attribute for extension value. |
| dateFormat | string | null | Java date format (e.g., `yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'`). |
| domainUnit | string | null | Domain unit (e.g., `ns`, `us`, `ms`). |
| domain2Unit | string | null | Extension domain unit. |

#### Member Mapping Fields (Attributes 1-25)

Members map attributes to semantic fields:

| Field Pattern | Type | Notes |
|---|---|---|
| m1 - m25 | string | Semantic label for each attribute: "Timestamp", "Level", "Logger", "Component", "Method", "Message", "Thread", "EventID", etc. |
| s1 - s25 | integer | Data type for each member: 0=None, 1=Integer, 2=Float, 3=Text, 4=Enumeration. |
| f1 - f25 | string | Format descriptor for each member: hex, dec,... |
| t1 - t25 | string | Tags (comma separated) for each member. |

#### Signal/Scope Naming Fields (name0, name1, name2)

| Field | Type | Default | Notes |
|---|---|---|---|
| nameMode | integer | 1 | Signal naming mode: 1=From source value, 2=Hierarchy, 3=Explicit name, 4=Explicit hierarchy. |
| name0 | string | null | Explicit signal/hierarchy name (for modes 3-4). |
| name1Source | integer | 0 | Attribute for signal name (for modes 1-2). |
| nameSeparator | string | . | Hierarchy separator (e.g., `.` for dot-separated paths). |
| namePrefix | string | # | Prefix for hierarchy level distinction. |
| name2Mode | integer | 0 | Extension mode for names: 0=Undefined, 1=Name extension from source. |
| name2Source | integer | 0 | Attribute for name extension. |

#### Severity Tagging Fields

| Field | Type | Notes |
|---|---|---|
| tagSource | integer | Attribute for severity/status matching. |
| errorPattern | string | Regex pattern for error entries (e.g., `ERROR\|FATAL`). |
| warningPattern | string | Regex pattern for warnings (e.g., `WARN\|WARNING`). |
| infoPattern | string | Regex pattern for info entries (e.g., `INFO\|NOTE`). |
| debugPattern | string | Regex pattern for debug entries (e.g., `DEBUG\|TRACE`). |
| fatalPattern | string | Regex pattern for fatal entries (e.g., `CRITICAL`). |
| successPattern | string | Optional regex pattern for success entries. |
| tracePattern | string | Optional regex pattern for trace entries. |


## Predefined Pattern Log Reader XML Examples

Below are several predefined pattern log reader configurations in XML format. They are useful for AI-assisted generation of new readers.

```xml
   <reader.log.xml name="Log4j XML 1.2">
        <properties len="2">
            <xmlFragment>true</xmlFragment>
            <relativeDomainValue>true</relativeDomainValue>
        </properties>
        <reader.log.xml.option name="Event" domainSource="3" warningPattern="WARN" path="log4j:event" action="1" s2="4" s3="0" s4="1" s5="4" s6="4" domainUnit="ms" tagSource="5" errorPattern="ERROR" m6="Thread" m4="Sequence" m2="Logger" namePrefix="_" m3="Timestamp" name1Source="2" fatalPattern="FATAL" domainMode="2" attributes="logger,timestamp,sequenceNumber,level,thread" nameMode="1"/>
        <reader.log.xml.option name="Message" warningPattern="warning|WARNING|Warning" path="log4j:event/log4j:message" action="2" m1="Message"/>
        <reader.log.xml.option name="Ignore other" path="*"/>
    </reader.log.xml>
    <reader.log.xml name="Java Logging">
        <properties len="1">
            <relativeDomainValue>true</relativeDomainValue>
        </properties>
        <reader.log.xml.option name="Record" warningPattern="warning|WARNING|Warning" path="record" action="1"/>
        <reader.log.xml.option name="Millies" domainSource="1" warningPattern="warning|WARNING|Warning" path="record/millis" action="2" domainUnit="ms" domainMode="2"/>
        <reader.log.xml.option name="Logger" warningPattern="warning|WARNING|Warning" path="record/logger" action="2" name1Source="1" nameMode="1"/>
        <reader.log.xml.option name="Sequence" warningPattern="warning|WARNING|Warning" path="record/sequence" action="2" s1="1" m1="Sequence"/>
        <reader.log.xml.option name="Level" warningPattern="warning|WARNING|Warning" path="record/level" action="2" tagSource="1"/>
        <reader.log.xml.option name="Class" warningPattern="warning|WARNING|Warning" path="record/class" action="2" m1="Class"/>
        <reader.log.xml.option name="Method" warningPattern="warning|WARNING|Warning" path="record/method" action="2" m1="Method"/>
        <reader.log.xml.option name="Thread" warningPattern="warning|WARNING|Warning" path="record/thread" action="2" m1="Thread"/>
        <reader.log.xml.option name="Message" warningPattern="warning|WARNING|Warning" path="record/message" action="2" m1="Message"/>
        <reader.log.xml.option name="Ignore other" path="*"/>
    </reader.log.xml>
    <reader.log.xml name="Log4j XML 2 full">
        <properties len="1">
            <relativeDomainValue>true</relativeDomainValue>
        </properties>
        <reader.log.xml.option name="Event" warningPattern="WARN" path="Events/Event" action="1" s2="4" s3="0" s4="1" s5="4" s6="4" domainUnit="ms" tagSource="3" errorPattern="ERROR" m6="Thread" m4="Priority" m5="Thread" m2="Logger" namePrefix="_" name1Source="2" fatalPattern="FATAL" attributes="loggerName,level,threadPriority,threadId" nameMode="1"/>
        <reader.log.xml.option name="Message" warningPattern="warning|WARNING|Warning" path="Event/Message" action="2" m1="Message"/>
        <reader.log.xml.option name="Timestamp" domainSource="2" domain2Source="3" warningPattern="warning|WARNING|Warning" path="Event/Instant" action="2" domain2Mode="2" domainUnit="s" domain2Unit="ns" domainMode="2" attributes="epochSecond,nanoOfSecond"/>
        <reader.log.xml.option name="Ignore other" path="*"/>
    </reader.log.xml>


```
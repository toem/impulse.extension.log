<!---
title: "YAML Log Reader"
author: "Thomas Haber"
keywords: [log, yaml, impulse, text log, parsing, serializer, extension, configuration, analysis, multi-object, hierarchy, tagging, domain, timestamp, Log4j, syslog]
description: "The YAML Log Reader extension for impulse enables flexible import and analysis of YAML-based logs using user-defined object and value mappings. Supports multi-format logs, hierarchical organization, timestamp extraction, multi-object handling, severity tagging, and advanced configuration for uniform visualization and processing across diverse YAML log sources."
category: "impulse-extension"
tags:
  - reference
  - serializer
--->
# YAML Log Reader

The YAML Log Reader is designed to handle a wide variety of YAML-based log formats using user-defined object and value mappings. It is highly configurable and supports complex log parsing scenarios, but requires careful setup for best results.

The YAML Log Reader lets you import, parse, and analyze YAML log files in impulse by mapping YAML objects and values to log fields. It enables uniform visualization and processing of logs from diverse sources, even when YAML schemas differ due to historical or toolchain reasons.

With the YAML Log Reader, you can:
- Parse mixed-format YAML logs using multiple object/value patterns
- Extract log level, timestamp, logger/location, method, and message fields from YAML values
- Organize logs hierarchically by logger or scope
- Tag log entries by severity (error, warning, info, etc.)
- Filter, ignore, or combine log entries using actions

## Supporting

This serializer supports:
- PROPERTIES: Provides options to customize serialisation behavior, filtering, and output attributes for serializers.
- CONFIGURATION: The serializer supports configuration management, allowing users to add and select configurations to override default name patterns and properties. 

![](images/ss_yaml-log_dialog1.png)

## Dialog Sections and Fields

The YAML Log Reader dialog allows you to define how YAML log files are parsed and mapped into impulse signals. It is divided into several sections for flexible setup and testing.

### General Section

This section contains the main identification and categorization properties of the YAML log reader.

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

#### Log Yaml Section

This section displays a table listing all defined YAML log entries.

**Columns:**
- **Name**: The name of the pattern (e.g., "Event").
- **Type**: The type of pattern (e.g., Log Yaml).
- **Description**: Short description of the pattern.
- **Location**: Optional location or source info.

**Controls (right side):**
- **Add**: Create a new pattern entry.
- **Insert**: Insert a pattern above the selected row.
- **Delete**: Remove the selected pattern.
- **View/Edit**: Open the selected pattern for detailed editing and testing.
- **Up/Down**: Move the selected pattern up or down to change evaluation order.

Yaml Log entries are evaluated top-to-bottom; the first matching pattern is applied.

### Serializer Configurations Section

This section displays a table of serializer configuration profiles for the reader. Configurations allow you to override default properties and pattern enablement for different import scenarios.

---

## Log Yaml Dialog

This dialog allows you to define a single YAML log entry for the YAML Log Reader.

![](images/ss_yaml-log-option_dialog1.png)

- **Name**: Enter a name for the pattern (e.g., "Event").
- **Description**: Short description of the pattern's purpose. When a Signal/Scope Name is defined, this description is applied to the created signal.
- **Icon**: Select or display an icon for the pattern. When a Signal/Scope Name is defined, this icon is applied to the created signal.
- **Tags**: Keywords or labels for categorizing. When a Signal/Scope Name is defined, these tags are applied to the created signal.
- **Enable**: Checkbox to activate or deactivate the pattern.
- **Object**: The YAML object name/path to match (e.g., `log4j:event`).
- **Values**: Comma-separated list of YAML values to extract (e.g., `logger,timestamp,sequenceNumber,level,thread`).
- **Action**: Select what happens when the pattern matches:
  - Ignore
  - Start new log sample
  - Add to previous sample (Multi-object pattern)
  - Finish sample (Multi-object pattern)

### Members

Map YAML values to log fields:
- **Source**: The YAML value  (e.g., `logger`, `level`, `timestamp`, `thread`).
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
  - **Name from source value** (1): Use the value from a selected value as the signal name.
  - **Hierarchy from source value** (2): Build a hierarchy from an value value (e.g., dot-separated logger path).
  - **Explicit name** (3): Enter a fixed name manually.
  - **Explicit hierarchy** (4): Enter a fixed hierarchy manually.
- **Source**: Choose value for signal/scope name.
- **Extension Mode**: 
  - **Name extension from source value** (1): Use an value value as an extension to the name.

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
- **Source**: Choose value for timestamp.
- **Domain unit**: Specify the time unit (e.g., `ms`).
- **Extension Mode**: Select extension mode for additional value combination:
  - **Undefined** (0)
  - **Float value** (1)
  - **Integer value** (2)
- **Extension Source**: Select additional value whose value will be added to the main domain value.

### Tag

Configure tagging for severity/status using regular expression patterns:

- **Source**: Select value for severity matching.
- **Error pattern**: Regex pattern for error entries (e.g., `ERROR|FATAL`).
- **Warning pattern**: Regex pattern for warnings (e.g., `WARN|WARNING`).
- **Info pattern**: Regex pattern for info entries (e.g., `INFO|NOTE`).
- **Debug pattern**: Regex pattern for debug entries (e.g., `DEBUG|TRACE`).
- **Fatal pattern**: Regex pattern for fatal entries (e.g., `FATAL|CRITICAL`).
- **Success/Trace pattern**: Optional regex patterns for other tags.

*Note*: Tag patterns are matched as regular expressions against the selected value. Matching is case-sensitive unless you use regex flags like `(?i)ERROR` for case-insensitive matching.

---

## Example

Suppose you have a YAML log file with entries like:

```yaml
logger: my.logger
timestamp: 1680000000
level: ERROR
thread: main
message: Something failed
```

### Step 1: Create a YAML Log Reader

- Go to **impulse Preferences → Serializers**, then right click on the root element and add a **YAML Log Reader**.
- Set a name, select the character set, and set the time base (e.g., `ms`).

### Step 2: Add Yaml Log entry

- Add a pattern for the main log event object:
  - **Object**: (root object or path to the log entry)
  - **Values**: `logger,timestamp,level,thread,message`
  - Map `logger` to Logger, `timestamp` to Timestamp, `level` to Level, `thread` to Thread, `message` to Message.
  - Set **Action** to "Start new log sample".
  - For the **Signal name**, select **Name from source value** and use `logger`.
  - For the **Domain value**, select **Integer value** and use `timestamp` with domain unit `1ms`.
  - For **Tag**, use `level` as source and set patterns for ERROR, WARN, FATAL, etc.

### Step 3: Test and Import

- Adjust your object/value mappings and tag patterns as needed.
- Import your YAML log file and analyze the structured signals in impulse.

---

## Known Limitations

---

## Data Structure

This section documents the concrete YAMLn Log Reader (type 'reader.log.yaml') data model.

### Yaml Log Reader 'reader.log.yaml' 

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Unique name of the yaml log reader. |
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
| Char Set | UTF-8 | charSet | Char Set |
| Relative Domain | true | relativeDomain | Apply relative domain values with a zero-indexed signal. This configuration is intended for relative domain bases (e.g., time) rather than absolute domain bases like Date. |
| Enable Logging | 4 (From majors onwards) | enableLogging | Enables console logging and sets the verbosity level for diagnostic output. |
| Show Log Output | 1280 (For errors only) | showLogOutput | Configures the threshold for displaying the output console.  |

#### Children

| Child | Type | Cardinality | Notes |
|---|---|---|---|
| Log YAML | reader.log.yaml.option | 0..n | Each `reader.log.yaml.option` entry defines a single log parsing yaml within the reader. |

### Log Pattern `reader.log.yaml.option`

#### Core Pattern Fields

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Name of this log yaml (e.g., "Event", "ISR"). |
| description | string | null | Description applied to created signals. |
| iconId | string | null | Icon applied to created signals. |
| tags | string | null | Tags applied to created signals. |
| enabled | boolean | true | Whether this yaml is active. |
| action | integer | 0 | Action when yaml matches: 0=Ignore, 1=Start new sample, 2=Add to previous, 3=Finish sample. |
| path | string | null | YAML object path pattern for selection. |
| values | string | null | Comma-separated list of value names to extract. All value references below are 1-based  (index 0 means no source).|

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

Members map values to semantic fields:

| Field Pattern | Type | Notes |
|---|---|---|
| m1 - m25 | string | Semantic label for each value: "Timestamp", "Level", "Logger", "Component", "Method", "Message", "Thread", "EventID", etc. |
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


## Predefined Pattern Log Reader YAML Examples

Below are several predefined pattern log reader configurations in YAML format. They are useful for AI-assisted generation of new readers.

```yaml
 
    <reader.log.yaml name="YAML Event Log Reader" namePattern="yaml.log" description="Parser for YAML event logs with timestamp, severity, source, and message fields">
        <properties len="1">
            <domainBase>dateTime</domainBase>
        </properties>
        <reader.log.yaml.option name="Event" domainSource="1" warningPattern="warning" dateFormat="yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" debugPattern="debug" action="1" s2="4" s3="4" s4="3" s5="3" infoPattern="info" fatalPattern="critical" domainMode="3" m1="Timestamp" m2="Level" m3="Logger" m4="EventID" m5="Message" nameMode="1" successPattern="alert" domainUnit="us" tagSource="2" values="timestamp,severity,source,event_id,message" description="YAML event entry" errorPattern="error" name1Source="3"/>
        <reader.log.yaml.option name="Metadata" path="metadata" action="2" s1="4" m1="Component" m2="Sensor" m3="Peer" m4="User" values="component,sensor,peer,user"/>
    </reader.log.yaml>

    <reader.log.yaml name="Log4j YAML Reader" namePattern="testi" description="YAML reader for log4j YAML format with timestamp, level, thread, message, and logger hierarchy">
        <properties len="2">
            <charSet>UTF-8</charSet>
            <relativeDomain>true</relativeDomain>
        </properties>
        <reader.log.yaml.option name="Event" tracePattern="TRACE" warningPattern="WARN" debugPattern="DEBUG" action="1" s1="4" s2="4" s3="3" s4="4" infoPattern="INFO" fatalPattern="FATAL" m1="Level" m2="Logger" m3="Message" m4="Thread" nameMode="1" tagSource="1" values="level,loggerName,message,thread" description="Log4j YAML event with level, logger, message, thread" errorPattern="ERROR" name1Source="2"/>
        <reader.log.yaml.option name="Timestamp" domainSource="1" path="instant" action="2" domain2Mode="2" domain2Unit="ns" domainMode="2" domain2Source="2" domainUnit="s" values="epochSecond,nanoOfSecond" description="Timestamp from instant object"/>
    </reader.log.yaml>

```
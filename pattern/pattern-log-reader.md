<!---
title: "Pattern Log Reader"
author: "Thomas Haber"
keywords: [log, pattern, impulse, text log, parsing, serializer, extension, regex, configuration, analysis, multi-line, hierarchy, tagging, domain, timestamp, Log4j, syslog]
description: "The Pattern Log Reader extension for impulse enables flexible import and analysis of text-based logs using user-defined regular expressions. Supports multi-format logs, hierarchical organization, timestamp extraction, multi-line handling, severity tagging, and advanced configuration for uniform visualization and processing across diverse log sources."
category: "impulse-extension"
tags:
  - reference
  - serializer
--->
# Pattern Log Reader

The Pattern Log Reader is designed to handle a wide variety of text log formats using user-defined regular expressions. It is highly configurable and supports complex log parsing scenarios, but requires careful setup for best results.

The Pattern Log Reader lets you import, parse, and analyze text-based log files in impulse using custom regular expressions ("patterns"). It enables uniform visualization and processing of logs from diverse sources, even when formats differ due to historical or toolchain reasons.

With the Pattern Log Reader, you can:
- Parse mixed-format logs using multiple patterns
- Extract log level, timestamp, logger/location, method, and message fields
- Organize logs hierarchically by logger or scope
- Tag log entries by severity (error, warning, info, etc.)
- Filter, ignore, or combine log lines using pattern actions
- Test and refine patterns interactively before import

## Supporting

This serializer supports:
- PROPERTIES: Provides options to customize serialisation behavior, filtering, and output attributes for serializers.
- CONFIGURATION: The serializer supports configuration management, allowing users to add and select configurations to override default name patterns and properties. 

![](images/ss_pattern-log_dialog1.png)

## Dialog Sections and Fields

The Pattern Log Reader dialog allows you to define how log files are parsed and mapped into impulse signals. It is divided into several sections for flexible setup and testing.

### General Section

This section contains the main identification and categorization properties of the pattern log reader.

- **Name**: The unique name of the this reader. Choose a descriptive name to clarify the log format.
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
- **Add signal with raw lines included**: Option to include a signal with raw log lines (`true`/`false`).
- **Skip Lines**: Number of lines to skip at the beginning (`0` by default).
- **Stop After Lines**: Maximum number of lines to process (`-1` for unlimited).

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


#### Log Pattern Section

This section displays a table listing all defined log patterns. Each row represents a pattern used to parse log lines.

**Columns:**
- **Name**: The name of the pattern (e.g., "Event").
- **Type**: The type of pattern (e.g., log event).
- **Description**: Short description of the pattern.
- **Location**: Optional location or source info.

**Controls (right side):**
- **Add**: Create a new pattern entry.
- **Insert**: Insert a pattern above the selected row.
- **Delete**: Remove the selected pattern.
- **View/Edit**: Open the selected pattern for detailed editing and testing.
- **Up/Down**: Move the selected pattern up or down to change evaluation order.

Patterns are evaluated top-to-bottom; the first matching pattern is applied. Use View/Edit to test patterns with sample log lines and configure group mappings.

### Serializer Configurations Section

This section displays a table of serializer configuration profiles for the reader. Configurations allow you to override default properties and pattern enablement for different import scenarios.


## Log Pattern Dialog

This dialog allows you to define and test a single log pattern for the Pattern Log Reader.

- **Name**: Enter a name for the pattern (e.g., "Event").
- **Description**: Short description of the pattern's purpose. When a Signal/Scope Name is defined, this description is applied to the created signal.
- **Icon**: Select or display an icon for the pattern. When a Signal/Scope Name is defined, this icon is applied to the created signal.
- **Tags**: Keywords or labels for categorizing. When a Signal/Scope Name is defined, these tags are applied to the created signal.
- **Enable**: Checkbox to activate or deactivate the pattern.
- **Test Log Lines**: Paste sample log lines here to test your pattern.
- **Pattern**: The regular expression used to match and extract log fields.
- **Action**: Select what happens when the pattern matches:
  - Ignore
  - Start new log sample
  - Add to previous sample (Multi-line pattern)
  - Finish sample (Multi-line pattern)

![](images/ss_pattern-log-option_dialog1.png)

### Members

Map regex groups to log fields:
- **Source**: Select group (e.g., Group 1, Group 2, etc.).
- **Name**: Assign a label (Level, Timestamp, Logger, Method, Message).
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

### Domain Value (e.g., time-stamp)

Configure timestamp parsing:

- **Mode**: Select domain value mode:
  - **Float value (e.g. 0,033ms, 0.4)** (1): Parse as floating-point time value.
  - **Integer value (e.g. 100 us, 50)** (2): Parse as integer time value.
  - **Date (e.g. yyyy-MM-dd HH:mm:ss,SSS)** (3): Parse as date/time string.
  - **Same as previous** (4): Use the previous domain value.
  - **Same as previous (per Signal)** (5): Use the previous domain value for each signal.
  - **Incrementing** (6): Use incrementing values for each entry.
  - **Incrementing (per Signal)** (7): Use incrementing values per signal.
  - **Reception time** (8): Use the time of reception as the domain value.

- **Source**: Choose group for timestamp.
- **Date Format**: Specify format (e.g., MM-dd-yyyy HH:mm:ss).
- **Domain Unit**: Choose the domain base source (e.g., `ms`).
- **Extension Mode**: Select extension mode for additional value combination:
  - **Undefined** (0): No extension used.
  - **Float value** (1): Parse extension as floating-point and add to main domain value.
  - **Integer value** (2): Parse extension as integer and add to main domain value.
- **Extension Source**: Select additional group whose value will be added to the main domain value.

*Example*: For a log line `[10.5s + 250ms] Event occurred`, you could use Group 1 for the main time (10.5), Group 2 for the extension (250), with Extension Mode set to Float value. The resulting timestamp would be 10.5 + 0.25 = 10.75 seconds.

### Signal/Scope Name

Configure how signals/scopes are named:

- **Mode**: Select naming mode:
  - **Name from source value** (1): Use the value from a selected regex group as the signal name.
  - **Hierarchy from source value** (2): Build a hierarchy from a group value (e.g., dot-separated logger path).
  - **Explicit name** (3): Enter a fixed name manually.
  - **Explicit hierarchy** (4): Enter a fixed hierarchy manually.
- **Source**: Choose group for signal/scope name.
- **Extension Mode**: 
  - **Name extension from source value** (1): Use a group value as an extension to the name.

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

### Tag

Configure tagging for severity/status using regular expression patterns:

- **Source**: Select group for severity matching.
- **Error pattern**: Regex pattern for error entries (e.g., `ERROR|FATAL`).
- **Warning pattern**: Regex pattern for warnings (e.g., `WARN|WARNING`).
- **Info pattern**: Regex pattern for info entries (e.g., `INFO|NOTE`).
- **Debug pattern**: Regex pattern for debug entries (e.g., `DEBUG|TRACE`).
- **Fatal pattern**: Regex pattern for fatal entries (e.g., `FATAL|CRITICAL`).
- **Success/Trace pattern**: Optional regex patterns for other tags.

*Note*: Tag patterns are matched as regular expressions against the selected source group value. Matching is case-sensitive unless you use regex flags like `(?i)ERROR` for case-insensitive matching.

This dialog provides all fields and mappings needed to configure, test, and validate a log pattern for parsing and organizing log data in impulse.

## Multi-line Log Handling

The Pattern Log Reader supports multi-line log entries through pattern actions:

- **Add to previous sample**: Appends the current line's extracted content to the most recent log sample.
- **Finish sample**: Marks the end of a multi-line log entry.

*Example*: For logs like:
```
START: Exception occurred
  at com.example.MyClass.method(MyClass.java:42)
  at com.example.Main.main(Main.java:15)
END: Exception
```

Configure patterns:
1. Pattern `START: (.*)` with action "Start new log sample"
2. Pattern `\s+at (.*)` with action "Add to previous sample"  
3. Pattern `END: (.*)` with action "Finish sample"

The resulting sample combines all extracted content from the matched patterns into a single log entry.

## Regular Expressions Quick Reference

The Pattern Log Reader uses Java regular expressions. Here are essential patterns:

**Basic Patterns:**
- `.` : Any single character
- `*` : Zero or more of the preceding character
- `+` : One or more of the preceding character
- `?` : Zero or one of the preceding character
- `\d` : Any digit (0-9)
- `\s` : Any whitespace character
- `\w` : Any word character (letter, digit, underscore)

**Groups and Alternatives:**
- `(...)` : Capturing group
- `|` : Alternative (OR)
- `[abc]` : Character class (a, b, or c)
- `[a-z]` : Character range

**Quantifiers:**
- `{n}` : Exactly n occurrences
- `{n,m}` : Between n and m occurrences
- `.*?` : Non-greedy match (shortest possible)

**Examples:**
- `(\d+)` : Captures one or more digits
- `(ERROR|WARN)` : Captures "ERROR" or "WARN"
- `\[(.*?)\]` : Captures content between square brackets

For comprehensive regex documentation, see:
- [Oracle Java Regex Tutorial](https://docs.oracle.com/javase/tutorial/essential/regex/)
- [RegexPal](https://www.regexpal.com/) for online testing

## Error Handling and Troubleshooting

**Pattern Matching Process:**
- Patterns are evaluated in table order (top to bottom)
- The first enabled pattern that matches a line is applied
- If no pattern matches, the line is ignored (no error is thrown)
- Use an "Ignore" pattern with `.*` as the last pattern to catch unmatched lines

**Common Issues:**
- **No matches**: Check pattern syntax and test with sample lines
- **Wrong groups**: Verify group numbering in the Members section
- **Time parsing fails**: Ensure Date Format matches the actual timestamp format
- **Missing hierarchy**: Check Separator and Source group settings

**Debugging Tips:**
- Use the **Test Log Lines** field to verify patterns before import
- Check the impulse Console for detailed error messages during import
- Start with simple patterns and add complexity gradually
- Use online regex testers to validate expressions

## Example

Suppose you have a log file containing lines like:

```
NOTE     [212 745 133.974 ns] in top.HHShell.m4_top.mDist_dsp_access.r2_top : getX4LegChangeDone happened
WARNING  [212 745 133.974 ns] in top.HHShell.m4_top.mDist_dsp_access.r4_top : getX4LegChangeDone = 0x1008000
ISR at 214435176 
ERROR    [214 435 176.000 ns] in top.HHShell.m4_top.mDist_dsp_wait.entry : Kernel changed state to: OUTPUT
# Leg ok - wait for next
NOTE00000[216 735 876.000 ns] in top.F4.generics : Calculated load 47%
```

### Step 1: Create a Pattern Log Reader

- Go to **impulse Preferences → Serializers** , then right click on the root element and add a  **Pattern Log Reader**.
- Set a name, select the character set, and set the time base (e.g., `ns`).

### Step 2: Add Log Patterns

#### Main Log Pattern

Add a pattern for the main log format:

```
(WARNING|NOTE|ERROR|DEBUG)[\s0]*\[([ 0-9a-z\.]+)\]\s*in\s*([^:]*):\s*(.*)
```

- Use the **Test Log Lines** field in the dialog to paste sample lines and verify that each pattern matches as expected.
- Adjust your regular expressions and group mappings until all log formats are correctly parsed.

- **Group 1**: Log level (e.g., WARNING, NOTE, ERROR, DEBUG)
- **Group 2**: Timestamp (e.g., 212 745 133.974 ns)
- **Group 3**: Logger/location (e.g., top.HHShell.m4_top.mDist_dsp_access.r2_top)
- **Group 4**: Message

Assign labels to each group using the Members section.  
Set **Action** to "Start new log sample".

For the **Signal name**, select **Hierarchy from source value** and set the separator to `.` to create a hierarchical signal tree.
For the **Domain value**, select **Float value** and specify the domain unit (e.g., `ns`).  

#### ISR Pattern

Add a pattern for ISR lines:

```
ISR\sat\s(.*)
```

- **Group 1**: Timestamp

Assign the label "Timestamp" to group 1.  
Set **Action** to "Start new log sample".  
Choose **Integer value** for the **Domain value** and set the domain unit to `ns`.  
For the name, select **Explicit name** and enter "ISR".

#### Ignore Pattern

Add a final pattern to ignore all other lines:

```
.*
```

Set **Action** to "Ignore".


## Known Limitations

--- 

## Data Structure

This section documents the concrete Pattern Log Reader (type 'reader.log.pattern') data model.

### Pattern Log Reader 'reader.log.pattern' 

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Unique name of the pattern log reader. |
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
| Domain base | dateTime (1970.01.01.01:00:00:001) | domainBase | The minimum distance between two samples, typically measured in units like nanoseconds (ns) or picoseconds (ps). Defines the granularity of the signal's domain. |
| Char Set |  | charSet | Char Set |
| Relative Domain | false | relativeDomain | Apply relative domain values with a zero-indexed signal. This configuration is intended for relative domain bases (e.g., time) rather than absolute domain bases like Date. |
| Enable Logging | 4 (From majors onwards) | enableLogging | Enables console logging and sets the verbosity level for diagnostic output. |
| Show Log Output | 1280 (For errors only) | showLogOutput | Configures the threshold for displaying the output console. |
| Add signal with raw lines includes | false | writeLines |  |
| Skip Lines | 0 | skipLines |  |
| Stop After Lines | -1 | stopAfterLines |  |

#### Children

| Child | Type | Cardinality | Notes |
|---|---|---|---|
| Log Patter | reader.log.pattern.option | 0..n | Each `reader.log.pattern.option` entry defines a single log parsing pattern within the reader. |

### Log Pattern `reader.log.pattern.option`

#### Core Pattern Fields

| Field | Type | Default | Notes |
|---|---|---|---|
| name | string | null | Name of this log pattern (e.g., "Event", "ISR"). |
| description | string | null | Description applied to created signals. |
| iconId | string | null | Icon applied to created signals. |
| tags | string | null | Tags applied to created signals. |
| enabled | boolean | true | Whether this pattern is active. |
| action | integer | 0 | Action when pattern matches: 0=Ignore, 1=Start new sample, 2=Add to previous, 3=Finish sample. |
| pattern | string | null | Java regular expression for matching log lines. All Regex groups references below are 1-based. |
| example | string | null | Log example lines for testing the regular expression. |

#### Domain/Time Fields

| Field | Type | Default | Notes |
|---|---|---|---|
| domainMode | integer | 1 | Time parsing mode: 1=Float, 2=Integer, 3=Date, 4=Previous, 5=Per-signal, 6=Incrementing, 7=Per-signal increment, 8=Reception time. |
| domainSource | integer | 0 | Regex group number for domain value. |
| domain2Mode | integer | 0 | Extension mode: 0=Undefined, 1=Float, 2=Integer. |
| domain2Source | integer | 0 | Regex group for extension value. |
| dateFormat | string | null | Java date format (e.g., `yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'`). |
| domainUnit | string | null | Domain unit (e.g., `ns`, `us`, `ms`). |
| domain2Unit | string | null | Extension domain unit. |

#### Member Mapping Fields (Groups 1-25)

Members map regex capture groups to semantic fields:

| Field Pattern | Type | Notes |
|---|---|---|
| m1 - m25 | string | Semantic label for each regex group: "Timestamp", "Level", "Logger", "Component", "Method", "Message", "Thread", "EventID", etc. |
| s1 - s25 | integer | Data type for each member: 0=None, 1=Integer, 2=Float, 3=Text, 4=Enumeration. |
| f1 - f25 | string | Format descriptor for each member: hex, dec,... |
| t1 - t25 | string | Tags (comma separated) for each member. |

#### Signal/Scope Naming Fields (name0, name1, name2)

| Field | Type | Default | Notes |
|---|---|---|---|
| nameMode | integer | 1 | Signal naming mode: 1=From source value, 2=Hierarchy, 3=Explicit name, 4=Explicit hierarchy. |
| name0 | string | null | Explicit signal/hierarchy name (for modes 3-4). |
| name1Source | integer | 0 | Regex group for signal name (for modes 1-2). |
| nameSeparator | string | . | Hierarchy separator (e.g., `.` for dot-separated paths). |
| namePrefix | string | # | Prefix for hierarchy level distinction. |
| name2Mode | integer | 0 | Extension mode for names: 0=Undefined, 1=Name extension from source. |
| name2Source | integer | 0 | Regex group for name extension. |

#### Severity Tagging Fields

| Field | Type | Notes |
|---|---|---|
| tagSource | integer | Regex group for severity/status matching. |
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
<reader.log.pattern name="Log4j [%p] %c %M - %m%n">
    <properties len="2">
        <domainBase>n</domainBase>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Event" warningPattern="WARN" action="1" s1="4" s2="4" s3="4" tagSource="1" pattern="\[(\s*?\S*?\s*?)\] (\s*?\S*?\s*?) (\s*?\S*?\s*?) \- (.*)" description="Generated from [%p] %c %M - %m%n" errorPattern="ERROR" m4="Message" m2="Logger" m3="Method" name1Source="2" m1="Level" fatalPattern="FATAL" domainMode="6" nameMode="1">
        <example>[WARN] de.toem.impulse.test.secondary.Log3 log - get Sinus Wave -0.564085071066512&lt;br/&gt;[TRACE] de.toem.impulse.test.primary.Log1 log -  PCI: MMCONFIG at [mem 0xe0000000-0xefffffff&lt;br/&gt;[TRACE] de.toem.impulse.test.primary.Log1 log -  No Local Variables are initialized for Method [_GTF&lt;br/&gt;[INFO] de.toem.impulse.test.secondary.Log2 log -  ACPI: Local APIC address 0xfee00000</example>
    </reader.log.pattern.option>
</reader.log.pattern>
<reader.log.pattern name="Log4j [%p] %d{MM-dd-yyyy HH﹕mm﹕ss} %c %M - %m%n">
    <properties len="2">
        <relativeDomainValue>true</relativeDomainValue>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Event" domainSource="2" warningPattern="WARN" dateFormat="MM-dd-yyyy HH:mm:ss" action="1" s1="4" s2="0" s3="4" s4="4" tagSource="1" pattern="\[(\s*?\S*?\s*?)\] (\S+\S+-\S+\S+-\S+\S+\S+\S+ \S+\S+:\S+\S+:\S+\S+) (\s*?\S*?\s*?) (\s*?\S*?\s*?) \- (.*)" description="Generated from [%p] %d{MM-dd-yyyy HH:mm:ss} %c %M - %m%n" errorPattern="ERROR" m4="Method" m5="Message" m2="Timestamp" m3="Logger" name1Source="3" m1="Level" fatalPattern="FATAL" domainMode="3" nameMode="1">
        <example>[WARN] 04-15-2020 10:44:13 de.toem.impulse.test.secondary.Log3 log - get Sinus Wave -0.564085071066512&lt;br/&gt;[TRACE] 04-15-2020 10:44:13 de.toem.impulse.test.primary.Log1 log -  No Local Variables are initialized for Method [_GTF&lt;br/&gt;[TRACE] 04-15-2020 10:44:13 de.toem.impulse.test.primary.Log1 log -  PCI: MMCONFIG at [mem 0xe0000000-0xefffffff&lt;br/&gt;[INFO] 04-15-2020 10:44:14 de.toem.impulse.test.secondary.Log2 log -  ACPI: Local APIC address 0xfee00000</example>
    </reader.log.pattern.option>
</reader.log.pattern>
<reader.log.pattern name="Log4j [%p] %r %c %M - %m%n">
    <properties len="1">
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Event" domainSource="2" warningPattern="WARN" action="1" s1="4" s2="0" s3="4" s4="4" domainUnit="ms" tagSource="1" pattern="\[(\s*?\S*?\s*?)\] (\s*?\S*?\s*?) (\s*?\S*?\s*?) (\s*?\S*?\s*?) \- (.*)" description="Generated from [%p] %r %c %M - %m%n" errorPattern="ERROR" m4="Method" m5="Message" m2="Timestamp" m3="Logger" name1Source="3" m1="Level" fatalPattern="FATAL" domainMode="2" nameMode="1">
        <example>[WARN] 1149 de.toem.impulse.test.secondary.Log3 log - get Sinus Wave -0.564085071066512&lt;br/&gt;[TRACE] 1148 de.toem.impulse.test.primary.Log1 log -  PCI: MMCONFIG at [mem 0xe0000000-0xefffffff&lt;br/&gt;[TRACE] 1148 de.toem.impulse.test.primary.Log1 log -  No Local Variables are initialized for Method [_GTF&lt;br/&gt;[INFO] 1361 de.toem.impulse.test.secondary.Log2 log -  ACPI: Local APIC address 0xfee00000&lt;br/&gt;[DEBUG] 1361 de.toem.impulse.test.primary.Log1 log -  Switched APIC routing to cluster x2apic.</example>
    </reader.log.pattern.option>
</reader.log.pattern>
<reader.log.pattern name="Log4j %d [%p] %c{1} – %m%n">
    <properties len="2">
        <relativeDomainValue>true</relativeDomainValue>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Event" domainSource="1" warningPattern="WARN" action="1" s2="4" s3="4" tagSource="2" pattern="(\S+\S+\S+\S+-\S+\S+-\S+\S+ \S+\S+:\S+\S+:\S+\S+,\S+\S+\S+) \[(\s*?\S*?\s*?)\] (\s*?\S*?\s*?) – (.*)" description="Generated from %d [%p] %c{1} – %m%n" errorPattern="ERROR" m4="Message" m2="Level" m3="Logger" name1Source="3" m1="Timestamp" fatalPattern="FATAL" domainMode="3" nameMode="1">
        <example>2020-04-15 10:44:13,845 [WARN] Log3 – get Sinus Wave -0.564085071066512&lt;br/&gt;2020-04-15 10:44:13,844 [TRACE] Log1 –  No Local Variables are initialized for Method [_GTF&lt;br/&gt;2020-04-15 10:44:13,844 [TRACE] Log1 –  PCI: MMCONFIG at [mem 0xe0000000-0xefffffff&lt;br/&gt;2020-04-15 10:44:14,057 [INFO] Log2 –  ACPI: Local APIC address 0xfee00000</example>
    </reader.log.pattern.option>
</reader.log.pattern>
<reader.log.pattern name="Log4j %d{HH﹕mm﹕ss.SSS} [%t] %-5level %logger{36} - %msg%n">
    <properties len="2">
        <relativeDomainValue>true</relativeDomainValue>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Log Pattern" domainSource="1" warningPattern="WARN" dateFormat="HH:mm:ss.SSS" action="1" s1="0" s2="4" s3="4" s4="4" tagSource="3" pattern="(\S+\S+:\S+\S+:\S+\S+\.\S+\S+\S+) \[(.*?)\] (\s*?\S*?\s*?) (\s*?\S*?\s*?) \- (.*)" description="Generated from %d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" errorPattern="ERROR" m4="Logger" m5="Message" m2="Thread" m3="Level" name1Source="4" m1="Timestamp" fatalPattern="FATAL" domainMode="1" nameMode="2"/>
<reader.log.pattern name="syslog RFC3164">
    <properties len="2">
        <domainBase>dateTime</domainBase>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Events" domainSource="2" dateFormat="MMM dd HH:mm:ss" action="1" s1="4" s2="0" pattern="&lt;?([0-9]*)&gt;?([A-Z][a-z][a-z]\s{1,2}\d{1,2}\s\d{2}[:]\d{2}[:]\d{2})\s([\w][\w\d\.@-]*)\s(.*)" m4="Message" m2="Timestamp" m3="Header" m1="Priority" domainMode="3">
        <example>&lt;135&gt;May 11 09:33:36 thomas  No Local Variables are initialized for Method [_GTF&lt;br/&gt;&lt;132&gt;May 11 09:33:36 thomas get Sinus Wave 0.9629702887498031&lt;br/&gt;Apr 24 08:55:18 thomas rsyslogd:  [origin software="rsyslogd" swVersion="8.1901.0" x-pid="1200" x-info="https://www.rsyslog.com"] rsyslogd was HUPed&lt;br/&gt;Apr 24 08:55:19 thomas NetworkManager[1173]: &lt;info&gt;  [1587711319.0627] Loaded device plugin: NMBluezManager (/usr/lib/x86_64-linux-gnu/NetworkManager/1.20.4/libnm-device-plugin-bluetooth.so)</example>
    </reader.log.pattern.option>
</reader.log.pattern>
<reader.log.pattern name="syslog RFC5424">
    <properties len="2">
        <domainBase>dateTime</domainBase>
        <enabled>false</enabled>
    </properties>
    <reader.log.pattern.option name="Events" domainSource="1" dateFormat="yyyy-MM-dd'T'HH:mm:ss.SSSX" action="1" s1="0" pattern="(?:(\d{4}[-]\d{2}[-]\d{2}[T]\d{2}[:]\d{2}[:]\d{2}(?:\.\d{1,6})?(?:[+-]\d{2}[:]\d{2}|Z)?)|-)\s(?:([\w][\w\d\.@-]*)|-)\s(.*)" m2="Header" m3="Message" m1="Timestamp" domainMode="3">
        <example>2020-05-11T09:33:36.097+02:00 thomas /home/thomas/Workspaces/impulse.test/de.toem.impulse.test/bin/log4j2.xml 5886 - -  No Local Variables are initialized for Method [_GTF&lt;br/&gt;2020-05-11T09:33:36.306+02:00 thomas /home/thomas/Workspaces/impulse.test/de.toem.impulse.test/bin/log4j2.xml 5886 - - get Sinus Wave 0.9629702887498031&lt;br/&gt;2020-05-11T09:33:36.309+02:00 thomas /home/thomas/Workspaces/impulse.test/de.toem.impulse.test/bin/log4j2.xml 5886 - -  rcu: &lt;br/&gt;2020-05-11T09:33:36.311+02:00 thomas /home/thomas/Workspaces/impulse.test/de.toem.impulse.test/bin/log4j2.xml 5886 - -  ACPI: Local APIC address 0xfee00000</example>
    </reader.log.pattern.option>
</reader.log.pattern>
```


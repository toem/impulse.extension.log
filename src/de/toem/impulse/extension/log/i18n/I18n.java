package de.toem.impulse.extension.log.i18n;

public class I18n extends de.toem.impulse.i18n.I18n {

    public static String Log_PatternLogReader = "Pattern Log Reader";
    public static String Log_PatternLogReader_IconId = "codicon-regex";
    public static String Log_PatternLogReader_Description = "Import line-based log files using flexible regular expression patterns. Supports multi-line entries, field extraction, data type conversion, and comes with pre-configured patterns for common log formats like Log4J and Java logging. Ideal for unstructured and semi-structured text logs.";
    public static String Log_PatternLogReader_HelpURL = "impulse-extension/${BND}/pattern-log-reader";

    public static String Log_PatternLogOptions = "Log Pattern";
    public static String Log_PatternLogOptions_Description = "Collection of pattern-based log reader configurations for extracting structured data from text-based log files";
    public static String Log_PatternLogOptions_HelpURL = "impulse-extension/${BND}/pattern-log-reader";
    
    public static String Log_PatternLogOption = "Log Pattern";
    public static String Log_PatternLogOption_IconId = "codicon-regex";
    public static String Log_PatternLogOption_Description = "Define and configure regular expression patterns to parse log lines, extract fields, and map them to typed signal structures with support for multi-line log entries";
    public static String Log_PatternLogOption_HelpURL = "impulse-extension/${BND}/pattern-log-reader";
    
    public static String Log_XmlLogReader = "Xml Log Reader";
    public static String Log_XmlLogReader_IconId = "codicon-bracket";
    public static String Log_XmlLogReader_Description = "Import XML-formatted log files with flexible element and attribute mapping configuration. Supports nested structures, XML fragments, and comes with pre-defined configurations for Log4J XML and Java logging formats. Perfect for structured XML logs from enterprise applications.";
    public static String Log_XmlLogReader_HelpURL = "impulse-extension/${BND}/xml-log-reader";

    public static String Log_XmlLogOptions = "Log Xml";
    public static String Log_XmlLogOptions_Description = "Collection of XML-based log reader configurations for extracting structured data from XML-formatted log files";
    public static String Log_XmlLogOptions_HelpURL = "impulse-extension/${BND}/xml-log-reader";
    
    public static String Log_XmlLogOption = "Log Xml";
    public static String Log_XmlLogOption_IconId = "codicon-bracket";
    public static String Log_XmlLogOption_Description = "Configure XML element selection, attribute extraction, and data mapping for importing structured logging data from XML logs with support for fragment parsing and nested element hierarchies";
    public static String Log_XmlLogOption_HelpURL = "impulse-extension/${BND}/xml-log-reader";
   
    public static String Log_JsonLogReader = "Json Log Reader";
    public static String Log_JsonLogReader_IconId = "codicon-json";
    public static String Log_JsonLogReader_Description = "Import JSON-formatted log files with customizable field and property extraction. Supports nested JSON structures, array handling, and comes with pre-configured settings for Log4J JSON logging format. Ideal for structured logs from modern applications and microservices.";
    public static String Log_JsonLogReader_HelpURL = "impulse-extension/${BND}/json-log-reader";

    public static String Log_JsonLogOptions = "Log Json";
    public static String Log_JsonLogOptions_Description = "Collection of JSON-based log reader configurations for extracting structured data from JSON-formatted log files";
    public static String Log_JsonLogOptions_HelpURL = "impulse-extension/${BND}/json-log-reader";
    
    public static String Log_JsonLogOption = "Log Json";
    public static String Log_JsonLogOption_IconId = "codicon-json";
    public static String Log_JsonLogOption_Description = "Configure JSON path expressions, field selection, and data type mapping for extracting and transforming structured logging data from JSON documents with support for nested objects and arrays";
    public static String Log_JsonLogOption_HelpURL = "impulse-extension/${BND}/json-log-reader";
    
    public static String Log_YamlLogReader = "Yaml Log Reader";
    public static String Log_YamlLogReader_IconId = "codicon-bracket";
    public static String Log_YamlLogReader_Description = "Import YAML-formatted log files with intuitive key-value mapping and hierarchical structure support. Comes with pre-configured settings for Log4J YAML logging format. Excellent for human-readable structured logs from cloud-native applications and configuration-driven systems.";
    public static String Log_YamlLogReader_HelpURL = "impulse-extension/${BND}/yaml-log-reader";

    public static String Log_YamlLogOptions = "Log Yaml";
    public static String Log_YamlLogOptions_Description = "Collection of YAML-based log reader configurations for extracting structured data from YAML-formatted log files";
    public static String Log_YamlLogOptions_HelpURL = "impulse-extension/${BND}/yaml-log-reader";
    
    public static String Log_YamlLogOption = "Log Yaml";
    public static String Log_YamlLogOption_IconId = "codicon-bracket";
    public static String Log_YamlLogOption_Description = "Configure YAML key selection, nested property access, and data type conversion for importing human-readable structured logging data from YAML documents with full support for complex hierarchical structures";
    public static String Log_YamlLogOption_HelpURL = "impulse-extension/${BND}/yaml-log-reader";
    
   
    
    public static String PatternLogConfiguration_ActionNew = "Start new log sample";
    public static String PatternLogConfiguration_ActionAdd = "Add to previous sample (Multi-line pattern)";
    public static String PatternLogConfiguration_ActionTerminate = "Finish sample (Multi-line pattern)";
    public static String PatternLogConfiguration_Source = "Group";

    public static String PatternConfigurationDialog_UseFirstPattern = "First enabled pattern";
    public static String PatternConfigurationDialog_TestLines = "Test Log\nLines:";

    public static String XmlLogConfiguration_ActionNew = "Start new log sample";
    public static String XmlLogConfiguration_ActionAdd = "Add to previous sample ";
    public static String XmlLogConfiguration_ActionTerminate = "Finish sample";
    public static String XmlLogConfiguration_Source1 = "XML Text";
    public static String XmlLogConfiguration_Source2 = "Attribute";

    public static String XmlConfigurationDialog_UseFirstElement = "First enabled element";
    public static String XmlConfigurationDialog_Attributes = "Attributes:";
    public static String XmlConfigurationDialog_AllowFragments = "Read XML fragments (Wrap into root element)";

    public static String JsonLogConfiguration_ActionNew = "Start new log sample";
    public static String JsonLogConfiguration_ActionAdd = "Add to previous sample ";
    public static String JsonLogConfiguration_ActionTerminate = "Finish sample";
    public static String JsonLogConfiguration_Source = "Value";

    public static String JsonConfigurationDialog_UseFirstElement = "First enabled object";
    public static String JsonConfigurationDialog_Values = "Values:";

    public static String YamlLogConfiguration_ActionNew = "Start new log sample";
    public static String YamlLogConfiguration_ActionAdd = "Add to previous sample ";
    public static String YamlLogConfiguration_ActionTerminate = "Finish sample";
    public static String YamlLogConfiguration_Source = "Value";

    public static String YamlConfigurationDialog_UseFirstElement = "First enabled object";
    public static String YamlConfigurationDialog_Values = "Values:";

    public static String CsvLogConfiguration_Source = "Row";
    public static String CsvConfigurationDialog_TestLines = "Test Log\nLines:";
}

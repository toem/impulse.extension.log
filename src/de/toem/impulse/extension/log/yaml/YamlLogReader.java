package de.toem.impulse.extension.log.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.serializer.AbstractSingleDomainRecordReader;
import de.toem.impulse.usecase.logging.AbstractLogOption;
import de.toem.impulse.usecase.logging.AbstractLogReader;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.CellAnnotation;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer;
import de.toem.toolkits.pattern.element.instancer.IInstancer;
import de.toem.toolkits.pattern.element.serializer.AbstractMultitonSerializerPreference;
import de.toem.toolkits.pattern.element.serializer.ICellSerializer;
import de.toem.toolkits.pattern.element.serializer.ISerializerDescriptor;
import de.toem.toolkits.pattern.element.serializer.SingletonSerializerPreference.DefaultSerializerConfiguration;
import de.toem.toolkits.pattern.properties.PropertyModel;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;
import de.toem.toolkits.ui.tlk.TLK;
import de.toem.toolkits.utils.serializer.ParseException;

@RegistryAnnotation(annotation = YamlLogReader.Annotation.class)
/**
 * YAML Log Reader implementation for the Impulse logging framework.
 *
 * This class provides comprehensive YAML log file parsing capabilities, supporting both full YAML documents
 * and YAML fragments. It uses Jackson YAML parser for efficient, streaming YAML processing
 * that can handle large log files without loading the entire document into memory.
 *
 * Key features:
 * - Streaming YAML parsing using Jackson YAML parser for memory efficiency
 * - Support for YAML object hierarchy with automatic path tracking
 * - Path-based YAML object matching for log entry identification
 * - Flexible value extraction from YAML object properties
 * - Multi-domain timestamp support (primary and secondary domains)
 * - Configurable log entry naming and tagging
 * - Custom member field extraction from YAML object values
 *
 * YAML Structure Support:
 * The reader can process YAML files with arbitrary object structure, where log entries are identified
 * through configurable object path patterns. Each log entry can extract data from:
 * - YAML object property values
 * - Object names and paths
 * - Nested object hierarchies
 *
 * Configuration:
 * Configuration is handled through YamlLogOption instances that define:
 * - Object path patterns for log entry identification
 * - Value mappings for timestamps, names, and custom fields
 * - Parsing actions (start, terminate, ignore)
 * - Domain and naming modes
 * 
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
public class YamlLogReader extends AbstractLogReader {
    public static class Annotation extends AbstractSingleDomainRecordReader.Annotation {
        public static final Class<? extends ICell> multiton = Preference.class;
        public static final String id = "reader.log.yaml";
        public static final String label = I18n.Log_YamlLogReader;
        public static final String iconId = I18n.Log_YamlLogReader_IconId;
        public static final String description = I18n.Log_YamlLogReader_Description;
        public static final String helpURL = I18n.Log_YamlLogReader_HelpURL;
        public static final String certificate = "lkGhgaMjdTsQstaUaqpnFiUrBr5WzH78\nVhODxCEfY7ExnE2ylazpEwuuq2EVmdJT\n+57yY2P5xU9V9eb6z39tGFvHTUlkFjBa\nK1Wm/iJ6QqtBA6OakazRw/uYLhTHA43+\n3trx8D9QEtUgzXTcobIL1a6f24Wnf6Ar\nX3DlZVr6iBO8xGrLiypyxggRK75GDJqj\nr/9w4EB70t6zG/7hLi3h9Eg2EThKVfzA\ncdMPKdHIntd7AlR4sWKyUIHu5QkMnJVq\nrprr1uDxehjJIsQWNHeQzZszdS6qb5KY\ngyc9c6uOS94iueNinCs3CA==\n";
    }

    // ========================================================================================================================
    // Members
    // ========================================================================================================================

    // ========================================================================================================================
    // Constructor
    // ========================================================================================================================

    /**
     * Default constructor for YamlLogReader.
     *
     * Creates a new YAML log reader instance with default configuration.
     * The reader will need to be configured with options before use.
     */
    public YamlLogReader() {
        super();
    }

    /**
     * Constructs a YamlLogReader with full configuration.
     *
     * Creates a new YAML log reader instance with the specified configuration parameters.
     * This constructor initializes the reader with all necessary components for immediate use.
     *
     * @param descriptor the serializer descriptor defining the reader's capabilities
     * @param contentName the name of the content being processed
     * @param contentType the MIME type of the content
     * @param cellType the type identifier for the reader cell
     * @param configuration the configuration string for the reader
     * @param properties array of property key-value pairs for configuration
     * @param in the input stream containing the YAML data to be parsed
     */
    public YamlLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

    /**
     * Preference configuration class for YamlLogReader.
     *
     * This class defines the configuration preferences and UI controls for the YAML log reader.
     * It extends the abstract log reader preference to provide YAML-specific configuration options
     * and integrates with the Impulse framework's preference system.
     *
     * Configuration Elements:
     * - YAML log options for object/value mapping
     * - Serializer configuration for data persistence
     *
     * @see AbstractLogReader.AbstractLogReaderPreference
     * @see YamlLogOption
     */
    @CellAnnotation(annotation = Preference.Annotation.class, dynamicChildren = { DefaultSerializerConfiguration.TYPE, YamlLogOption.TYPE })
    public static class Preference extends AbstractLogReader.AbstractLogReaderPreference {
        public static final String TYPE = Annotation.id;

        static class Annotation {
            public static final String id = YamlLogReader.Annotation.id;
            public static final String label = YamlLogReader.Annotation.label;
            public static final String description = YamlLogReader.Annotation.description;
            public static final String iconId = YamlLogReader.Annotation.iconId;
            public static final String helpURL = YamlLogReader.Annotation.helpURL;
            public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
            public static final Class<? extends ICell>[] additional = new Class[] { YamlLogOption.class };

        }

        /**
         * Instancer for YamlLogReader preferences.
         *
         * This class handles the instantiation and initialization of YAML log reader preference cells.
         * It extends the abstract default instancer to provide YAML-specific setup logic.
         */
        @RegistryAnnotation(annotation = Instancer.Annotation.class)
        public static class Instancer extends AbstractDefaultInstancer {

            static class Annotation {
                public static final String id = "de.toem.instancer." + TYPE;
                public static final String cellType = TYPE;
            }

            /**
             * Initializes a single cell instance.
             *
             * This method is called to initialize a new cell instance with the specified ID and container.
             * It performs any necessary setup for the YAML log reader preference cell.
             *
             * @param id the unique identifier for the cell
             * @param cell the cell instance to initialize
             * @param container the parent container for the cell
             */
            @Override
            protected void initOne(String id, ICell cell, ICell container) {
                super.initOne(id, cell, container);
                // cell.setValue("script", Scripting.loadScriptFromResources(ScriptedCellReaderPreference.class, "scriptedReader.js"));
            }
        }

        /**
         * Returns the serializer class for this preference.
         *
         * @return the YamlLogReader class for serialization
         */
        @Override
        public Class<? extends ICellSerializer> getClazz() {
            return YamlLogReader.class;
        }

        /**
         * Returns the cell type identifier for this preference.
         *
         * @return the cell type string identifier
         */
        @Override
        public String getCellType() {
            return YamlLogReader.Annotation.cellType;
        }

        // ========================================================================================================================
        // Controls
        // ========================================================================================================================

        /**
         * UI Controls class for YamlLogReader preferences.
         *
         * This class provides the user interface controls for configuring YAML log reader preferences.
         * It extends the multiton serializer preference controls to add YAML-specific configuration options.
         *
         * UI Sections:
         * - YAML log options table for object/value mappings
         * - Standard serializer configuration controls
         *
         * @see AbstractMultitonSerializerPreference.Controls
         */
        static public class Controls extends AbstractMultitonSerializerPreference.Controls {

            /**
             * Constructs UI controls for the specified cell class.
             *
             * @param clazz the cell class for which to create controls
             */
            public Controls(Class<? extends ICell> clazz) {
                super(clazz);
            }

            /**
             * Fills the fourth section of the preference controls with YAML-specific options.
             *
             * This method adds a table control for configuring YAML log options, allowing users
             * to define object paths, value mappings, and parsing rules for YAML log files.
             */
            protected void fillSection4() {

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON, I18n.Log_YamlLogOptions, null,
                        I18n.Log_YamlLogOptions_Description, I18n.Log_YamlLogOptions_HelpURL);

                super.fillSection4();

            };
        }

        /**
         * Returns the UI control provider for this preference class.
         *
         * @return the control provider instance for UI configuration
         */
        public static ITlkControlProvider getControls() {
            return new Controls(AbstractLogReaderPreference.class);
        }
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Creates and returns the property model for YamlLogReader configuration.
     *
     * This method extends the base log reader property model with YAML-specific properties.
     *
     * @param descriptor the serializer descriptor
     * @param context the context object for property resolution
     * @return the configured property model
     */
    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return AbstractLogReader.getPropertyModel(descriptor, context);
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================

    /**
     * Creates a YAML option parser for the specified log option.
     *
     * This method creates and configures a YamlOptionParser instance
     * that will handle the parsing logic for a specific YAML log option configuration.
     *
     * @param option the YAML log option to create a parser for
     * @return the configured YAML option parser
     * @throws ParseException if the option configuration is invalid
     */
    @Override
    protected YamlOptionParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new YamlOptionParser((YamlLogOption) option);
    }

    /**
     * Parses YAML log data from the input stream using Jackson YAML parser.
     *
     * This is the main parsing method that processes YAML log files. It uses a Jackson YAML parser
     * for efficient streaming YAML processing and supports both full YAML documents and YAML fragments.
     * The method handles object matching, value extraction, and path tracking.
     *
     * Processing Flow:
     * 1. Creates YAML parser with proper character encoding
     * 2. Initializes parsing stacks for object hierarchy tracking
     * 3. Processes JSON tokens (START_OBJECT, END_OBJECT, etc.) with option matching
     * 4. Extracts property values and builds attribute maps
     * 5. Writes completed log messages to output
     *
     * YAML Structure Support:
     * The method supports complex YAML structures including nested objects and arrays,
     * with automatic path construction for hierarchical matching.
     *
     * @param progress the progress monitor for parsing operations
     * @param in the input stream containing YAML log data
     * @throws ParseException if parsing fails due to configuration or data errors
     * @throws IOException if an I/O error occurs during reading
     */
    @Override
    protected void parseLogs(IProgress progress, InputStream in) throws ParseException, IOException {

        YAMLFactory yfactory = new YAMLFactory();
        YAMLParser yamlReader = yfactory.createParser(new InputStreamReader(in, charSet));
        closable = yamlReader;

        // extract log data
        LogMessage message = new LogMessage();
        Stack<YamlOptionParser> parserStack = new Stack<>();
        Stack<Map<String, String>> attributeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();

        try {
            JsonToken nextToken = yamlReader.nextToken();
            while (nextToken != null) {
                // Utils.log("NExt " + nextToken,yamlReader.getCurrentName(),yamlReader.getText());

                if (JsonToken.START_OBJECT.equals(nextToken)) {

                    boolean match = false;
                    String name = yamlReader.getCurrentName();
                    String path = pathStack.isEmpty() ? "/" : pathStack.peek();
                    if (name == null)
                        name = "";
                    for (AbstractOptionParser p : parser) {
                        YamlOptionParser parser = (YamlOptionParser) p;
                        if (parser.matches(path, name)) {
                            parserStack.push(parser);
                            try {
                                parser.startObject(message);
                            } catch (ParseException e) {
                                throw new SAXException(e);
                            }
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        throw new SAXException(new ParseException(-1, "No match for element \"" + name + "\""));
                    }
                    attributeStack.push(new HashMap<>());
                    pathStack.push(path + "/" + name);

                } else if (JsonToken.END_OBJECT.equals(nextToken)) {

                    YamlOptionParser parser = parserStack.pop();
                    Map<String, String> attributes = attributeStack.pop();
                    pathStack.pop();
                    if (parser != null)
                        try {
                            parser.endObject(attributes, message);

                        } catch (ParseException e) {
                            throw new SAXException(e);
                        }

                } else if (JsonToken.START_ARRAY.equals(nextToken)) {

                    String path = pathStack.isEmpty() ? "/" : pathStack.peek() + "/";
                    pathStack.push(path);

                } else if (JsonToken.END_ARRAY.equals(nextToken)) {

                    pathStack.pop();

                } else if (JsonToken.VALUE_FALSE.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = yamlReader.getCurrentName();
                    attributes.put(name, yamlReader.getValueAsString());

                } else if (JsonToken.VALUE_TRUE.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = yamlReader.getCurrentName();
                    attributes.put(name, yamlReader.getValueAsString());

                } else if (JsonToken.VALUE_NUMBER_FLOAT.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = yamlReader.getCurrentName();
                    attributes.put(name, yamlReader.getValueAsString());

                } else if (JsonToken.VALUE_NUMBER_INT.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = yamlReader.getCurrentName();
                    attributes.put(name, yamlReader.getValueAsString());

                } else if (JsonToken.VALUE_STRING.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = yamlReader.getCurrentName();
                    attributes.put(name, yamlReader.getValueAsString());

                }
                nextToken = yamlReader.nextToken();
            }
        } catch (Throwable e) {
            if (e instanceof ParseException)
                throw (ParseException) e;
            throw new ParseException("Could not parse YAML structure", e);
        }
    }

    /**
     * YAML Option Parser for processing YAML objects based on configuration.
     *
     * This inner class handles the parsing logic for individual YAML log options. It matches
     * YAML objects against configured paths, extracts data from property values,
     * and populates log messages with the extracted information.
     *
     * Key Responsibilities:
     * - Object path matching against configured patterns
     * - Property value extraction for timestamps, names, and custom fields
     * - Multi-domain timestamp parsing (primary and secondary)
     * - Log entry naming and tagging based on YAML data
     * - Action handling (start, terminate, ignore) for log entry lifecycle
     *
     * Path Matching:
     * The parser supports hierarchical path matching with wildcards and specific path patterns.
     * Object names can be exact matches or use "*" for any object at that level.
     *
     * @see YamlLogOption
     * @see AbstractOptionParser
     */
    class YamlOptionParser extends AbstractOptionParser {

        // The object name to match (null for any object)
        public String name;
        // The parent path to match (null for any path)
        public String ppath;

        // Array of source value names indexed by source type
        protected String[] sourceValues;

        // Value name for domain/timestamp extraction
        protected String domainValue;
        // Value name for secondary domain extraction
        protected String domain2Value;
        // Value name for primary name extraction
        protected String name1Value;
        // Value name for secondary name extraction
        protected String name2Value;
        // Value name for tag extraction
        protected String tagValue;

        /**
         * Constructs a YamlOptionParser with the specified YAML log option.
         *
         * This constructor initializes the parser with configuration from the provided YamlLogOption,
         * setting up object path matching, source value mappings, and parsing parameters.
         * It parses the object path to extract name and parent path components, and configures
         * value mappings for timestamps, names, tags, and custom member fields.
         *
         * @param option the YamlLogOption containing configuration for this parser
         * @throws ParseException if the option configuration is invalid or malformed
         */
        public YamlOptionParser(YamlLogOption option) throws ParseException {
            super(option);

            if (!Utils.isEmpty(option.path)) {
                int pos = option.path.lastIndexOf('/');
                if (pos >= 0) {
                    name = option.path.substring(pos + 1).trim();
                    ppath = option.path.substring(0, pos).trim();
                    if (!Utils.isEmpty(ppath) && !ppath.startsWith("/"))
                        ppath = "/" + ppath;
                } else
                    name = option.path.trim();

                if (name != null && name.equals("*"))
                    name = null;
                if (ppath != null && (ppath.equals("*") || Utils.isEmpty(ppath)))
                    ppath = null;
            } else
                name = "";

            // source
            sourceValues = new String[option.getMaxSource() + 1];
            for (int n = YamlLogOption.SOURCE_VALUE1; n < sourceValues.length; n++) {
                if (option.hasValidSource(n)) {
                    String id = option.getSourceIdentifier(n);
                    sourceValues[n] = id;
                    if (n == domainSource)
                        domainValue = option.getSourceIdentifier(n);
                    if (n == domain2Source)
                        domain2Value = option.getSourceIdentifier(n);
                    if (n == name1Source)
                        name1Value = option.getSourceIdentifier(n);
                    if (n == name2Source)
                        name2Value = option.getSourceIdentifier(n);
                    if (n == tagSource)
                        tagValue = option.getSourceIdentifier(n);

                }
            }
        }

        /**
         * Checks if the given object path and name match this parser's configuration.
         *
         * This method performs hierarchical matching against the configured object path pattern.
         * It verifies that the object name matches (or is wildcard) and that the parent path
         * ends with the configured parent path pattern.
         *
         * @param path the current object path in the YAML document
         * @param name the object name to match
         * @return true if the object matches this parser's configuration, false otherwise
         */
        public boolean matches(String path, String name) {
            if (this.name != null)
                if (!this.name.equals(name))
                    return false;
            if (this.ppath != null)
                if (!path.endsWith(ppath))
                    return false;
            return true;
        }

        /**
         * Processes the start of a YAML object.
         *
         * This method is called when a matching YAML object starts. It handles action processing
         * (start/terminate), and prepares for data extraction from object properties.
         * For ACTION_START, it may write the previous message and clear it.
         *
         * @param message the log message to populate with extracted data
         * @throws ParseException if parsing of extracted values fails
         */
        public void startObject(LogMessage message) throws ParseException {

            if (action == AbstractLogOption.ACTION_IGNORE)
                return;

            if (action == AbstractLogOption.ACTION_START && !message.isEmpty()) {
                write(message);
                message.clear();
            }

            boolean changed = false;

            // // rec position
            // if (recPosIndex != -1 && message.values[recPosIndex] == null) {
            // message.values[recPosIndex] = lineNo;
            // changed |= true;
            // }

            if (changed)
                message.setEmpty(false);

        }

        /**
         * Processes the end of a YAML object, extracting data from property values.
         *
         * This method is called when a matching YAML object ends. It extracts timestamp domains,
         * names, and custom member values from the object's property values, updates the log message,
         * and handles ACTION_TERMINATE by writing the completed message.
         *
         * @param attributes the map of property names to values from the YAML object
         * @param message the log message to populate with extracted data
         * @throws SAXException if a SAX parsing error occurs
         * @throws ParseException if parsing of extracted values fails
         */
        public void endObject(Map<String, String> attributes, LogMessage message) throws SAXException, ParseException {

            boolean changed = false;

            // position
            if (domainMode != AbstractLogOption.DOMAIN_UNDEFINED) {
                final String domainText = (domainSource > 0 && domainValue != null) ? attributes.get(domainValue) : null;
                final Long position = parseDomain(domainText);
                message.position = position;
                message.positionParser = this;
                changed |= true;
            }
            // position
            if (domain2Mode != AbstractLogOption.DOMAIN_UNDEFINED) {
                final String domainText = (domain2Source > 0 && domain2Value != null) ? attributes.get(domain2Value) : null;
                final Long position = parseDomain2(domainText);
                message.position2 = position;
                changed |= true;
            }

            // name
            if (nameMode != AbstractLogOption.NAME_UNDEFINED) {
                if (nameMode == AbstractLogOption.NAME_EXPLICIT)
                    message.name1 = name0;
                else {
                    if (name1Value != null) {
                        String name = attributes.get(name1Value);
                        if (!Utils.isEmpty(name))
                            message.name1 = name.trim();
                    }
                    message.nameParser = this;
                }
                changed |= true;
            }
            if (name2Mode != AbstractLogOption.NAME_UNDEFINED && name2Value != null) {
                String name = attributes.get(name2Value);
                if (!Utils.isEmpty(name))
                    message.name2 = name.trim();
                changed |= true;
            }

            // members
            for (int n = YamlLogOption.MEMBER_MIN; n <= YamlLogOption.MEMBER_MAX && n <= maxMemberSource; n++) {
                int widx = memberIndex[n];
                if ((widx >= 0 || n == tagSource)) {
                    String name = sourceValues[n];
                    String text = attributes.get(name);
                    if (!Utils.isEmpty(text)) {
                        text = text.trim();
                        if (widx >= 0) {
                            Object previous = message.values[widx];
                            if (previous instanceof String)
                                message.values[widx] = ((String) previous) + text;
                            else
                                message.values[widx] = text;
                            changed |= true;
                        }
                        if (n == tagSource) {
                            int tag = parseTags(text);
                            if (tag > 0 && (message.tag == 0 || tag < message.tag)) {
                                message.tag = tag;
                                changed |= true;
                            }
                        }
                    }
                }
            }

            if (changed)
                message.setEmpty(false);

            if (action == AbstractLogOption.ACTION_TERMINATE && !message.isEmpty()) {
                write(message);
                message.clear();
            }
        }

    }

}

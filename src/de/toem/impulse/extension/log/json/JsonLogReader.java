package de.toem.impulse.extension.log.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

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

/**
 * JSON-based log reader for impulse.
 *
 * This reader parses JSON-formatted log files and converts JSON objects into
 * impulse records based on configurable path patterns and field mappings.
 * It uses the Jackson JSON parser to stream through JSON structures and
 * extract relevant data according to configured JsonLogOption instances.
 *
 * Key features:
 * - Streaming JSON parsing using Jackson library for memory efficiency
 * - Path-based pattern matching for JSON object selection
 * - Support for nested JSON structures with stack-based parsing
 * - Flexible field mapping from JSON properties to impulse signals
 * - Configurable domain, name, and member extraction from JSON data
 *
 * Implementation notes:
 * - This reader extends {@link de.toem.impulse.usecase.logging.AbstractLogReader}
 *   and follows the project property-model conventions for configuration.
 * - Uses {@link com.fasterxml.jackson.core.JsonParser} for efficient JSON streaming
 * - Maintains parsing state with stacks for nested objects and attributes
 * - Supports various JSON value types (string, number, boolean, null)
 * - Path matching uses hierarchical JSON structure navigation
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
@RegistryAnnotation(annotation = JsonLogReader.Annotation.class)
public class JsonLogReader extends AbstractLogReader {
    public static class Annotation extends AbstractSingleDomainRecordReader.Annotation {
        public static final Class<? extends ICell> multiton = Preference.class;
        public static final String id = "reader.log.json";
        public static final String label = I18n.Log_JsonLogReader;
        public static final String iconId = I18n.Log_JsonLogReader_IconId;
        public static final String description = I18n.Log_JsonLogReader_Description;
        public static final String helpURL = I18n.Log_JsonLogReader_HelpURL;
        public static final String certificate = "lkGhgaMjdTspk3qTVmQ3sMwkeYbjTaQ4\nVhODxCEfY7ExnE2ylazpEwuuq2EVmdJT\n+57yY2P5xU/CytamsEr37VoEU1/z3rUq\nK1Wm/iJ6QqtBA6OakazRw/uYLhTHA43+\n3trx8D9QEtUgzXTcobIL1a6f24Wnf6Ar\nX3DlZVr6iBO8xGrLiypyxggRK75GDJqj\nr/9w4EB70t6zG/7hLi3h9Eg2EThKVfzA\ncdMPKdHIntd7AlR4sWKyUCOSdWO3Ef2k\nV8Wv/YiNokauQKOt+b7p1Jcsk87Lz0Wa\n496NUuvs32DD38sFF2n0Sw==\n";

    }

    // ========================================================================================================================
    // Members
    // ========================================================================================================================

    // ========================================================================================================================
    // Constructor
    // ========================================================================================================================

    /**
     * Default constructor.
     */
    public JsonLogReader() {
        super();
    }

    /**
     * Constructs a JsonLogReader with the specified parameters.
     *
     * @param descriptor the serializer descriptor
     * @param contentName the content name
     * @param contentType the content type
     * @param cellType the cell type
     * @param configuration the configuration
     * @param properties the properties
     * @param in the input stream
     */
    public JsonLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

    /**
     * Preference class for JsonLogReader.
     */
    @CellAnnotation(annotation = Preference.Annotation.class, dynamicChildren = { DefaultSerializerConfiguration.TYPE, JsonLogOption.TYPE })
    public static class Preference extends AbstractLogReader.AbstractLogReaderPreference {
        // The type identifier
        public static final String TYPE = Annotation.id;

        static class Annotation {
            public static final String id = JsonLogReader.Annotation.id;
            public static final String label = JsonLogReader.Annotation.label;
            public static final String description = JsonLogReader.Annotation.description;
            public static final String iconId = JsonLogReader.Annotation.iconId;
            public static final String helpURL = JsonLogReader.Annotation.helpURL;
            public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
            public static final Class<? extends ICell>[] additional = new Class[] { JsonLogOption.class };
        }

        @RegistryAnnotation(annotation = Instancer.Annotation.class)
        public static class Instancer extends AbstractDefaultInstancer {

            static class Annotation {
                public static final String id = "de.toem.instancer." + TYPE;
                public static final String cellType = TYPE;
            }

            /**
             * Initializes the cell.
             *
             * @param id the cell ID
             * @param cell the cell
             * @param container the container
             */
            @Override
            protected void initOne(String id, ICell cell, ICell container) {
                super.initOne(id, cell, container);
                // cell.setValue("script", Scripting.loadScriptFromResources(ScriptedCellReaderPreference.class, "scriptedReader.js"));
            }
        }

        /**
         * Returns the serializer class.
         *
         * @return the class
         */
        @Override
        public Class<? extends ICellSerializer> getClazz() {
            return JsonLogReader.class;
        }

        /**
         * Returns the cell type.
         *
         * @return the cell type
         */
        @Override
        public String getCellType() {
            return JsonLogReader.Annotation.cellType;
        }

        // ========================================================================================================================
        // Controls
        // ========================================================================================================================

        /**
         * Controls for Preference.
         */
        static public class Controls extends AbstractMultitonSerializerPreference.Controls {

            /**
             * Constructor.
             *
             * @param clazz the class
             */
            public Controls(Class<? extends ICell> clazz) {
                super(clazz);
            }

            /**
             * Fills section 4.
             */
            protected void fillSection4() {

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON, I18n.Log_JsonLogOptions, null,
                        I18n.Log_JsonLogOptions_Description, I18n.Log_JsonLogOptions_HelpURL);

                super.fillSection4();

            };
        }

        /**
         * Returns the controls provider.
         *
         * @return the controls
         */
        public static ITlkControlProvider getControls() {
            return new Controls(AbstractLogReaderPreference.class);
        }
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    /**
     * Creates and returns the property model for this reader.
     *
     * @param descriptor serializer descriptor providing contextual information
     * @param context additional context (may be null)
     * @return configured PropertyModel
     */
    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return AbstractLogReader.getPropertyModel(descriptor, context);
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================


    /**
     * Factory method for creating a JsonOptionParser from an AbstractLogOption.
     *
     * @param option option describing the JSON parsing behavior
     * @return a new JsonOptionParser instance
     * @throws ParseException if the option cannot be converted into a parser
     */
    @Override
    protected JsonOptionParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new JsonOptionParser((JsonLogOption) option);
    }

    /**
     * Main parsing loop for JSON log files.
     *
     * Reads the JSON input stream using Jackson streaming parser and processes
     * JSON tokens to extract structured data. Uses stacks to maintain parsing
     * state for nested objects and arrays, matching configured patterns and
     * extracting relevant data into LogMessage instances.
     *
     * @param progress progress/cancellation interface
     * @param in input stream to read JSON from
     * @throws ParseException on parsing errors
     * @throws IOException on IO errors
     */
    @Override
    protected void parseLogs(IProgress progress, InputStream in) throws ParseException, IOException {

        JsonFactory jfactory = new JsonFactory();
        JsonParser jsonReader = jfactory.createParser(new InputStreamReader(in, charSet));
        closable = jsonReader;

        // extract log data
        LogMessage message = new LogMessage();
        Stack<JsonOptionParser> parserStack = new Stack<>();
        Stack<Map<String, String>> attributeStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();

        try {
            JsonToken nextToken = jsonReader.nextToken();
            while (nextToken != null) {
                // Utils.log("NExt " + nextToken,jsonReader.getCurrentName(),jsonReader.getText());

                if (JsonToken.START_OBJECT.equals(nextToken)) {

                    boolean match = false;
                    String name = jsonReader.getCurrentName();
                    String path = pathStack.isEmpty() ? "/" : pathStack.peek();
                    if (name == null)
                        name = "";
                    for (AbstractOptionParser p : parser) {
                        JsonOptionParser parser = (JsonOptionParser) p;
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

                    JsonOptionParser parser = parserStack.pop();
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
                    String name = jsonReader.getCurrentName();
                    attributes.put(name, jsonReader.getValueAsString());

                } else if (JsonToken.VALUE_TRUE.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = jsonReader.getCurrentName();
                    attributes.put(name, jsonReader.getValueAsString());

                } else if (JsonToken.VALUE_NUMBER_FLOAT.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = jsonReader.getCurrentName();
                    attributes.put(name, jsonReader.getValueAsString());

                } else if (JsonToken.VALUE_NUMBER_INT.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = jsonReader.getCurrentName();
                    attributes.put(name, jsonReader.getValueAsString());

                } else if (JsonToken.VALUE_STRING.equals(nextToken)) {

                    Map<String, String> attributes = attributeStack.peek();
                    String name = jsonReader.getCurrentName();
                    attributes.put(name, jsonReader.getValueAsString());

                }
                nextToken = jsonReader.nextToken();
            }
        } catch (Throwable e) {
            if (e instanceof ParseException)
                throw (ParseException) e;
            throw new ParseException("Could not parse JSON structure", e);
        }
    }

    class JsonOptionParser extends AbstractOptionParser {

        // Object name for matching
        public String name;
        // Parent path for matching
        public String ppath;

        // Array of source value identifiers
        protected String[] sourceValues;

        // Domain value identifier
        protected String domainValue;
        // Second domain value identifier
        protected String domain2Value;
        // First name value identifier
        protected String name1Value;
        // Second name value identifier
        protected String name2Value;
        // Tag value identifier
        protected String tagValue;

        /**
         * Constructs a JsonOptionParser for the provided option.
         *
         * Parses the option's path to extract name and parent path components,
         * and initializes source value mappings for domain, name, and member extraction.
         *
         * @param option JSON log option configuration
         * @throws ParseException if the option configuration is invalid
         */
        public JsonOptionParser(JsonLogOption option) throws ParseException {
            super(option);

            // name / path
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
            for (int n = JsonLogOption.SOURCE_VALUE1; n < sourceValues.length; n++) {
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
         * Checks if this parser matches the given JSON path and name.
         *
         * @param path the current JSON path
         * @param name the current JSON object name
         * @return true if this parser should handle the object
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
         * Called when starting to parse a JSON object.
         *
         * Handles action-based logic for starting new messages or continuing existing ones.
         *
         * @param message the log message to populate
         * @throws ParseException on parsing errors
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
         * Called when finishing parsing a JSON object.
         *
         * Extracts domain values, names, tags, and member data from the collected
         * attributes and updates the log message accordingly.
         *
         * @param attributes map of JSON property names to values
         * @param message the log message to update
         * @throws SAXException on parsing errors
         * @throws ParseException on semantic validation errors
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
            for (int n = JsonLogOption.MEMBER_MIN; n <= JsonLogOption.MEMBER_MAX && n <= maxMemberSource; n++) {
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

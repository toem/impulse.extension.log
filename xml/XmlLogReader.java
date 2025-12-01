package de.toem.impulse.extension.log.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

@RegistryAnnotation(annotation = XmlLogReader.Annotation.class)
/**
 * XML Log Reader implementation for the Impulse logging framework.
 *
 * This class provides comprehensive XML log file parsing capabilities, supporting both full XML documents
 * and XML fragments. It uses SAX (Simple API for XML) parsing for efficient, streaming XML processing
 * that can handle large log files without loading the entire document into memory.
 *
 * Key features:
 * - Streaming XML parsing using SAX parser for memory efficiency
 * - Support for XML fragments with automatic wrapper element injection
 * - XPath-like element matching for log entry identification
 * - Flexible attribute and text content extraction
 * - Multi-domain timestamp support (primary and secondary domains)
 * - Configurable log entry naming and tagging
 * - Custom member field extraction from XML elements and attributes
 *
 * Implementation notes:
 * - This class extends {@link de.toem.impulse.usecase.logging.AbstractLogReader}
 *   and follows the project conventions for log processing.
 * - Uses SAX parser for streaming XML processing to handle large files efficiently
 * - Supports XPath-like element path patterns for flexible log entry identification
 * - Integrates with XmlLogOption for configuration of parsing rules
 * - Compatible with XML streaming parser token-based processing
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
public class XmlLogReader extends AbstractLogReader {
    public static class Annotation extends AbstractSingleDomainRecordReader.Annotation {
        public static final Class<? extends ICell> multiton = Preference.class;
        public static final String id = "reader.log.xml";
        public static final String label = I18n.Log_XmlLogReader;
        public static final String iconId = I18n.Log_XmlLogReader_IconId;
        public static final String description = I18n.Log_XmlLogReader_Description;
        public static final String helpURL = I18n.Log_XmlLogReader_HelpURL;
        public static final String certificate = "v6QyhCYtG9Epk3qTVmQ3sMwkeYbjTaQ4\nVhODxCEfY7ExnE2ylazpEwuuq2EVmdJT\nYaD1fRGMZRni4xwAZwQt1knTYiG9BdUp\n3rNBnVNeVpd47BnG6+wzj2nDz1RCnLzq\njAfQ/pAoIJPIojTjRS0EFf8+W82FnBC0\ns7N1MP8Ex5ANhO5iOpZmOIoQBaE3yKML\nNBRJfWIaEuVfQEq29qlxAMrAkvX6B2MX\nQm0OcTcg9kQmLZBqdxFvMSTyFSgPz6Yb\nQ9qlTgw7x7Ae0kHenJDXWKRAY0mQOAIt\n24KMpGDrCHDgESbkyQMopA==\n";

    }

    // ========================================================================================================================
    // Members
    // ========================================================================================================================

    // ========================================================================================================================
    // Constructor
    // ========================================================================================================================

    /**
     * Default constructor for XmlLogReader.
     * 
     * Creates a new XML log reader instance with default configuration.
     * The reader will need to be configured with options before use.
     */
    public XmlLogReader() {
        super();
    }

    /**
     * Constructs an XmlLogReader with full configuration.
     * 
     * Creates a new XML log reader instance with the specified configuration parameters.
     * This constructor initializes the reader with all necessary components for immediate use.
     * 
     * @param descriptor the serializer descriptor defining the reader's capabilities
     * @param contentName the name of the content being processed
     * @param contentType the MIME type of the content
     * @param cellType the type identifier for the reader cell
     * @param configuration the configuration string for the reader
     * @param properties array of property key-value pairs for configuration
     * @param in the input stream containing the XML data to be parsed
     */
    public XmlLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

    /**
     * Preference configuration class for XmlLogReader.
     * 
     * This class defines the configuration preferences and UI controls for the XML log reader.
     * It extends the abstract log reader preference to provide XML-specific configuration options
     * and integrates with the Impulse framework's preference system.
     * 
     * Configuration Elements:
     * - XML fragment processing toggle
     * - XML log options for element/attribute mapping
     * - Serializer configuration for data persistence
     * 
     * @see AbstractLogReader.AbstractLogReaderPreference
     * @see XmlLogOption
     */
    @CellAnnotation(annotation = Preference.Annotation.class, dynamicChildren = { DefaultSerializerConfiguration.TYPE, XmlLogOption.TYPE })
    public static class Preference extends AbstractLogReader.AbstractLogReaderPreference {
        public static final String TYPE = Annotation.id;

        static class Annotation {
            public static final String id = XmlLogReader.Annotation.id;
            public static final String label = XmlLogReader.Annotation.label;
            public static final String description = XmlLogReader.Annotation.description;
            public static final String iconId = XmlLogReader.Annotation.iconId;
            public static final String helpURL = XmlLogReader.Annotation.helpURL;
            public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
            public static final Class<? extends ICell>[] additional = new Class[] { XmlLogOption.class };

        }

        /**
         * Instancer for XmlLogReader preferences.
         * 
         * This class handles the instantiation and initialization of XML log reader preference cells.
         * It extends the abstract default instancer to provide XML-specific setup logic.
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
             * It performs any necessary setup for the XML log reader preference cell.
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
         * @return the XmlLogReader class for serialization
         */
        @Override
        public Class<? extends ICellSerializer> getClazz() {
            return XmlLogReader.class;
        }

        /**
         * Returns the cell type identifier for this preference.
         * 
         * @return the cell type string identifier
         */
        @Override
        public String getCellType() {
            return XmlLogReader.Annotation.cellType;
        }

        // ========================================================================================================================
        // Controls
        // ========================================================================================================================

        /**
         * UI Controls class for XmlLogReader preferences.
         * 
         * This class provides the user interface controls for configuring XML log reader preferences.
         * It extends the multiton serializer preference controls to add XML-specific configuration options.
         * 
         * UI Sections:
         * - XML log options table for element/attribute mappings
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
             * Fills the fourth section of the preference controls with XML-specific options.
             * 
             * This method adds a table control for configuring XML log options, allowing users
             * to define element paths, attribute mappings, and parsing rules for XML log files.
             */
            protected void fillSection4() {

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON | TLK.OPEN, I18n.Log_XmlLogOptions, null,
                        I18n.Log_XmlLogOptions_Description, I18n.Log_XmlLogOptions_HelpURL);

                fillChildTable(container(), ISerializerDescriptor.Configuration.class, tlk().ld(cols(), TLK.FILL, TLK.NO_HINT, TLK.GRAB, TLK.NO_HINT),
                        TLK.GROUP | TLK.CHECK | TLK.BUTTON, I18n.General_SerializerConfigurations, null,
                        I18n.General_SerializerConfigurations_Description, I18n.General_SerializerConfigurations_HelpURL);              
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
     * Creates and returns the property model for XmlLogReader configuration.
     * 
     * This method extends the base log reader property model with XML-specific properties
     * such as fragment processing support.
     * 
     * @param descriptor the serializer descriptor
     * @param context the context object for property resolution
     * @return the configured property model
     */
    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return AbstractLogReader.getPropertyModel(descriptor, context).add("xmlFragment", false, null, "xmlFragment", null, null);
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================

    /**
     * Creates an XML option parser for the specified log option.
     * 
     * This method creates and configures an XmlOptionParser instance
     * that will handle the parsing logic for a specific XML log option configuration.
     * 
     * @param option the XML log option to create a parser for
     * @return the configured XML option parser
     * @throws ParseException if the option configuration is invalid
     */
    @Override
    protected XmlOptionParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new XmlOptionParser((XmlLogOption) option);
    }

    /**
     * Parses XML log data from the input stream using SAX parsing.
     * 
     * This is the main parsing method that processes XML log files. It uses a SAX parser
     * for efficient streaming XML processing and supports both full XML documents and XML fragments.
     * The method handles element matching, attribute extraction, and text content processing.
     * 
     * Processing Flow:
     * 1. Checks for XML fragment mode and wraps input if necessary
     * 2. Initializes parsing stacks for element hierarchy tracking
     * 3. Creates SAX parser with custom handler for XML events
     * 4. Processes start/end element events with option matching
     * 5. Extracts text content and attribute values
     * 6. Writes completed log messages to output
     * 
     * XML Fragment Support:
     * When xmlFragment property is true, the method automatically wraps the input stream
     * with dummy root elements to create valid XML structure for parsing.
     * 
     * @param progress the progress monitor for parsing operations
     * @param in the input stream containing XML log data
     * @throws ParseException if parsing fails due to configuration or data errors
     * @throws IOException if an I/O error occurs during reading
     */
    @Override
    protected void parseLogs(IProgress progress, InputStream in) throws ParseException, IOException {

        if (getTypedProperty("xmlFragment", Boolean.class)) {
            in = new SequenceInputStream(Collections.enumeration(Arrays.asList(
                    new InputStream[] { new ByteArrayInputStream("<dummy>".getBytes()), in, new ByteArrayInputStream("</dummy>".getBytes()), })));
        }

        // extract log data
        LogMessage message = new LogMessage();
        Stack<XmlOptionParser> parserStack = new Stack<>();
        Stack<StringBuilder> textStack = new Stack<>();
        Stack<String> pathStack = new Stack<>();

        // SAX parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);

        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();

            saxParser.parse(in, new DefaultHandler() {

            /**
             * Called at the start of document parsing.
             * 
             * @throws SAXException if a parsing error occurs
             */
            @Override
            public void startDocument() throws SAXException {

                super.startDocument();
            }

            /**
             * Called at the end of document parsing.
             * 
             * @throws SAXException if a parsing error occurs
             */
            @Override
            public void endDocument() throws SAXException {

                super.endDocument();
            }

            /**
             * Called when an XML element starts.
             * 
             * This method handles element matching, parser stacking, and data extraction
             * from element attributes for matching log options.
             * 
             * @param uri the namespace URI
             * @param localName the local name
             * @param qName the qualified name
             * @param attributes the element attributes
             * @throws SAXException if a parsing error occurs
             */
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                    boolean match = false;
                    String path = pathStack.isEmpty() ? "/" : pathStack.peek();
                    for (AbstractOptionParser p : parser) {
                        XmlOptionParser parser = (XmlOptionParser) p;
                        if (parser.matches(path, qName)) {
                            parserStack.push(parser);
                            try {
                                parser.startElement(uri, localName, qName, attributes, message);
                            } catch (ParseException e) {
                                throw new SAXException(e);
                            }
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        if (!"dummy".equals(qName))
                            throw new SAXException(new ParseException(-1, "No match for element \"" + qName + "\""));
                        parserStack.push(null);
                    }
                    textStack.push(new StringBuilder());
                    pathStack.push(path + "/" + qName);
                    super.startElement(uri, localName, qName, attributes);
                }

            /**
             * Called when an XML element ends.
             * 
             * This method handles element termination, text content extraction,
             * and log message writing for matching parsers.
             * 
             * @param uri the namespace URI
             * @param localName the local name
             * @param qName the qualified name
             * @throws SAXException if a parsing error occurs
             */
            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                    XmlOptionParser parser = parserStack.pop();
                    String text = textStack.pop().toString().trim();
                    pathStack.pop();
                    if (parser != null)
                        try {
                            parser.endElement(uri, localName, qName, text, message);

                        } catch (ParseException e) {
                            throw new SAXException(e);
                        }
                    super.endElement(uri, localName, qName);
                }

            /**
             * Called when character data is encountered.
             * 
             * This method accumulates text content for the current element.
             * 
             * @param ch the character array
             * @param start the start position
             * @param length the length of the character data
             * @throws SAXException if a parsing error occurs
             */
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (length > 0) {
                    textStack.peek().append(ch, start, length);
                    super.characters(ch, start, length);
                }
            }

            /**
             * Resolves external entities.
             * 
             * This method returns an empty input source to prevent external entity resolution.
             * 
             * @param publicId the public identifier
             * @param systemId the system identifier
             * @return an empty input source
             * @throws SAXException if a parsing error occurs
             * @throws IOException if an I/O error occurs
             */
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, java.io.IOException {
                return new org.xml.sax.InputSource(new java.io.StringReader(""));
            }
            });

            // write final message if not already done
            if (!message.isEmpty())
                write(message);

        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
            if (e.getException() instanceof ParseException)
                throw (ParseException) e.getException();
            // Allow normal Sax exceptions
            addParseExceptionMessage(e);
            // throw new ParseException("Invalid XML structure", e);
        }

        // lineNo++;
        // }

    }

    /**
     * XML Option Parser for processing XML elements based on configuration.
     * 
     * This inner class handles the parsing logic for individual XML log options. It matches
     * XML elements against configured paths, extracts data from attributes and text content,
     * and populates log messages with the extracted information.
     * 
     * Key Responsibilities:
     * - Element path matching against configured patterns
     * - Attribute value extraction for timestamps, names, and custom fields
     * - Text content processing for element values
     * - Multi-domain timestamp parsing (primary and secondary)
     * - Log entry naming and tagging based on XML data
     * - Action handling (start, terminate, ignore) for log entry lifecycle
     * 
     * Path Matching:
     * The parser supports XPath-like element matching with wildcards and specific path patterns.
     * Element names can be exact matches or use "*" for any element at that level.
     * 
     * @see XmlLogOption
     * @see AbstractOptionParser
     */
    class XmlOptionParser extends AbstractOptionParser {

        // The element name to match (null for any element)
        public String name;
        // The parent path to match (null for any path)
        public String ppath;

        // Array of source attribute names indexed by source type
        protected String[] sourceAttributes;

        // Attribute name for domain/timestamp extraction
        protected String domainAttribute;
        // Attribute name for secondary domain extraction
        protected String domain2Attribute;
        // Attribute name for primary name extraction
        protected String name1Attribute;
        // Attribute name for secondary name extraction
        protected String name2Attribute;
        // Attribute name for tag extraction
        protected String tagAttribute;

        /**
         * Constructs an XmlOptionParser with the specified XML log option.
         * 
         * This constructor initializes the parser with configuration from the provided XmlLogOption,
         * setting up element path matching, source attribute mappings, and parsing parameters.
         * It parses the element path to extract name and parent path components, and configures
         * attribute mappings for timestamps, names, tags, and custom member fields.
         * 
         * @param option the XmlLogOption containing configuration for this parser
         * @throws ParseException if the option configuration is invalid or malformed
         */
        public XmlOptionParser(XmlLogOption option) throws ParseException {
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
            sourceAttributes = new String[option.getMaxSource() + 1];
            for (int n = XmlLogOption.SOURCE_ATTRIBUTE1; n < sourceAttributes.length; n++) {
                if (option.hasValidSource(n)) {
                    String id = option.getSourceIdentifier(n);
                    sourceAttributes[n] = id;
                    if (n == domainSource)
                        domainAttribute = option.getSourceIdentifier(n);
                    if (n == domain2Source)
                        domain2Attribute = option.getSourceIdentifier(n);
                    if (n == name1Source)
                        name1Attribute = option.getSourceIdentifier(n);
                    if (n == name2Source)
                        name2Attribute = option.getSourceIdentifier(n);
                    if (n == tagSource)
                        tagAttribute = option.getSourceIdentifier(n);

                }
            }
        }

        /**
         * Checks if the given element path and name match this parser's configuration.
         * 
         * This method performs XPath-like matching against the configured element path pattern.
         * It verifies that the element name matches (or is wildcard) and that the parent path
         * ends with the configured parent path pattern.
         * 
         * @param path the current element path in the XML document
         * @param name the element name to match
         * @return true if the element matches this parser's configuration, false otherwise
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
         * Processes the start of an XML element, extracting data from attributes.
         * 
         * This method is called when a matching XML element starts. It handles action processing
         * (start/terminate), extracts timestamp domains, names, and custom member values from
         * element attributes, and updates the log message accordingly. For ACTION_START,
         * it may write the previous message and clear it.
         * 
         * @param uri the namespace URI of the element
         * @param localName the local name of the element
         * @param qName the qualified name of the element
         * @param attributes the attributes of the element
         * @param message the log message to populate with extracted data
         * @throws ParseException if parsing of extracted values fails
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes, LogMessage message) throws ParseException {

            if (action == AbstractLogOption.ACTION_IGNORE)
                return;

            if (action == AbstractLogOption.ACTION_START && !message.isEmpty()) {
                write(message);
                message.clear();
            }

            boolean changed = false;

            // position
            if (domainMode != AbstractLogOption.DOMAIN_UNDEFINED && domainSource != XmlLogOption.SOURCE_TEXT) {
                final String domainText = (domainSource > 0 && domainAttribute != null) ? attributes.getValue(domainAttribute) : null;
                final Long position = parseDomain(domainText);
                message.position = position;
                message.positionParser = this;
                changed |= true;
            }
            // position
            if (domain2Mode != AbstractLogOption.DOMAIN_UNDEFINED && domain2Source != XmlLogOption.SOURCE_TEXT) {
                final String domainText = (domain2Source > 0 && domain2Attribute != null) ? attributes.getValue(domain2Attribute) : null;
                final Long position = parseDomain2(domainText);
                message.position2 = position;
                changed |= true;
            }

            // name
            if (nameMode != AbstractLogOption.NAME_UNDEFINED && name1Source != XmlLogOption.SOURCE_TEXT) {
                if (nameMode == AbstractLogOption.NAME_EXPLICIT)
                    message.name1 = name0;
                else {
                    if (name1Attribute != null) {
                        String name = attributes.getValue(name1Attribute);
                        if (!Utils.isEmpty(name))
                            message.name1 = name.trim();
                    }
                    message.nameParser = this;
                }
                changed |= true;
            }
            if (name2Mode != AbstractLogOption.NAME_UNDEFINED && name2Source != XmlLogOption.SOURCE_TEXT && name2Attribute != null) {
                String name = attributes.getValue(name2Attribute);
                if (!Utils.isEmpty(name))
                    message.name2 = name.trim();
                changed |= true;
            }

            // members
            for (int n = XmlLogOption.MEMBER_MIN; n <= XmlLogOption.MEMBER_MAX && n <= maxMemberSource; n++) {
                int widx = memberIndex[n];
                if (n != XmlLogOption.SOURCE_TEXT && (widx >= 0 || n == tagSource)) {
                    String name = sourceAttributes[n];
                    String text = attributes.getValue(name);
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

        }

        /**
         * Processes the end of an XML element, extracting data from text content.
         * 
         * This method is called when a matching XML element ends. It extracts timestamp domains,
         * names, and custom member values from the element's text content, updates the log message,
         * and handles ACTION_TERMINATE by writing the completed message.
         * 
         * @param uri the namespace URI of the element
         * @param localName the local name of the element
         * @param qName the qualified name of the element
         * @param text the text content of the element
         * @param message the log message to populate with extracted data
         * @throws SAXException if a SAX parsing error occurs
         * @throws ParseException if parsing of extracted values fails
         */
        public void endElement(String uri, String localName, String qName, String text, LogMessage message) throws SAXException, ParseException {

            boolean changed = false;

            // position
            if (domainMode != AbstractLogOption.DOMAIN_UNDEFINED && domainSource == XmlLogOption.SOURCE_TEXT) {
                final Long position = parseDomain(text);
                message.position = position;
                message.positionParser = this;
                changed |= true;
            }
            if (domain2Mode != AbstractLogOption.DOMAIN_UNDEFINED && domain2Source == XmlLogOption.SOURCE_TEXT) {
                final Long position = parseDomain(text);
                message.position2 = position;
                changed |= true;
            }

            // name
            if (nameMode != AbstractLogOption.NAME_UNDEFINED && name1Source == XmlLogOption.SOURCE_TEXT) {
                if (nameMode == AbstractLogOption.NAME_EXPLICIT)
                    message.name1 = name0;
                else {
                    if (!Utils.isEmpty(text))
                        message.name1 = text.trim();
                }
                message.nameParser = this;
                changed |= true;
            }
            if (name2Mode != AbstractLogOption.NAME_UNDEFINED && name2Source == XmlLogOption.SOURCE_TEXT) {
                if (!Utils.isEmpty(text))
                    message.name2 = text.trim();
                changed |= true;
            }

            // members
            int widx = memberIndex[XmlLogOption.SOURCE_TEXT];
            if ((widx >= 0 || XmlLogOption.SOURCE_TEXT == tagSource)) {
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
                    if (XmlLogOption.SOURCE_TEXT == tagSource) {
                        int tag = parseTags(text);
                        if (tag > 0 && (message.tag == 0 || tag < message.tag)) {
                            message.tag = tag;
                            changed |= true;
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

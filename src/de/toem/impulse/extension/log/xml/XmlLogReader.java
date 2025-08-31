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

    public XmlLogReader() {
        super();
    }

    public XmlLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

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

        @RegistryAnnotation(annotation = Instancer.Annotation.class)
        public static class Instancer extends AbstractDefaultInstancer {

            static class Annotation {
                public static final String id = "de.toem.instancer." + TYPE;
                public static final String cellType = TYPE;
            }

            @Override
            protected void initOne(String id, ICell cell, ICell container) {
                super.initOne(id, cell, container);
                // cell.setValue("script", Scripting.loadScriptFromResources(ScriptedCellReaderPreference.class, "scriptedReader.js"));
            }
        }

        @Override
        public Class<? extends ICellSerializer> getClazz() {
            return XmlLogReader.class;
        }

        @Override
        public String getCellType() {
            return XmlLogReader.Annotation.cellType;
        }

        // ========================================================================================================================
        // Controls
        // ========================================================================================================================

        static public class Controls extends AbstractMultitonSerializerPreference.Controls {

            public Controls(Class<? extends ICell> clazz) {
                super(clazz);
            }

            protected void fillSection4() {

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON, I18n.Log_XmlLogOptions, null,
                        I18n.Log_XmlLogOptions_Description, I18n.Log_XmlLogOptions_HelpURL);

                super.fillSection4();

            };
        }

        public static ITlkControlProvider getControls() {
            return new Controls(AbstractLogReaderPreference.class);
        }
    }

    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return AbstractLogReader.getPropertyModel(descriptor, context).add("xmlFragment", false, null, "xmlFragment", null, null);
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================

    @Override
    protected XmlOptionParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new XmlOptionParser((XmlLogOption) option);
    }

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

                @Override
                public void startDocument() throws SAXException {

                    super.startDocument();
                }

                @Override
                public void endDocument() throws SAXException {

                    super.endDocument();
                }

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

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (length > 0) {
                        textStack.peek().append(ch, start, length);
                        super.characters(ch, start, length);
                    }
                }

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

    class XmlOptionParser extends AbstractOptionParser {

        public String name;
        public String ppath;

        protected String[] sourceAttributes;

        protected String domainAttribute;
        protected String domain2Attribute;
        protected String name1Attribute;
        protected String name2Attribute;
        protected String tagAttribute;

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

        public boolean matches(String path, String name) {
            if (this.name != null)
                if (!this.name.equals(name))
                    return false;
            if (this.ppath != null)
                if (!path.endsWith(ppath))
                    return false;
            return true;
        }

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

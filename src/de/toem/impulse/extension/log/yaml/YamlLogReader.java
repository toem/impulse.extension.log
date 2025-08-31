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

    public YamlLogReader() {
        super();
    }

    public YamlLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

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
            return YamlLogReader.class;
        }

        @Override
        public String getCellType() {
            return YamlLogReader.Annotation.cellType;
        }

        // ========================================================================================================================
        // Controls
        // ========================================================================================================================

        static public class Controls extends AbstractMultitonSerializerPreference.Controls {

            public Controls(Class<? extends ICell> clazz) {
                super(clazz);
            }

            protected void fillSection4() {

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON, I18n.Log_YamlLogOptions, null,
                        I18n.Log_YamlLogOptions_Description, I18n.Log_YamlLogOptions_HelpURL);

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
        return AbstractLogReader.getPropertyModel(descriptor, context);
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================

    @Override
    protected YamlOptionParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new YamlOptionParser((YamlLogOption) option);
    }

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

    class YamlOptionParser extends AbstractOptionParser {

        public String name;
        public String ppath;

        protected String[] sourceValues;

        protected String domainValue;
        protected String domain2Value;
        protected String name1Value;
        protected String name2Value;
        protected String tagValue;

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

        public boolean matches(String path, String name) {
            if (this.name != null)
                if (!this.name.equals(name))
                    return false;
            if (this.ppath != null)
                if (!path.endsWith(ppath))
                    return false;
            return true;
        }

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

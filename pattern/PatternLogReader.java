package de.toem.impulse.extension.log.pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.samples.ISample;
import de.toem.impulse.samples.ITextSamplesWriter;
import de.toem.impulse.serializer.AbstractSingleDomainRecordReader;
import de.toem.impulse.usecase.logging.AbstractLogOption;
import de.toem.impulse.usecase.logging.AbstractLogReader;
import de.toem.impulse.usecase.logging.LogWriter;
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
 * Pattern-based log reader for impulse.
 *
 * This reader applies configurable regular expression patterns to log files and
 * converts matching lines into impulse records. Patterns are configured via
 * {@link PatternLogOption} instances and can extract domain values, names,
 * tags and member attributes from capture groups.
 *
 * Key features:
 * - Uses Java regular expressions to match and extract data from log lines
 * - Supports multi-line message assembly using pattern actions (start/terminate)
 * - Optional writing of raw lines into a separate "lines" writer
 * - Configurable skip/stop line counts and progress reporting
 *
 * Implementation notes:
 * - This reader extends {@link de.toem.impulse.usecase.logging.AbstractLogReader}
 *   and follows the project property-model conventions for configuration.
 * - Time and domain parsing, member mapping and writer interaction are
 *   implemented in the nested {@link PatternParser} class.
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
@RegistryAnnotation(annotation = PatternLogReader.Annotation.class)
public class PatternLogReader extends AbstractLogReader {
    /**
     * Annotation class for PatternLogReader.
     */
    public static class Annotation extends AbstractSingleDomainRecordReader.Annotation {
        public static final Class<? extends ICell> multiton = Preference.class;
        public static final String id = "reader.log.pattern";
        public static final String label = I18n.Log_PatternLogReader;
        public static final String iconId = I18n.Log_PatternLogReader_IconId;
        public static final String description = I18n.Log_PatternLogReader_Description;
        public static final String helpURL = I18n.Log_PatternLogReader_HelpURL;
        public static final String certificate = "CAEM99TkOMQmlPZAuLeIxswkeYbjTaQ4\nVhODxCEfY7ExnE2ylazpEwuuq2EVmdJT\n+o/3/8jG9p3MloNhSh7gsTKIahjicUpx\nvID2SzveHSdksO5h9sab7l6KlYUJqHHA\nmA8c/uX5tY0u3DkPqRB9b830VQQeWcKE\n4aNvucHquds9qv+AuWsWjgafPwQF6msf\nlaibGvpNcr0t0o8MLAz5JY0nWR4fv8rp\n+3NEdgmKAuNK76Mi2K/D/QAwq1Yoix31\nRsacUplMvefpKvUUJOmePyZajbHs4liK\nvVaxWdD5mtbCSZVhQ0fsSA==\n";
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
    public PatternLogReader() {
        super();
    }

    /**
     * Constructs a PatternLogReader with the specified parameters.
     *
     * @param descriptor the serializer descriptor
     * @param contentName the content name
     * @param contentType the content type
     * @param cellType the cell type
     * @param configuration the configuration
     * @param properties the properties
     * @param in the input stream
     */
    public PatternLogReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    /**
     * Creates a property model for the pattern log reader.
     * The model exposes configuration keys such as writeLines, skipLines and stopAfterLines
     * that control parsing behavior.
     *
     * @param descriptor serializer descriptor context
     * @param context additional context (may be null)
     * @return the property model for this reader
     */

    // ========================================================================================================================
    // Preferences
    // ========================================================================================================================

    /**
     * Preference class for PatternLogReader.
     */
    @CellAnnotation(annotation = Preference.Annotation.class, dynamicChildren = { DefaultSerializerConfiguration.TYPE, PatternLogOption.TYPE })
    public static class Preference extends AbstractLogReader.AbstractLogReaderPreference {
        // The type identifier
        public static final String TYPE = Annotation.id;

        /**
         * Annotation for Preference.
         */
        static class Annotation {
            public static final String id = PatternLogReader.Annotation.id;
            public static final String label = PatternLogReader.Annotation.label;
            public static final String description = PatternLogReader.Annotation.description;
            public static final String iconId = PatternLogReader.Annotation.iconId;
            public static final String helpURL = PatternLogReader.Annotation.helpURL;
            public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
            public static final Class<? extends ICell>[] additional = new Class[] { PatternLogOption.class };

        }

        /**
         * Instancer for Preference.
         */
        @RegistryAnnotation(annotation = Instancer.Annotation.class)
        public static class Instancer extends AbstractDefaultInstancer {

            /**
             * Annotation for Instancer.
             */
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
            return PatternLogReader.class;
        }

        /**
         * Returns the cell type.
         *
         * @return the cell type
         */
        @Override
        public String getCellType() {
            return PatternLogReader.Annotation.cellType;
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

                fillChildTable(container(), AbstractLogOption.class, cols(), TLK.EXPAND | TLK.CHECK | TLK.BUTTON| TLK.OPEN, I18n.Log_PatternLogOptions, null,
                        I18n.Log_PatternLogOptions_Description, I18n.Log_PatternLogOptions_HelpURL);

                fillChildTable(container(), ISerializerDescriptor.Configuration.class, tlk().ld(cols(), TLK.FILL, TLK.NO_HINT, TLK.GRAB, TLK.NO_HINT),
                        TLK.GROUP | TLK.CHECK | TLK.BUTTON, I18n.General_SerializerConfigurations, null,
                        I18n.General_SerializerConfigurations_Description, I18n.General_SerializerConfigurations_HelpURL);
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
     * The model exposes configuration entries used by the parser:
     * - writeLines: whether parsed raw lines should be written to a separate writer
     * - skipLines: number of initial lines to skip
     * - stopAfterLines: stop parsing after this many lines
     *
     * @param descriptor serializer descriptor providing contextual information
     * @param context additional context (may be null)
     * @return configured PropertyModel
     */
    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return AbstractLogReader.getPropertyModel(descriptor, context).add("writeLines", false, null, "Add signal with raw lines includes").add("skipLines", 0, null,null,null, "Skip Lines").add("stopAfterLines",  -1, null,null,null, "Stop After Lines");
    }

    // ========================================================================================================================
    // Parse
    // ========================================================================================================================

    @Override
    /**
     * Factory method for creating a {@link PatternParser} from an {@link AbstractLogOption}.
     *
     * @param option option describing the pattern and parsing behavior
     * @return a new PatternParser instance
     * @throws ParseException if the option cannot be converted into a parser
     */
    protected PatternParser createOptionParser(AbstractLogOption option) throws ParseException {
        return new PatternParser((PatternLogOption) option);
    }

    /**
     * Main parsing loop. Reads the input stream line-by-line, applies all
     * configured pattern parsers and writes resulting messages via the
     * configured writers.
     *
     * Behavior highlights:
     * - Skips empty lines and respects skipLines/stopAfterLines properties
     * - Each non-empty line must match at least one configured pattern; otherwise
     *   a ParseException is thrown
     * - If a "lines" writer is configured, raw lines are written there
     *
     * @param progress progress/cancellation interface
     * @param in input stream to read
     * @throws ParseException on parsing errors
     * @throws IOException on IO errors
     */
    @Override
    protected void parseLogs(IProgress progress, InputStream in) throws ParseException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charSet));
        closable = reader;

        // extract log data
        LogMessage message = new LogMessage();

        // skip/stop
        int skipLines = Utils.parseInt(getProperty("skipLines"), -1);
        int stopAfterLines = Utils.parseInt(getProperty("stopAfterLines"), -1);


        ITextSamplesWriter linesWriter = lines != null ? ((ITextSamplesWriter) getWriter(lines)) : null;
        int nextLinesTargetId = 1;
        if (linesWriter != null) {
            linesWriter.setEnum(ISample.ENUM_RELATION_STYLE, 1, "Parsed");
            linesWriter.setEnum(ISample.ENUM_RELATION_DOMAINBASE, 1, PatternLogReader.this.domainBase.toString());
        }

        // read lines
        while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {


            if (Utils.isEmpty(line)) {
                lineNo++;
                continue;
            }
            if (skipLines > 0 && lineNo < skipLines) {
                lineNo++;
                continue;
            }
            if (stopAfterLines > 0 && lineNo >= stopAfterLines)
                break;

            boolean matched = false;
            for (AbstractOptionParser p : parser) {
                PatternParser parser = (PatternParser) p;
                Matcher m = parser.matcher(line);
                if (m != null && m.matches()) {

                    // handle pattern options
                    LogWriter writer = parser.parse(m, message);
                    if (linesWriter != null && writer != null) {
                        if (writer.linesTargetId == 0) {
                            writer.linesTargetId = nextLinesTargetId++;
                            linesWriter.setEnum(ISample.ENUM_RELATION_TARGET, writer.linesTargetId, writer.writer.getId());
                        }
                        linesWriter.attachRelation(ISample.AT_RELATION_ABS_POS|ISample.AT_RELATION_CONTENT_FLAG, writer.linesTargetId, 1, writer.current, 1,ISample.CONTENT_SAMPLE,writer.writer.getCount()-1);
                    }
                    matched = true;
                    break;
                }
            }
            if (!matched)
                throw new ParseException(-1, "No match");

            if (linesWriter != null)
                linesWriter.write(lineNo, false, line);
            
            // next line / progress
            if ((lineNo++ % 1000) == 0)
                flushAndSetProgress(progress);
        }

    }

    class PatternParser extends AbstractOptionParser {

        // The compiled pattern
        private Pattern pattern;
        // The matcher instance
        private Matcher matcher;

        /**
         * Constructs a PatternParser for the provided option.
         * The option's regular expression is compiled and prepared for matching.
         *
         * @param option pattern configuration
         * @throws ParseException if the regex is invalid
         */
        public PatternParser(PatternLogOption option) throws ParseException {
            super(option);

            // detect pattern
            try {
                pattern = Pattern.compile(option.pattern);
                matcher = pattern != null ? pattern.matcher("") : null;
            } catch (Throwable e) {
                throw new ParseException(0, "Invalid pattern", e);
            }

        }

        /**
         * Returns a Matcher for the given input line by resetting the compiled
         * pattern matcher. Returns null if the pattern compilation previously
         * failed.
         *
         * @param line input line
         * @return Matcher or null
         */
        public final Matcher matcher(String line) {
            return matcher != null ? matcher.reset(line) : null;
        }

    /**
     * Parses the matched groups from the provided Matcher and updates the
     * given LogMessage. Depending on the parser action the method can
     * produce a {@link LogWriter} indicating that the current message
     * should be written out.
     *
     * This method extracts domain values, names, tags and member values
     * using the configured group indices on the parser instance. It also
     * updates line numbers and the message empty flag.
     *
     * @param m regex matcher with successful match
     * @param message mutable log message to populate
     * @return a LogWriter when a write is triggered; otherwise null
     * @throws ParseException on semantic validation errors
     */
    public LogWriter parse(Matcher m, LogMessage message) throws ParseException {

            if (action == AbstractLogOption.ACTION_IGNORE)
                return null;

            LogWriter writer = null;
            if (action == AbstractLogOption.ACTION_START && !message.isEmpty()) {
                writer = write(message);
                message.clear();
            }

            boolean changed = false;

            // position
            if (domainMode != AbstractLogOption.DOMAIN_UNDEFINED) {
                final String domainText = (domainSource > 0 && domainSource <= m.groupCount()) ? m.group(domainSource) : null;
                final Long position = parseDomain(domainText);
                message.position = position;
                message.positionParser = this;
                changed |= true;
            }
            if (domain2Mode != AbstractLogOption.DOMAIN_UNDEFINED) {
                final String domainText = (domain2Source > 0 && domain2Source <= m.groupCount()) ? m.group(domain2Source) : null;
                final Long position = parseDomain2(domainText);
                message.position2 = position;
                changed |= true;
            }

            // name
            if (nameMode != AbstractLogOption.NAME_UNDEFINED) {
                if (nameMode == AbstractLogOption.NAME_EXPLICIT)
                    message.name1 = name0;
                else {
                    if (name1Source > 0 && name1Source <= m.groupCount()) {
                        message.name1 = m.group(name1Source).trim();
                    }
                }
                message.nameParser = this;
                changed |= true;
            }
            if (name2Mode != AbstractLogOption.NAME_UNDEFINED && name2Source > 0 && name2Source <= m.groupCount()) {
                message.name2 = m.group(name2Source).trim();
                changed |= true;
            }

            // members
            for (int n = PatternLogOption.MEMBER_MIN; n <= PatternLogOption.MEMBER_MAX && n <= maxMemberSource && n <= m.groupCount(); n++) {
                int widx = memberIndex[n];
                if (widx >= 0 || n == tagSource) {
                    String text = m.group(n).trim();
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

            // rec position
            if ( message.lineNo == -1) {
                message.lineNo = lineNo;
                changed |= true;
            }

            if (changed)
                message.setEmpty(false);

            if (action == AbstractLogOption.ACTION_TERMINATE) {
                writer = write(message);
                message.clear();
            }

            return writer;
        }
    }
}

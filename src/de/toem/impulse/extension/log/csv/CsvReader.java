
package de.toem.impulse.extension.log.csv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.toem.impulse.cells.record.IRecord;
import de.toem.impulse.cells.record.RecordScope;
import de.toem.impulse.cells.record.RecordSignal;
import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.samples.IEventSamplesWriter;
import de.toem.impulse.samples.IFloatSamplesWriter;
import de.toem.impulse.samples.IIntegerSamplesWriter;
import de.toem.impulse.samples.ISample;
import de.toem.impulse.samples.ISamples;
import de.toem.impulse.samples.IStructSamplesWriter;
import de.toem.impulse.samples.ITextSamplesWriter;
import de.toem.impulse.samples.domain.DateBase;
import de.toem.impulse.samples.domain.DomainBases;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.impulse.samples.domain.TimeBase;
import de.toem.impulse.samples.raw.StructMember;
import de.toem.impulse.serializer.AbstractSingleDomainRecordReader;
import de.toem.impulse.serializer.IRecordReader;
import de.toem.impulse.usecase.logging.Log;
import de.toem.impulse.usecase.logging.LogWriter;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.serializer.ISerializerDescriptor;
import de.toem.toolkits.pattern.element.serializer.IUriStream;
import de.toem.toolkits.pattern.element.serializer.SingletonSerializerPreference.DefaultSerializerConfiguration;
import de.toem.toolkits.pattern.properties.PropertyModel;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.pattern.threading.IProgress;
import de.toem.toolkits.utils.serializer.ParseException;

@RegistryAnnotation(annotation = CsvReader.Annotation.class)
public class CsvReader extends AbstractSingleDomainRecordReader {
    public static class Annotation extends AbstractSingleDomainRecordReader.Annotation {
        public static final Class<? extends ICell> multiton = CsvPreference.class;
        public static final String id = "reader.csv";
        public static final String label = I18n.Log_CsvReader;
        public static final String iconId = I18n.Log_CsvReader_IconId;
        public static final String description = I18n.Log_CsvReader_Description;
        public static final String helpURL = I18n.Log_CsvReader_HelpURL;
        public static final String certificate = "v6QyhCYtG9EQstaUaqpnFiUrBr5WzH78\nVhODxCEfY7ExnE2ylazpEwuuq2EVmdJT\nYaD1fRGMZRmcqBmM6A4B2knTYiG9BdUp\n3rNBnVNeVpd47BnG6+wzj2nDz1RCnLzq\njAfQ/pAoIJPIojTjRS0EFf8+W82FnBC0\ns7N1MP8Ex5ANhO5iOpZmOIoQBaE3yKML\nNBRJfWIaEuVfQEq29qlxAMrAkvX6B2MX\nQm0OcTcg9kSYBZIyojRaC7xHNL7qfvd3\nHkxeYAJhOSQBd+w2qGhA/Iw3YpsRAgNg\nFxzaMg/evTlhv9HLhQQIMg==\n";
    }

    // change
    private boolean opened;

    // domain base
    protected IDomainBase domainBase;
    protected boolean relativeDomainValue;
    protected long domainOffset;
    protected boolean hasDomainOffset;

    // domain mode
    protected int domainMode;
    protected int domainSource;
    protected int domain2Mode;
    protected int domain2Source;
    protected DateFormat dateScanner;
    protected IDomainBase domainValueUnit;
    protected IDomainBase domain2ValueUnit;

    // scope & signal names
    protected int nameMode;
    protected String name0;
    protected int name1Source;
    protected int name2Mode;
    protected int name2Source;
    protected String nameSeparator;
    protected String namePrefix;

    protected boolean addRecPos;

    protected int tagSource;
    protected Pattern fatalPattern;
    protected Pattern errorPattern;
    protected Pattern warningPattern;
    protected Pattern successPattern;
    protected Pattern infoPattern;
    protected Pattern debugPattern;
    protected Pattern tracePattern;

    // input
    protected int maxSource;
    protected char[] del;
    protected char del2;
    protected char del3;
    protected char del4;
    protected char del5;
    protected char del6;
    protected char quote;
    protected String c1Quote;
    protected String c2Quote;
    protected String[] splitted;

    // signals
    private int[] type;

    // mode
    public int mode;

    // separate mode
    private IRecord.Signal[] sepSignals;

    // struct mode
    private IRecord.Signal structSignal;
    private StructMember[] structMembers;

    // log mode
    protected List<String> logMembers = new ArrayList<>();
    protected Map<String, Integer> logTypes = new HashMap<>();
    protected Map<String, String> logFormats = new HashMap<>();
    protected Map<String, String> logContents = new HashMap<>();
    protected Map<String, LogWriter> logWriters = new HashMap<String, LogWriter>();
    protected String[] sourceMembers = new String[CsvPreference.MEMBER_MAX + 1]; // all valid members; else null
    protected int[] memberIndex = new int[CsvPreference.MEMBER_MAX + 1]; // writer index of valid members; else -1
    protected int maxMemberSource;
    protected int recPosIndex;

    // read lines
    protected Closeable closable = null;
    protected String line = null;
    protected int lineNo = 0;

    // ========================================================================================================================
    // Constructor
    // ========================================================================================================================

    public CsvReader() {
        super();
    }

    public CsvReader(ISerializerDescriptor descriptor, String contentName, String contentType, String cellType, String configuration,
            String[][] properties, InputStream in) {
        super(descriptor, configuration, properties, getPropertyModel(descriptor, null), in);
    }

    // ========================================================================================================================
    // Applicable
    // ========================================================================================================================

    @Override
    protected int isApplicable(byte[] buffer, String charset) {

        return APPLICABLE;

        // return findConfiguration(buffer, buffer.length, charset) != null ? APPLICABLE : NOT_APPLICABLE;

    }

    @Override
    protected int isApplicable(String name, String contentType) {
        return APPLICABLE;
        // return getDetectSniffSize() | SNIFF_PREFERRED;
    }

//    protected float configMatches(CsvPreference config, byte[] header, int length, String charSet) {
//        try {
//
//            // char set
//            if (!Utils.isEmpty(config.charSet))
//                charSet = config.charSet;
//            if (Utils.isEmpty(charSet))
//                charSet = Charset.defaultCharset().displayName();
//
//            // reader
//            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(header, 0, length), charSet));
//
//            // skip lines before first row
//            for (int i = 0; i < config.firstRow - 1; i++)
//                reader.readLine();
//
//            // get first line
//            String line = reader.readLine();
//            if (Utils.isEmpty(line))
//                return 0;
//
//            // check if no of columns available
//            this.splitted = new String[CsvPreference.SOURCE_MAX + 1];
//            initSplit(config);
//            int sources = split(line, splitted);
//
//            reader.close();
//
//            if (sources >= config.getMaxSource() && sources > 0)
//                return 1.0f * config.getMaxSource() / sources;
//
//        } catch (Throwable e) {
//        }
//        return 0;
//    }
    // ========================================================================================================================
    // Property Model
    // ========================================================================================================================

    static public PropertyModel getPropertyModel(ISerializerDescriptor descriptor, Object context) {
        return getDefaultPropertyModel().add("domainBase", "us", DomainBases.ALL_LABELS_ARRAY, null, "Domain Base", null, null)
                .add("charSet", "", Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().keySet().size()]), null,
                        "CharSets", null)
                .add("include", "", null, null, "Include Logger", null, null).add("exclude", "", null, null, "Exclude Logger", null, null)
                .add("start", "", null, null, "Start", null, null).add("end", "", null, null, "End", null, null)
                .add("addRecPos", false, null, "addRecPos", null, null).add("relativeDomainValue", false, null, "Start", null, null);

    }

    // ========================================================================================================================
    // Supports
    // ========================================================================================================================

    public static boolean supports(Object request, Object context) {
        if (((Integer) IRecordReader.SUPPORT_CONFIGURATION).equals(request) && DefaultSerializerConfiguration.TYPE.equals(context))
            return true;
        return false;
    }

    // ========================================================================================================================
    // Psrse
    // ========================================================================================================================

    @Override
    protected void parse(IProgress progress, InputStream in) throws ParseException {

        try {

            // input
            BufferedInputStream buffered = new BufferedInputStream(in);

            CsvPreference preference = (CsvPreference) this.getDescriptor();
            
            this.maxSource = preference.getMaxSource();
            if (maxSource <= 0)
                throw new ParseException(-1, "No columns configured");

            // domain base
            domainBase = DomainBases.parse(preference.domainBase);
            if (domainBase == null)
                domainBase = TimeBase.ms;
            initRecord(preference.getId(), domainBase);
            relativeDomainValue = preference.relativeDomainValue;

            // charset / reader
            String charSet = Charset.defaultCharset().displayName();
            if (!Utils.isEmpty(preference.charSet))
                charSet = preference.charSet;
            else if (in instanceof IUriStream) {
                if (!Utils.isEmpty(((IUriStream) in).getCharSet()))
                    charSet = ((IUriStream) in).getCharSet();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(buffered, charSet));
            closable = reader;

            // read lines before first row
            for (int i = 0; i < preference.firstRow - 1; i++)
                reader.readLine();

            // read header if available
            if (preference.sepOther && Utils.isEmpty(preference.delimeter))
                throw new ParseException(-1, "Empty other separator");
            if (!preference.sepOther && !preference.sepComma && !preference.sepSemi && !preference.sepSpace
                    && !preference.sepTab)
                throw new ParseException(-1, "No separator selected");
            this.splitted = new String[maxSource + 1];
            initSplit(preference);
            String[] labels = null;
            if (preference.hasLabels) {
                String header = reader.readLine();
                if (!Utils.isEmpty(header)) {
                    labels = new String[maxSource + 1];
                    int count = split(header, labels);
                    if (count < maxSource)
                        throw new ParseException(-1, "Invalid no of labels in header");
                } else
                    throw new ParseException(-1, "No header found");

            }

            // mode
            this.mode = preference.mode;

            // separate signals
            if (mode == CsvPreference.MODE_SEPERATE) {
                sepSignals = new RecordSignal[maxSource + 1];
                type = new int[maxSource + 1];
                for (int n = CsvPreference.SOURCE_MIN; n <= maxSource; n++) {
                    type[n] = preference.getType(n);
                    String name = preference.getMemberName(n, labels != null ? labels[n] : null);
                    if (preference.getSignalType(n) != ISample.DATA_TYPE_UNKNOWN)
                        sepSignals[n] = this.addSignal(null, name, null, null, preference.getSignalType(n),
                                -1, ISamples.FORMAT_DEFAULT);
                }
            }

            // structure signal
            else if (mode == CsvPreference.MODE_STRUCT) {
                structMembers = new StructMember[maxSource + 1];
                structSignal = this.addSignal(null, Utils.isEmpty(preference.name0) ? "s" : preference.name0, null, null,
                        ISample.DATA_TYPE_STRUCT,-1, ISamples.FORMAT_DEFAULT);
                type = new int[maxSource + 1];
                for (int n = CsvPreference.SOURCE_MIN; n <= maxSource; n++) {
                    type[n] = preference.getType(n);
                    String name = preference.getMemberName(n, labels != null ? labels[n] : null);
                    structMembers[n] = ((IStructSamplesWriter) getWriter(structSignal)).createMember(structMembers, n, name,null,null,null,
                            preference.getMemberType(n),-1, ISamples.FORMAT_DEFAULT);
                }
            }

            // log signal(s)
            else if (mode == CsvPreference.MODE_LOG) {

                // members
                for (int n = CsvPreference.MEMBER_MIN; n <= CsvPreference.MEMBER_MAX; n++) {
                    if (preference.hasValidMember(n) && preference.hasValidSource(n)) {
                        String m = preference.getMemberName(n, null);
                        int type = preference.getMemberType(n);
                        if (!Utils.isEmpty(m) && type != CsvPreference.SIGNAL_NONE) {
                            if (!logMembers.contains(m)) {
                                logMembers.add(m);
                                if (!Log.contains(m)) {
                                    logTypes.put(m, preference.getMemberType(n));
//                                    logContents.put(m, preference.getMemberContent(n));
//                                    logFormats.put(m, preference.getMemberFormat(n));
                                }
                            }
                        }
                    }

                }
                addRecPos = preference.addRecPos;
//                if (addRecPos)
//                    logMembers.add(Log.RECPOS);
                Log.sort(logMembers);

                // // members index
                for (int n = CsvPreference.MEMBER_MIN; n <= CsvPreference.MEMBER_MAX; n++) {
                    if (preference.hasValidMember(n) && preference.hasValidSource(n)) {
                        String name = preference.getMemberName(n, null);
                        sourceMembers[n] = name;
                        memberIndex[n] = logMembers.indexOf(name);
                        maxMemberSource = n;
                    } else
                        memberIndex[n] = -1;
                }
//                if (addRecPos)
//                    recPosIndex = logMembers.indexOf(Log.RECPOS);

                // tag pattern
                if (preference.tagSource != CsvPreference.SOURCE_NONE) {
                    if (!Utils.isEmpty(preference.fatalPattern))
                        try {
                            fatalPattern = Pattern.compile(preference.fatalPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid fatal pattern.");
                        }
                    if (!Utils.isEmpty(preference.errorPattern))
                        try {
                            errorPattern = Pattern.compile(preference.errorPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid error pattern.");
                        }
                    if (!Utils.isEmpty(preference.warningPattern))
                        try {
                            warningPattern = Pattern.compile(preference.warningPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid warning pattern.");
                        }
                    if (!Utils.isEmpty(preference.successPattern))
                        try {
                            successPattern = Pattern.compile(preference.successPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid success pattern.");
                        }
                    if (!Utils.isEmpty(preference.infoPattern))
                        try {
                            infoPattern = Pattern.compile(preference.infoPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid info pattern.");
                        }
                    if (!Utils.isEmpty(preference.debugPattern))
                        try {
                            debugPattern = Pattern.compile(preference.debugPattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid debug pattern.");
                        }
                    if (!Utils.isEmpty(preference.tracePattern))
                        try {
                            tracePattern = Pattern.compile(preference.tracePattern);
                        } catch (Throwable e) {
                            throw new ParseException(0, "Invalid trace pattern.");
                        }
                }

            }

            // domain mode
            this.domainSource = preference.domainSource;
            this.domainMode = preference.domainMode;
            this.domain2Source = preference.domain2Source;
            this.domain2Mode = preference.domain2Mode;

            // date scanner
            if (domainMode != CsvPreference.DOMAIN_UNDEFINED) {
                if (domainMode == CsvPreference.DOMAIN_DATE) {
                    try {
                        dateScanner = new SimpleDateFormat(preference.dateFormat, Locale.ENGLISH);
                    } catch (Throwable e) {
                        throw new ParseException(0, "Invalid date format. Can not create scanner.");
                    }
                } else {
                    domainValueUnit = DomainBases.parse(
                            domainBase instanceof DateBase ? TimeBase.DOMAIN_CLASS : domainBase.getClazz(),preference.domainUnit);
                }
            }
            if (domain2Mode != CsvPreference.DOMAIN_UNDEFINED) {
                domain2ValueUnit = DomainBases.parse(
                        domainBase instanceof DateBase ? TimeBase.DOMAIN_CLASS : domainBase.getClazz(),preference.domain2Unit);
            }

            // naming
            this.nameMode = preference.nameMode;
            this.name0 = preference.name0;
            this.name1Source = preference.name1Source;
            this.name2Mode = preference.name2Mode;
            this.name2Source = preference.name2Source;
            this.nameSeparator = preference.nameSeparator;
            this.namePrefix = preference.namePrefix;
            this.tagSource = preference.tagSource;

            changed(CHANGED_RECORD);

            // wait to open
            opened = false;

            // parse lines
            while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {

                // skip empty lines
                if (Utils.isEmpty(line)) {
                    lineNo++;
                    continue;
                }

                handleLine(line);
                lineNo++;
            }

        } catch (ParseException e) {
            e.snippet = line;
            e.position = lineNo;
            throw e;
        } catch (Throwable t) {
            ParseException e = new ParseException(-1, t);
            e.snippet = line;
            e.position = lineNo;
            throw e;
        } finally {

            // close
            close(current() + 1);
            try {
                if (closable != null)
                    closable.close();
            } catch (IOException e) {
            }
        }

    }

    private void handleLine(String line) throws ParseException {

        // values
        int count = split(line, splitted);
        if (count < maxSource)
            throw new ParseException(-1, "Invalid no of values");

        // position
        Long position = null;
        Long position2 = null;
        if (domainMode != CsvPreference.DOMAIN_UNDEFINED) {
            final String domainText = Utils.trim((domainSource > 0 && domainSource <= maxSource) ? splitted[domainSource] : null);
            position = parseDomain(domainText);
        }
        if (domain2Mode != CsvPreference.DOMAIN_UNDEFINED) {
            final String domainText = Utils.trim((domain2Source > 0 && domain2Source <= maxSource) ? splitted[domain2Source] : null);
            position2 = parseDomain2(domainText);
        }

        if (sepSignals != null || structSignal != null) {

            // relative domain modes
            if (domainMode == CsvPreference.DOMAIN_RECORD_INC || domainMode == CsvPreference.DOMAIN_SIGNAL_INC)
                position = !opened ? 0 : current + 1;

            // has position
            if (position != null) {

                // add extension
                if (position2 != null)
                    position += position2;

                // offset
                if (relativeDomainValue && !hasDomainOffset) {
                    domainOffset = position;
                    hasDomainOffset = true;
                }
                if (hasDomainOffset)
                    position -= domainOffset;

                // current
                if (current < position)
                    current = position;

                // open
                if (!opened) {
                    this.open(current);
                    opened = true;
                }

                // values
                if (sepSignals != null) {
                    for (int n = CsvPreference.SOURCE_MIN; n <= maxSource; n++) {
                        if (splitted.length >= n && splitted[n] != null && sepSignals[n] != null) {
                            String val = splitted[n];
                            switch (type[n]) {
                            case CsvPreference.SIGNAL_FLOAT:
                                val = val.replace(",", ".").replace(" ", "").trim();
                                ((IFloatSamplesWriter) getWriter(sepSignals[n])).write((long) position, false, Utils.parseDouble(val, 0));
                                break;
                            case CsvPreference.SIGNAL_INTEGER:
                                val = val.replace(".", "").replace(" ", "").trim();
                                ((IIntegerSamplesWriter) getWriter(sepSignals[n])).write((long) position, false, Utils.parseLong(val, 0));
                                break;
                            case CsvPreference.SIGNAL_TEXT:
                                val = val.trim();
                                ((ITextSamplesWriter) getWriter(sepSignals[n])).write((long) position, false, val);
                                break;
                            case CsvPreference.SIGNAL_ENUMERATION:
                                val = val.trim();
                                ((IEventSamplesWriter) getWriter(sepSignals[n])).write((long) position, false, val);
                                break;
                            // case CsvPreference.SIGNAL_DOMAIN:
                            //
                            }
                        }
                    }
                } else if (structMembers != null) {
                    for (int n = CsvPreference.SOURCE_MIN; n <= maxSource; n++) {
                        if (splitted.length >= n && splitted[n] != null && structMembers[n] != null) {
                            String val = splitted[n];
                            switch (type[n]) {
                            case CsvPreference.SIGNAL_FLOAT:
                                val = val.replace(",", ".").replace(" ", "").trim();
                                structMembers[n].setValue(Utils.parseDouble(val, 0));
                                structMembers[n].setValid(true);
                                break;
                            case CsvPreference.SIGNAL_INTEGER:
                                val = val.replace(".", "").replace(" ", "").trim();
                                structMembers[n].setValue(Utils.parseLong(val, 0));
                                structMembers[n].setValid(true);
                                break;
                            case CsvPreference.SIGNAL_TEXT:
                                val = val.trim();
                                structMembers[n].setValue(val);
                                structMembers[n].setValid(true);
                                break;
                            case CsvPreference.SIGNAL_ENUMERATION:
                                val = val.trim();
                                structMembers[n].setValue(val);
                                structMembers[n].setValid(true);
                                break;
                            default:
                                structMembers[n].setValid(false);
                            }
                        } else {
                            structMembers[n].setValid(false);
                        }
                    }
                    ((IStructSamplesWriter) getWriter(structSignal)).write((long) position, false, structMembers);
                }

                changed(CHANGED_SIGNALS);
            } else
                throw new ParseException(-1, "No domain information (e.g. time-stamp)");
        } else if (logWriters != null) {

            // name
            String name1 = null, name2 = null;
            if (nameMode != CsvPreference.NAME_UNDEFINED) {
                if (nameMode == CsvPreference.NAME_EXPLICIT)
                    name1 = name0;
                else {
                    name1 = Utils.trim((name1Source > 0 && name1Source <= maxSource) ? splitted[name1Source] : null);
                }
            }
            if (domain2Mode != CsvPreference.DOMAIN_UNDEFINED) {
                name2 = Utils.trim((name2Source > 0 && name2Source <= maxSource) ? splitted[name2Source] : null);
            }

            // writer
            LogWriter logWriter = getLogWriter(name1, name2);

            if (logWriter != null) {

                // relative domain modes
                if (domainMode == CsvPreference.DOMAIN_RECORD_INC)
                    position = !logWriter.isOpen() ? 0 : logWriter.getCurrent() + 1;
                else if (domainMode == CsvPreference.DOMAIN_SIGNAL_INC)
                    position = !opened ? 0 : current + 1;

                // has position
                if (position != null) {

                    // add extension
                    if (position2 != null)
                        position += position2;

                    // offset
                    if (relativeDomainValue && !hasDomainOffset) {
                        domainOffset = position;
                        hasDomainOffset = true;
                    }
                    if (hasDomainOffset)
                        position -= domainOffset;

                    // current

                    // open
                    if (!opened) {
                        current = position;
                        this.open(current);
                        opened = true;
                    } else if (current < position)
                        current = position;
                    if (!logWriter.isOpen()) {
                        logWriter.open(position);
                    }

                    // start message
                    logWriter.start(position);

                    // members
                    for (int n = CsvPreference.MEMBER_MIN; n <= maxMemberSource; n++) {
                        int widx = memberIndex[n];
                        if (widx >= 0 || n == tagSource) {
                            String text = Utils.trim(splitted[n]);
                            logWriter.setValue(widx, text);
                            if (n == tagSource) {
                                int tag = parseTags(text);
                                if (tag > 0 && (logWriter.getTag() == 0 || tag < logWriter.getTag())) {
                                    logWriter.setTag(tag);
                                }
                            }
                        }
                    }

                    // rec position
                    if (recPosIndex != -1) {
                        logWriter.setValue(recPosIndex, lineNo);
                    }

                    logWriter.finish();
                    changed(CHANGED_SIGNALS);

                }
            }
        }
    }

    protected Long parseDomain(String text) throws ParseException {

        // domain value
        Long position = null;

        // date
        if (domainMode == CsvPreference.DOMAIN_FLOAT) {
            try {
                if (!Utils.isEmpty(text)) {
                    String val = text.replace(",", ".").replace(" ", "").trim();
                    double d = 0;
                    if (domainValueUnit == null) {
                        // need to parse unit
                        int vl = val.length();
                        boolean foundUnitLetters = false;
                        while (vl > 0) {
                            if (!Character.isLetter(val.charAt(vl - 1)))
                                break;
                            vl--;
                            foundUnitLetters = true;
                        }
                        d = Double.parseDouble(val.substring(0, vl));
                        if (foundUnitLetters) {
                            IDomainBase domainValueUnit = DomainBases.parse( domainBase.getClazz(),val.substring(vl));
                            if (domainValueUnit == null)
                                throw new ParseException(0, "Invalid domain unit:" + val.substring(vl));
                            position = (long) (d * domainValueUnit.toCommonBase(1) / domainBase.toCommonBase(1));
                        } else
                            position = (long) d;
                    } else {
                        d = Double.parseDouble(val);
                        position = (long) (d * domainValueUnit.toCommonBase(1) / domainBase.toCommonBase(1));
                    }
                }

            } catch (NumberFormatException e) {
                throw new ParseException(0, "Invalid time format");
            }
        } else if (domainMode == CsvPreference.DOMAIN_INTEGER) {
            try {
                if (!Utils.isEmpty(text)) {
                    String val = text.replaceAll("[\\.,: ]", "").trim();
                    long l = 0;
                    if (domainValueUnit == null) {
                        // need to parse unit
                        int vl = val.length();
                        boolean foundUnitLetters = false;
                        while (vl > 0) {
                            if (!Character.isLetter(val.charAt(vl - 1)))
                                break;
                            vl--;
                            foundUnitLetters = true;
                        }
                        l = Long.parseLong(val.substring(0, vl).trim());
                        if (foundUnitLetters) {
                            IDomainBase domainValueUnit = DomainBases.parse(domainBase.getClazz(), val.substring(vl));
                            if (domainValueUnit == null)
                                throw new ParseException(0, "Invalid domain unit:" + val.substring(vl));
                            position = domainValueUnit.convertTo((domainBase), l);
                        } else
                            position = l;
                    } else {
                        l = Long.parseLong(val.trim());
                        if (domainBase instanceof DateBase)
                            position = domainValueUnit.convertTo(TimeBase.ms, l);
                        else
                            position = domainValueUnit.convertTo((domainBase), l);
                    }
                }

            } catch (NumberFormatException e) {
                throw new ParseException(0, "Invalid time format");
            }
        } else if (domainMode == CsvPreference.DOMAIN_DATE && dateScanner != null) {
            try {
                if (!Utils.isEmpty(text)) {
                    Date date = dateScanner.parse(text);
                    if (domainBase instanceof TimeBase)
                        position = TimeBase.ms.convertTo(domainBase, date.getTime());
                    else if (domainBase instanceof DateBase)
                        position = date.getTime();
                    else
                        throw new ParseException(0, "Invalid domain base for 'Date' mode. Use Time or Date.");
                }
            } catch (java.text.ParseException e) {
                throw new ParseException(0, "Invalid date format");
            }
        } else if (domainMode == CsvPreference.DOMAIN_RECORD_INC) {
            position = null;
        } else if (domainMode == CsvPreference.DOMAIN_SIGNAL_INC) {
            position = null;
        } else if (domainMode == CsvPreference.DOMAIN_RECEPTION) {
            if (domainBase instanceof TimeBase)
                position = TimeBase.ms.convertTo(domainBase, position = Utils.millies()).longValue();
            else if (domainBase instanceof DateBase)
                position = Utils.millies();
            else
                throw new ParseException(0, "Invalid domain base for 'Reception time' mode. Use Time or Date.");
        }

        return position;
    }

    protected Long parseDomain2(String text) throws ParseException {

        // domain value
        Long position = null;

        // date
        if (domain2Mode == CsvPreference.DOMAIN_FLOAT) {
            try {
                if (!Utils.isEmpty(text)) {
                    String val = text.replace(",", ".").replace(" ", "").trim();
                    double d = 0;
                    if (domain2ValueUnit == null) {
                        // need to parse unit
                        int vl = val.length();
                        boolean foundUnitLetters = false;
                        while (vl > 0) {
                            if (!Character.isLetter(val.charAt(vl - 1)))
                                break;
                            vl--;
                            foundUnitLetters = true;
                        }
                        d = Double.parseDouble(val.substring(0, vl));
                        if (foundUnitLetters) {
                            IDomainBase domainValueUnit = DomainBases.parse( domainBase.getClazz(),val.substring(vl));
                            if (domainValueUnit == null)
                                throw new ParseException(0, "Invalid domain unit:" + val.substring(vl));
                            position = (long) (d * domainValueUnit.toCommonBase(1) / domainBase.toCommonBase(1));
                        } else
                            position = (long) d;
                    } else {
                        d = Double.parseDouble(val);
                        position = (long) (d * domain2ValueUnit.toCommonBase(1) / domainBase.toCommonBase(1));
                    }
                }

            } catch (NumberFormatException e) {
                throw new ParseException(0, "Invalid time format");
            }
        } else if (domain2Mode == CsvPreference.DOMAIN_INTEGER) {
            try {
                if (!Utils.isEmpty(text)) {
                    String val = text.replaceAll("[\\.,: ]", "").trim();
                    long l = 0;
                    if (domain2ValueUnit == null) {
                        // need to parse unit
                        int vl = val.length();
                        boolean foundUnitLetters = false;
                        while (vl > 0) {
                            if (!Character.isLetter(val.charAt(vl - 1)))
                                break;
                            vl--;
                            foundUnitLetters = true;
                        }
                        l = Long.parseLong(val.substring(0, vl).trim());
                        if (foundUnitLetters) {
                            IDomainBase domainValueUnit = DomainBases.parse( domainBase.getClazz(),val.substring(vl));
                            if (domainValueUnit == null)
                                throw new ParseException(0, "Invalid domain unit:" + val.substring(vl));
                            position = domainValueUnit.convertTo((domainBase), l);
                        } else
                            position = l;
                    } else {
                        l = Long.parseLong(val.trim());
                        if (domainBase instanceof DateBase)
                            position = domain2ValueUnit.convertTo(TimeBase.ms, l);
                        else
                            position = domain2ValueUnit.convertTo((domainBase), l);
                    }
                }

            } catch (NumberFormatException e) {
                throw new ParseException(0, "Invalid time format");
            }
        }

        return position;
    }

    protected int parseTags(String text) {
        if (fatalPattern != null) {
            Matcher matcher = fatalPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_FATAL;
            }
        }
        if (errorPattern != null) {
            Matcher matcher = errorPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_ERROR;
            }
        }
        if (warningPattern != null) {
            Matcher matcher = warningPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_WARNING;
            }
        }
        if (successPattern != null) {
            Matcher matcher = successPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_SUCCESS;
            }
        }
        if (infoPattern != null) {
            Matcher matcher = infoPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_INFO;
            }
        }
        if (debugPattern != null) {
            Matcher matcher = debugPattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_DEBUG;
            }
        }
        if (tracePattern != null) {
            Matcher matcher = tracePattern.matcher(text);
            if (matcher.matches()) {
                return Log.TAG_TRACE;
            }
        }
        return Log.TAG_NONE;
    }

    public LogWriter getLogWriter(String name1, String name2) {

        // hash
        String id = (name1 != null) ? (String.valueOf(name1) + (name2 != null ? name2 : "")) : "log";
        LogWriter logWriter = logWriters.get(id);
        if (logWriter != null)
            return logWriter;

        // scope
        ICell scope = base;
        ICell child;

        // hierarchy
        String[] path = null;
        if ((nameMode == CsvPreference.NAME_SOURCE_HIERARCHY || nameMode == CsvPreference.NAME_EXPLICIT_HIERARCHY) && !Utils.isEmpty(name1)
                && !Utils.isEmpty(nameSeparator)) {
            path = name1.split(nameSeparator);
            for (int n = 0; n < path.length - 1; n++) {
                String p = path[n];
                child = scope.getChild(p, RecordScope.class);
                if (child == null)
                    child = addScope(scope, p);
                scope = child;
            }
        }

        // name
        String name = (nameMode != CsvPreference.NAME_UNDEFINED)
                ? ((path != null && path.length > 0) ? path[path.length - 1] : String.valueOf(name1))
                : "Log";
        if (!Utils.isEmpty(name2))
            name = name + " (" + name2 + ")";
        if (!Utils.isEmpty(namePrefix))
            name = namePrefix + name;

        // signal
        child = scope.getChild(name, RecordSignal.class);
        if (child == null)
            child = addSignal(scope, name, null, ISamples.TAG_LOG, ISample.DATA_TYPE_STRUCT, -1, ISamples.FORMAT_DEFAULT);

        // writer
        if (child instanceof RecordSignal) {
            IStructSamplesWriter writer = (IStructSamplesWriter) getWriter((RecordSignal) child);
//            writer.setTagDomain(TagDomain.Log);
            logWriter = new LogWriter(writer, logMembers, logTypes, logContents, logFormats, null);
            logWriters.put(id, logWriter);
        }
        changed(CHANGED_RECORD);

        return logWriter;
    }

    void initSplit(CsvPreference config) {
        this.del = new char[5];
        this.del[0] = config.sepOther && !Utils.isEmpty(config.delimeter) ? config.delimeter.charAt(0) : 0xffff;
        this.del[1] = config.sepComma ? ',' : 0xffff;
        this.del[2] = config.sepSemi ? ';' : 0xffff;
        this.del[3] = config.sepSpace ? ' ' : 0xffff;
        this.del[4] = config.sepTab ? '\t' : 0xffff;
        this.quote = config.quote == CsvPreference.QUOTE_SINGLE ? '\'' : (config.quote == CsvPreference.QUOTE_DOUBLE ? '"' : 0xffff);
        this.c1Quote = "" + this.quote;
        this.c2Quote = "" + this.quote + this.quote;
    }

    int split(String text, String[] splitted) {
        for (int n = 0; n < splitted.length; n++)
            splitted[n] = null;
        if (Utils.isEmpty(text))
            return 0;
        int pos = 0;
        int started = pos;
        int columns = 0;
        int quotes = 0;
        char[] array = text.toCharArray();
        while (pos < array.length && (columns + 1) < splitted.length) {
            char c = array[pos];
            if (quote != 0 && c == quote)
                quotes++;
            if ((quotes % 2 == 0) && (c == del[0] || c == del[1] || c == del[2] || c == del[3] || c == del[4])) {
                String fragment = new String(array, started, pos - started);
                if (quotes > 0) {
                    fragment = fragment.trim();
                    if (fragment.charAt(0) == quote && fragment.charAt(fragment.length() - 1) == quote) {
                        fragment = fragment.substring(1, fragment.length() - 1);
                    }
                    fragment = fragment.replace(c2Quote, c1Quote);
                }
                quotes = 0;
                splitted[++columns] = fragment;
                started = pos + 1;
            }
            pos++;
        }
        if (pos > started && (columns + 1) < splitted.length) {
            String fragment = new String(array, started, pos - started);
            if (quotes > 0) {
                fragment = fragment.trim();
                if (fragment.charAt(0) == quote && fragment.charAt(fragment.length() - 1) == quote) {
                    fragment = fragment.substring(1, fragment.length() - 1);
                    fragment = fragment.replace(c2Quote, c1Quote);
                }
            }
            splitted[++columns] = fragment;
            started = pos + 1;
        }
        return columns;
    }
}

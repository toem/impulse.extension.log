package de.toem.impulse.extension.log.csv;

import java.nio.charset.Charset;

import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.samples.ISample;
import de.toem.impulse.samples.ISamples;
import de.toem.impulse.samples.domain.DateBase;
import de.toem.impulse.samples.domain.DomainBases;
import de.toem.impulse.samples.domain.IDomainBase;
import de.toem.impulse.samples.domain.TimeBase;
import de.toem.impulse.usecase.logging.Log;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.CellAnnotation;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.IElementEvent;
import de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer;
import de.toem.toolkits.pattern.element.instancer.IInstancer;
import de.toem.toolkits.pattern.element.serializer.AbstractMultitonSerializerPreference;
import de.toem.toolkits.pattern.element.serializer.ICellSerializer;
import de.toem.toolkits.pattern.element.serializer.SingletonSerializerPreference.DefaultSerializerConfiguration;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.pattern.scan.TextScanResult;
import de.toem.toolkits.ui.controller.abstrac.IController;
import de.toem.toolkits.ui.controller.base.CheckController;
import de.toem.toolkits.ui.controller.base.ComboController;
import de.toem.toolkits.ui.controller.base.CompositeController;
import de.toem.toolkits.ui.controller.base.GroupController;
import de.toem.toolkits.ui.controller.base.RadioSetController;
import de.toem.toolkits.ui.controller.base.TextController;
import de.toem.toolkits.ui.part.ITlkPart;
import de.toem.toolkits.ui.proposal.ContentProposal;
import de.toem.toolkits.ui.proposal.ContentProposalExtension;
import de.toem.toolkits.ui.proposal.PatternContentProposal;
import de.toem.toolkits.ui.proposal.SimpleDateContentProposal;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;
import de.toem.toolkits.ui.tlk.TLK;

// ========================================================================================================================
// Preferences
// ========================================================================================================================

@CellAnnotation(annotation = CsvPreference.Annotation.class, dynamicChildren = { DefaultSerializerConfiguration.TYPE })
public class CsvPreference extends AbstractMultitonSerializerPreference {
    public static final String TYPE = Annotation.id;

    static class Annotation {
        public static final String id = CsvReader.Annotation.id;
        public static final String label = CsvReader.Annotation.label;
        public static final String description = CsvReader.Annotation.description;
        public static final String iconId = CsvReader.Annotation.iconId;
        public static final String helpURL = CsvReader.Annotation.helpURL;
        public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };

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
        return CsvReader.class;
    }

    @Override
    public String getCellType() {
        return CsvReader.Annotation.cellType;
    }

    // @Override
    // public boolean supports(SingletonSerializerPreference serializer) {
    // return "de.toem.impulse.serializer.csv".equals(serializer.id);
    // }

    // ========================================================================================================================
    // Members
    // ========================================================================================================================

    public String charSet;
    public String domainBase = TimeBase.us.toString();

    public int firstRow = 1;
    public int noOfColumns = 5;
    public boolean hasLabels = true;
    public boolean sepTab;
    public boolean sepSpace;
    public boolean sepComma;
    public boolean sepSemi = true;
    public boolean sepOther;
    public String delimeter = "@";
    public static final int QUOTE_NONE = 0;
    public static final int QUOTE_DOUBLE = 1;
    public static final int QUOTE_SINGLE = 2;
    public static final Object[] QUOTE_OPTIONS = { QUOTE_NONE, QUOTE_DOUBLE, QUOTE_SINGLE };
    public static final String[] QUOTE_LABELS = new String[] { "", "\"", "\'" };
    public int quote;

    // mode
    public static final int MODE_SEPERATE = 0;
    public static final int MODE_STRUCT = 1;
    public static final int MODE_LOG = 2;
    public int mode = MODE_SEPERATE;
    public static final String[] MODE_LABELS = new String[] { "Create individual signals per column",
            "Create one signal with individual column members", "Create single/multiple log signals" };

    // source
    public static final int SOURCE_NONE = 0;
    public static final int SOURCE_MIN = 1;
    public static final int SOURCE_MAX = 25;
    public static final String[] SOURCE_LABELS = new String[SOURCE_MAX + 1];
    static {
        SOURCE_LABELS[0] = I18n.General_None;
        for (int n = SOURCE_MIN; n <= SOURCE_MAX; n++)
            SOURCE_LABELS[n] = I18n.General_Column + " " + n + "/" + (char) ('A' + n - 1);
    }
    // signal/member names
    public static final int MEMBER_MIN = 1;
    public static final int MEMBER_MAX = 25;
    public String member1;
    public String member2;
    public String member3;
    public String member4;
    public String member5;
    public String member6;
    public String member7;
    public String member8;
    public String member9;
    public String member10;
    public String member11;
    public String member12;
    public String member13;
    public String member14;
    public String member15;
    public String member16;
    public String member17;
    public String member18;
    public String member19;
    public String member20;
    public String member21;
    public String member22;
    public String member23;
    public String member24;
    public String member25;

    // signal/member types
    public static final int SIGNAL_NONE = 0;
    public static final int SIGNAL_INTEGER = 1;
    public static final int SIGNAL_FLOAT = 2;
    public static final int SIGNAL_TEXT = 3;
    public static final int SIGNAL_ENUMERATION = 4;
    public static final Object[] SIGNAL_OPTIONS = { SIGNAL_NONE, SIGNAL_INTEGER, SIGNAL_FLOAT, SIGNAL_TEXT, SIGNAL_ENUMERATION };
    public static final String[] SIGNAL_LABELS = { I18n.General_None, I18n.Samples_SignalType_Integer, I18n.Samples_SignalType_Float, I18n.Samples_SignalType_Text,
            I18n.Samples_SignalType_Enumeration };

    public int s1 = SIGNAL_NONE; // column = 0
    public int s2 = SIGNAL_FLOAT;
    public int s3 = SIGNAL_FLOAT;
    public int s4 = SIGNAL_FLOAT;
    public int s5 = SIGNAL_FLOAT;
    public int s6 = SIGNAL_NONE;
    public int s7 = SIGNAL_NONE;
    public int s8 = SIGNAL_NONE;
    public int s9 = SIGNAL_NONE;
    public int s10 = SIGNAL_NONE;
    public int s11 = SIGNAL_NONE;
    public int s12 = SIGNAL_NONE;
    public int s13 = SIGNAL_NONE;
    public int s14 = SIGNAL_NONE;
    public int s15 = SIGNAL_NONE;
    public int s16 = SIGNAL_NONE;
    public int s17 = SIGNAL_NONE;
    public int s18 = SIGNAL_NONE;
    public int s19 = SIGNAL_NONE;
    public int s20 = SIGNAL_NONE;
    public int s21 = SIGNAL_NONE;
    public int s22 = SIGNAL_NONE;
    public int s23 = SIGNAL_NONE;
    public int s24 = SIGNAL_NONE;
    public int s25 = SIGNAL_NONE;
    public String d1;
    public String d2;
    public String d3;
    public String d4;
    public String d5;
    public String d6;
    public String d7;
    public String d8;
    public String d9;
    public String d10;
    public String d11;
    public String d12;
    public String d13;
    public String d14;
    public String d15;
    public String d16;
    public String d17;
    public String d18;
    public String d19;
    public String d20;
    public String d21;
    public String d22;
    public String d23;
    public String d24;
    public String d25;

    // domain
    public static final int DOMAIN_UNDEFINED = 0;
    public static final int DOMAIN_FLOAT = 1;
    public static final int DOMAIN_INTEGER = 2;
    public static final int DOMAIN_DATE = 3;
    public static final int DOMAIN_RECORD_INC = 4;
    public static final int DOMAIN_SIGNAL_INC = 5;
    public static final int DOMAIN_RECEPTION = 6;
    public static final String[] DOMAIN_LABELS = { I18n.Logging_DomainFloat, I18n.Logging_DomainInteger,
            I18n.Logging_DomainDate, I18n.Logging_DomainIncrementing, I18n.Logging_DomainIncrementingSignal,
            I18n.Logging_DomainReceptionTime };
    public static final Object[] DOMAIN_OPTIONS = { DOMAIN_FLOAT, DOMAIN_INTEGER, DOMAIN_DATE, DOMAIN_RECORD_INC, DOMAIN_SIGNAL_INC,
            DOMAIN_RECEPTION };
    public int domainMode = DOMAIN_FLOAT;
    public int domainSource = SOURCE_MIN;
    public String dateFormat = "yyyy-MM-dd HH:mm:ss,SSS";
    public String domainUnit;
    public static final String[] DOMAIN2_LABELS = { I18n.General_Undefined, I18n.Logging_DomainFloat, I18n.Logging_DomainInteger };
    public static final Object[] DOMAIN2_OPTIONS = { DOMAIN_UNDEFINED, DOMAIN_FLOAT, DOMAIN_INTEGER };
    public int domain2Mode = DOMAIN_UNDEFINED;
    public int domain2Source = SOURCE_NONE;
    public String domain2Unit;
    public boolean relativeDomainValue;

    // signal naming
    public static final int NAME_UNDEFINED = 0;
    public static final int NAME_SOURCE = 1;
    public static final int NAME_SOURCE_HIERARCHY = 2;
    public static final int NAME_EXPLICIT = 3;
    public static final int NAME_EXPLICIT_HIERARCHY = 4;
    public static final String[] NAME_LABELS = { I18n.General_Undefined, I18n.Logging_NameSource, I18n.Logging_NameHierarchySource,
            I18n.Logging_NameExplicit, I18n.Logging_NameHierarchyExplicit };
    public static final Object[] NAME_OPTIONS = { NAME_UNDEFINED, NAME_SOURCE, NAME_SOURCE_HIERARCHY, NAME_EXPLICIT, NAME_EXPLICIT_HIERARCHY };
    public static final String[] NAME2_LABELS = { I18n.General_Undefined, I18n.Logging_NameExtensionSource };
    public static final Object[] NAME2_OPTIONS = { NAME_UNDEFINED, NAME_SOURCE };
    public int nameMode = NAME_UNDEFINED;
    public String name0;
    public int name1Source = SOURCE_MIN;
    public int name2Mode = NAME_UNDEFINED;
    public int name2Source = SOURCE_NONE;
    public String nameSeparator = "\\.";
    public String namePrefix = null;

    // rec pos
    public boolean addRecPos = true;

    // tags
    public int tagSource = SOURCE_NONE;
    public String fatalPattern;
    public String errorPattern = "error|ERROR|Error";
    public String warningPattern = "warning|WARNING|Warning";
    public String successPattern;
    public String infoPattern;
    public String debugPattern;
    public String tracePattern;

    public boolean hasValidSource(int n) {
        return n >= SOURCE_MIN && n <= SOURCE_MAX && n <= noOfColumns;
    }

    public String getSourceIdentifier(int n) {
        return null;
    }

    public int getMaxSource() {
        return Math.max(Math.min(noOfColumns, SOURCE_MAX), 0);
    }

    public boolean hasValidMember(int n) {
        return getType(n) != SIGNAL_NONE && hasValidSource(n) && !Utils.isEmpty(getMemberName(n, null));
    }

    public String getMemberName(int n, String label) {
        String name = Utils.trim((String) this.getValue("member" + n, String.class));
        if (!Utils.isEmpty(name))
            return name;
        if (mode == MODE_LOG)
            return null;
        if (hasLabels && !Utils.isEmpty(label))
            return label;
        return "s" + n;
    }

    public int getType(int n) {
        return this.getValueAsInt("s" + n);
    }

    public int getSignalType(int n) {
        switch (this.getValueAsInt("s" + n)) {
        case SIGNAL_FLOAT:
            return ISample.DATA_TYPE_FLOAT;
        case SIGNAL_INTEGER:
            return ISample.DATA_TYPE_INTEGER;
        case SIGNAL_TEXT:
            return ISample.DATA_TYPE_TEXT;
        case SIGNAL_ENUMERATION:
            return ISample.DATA_TYPE_ENUM;
        // case SIGNAL_TIME:
        // return ISample.Float;
        }
        return ISample.DATA_TYPE_UNKNOWN;
    }

//    public SignalDescriptor getSignalDescriptor(int n) {
//        String d = (String) this.getValue("d" + n, String.class);
//        if (Utils.isEmpty(d))
//            return SignalDescriptor.DEFAULT;
//        if (!d.contains("<"))
//            d = d.trim() + "<>";
//        int signalType = getSignalType(n);
//        return SignalDescriptor.parseUser(signalType, d);
//    }

    public int getMemberType(int n) {
        switch (this.getValueAsInt("s" + n)) {
        case SIGNAL_FLOAT:
            return ISamples.DATA_TYPE_FLOAT;
        case SIGNAL_INTEGER:
            return ISamples.DATA_TYPE_INTEGER;
        case SIGNAL_TEXT:
            return ISamples.DATA_TYPE_TEXT;
        case SIGNAL_ENUMERATION:
            return ISamples.DATA_TYPE_ENUM;
        }
        return ISamples.DATA_TYPE_UNKNOWN;
    }

//    public String getMemberContent(int n) {
//        SignalDescriptor d = getSignalDescriptor(n);
//        return d.getContent();
//    }
//
//    public int getMemberFormat(int n) {
//        SignalDescriptor d = getSignalDescriptor(n);
//        return d.getFormat();
//    }

    public static String fieldNameForAttribute(String old) {
        if ("time".equals(old))
            return "domainType";
        else if ("timeUnit".equals(old))
            return "domainUnit";
        return null;
    }

    // ========================================================================================================================
    // Controls
    // ========================================================================================================================

    static public class Controls extends AbstractMultitonSerializerPreference.Controls {

        IController[] memberTextController = new IController[CsvPreference.SOURCE_MAX + 1];

        public Controls(Class<? extends ICell> clazz) {
            super(clazz);
        }

        class MemberTextController extends TextController {

            int source;

            @Override
            public boolean isAffected(IElementEvent event) {
                return true;
            }

            @Override
            public boolean needsUpdate(IElementEvent event) {
                return true;
            }

            public MemberTextController(ITlkPart editor, int source) {
                super(editor, field("member" + source));
                this.source = source;
            }

            @Override
            public boolean enabled(Object value) {
                return super.enabled(value) && this.getCellValueAsInt("noOfColumns") >= source;
            }

            // @Override
            // public void populate() {
            // addItems(Log.MEMBERS);
            // super.populate();
            // }

            @Override
            public void doUpdateControl(IElementEvent event) {

                // access(text -> text.setOptions(this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG ? Log.MEMBERS : null));
                if (this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG)
                    this.initEmptyMessage(null);
                else if (this.getCellValueAsBoolean("hasLabels"))
                    this.initEmptyMessage("{label}");
                else
                    this.initEmptyMessage("s" + source);
                super.doUpdateControl(event);
            }
        }

        class TypeController extends ComboController {

            int source;

            public TypeController(ITlkPart editor, int source) {
                super(editor, field("s" + source), CsvPreference.SIGNAL_LABELS, CsvPreference.SIGNAL_OPTIONS);
                this.source = source;
            }

            @Override
            public boolean enabled(Object value) {
                return super.enabled(value) && this.getCellValueAsInt("noOfColumns") >= source
                        && (this.getCellValueAsInt("mode") != CsvPreference.MODE_LOG || !Utils.isEmpty(this.getCellValueAsString("member" + source)));
            }

            @Override
            public boolean isAffected(IElementEvent event) {
                return true;
            }

            @Override
            public boolean needsUpdate(IElementEvent event) {
                return true;
            }

            @Override
            public void populate() {
                if (this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG) {
                    if (!checkControl())
                        return;
                    String m = this.getCellValueAsString("member" + source);
                    if (!Log.contains(m)) {
                        super.populate();
                        return;
                    }
                    addItem(CsvPreference.SIGNAL_LABELS[0], CsvPreference.SIGNAL_OPTIONS[0]);
                    switch (Log.memberType(m)) {
                    case ISample.DATA_TYPE_INTEGER:
                        addItem(CsvPreference.SIGNAL_LABELS[1], CsvPreference.SIGNAL_OPTIONS[1]);
                        break;
                    case ISample.DATA_TYPE_FLOAT:
                        addItem(CsvPreference.SIGNAL_LABELS[2], CsvPreference.SIGNAL_OPTIONS[2]);
                        break;
                    case ISample.DATA_TYPE_TEXT:
                        addItem(CsvPreference.SIGNAL_LABELS[3], CsvPreference.SIGNAL_OPTIONS[3]);
                        break;
                    case ISample.DATA_TYPE_ENUM:
                        addItem(CsvPreference.SIGNAL_LABELS[4], CsvPreference.SIGNAL_OPTIONS[4]);
                        break;
                    }
                } else
                    super.populate();
            }

            @Override
            public Object value() {
                if (this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG) {
                    String m = this.getCellValueAsString("member" + source);
                    if (Utils.isEmpty(m))
                        return CsvPreference.SIGNAL_NONE;
                    if (!Log.contains(m))
                        return super.value();
                    if (super.getValueAsInt() == CsvPreference.SIGNAL_NONE)
                        return super.value();
                    switch (Log.memberType(m)) {
                    case ISample.DATA_TYPE_INTEGER:
                        return CsvPreference.SIGNAL_INTEGER;
                    case ISample.DATA_TYPE_FLOAT:
                        return CsvPreference.SIGNAL_FLOAT;
                    case ISample.DATA_TYPE_TEXT:
                        return CsvPreference.SIGNAL_TEXT;
                    case ISample.DATA_TYPE_ENUM:
                        return CsvPreference.SIGNAL_ENUMERATION;
                    }
                    return null;
                } else
                    return super.value();
            }

        }

        class DescriptorController extends TextController {

            int source;
            IController type;

            public DescriptorController(ITlkPart editor, int source) {
                super(editor, field("d" + source));
                this.type = tlk().getController("s" + source);

                add(new ContentProposalExtension(true) {
                    @Override
                    public ContentProposal[] getProposals(String contents, int position) {
                        clear();
                        add("default<>", null, null);
                        switch (type.getValueAsInt()) {
                        case CsvPreference.SIGNAL_ENUMERATION:
                            add("default<df=Event>", null, null);
                            add("default<df=Text>", null, null);
                            add("label<df=Text>", null, null);
                            add("event<df=Text>", null, null);
                            add("state<df=Text>", null, null);
                            break;
                        case CsvPreference.SIGNAL_FLOAT:
                            add("default<df=Decimal>", null, null);
                            add("default<df=UserDec0>", null, null);
                            break;
                        case CsvPreference.SIGNAL_INTEGER:
                            add("default<df=Decimal>", null, null);
                            add("default<df=Hex>", null, null);
                            add("default<df=Bin>", null, null);
                            break;
                        case CsvPreference.SIGNAL_TEXT:
                            add("label<df=Text>", null, null);
                            add("event<df=Text>", null, null);
                            add("state<df=Text>", null, null);
                            break;
                        default:
                            break;
                        }
                        add("default<df=None>", null, null);
                        add("default<df=Index>", null, null);
                        add("default<df=\u0394Domain>", null, null);
                        add("default<df=\u0394Value>", null, null);
                        return super.getProposals(contents, position);
                    }
                });
            }

            @Override
            public boolean enabled(Object value) {
                return super.enabled(value) && (type.isEnabled() && type.getValueAsInt() != CsvPreference.SIGNAL_NONE)
                        && this.getCellValueAsInt("noOfColumns") >= source;
            }
        }

        class SourceSelectController extends ComboController {

            String[] labels;

            public SourceSelectController(ITlkPart editor, Object source) {
                super(editor, source);
            }

            @Override
            public boolean isAffected(IElementEvent event) {
                return true;
            }

            @Override
            public boolean needsUpdate(IElementEvent event) {
                return true;
            }

            @Override
            public void populate() {
                super.populate();
                addItem(CsvPreference.SOURCE_LABELS[0], CsvPreference.SOURCE_NONE);
                for (int n = CsvPreference.SOURCE_MIN; n <= CsvPreference.SOURCE_MAX && n <= this.getCellValueAsInt("noOfColumns"); n++)
                    // if (hasValidSource(this, n)) {
                    addItem(CsvPreference.SOURCE_LABELS[n], n);

                // if (Utils.isEmpty(this.getCellValueAsString("member" + n)))
                // addItem(sourceLabels[n], SOURCE_OPTIONS[n]);
                // else
                // addItem(this.getCellValueAsString("member" + n), SOURCE_OPTIONS[n]);
                // }
            }

            @Override
            public boolean enabled(Object value) {
                return super.enabled(value);
            }

        }

        protected void fillSection4() {

            // char set
            String[] charSets = Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().keySet().size()]);
            tlk().addCombo(container(), new ComboController(editor(), field("charSet"), charSets, charSets).initNullItem("Default"), cols(),
                    TLK.LABEL | TLK.READ_ONLY, "Char Set:");

            // domain base
            // Object domainClassBase = tlk().addComposite(container(), null, 2, cols(), TLK.LABEL, I18n.Samples_DomainBase, null);
            // addDomainClass(domainClassBase, tlk().ld(1, TLK.FILL, TLK.DEFAULT), TLK.NULL);
            // addDomainBase(domainClassBase, field("domainBase"), tlk().ld(1, TLK.FILL, 25), TLK.NULL);

            Object geometry = tlk().addGroup(container(), null, 5, tlk().ld(cols(), TLK.FILL, TLK.DEFAULT), TLK.NULL, "Geometry", null);

            tlk().addText(geometry, new TextController(editor(), field("firstRow")) {
                protected TextScanResult validate(String formatted, int options) {
                    int firstRow = Utils.parseInt(formatted, -1);
                    if (firstRow >= 0)
                        return TextScanResult.SCAN_OK;
                    return TextScanResult.SCAN_ERROR;
                }
            }, 1, TLK.LABEL | TLK.BORDER, "First row:");
            tlk().addText(geometry, new TextController(editor(), field("noOfColumns")) {
                protected TextScanResult validate(String formatted, int options) {
                    int noOfColumns = Utils.parseInt(formatted, -1);
                    if (noOfColumns >= CsvPreference.SOURCE_MIN && noOfColumns <= CsvPreference.SOURCE_MAX)
                        return TextScanResult.SCAN_OK;
                    return TextScanResult.SCAN_ERROR;
                }
            }, 1, TLK.LABEL | TLK.BORDER, "No of Columns:");
            tlk().addButton(geometry, new CheckController(editor(), field("hasLabels")), 1, TLK.CHECK, "First row has labels", null);

            tlk().addLabel(geometry, null, 1, TLK.NULL, "Separator:", null);
            tlk().addButton(geometry, new CheckController(editor(), field("sepTab")), 1, TLK.CHECK, "Tab", null);
            tlk().addButton(geometry, new CheckController(editor(), field("sepSpace")), 1, TLK.CHECK, "Space", null);
            tlk().addButton(geometry, new CheckController(editor(), field("sepComma")), 1, TLK.CHECK, "Comma", null);
            tlk().addButton(geometry, new CheckController(editor(), field("sepSemi")), 1, TLK.CHECK, "Semicolon", null);
            tlk().addLabel(geometry, null, 1, TLK.NULL, "", null);
            tlk().addButton(geometry, new CheckController(editor(), field("sepOther")), 1, TLK.CHECK, "Other:", null);

            tlk().addText(geometry, new TextController(editor(), field("delimeter")) {
                protected TextScanResult validate(String formatted, int options) {
                    if (formatted == null || formatted.length() == 0)
                        return TextScanResult.SCAN_ERROR;
                    if (formatted.length() > 1)
                        return TextScanResult.SCAN_ERROR;
                    return TextScanResult.SCAN_OK;
                }
            }, 1, TLK.BORDER, "Other:");
            tlk().addCombo(geometry, new ComboController(editor(), field("quote"), CsvPreference.QUOTE_LABELS, CsvPreference.QUOTE_OPTIONS) {

            }, 1, TLK.LABEL | TLK.BORDER | TLK.READ_ONLY, "Quotes:");

            tlk().addButtonSet(container(), new RadioSetController(editor(), field("mode")) {

                public boolean needsUpdate(IElementEvent event) {
                    return true;
                }

            }, 2, cols(), TLK.RADIO | TLK.LABEL, CsvPreference.MODE_LABELS, null, null, "Mode:");

            // signals/members
            Object membergroup = tlk().addGroup(container(), new GroupController(editor(), null) {

                @Override
                public boolean isAffected(IElementEvent event) {
                    return true;
                }

                public Object value() {
                    switch (this.getCellValueAsInt("mode")) {
                    case CsvPreference.MODE_SEPERATE:
                        return I18n.General_Signals;
                    case CsvPreference.MODE_STRUCT:
                        return I18n.Samples_Members;
                    case CsvPreference.MODE_LOG:
                        return I18n.Samples_Members;
                    }
                    return "";
                }

            }, cols(), tlk().ld(cols(), TLK.FILL, TLK.DEFAULT), TLK.NULL, null, null);

            for (int rowIndex = CsvPreference.MEMBER_MIN; rowIndex <= CsvPreference.MEMBER_MAX - 4; rowIndex += 5) {
                final int first = rowIndex;
                Object members = tlk().addComposite(membergroup, new CompositeController(editor(), null) {

                    @Override
                    public void doUpdateControl(IElementEvent event) {
                        super.doUpdateControl(event);
                        tlk().showControl(enabled, this);
                    }

                    @Override
                    public boolean enabled(Object value) {
                        return super.enabled(value) && this.getCellValueAsInt("noOfColumns") >= first;
                    }
                }, 6, cols(), TLK.NONE, null, null);

                tlk().addLabel(members, null, 1, TLK.NULL, "Column", null);
                for (int n = rowIndex; n < rowIndex + 5; n++)
                    tlk().addLabel(members, null, tlk().ld(1, TLK.GRAB, TLK.NO_HINT), TLK.NULL, "" + n + "/" + (char) ('A' + n - 1), null);
                tlk().addLabel(members, null, 1, TLK.NULL, "Name:", null);
                for (int n = rowIndex; n < rowIndex + 5; n++)
                    tlk().addText(members, memberTextController[n] = new MemberTextController(editor(), n), tlk().ld(1, TLK.FILL, TLK.NO_HINT),
                            TLK.NULL, null);
                tlk().addLabel(members, null, 1, TLK.NULL, "Type:", null);
                for (int n = rowIndex; n < rowIndex + 5; n++)
                    tlk().addCombo(members, new TypeController(editor(), n), tlk().ld(1, TLK.FILL, TLK.NO_HINT), TLK.READ_ONLY, null);
                tlk().addLabel(members, null, 1, TLK.NULL, "Descriptor:", null);
                for (int n = rowIndex; n < rowIndex + 5; n++)
                    tlk().addText(members, new DescriptorController(editor(), n), tlk().ld(1, TLK.FILL, TLK.NO_HINT), TLK.NONE, null);
            }

            Object parameters = tlk().addComposite(container(), null, 2, cols(), TLK.NULL, null, null);

            // ========================================================================================================================
            // signal name
            Object name = tlk().addGroup(parameters, null, cols(), tlk().ld(1, TLK.GRAB, TLK.DEFAULT, TLK.FILL, TLK.DEFAULT), TLK.NULL,
                    I18n.Logging_SignalScopeName, null);
            tlk().addCombo(name, new ComboController(editor(), field("nameMode"), CsvPreference.NAME_LABELS, CsvPreference.NAME_OPTIONS) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY, I18n.General_Mode);
            tlk().addCombo(name, new SourceSelectController(editor(), field("name1Source")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value)
                            && (this.getCellValueAsInt("nameMode") == CsvPreference.NAME_SOURCE
                                    || this.getCellValueAsInt("nameMode") == CsvPreference.NAME_SOURCE_HIERARCHY)
                            && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.READ_ONLY | TLK.LABEL, I18n.General_Source);
            tlk().addText(name, new TextController(editor(), field("name0")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && (((this.getCellValueAsInt("nameMode") == CsvPreference.NAME_EXPLICIT
                            || this.getCellValueAsInt("nameMode") == CsvPreference.NAME_EXPLICIT_HIERARCHY)
                            && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG)
                            || this.getCellValueAsInt("mode") == CsvPreference.MODE_STRUCT);
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    this.initEmptyMessage("s");
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.add(new PatternContentProposal()), cols(), TLK.LABEL | TLK.BORDER, I18n.General_Name);
            tlk().addText(name, new TextController(editor(), field("nameSeparator")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value)
                            && (this.getCellValueAsInt("nameMode") == CsvPreference.NAME_EXPLICIT_HIERARCHY
                                    || this.getCellValueAsInt("nameMode") == CsvPreference.NAME_SOURCE_HIERARCHY)
                            && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.add(new PatternContentProposal()), tlk().ld(cols() - 2, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.BORDER,
                    I18n.Logging_HierarchySepPrefix);
            tlk().addText(name, new TextController(editor(), field("namePrefix")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value)
                            && (this.getCellValueAsInt("nameMode") == CsvPreference.NAME_EXPLICIT_HIERARCHY
                                    || this.getCellValueAsInt("nameMode") == CsvPreference.NAME_SOURCE_HIERARCHY)
                            && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.add(new PatternContentProposal()), tlk().ld(1, TLK.GRAB, TLK.NO_HINT), TLK.BORDER, null);

            tlk().addCombo(name, new ComboController(editor(), field("name2Mode"), CsvPreference.NAME2_LABELS, CsvPreference.NAME2_OPTIONS) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY, I18n.Logging_ExtensionMode);

            tlk().addCombo(name, new SourceSelectController(editor(), field("name2Source")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("name2Mode") != CsvPreference.NAME_UNDEFINED
                            && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.READ_ONLY | TLK.LABEL, I18n.Logging_ExtensionSource);

            // ========================================================================================================================
            // domain
            Object domain = tlk().addGroup(parameters, null, cols(), tlk().ld(1, TLK.GRAB, TLK.DEFAULT, TLK.FILL, TLK.DEFAULT), TLK.NULL,
                    "Domain value (e.g. time-stamp)", null);

            tlk().addCombo(domain, new ComboController(editor(), field("domainMode"), CsvPreference.DOMAIN_LABELS, CsvPreference.DOMAIN_OPTIONS) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value);

                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY, I18n.General_Mode);

            tlk().addCombo(domain, new SourceSelectController(editor(), field("domainSource")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && (this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_FLOAT
                            || this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_INTEGER
                            || this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_DATE);
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.READ_ONLY | TLK.LABEL, I18n.General_Source);

            tlk().addText(domain, new TextController(editor(), field("dateFormat")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("domainSource") != CsvPreference.SOURCE_NONE
                            && this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_DATE;
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.add(new SimpleDateContentProposal()), tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.BORDER, I18n.General_DateFormat);

            tlk().addCombo(domain, new ComboController(editor(), field("domainUnit"), DomainBases.ALL_LABELS_ARRAY,
                    DomainBases.ALL_OPTIONS_ARRAY) {

                @Override
                protected boolean filterItem(String label, Object value) {
                    IDomainBase domain = DomainBases.parse((String) getCellValue("domainBase", String.class));
                    Class<?> domainClass = domain != null ? domain.getClass() : null;
                    if (domainClass == DateBase.class)
                        domainClass = TimeBase.class;
                    if (value == null)
                        return false;
                    IDomainBase unit = DomainBases.parse((String) value);

                    return unit == null || (domainClass != null && !unit.getClass().equals(domainClass));
                }

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("domainSource") != CsvPreference.SOURCE_NONE
                            && (this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_INTEGER
                                    || this.getCellValueAsInt("domainMode") == CsvPreference.DOMAIN_FLOAT);
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.initNullItem("Parse unit from value"), tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY, I18n.Samples_DomainUnit);

            tlk().addCombo(domain, new ComboController(editor(), field("domain2Mode"), CsvPreference.DOMAIN2_LABELS, CsvPreference.DOMAIN2_OPTIONS) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value);

                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY, I18n.Logging_ExtensionMode);

            tlk().addCombo(domain, new SourceSelectController(editor(), field("domain2Source")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && (this.getCellValueAsInt("domain2Mode") == CsvPreference.DOMAIN_FLOAT
                            || this.getCellValueAsInt("domain2Mode") == CsvPreference.DOMAIN_INTEGER
                            || this.getCellValueAsInt("domain2Mode") == CsvPreference.DOMAIN_DATE);
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }, tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.READ_ONLY | TLK.LABEL, I18n.Logging_ExtensionSource);

            tlk().addCombo(domain, new ComboController(editor(), field("domain2Unit"), DomainBases.ALL_LABELS_ARRAY,
                    DomainBases.ALL_OPTIONS_ARRAY) {

                @Override
                protected boolean filterItem(String label, Object value) {
                    IDomainBase domain = DomainBases.parse((String) getCellValue("domainBase", String.class));
                    Class<?> domainClass = domain != null ? domain.getClass() : null;
                    if (domainClass == DateBase.class)
                        domainClass = TimeBase.class;
                    if (value == null)
                        return false;
                    IDomainBase unit = DomainBases.parse((String) value);

                    return unit == null || (domainClass != null && !unit.getClass().equals(domainClass));
                }

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("domain2Source") != CsvPreference.SOURCE_NONE
                            && (this.getCellValueAsInt("domain2Mode") == CsvPreference.DOMAIN_INTEGER
                                    || this.getCellValueAsInt("domain2Mode") == CsvPreference.DOMAIN_FLOAT);
                }

                @Override
                public void doUpdateControl(IElementEvent event) {
                    super.doUpdateControl(event);
                    tlk().showControl(enabled, this);
                }
            }.initNullItem(I18n.Logging_ParseFromValue), tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT), TLK.LABEL | TLK.READ_ONLY,
                    I18n.Logging_ExtensionUnit);

            tlk().addButton(domain, new CheckController(editor(), field("relativeDomainValue")), cols(), TLK.CHECK,
                    "Use relative (to the first row) domain value", null);

            // ========================================================================================================================
            // tag
            Object tag = tlk().addGroup(parameters, null, 6, tlk().ld(2, TLK.FILL, TLK.DEFAULT, TLK.FILL, TLK.DEFAULT), TLK.NULL, I18n.General_Tag,
                    null);
            tlk().addCombo(tag, new SourceSelectController(editor(), field("tagSource")) {

                @Override
                public boolean enabled(Object value) {
                    return super.enabled(value) && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                }
            }, tlk().ld(3, true, false), TLK.READ_ONLY | TLK.LABEL, I18n.General_Source);

            for (String t : Log.TAGS)
                if (!Utils.isEmpty(t))
                    tlk().addText(tag, new TextController(editor(), field(t.toLowerCase() + I18n.General_Pattern)) {

                        @Override
                        public boolean enabled(Object value) {
                            return super.enabled(value) && this.getCellValueAsInt("tagSource") != CsvPreference.SOURCE_NONE
                                    && this.getCellValueAsInt("mode") == CsvPreference.MODE_LOG;
                        }

                        @Override
                        public void doUpdateControl(IElementEvent event) {
                            super.doUpdateControl(event);
                            tlk().showControl(enabled, this);
                        }
                    }.add(new PatternContentProposal()), 2, TLK.LABEL | TLK.BORDER, t + " pattern:");

        };
    }

    public static ITlkControlProvider getControls() {
        return new Controls( CsvPreference.class);
    }
}

package de.toem.impulse.extension.log.pattern;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.usecase.logging.AbstractLogOption;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.CellAnnotation;
import de.toem.toolkits.pattern.element.FieldAnnotation;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer;
import de.toem.toolkits.pattern.element.instancer.IInstancer;
import de.toem.toolkits.pattern.ide.Ide;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.ui.controller.base.ButtonController;
import de.toem.toolkits.ui.controller.base.RadioSetController;
import de.toem.toolkits.ui.controller.base.TextBoxController;
import de.toem.toolkits.ui.controller.base.TextController;
import de.toem.toolkits.ui.proposal.PatternContentProposal;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;
import de.toem.toolkits.ui.tlk.TLK;
import de.toem.toolkits.utils.text.MultilineText;

/**
 * Pattern log option for configuring regular expression patterns in log readers.
 *
 * This option defines a single pattern configuration that can be used by
 * {@link PatternLogReader} to parse log lines. Each option specifies a regular
 * expression pattern and how to extract and map captured groups to domain values,
 * names, tags, and member attributes.
 *
 * Key features:
 * - Configurable regular expression patterns with capture groups
 * - Support for different actions (ignore, start new message, add to message, terminate)
 * - Flexible source mapping for domain, name, and member extraction
 * - Built-in pattern testing with example text
 * - Dynamic source validation based on pattern complexity
 *
 * Implementation notes:
 * - This class extends {@link de.toem.impulse.usecase.logging.AbstractLogOption}
 *   and follows the project property-model conventions for configuration.
 * - Pattern compilation and group counting are performed dynamically
 * - The controls provide an integrated testing interface for pattern validation
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
@CellAnnotation(annotation = PatternLogOption.Annotation.class)
public class PatternLogOption extends AbstractLogOption {
    // The type identifier
    public static final String TYPE = Annotation.id;

    /**
     * Annotation class for PatternLogOption.
     */
    public static class Annotation {
        public static final String id = "reader.log.pattern.option";
        public static final String label = I18n.Log_PatternLogOption;
        public static final String iconId = I18n.Log_PatternLogOption_IconId;
        public static final String description = I18n.Log_PatternLogOption_Description;
        public static final String helpURL = I18n.Log_PatternLogOption_HelpURL;
        public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
    }

    /**
     * Instancer for PatternLogOption.
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
    // ========================================================================================================================
    // Members
    // ========================================================================================================================

    // text pattern
    // Example text for testing the pattern
    public String example;
    @FieldAnnotation(affects = { FieldAnnotation.ALL_FIELDS })
    // The regular expression pattern
    public String pattern = ".*";

    // action
    // Action labels for the pattern
    public static final String[] ACTION_LABELS = { I18n.General_Ignore, I18n.PatternLogConfiguration_ActionNew,
            I18n.PatternLogConfiguration_ActionAdd, I18n.PatternLogConfiguration_ActionTerminate };
    // Whether to add record position
    public boolean addRecPos = true;

    // source
    // Constant for no source
    public static final int SOURCE_NONE = 0;
    // Source labels
    public static final String[] SOURCE_LABELS = { I18n.General_None, I18n.PatternLogConfiguration_Source + " 1",
            I18n.PatternLogConfiguration_Source + " 2", I18n.PatternLogConfiguration_Source + " 3", I18n.PatternLogConfiguration_Source + " 4",
            I18n.PatternLogConfiguration_Source + " 5", I18n.PatternLogConfiguration_Source + " 6", I18n.PatternLogConfiguration_Source + " 7",
            I18n.PatternLogConfiguration_Source + " 8", I18n.PatternLogConfiguration_Source + " 9", I18n.PatternLogConfiguration_Source + " 10",
            I18n.PatternLogConfiguration_Source + " 11", I18n.PatternLogConfiguration_Source + " 12", I18n.PatternLogConfiguration_Source + " 13",
            I18n.PatternLogConfiguration_Source + " 14", I18n.PatternLogConfiguration_Source + " 15", I18n.PatternLogConfiguration_Source + " 16",
            I18n.PatternLogConfiguration_Source + " 17", I18n.PatternLogConfiguration_Source + " 18", I18n.PatternLogConfiguration_Source + " 19",
            I18n.PatternLogConfiguration_Source + " 20" };

    /**
     * Gets the source identifier for the given number.
     *
     * @param n the source number
     * @return the identifier
     */
    public String getSourceIdentifier(int n) {
        return null;
    }

    /**
     * Gets the maximum source number based on the pattern.
     *
     * @return the maximum source
     */
    public int getMaxSource() {
        if (pattern == null)
            return 0;
        int noOfGroups = 0;
        int idx = 0;
        while (true) {
            idx = pattern.indexOf('(', idx);
            if (idx >= 0)
                noOfGroups++;
            else
                break;
            idx++;
        }
        return Math.min(SOURCE_LABELS.length - 1, noOfGroups);
    }

    /**
     * Checks if the given source number is valid.
     *
     * @param n the source number
     * @return true if valid
     */
    public boolean hasValidSource(int n) {
        return n <= getMaxSource();
    }

    // ========================================================================================================================
    // Controls
    // ========================================================================================================================

    /**
     * Controls for PatternLogOption.
     */
    public static class Controls extends AbstractLogOption.Controls {

        /**
         * Constructor.
         *
         * @param clazz the class
         */
        public Controls(Class<? extends ICell> clazz) {
            super(clazz);
            this.sourceLabels = SOURCE_LABELS;

        }

        /**
         * Fills the match section.
         *
         * @throws NoSuchFieldException if field not found
         * @throws SecurityException if security issue
         */
        @Override
        protected void fillMatch() throws NoSuchFieldException, SecurityException {

            tlk().addTextBox(container(), new TextBoxController(editor(), field("example")),
                    tlk().ld(cols(), TLK.FILL, TLK.NO_HINT, TLK.FILL, 5), TLK.LABEL | TLK.BORDER | TLK.MULTI | TLK.V_SCROLL,
                    I18n.PatternConfigurationDialog_TestLines);

            tlk().addText(container(), new TextController(editor(), field("pattern")).add(new PatternContentProposal()),
                    tlk().ld(cols() - 1, TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.General_Pattern);
            tlk().addButton(container(), new ButtonController(editor(), null) {

                @Override
                public void execute(String id, Object data) {
                    String message = "";
                    try {
                        String lines = getCellValueAsString("example");
                        if (!Utils.isEmpty(lines)) {
                            lines = MultilineText.toAscii(lines);
                            BufferedReader reader = new BufferedReader(new StringReader(lines));
                            String line;
                            int lineNo = 0;
                            Pattern p = Pattern.compile(getCellValueAsString("pattern"));
                            while ((line = reader.readLine()) != null) {
                                message += "Line " + ++lineNo + " --------------------------------------------------------\n";
                                if (!Utils.isEmpty(line)) {
                                    Matcher m = p.matcher(line);
                                    if (m.matches()) {
                                        for (int index = 0; index < m.groupCount(); index++) {
                                            String group = (String) getCellValue("member" + (index + 1), String.class);
                                            if (Utils.isEmpty(group))
                                                group = I18n.PatternLogConfiguration_Source + " " + (index + 1);
                                            message += group + " = " + m.group(index + 1) + "\n";
                                        }
                                    } else
                                        message += "No match!\n";
                                }
                            }
                            reader.close();
                        }
                    } catch (Throwable e) {
                        message += "Exception --------------------------------------------------------\n";
                        message += e.getMessage();
                    }
                    Ide.showConsole(Ide.DEFAULT_CONSOLE);
                    Ide.defaultConsoleStream(Ide.DEFAULT_CONSOLE).println(message);

                }
            }, tlk().ld(1, TLK.RIGHT, TLK.DEFAULT, TLK.FILL, TLK.DEFAULT), TLK.NULL, I18n.General_Test, null);

        }

        /**
         * Fills the action section.
         *
         * @throws NoSuchFieldException if field not found
         * @throws SecurityException if security issue
         */
        @Override
        protected void fillAction() throws NoSuchFieldException, SecurityException {
            tlk().addButtonSet(container(), new RadioSetController(editor(), clazz().getField("action")) {

                @Override
                public void changed(boolean finalized) {
                    super.changed(finalized);
                    tlk().reflow();
                }
     
            }, 2, cols(), TLK.RADIO | TLK.LABEL,
                    PatternLogOption.ACTION_LABELS, null, null, I18n.General_Action);
        }
    }

    /**
     * Returns the controls provider.
     *
     * @return the controls
     */
    public static ITlkControlProvider getControls() {
        return new Controls(PatternLogOption.class);
    }
}

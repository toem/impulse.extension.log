package de.toem.impulse.extension.log.xml;

import de.toem.impulse.extension.log.i18n.I18n;
import de.toem.impulse.usecase.logging.AbstractLogOption;
import de.toem.toolkits.pattern.element.CellAnnotation;
import de.toem.toolkits.pattern.element.FieldAnnotation;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer;
import de.toem.toolkits.pattern.element.instancer.IInstancer;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;
import de.toem.toolkits.ui.controller.base.RadioSetController;
import de.toem.toolkits.ui.controller.base.TextController;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;
import de.toem.toolkits.ui.tlk.TLK;

/**
 * XML log option for configuring XML element parsing patterns.
 *
 * This option defines a single XML parsing configuration that can be used by
 * {@link XmlLogReader} to parse XML elements based on XPath-like path patterns
 * and attribute mappings. Each option specifies an XML element path pattern
 * and a comma-separated list of attribute names to extract as source values
 * for domain, name, and member data extraction.
 *
 * Key features:
 * - Path-based XML element selection using hierarchical path patterns
 * - Configurable attribute mapping from XML attributes to source values
 * - Support for element text content as source
 * - Dynamic source validation based on configured attribute list
 * - Integration with XML streaming parser for efficient processing
 *
 * Implementation notes:
 * - This class extends {@link de.toem.impulse.usecase.logging.AbstractLogOption}
 *   and follows the project property-model conventions for configuration.
 * - Path patterns support hierarchical navigation (e.g., "parent/child")
 * - Attribute values are comma-separated and indexed for source mapping
 * - Supports both element text content and XML attributes as sources
 * - Compatible with streaming XML parser token-based processing
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
@CellAnnotation(annotation = XmlLogOption.Annotation.class)
public class XmlLogOption extends AbstractLogOption {
    public static final String TYPE = Annotation.id;

    public static class Annotation {
        public static final String id = "reader.log.xml.option";
        public static final String label = I18n.Log_XmlLogOption;
        public static final String iconId = I18n.Log_XmlLogOption_IconId;
        public static final String description = I18n.Log_XmlLogOption_Description;
        public static final String helpURL = I18n.Log_XmlLogOption_HelpURL;
        public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
    }

    /**
     * Instancer for XmlLogOption.
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

    // xml pattern
    // XML element path pattern for selection
    public String path;
    @FieldAnnotation(affects = { FieldAnnotation.ALL_FIELDS })
    // Comma-separated list of attribute names to extract
    public String attributes;

    // source
    // Source index for element text content
    public static final int SOURCE_TEXT = 1;
    // First attribute source index
    public static final int SOURCE_ATTRIBUTE1 = 2;

    // action
    // Action labels for XML parsing
    public static final String[] ACTION_LABELS = { I18n.General_Ignore, I18n.XmlLogConfiguration_ActionNew, I18n.XmlLogConfiguration_ActionAdd,
            I18n.XmlLogConfiguration_ActionTerminate};
    // Whether to add record position
    public boolean addRecPos = true;

    // source
    // Constant for no source
    public static final int SOURCE_NONE = 0;
    // Source labels for attribute mapping
    public static final String[] SOURCE_LABELS = { I18n.General_None, I18n.XmlLogConfiguration_Source1, I18n.XmlLogConfiguration_Source2 + " 1",
            I18n.XmlLogConfiguration_Source2 + " 2", I18n.XmlLogConfiguration_Source2 + " 3", I18n.XmlLogConfiguration_Source2 + " 4",
            I18n.XmlLogConfiguration_Source2 + " 5", I18n.XmlLogConfiguration_Source2 + " 6", I18n.XmlLogConfiguration_Source2 + " 7",
            I18n.XmlLogConfiguration_Source2 + " 8", I18n.XmlLogConfiguration_Source2 + " 9", I18n.XmlLogConfiguration_Source2 + " 10", I18n.XmlLogConfiguration_Source2 + " 11",
            I18n.XmlLogConfiguration_Source2 + " 12", I18n.XmlLogConfiguration_Source2 + " 13", I18n.XmlLogConfiguration_Source2 + " 14",
            I18n.XmlLogConfiguration_Source2 + " 15", I18n.XmlLogConfiguration_Source2 + " 16", I18n.XmlLogConfiguration_Source2 + " 17",
            I18n.XmlLogConfiguration_Source2 + " 18", I18n.XmlLogConfiguration_Source2 + " 19"
};

    // ========================================================================================================================
    // Accessors
    // ========================================================================================================================

    /**
     * Checks if the given source number is valid based on the configured attributes.
     *
     * @param n the source number to check
     * @return true if the source number is valid
     */
    public boolean hasValidSource(int n) {
        if (n == SOURCE_TEXT)
            return true;
        String[] splitted = attributes != null ? attributes.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_ATTRIBUTE1) && n >= SOURCE_ATTRIBUTE1 ? true : false;
    }

    /**
     * Gets the source identifier for the given source number.
     *
     * @param n the source number
     * @return the source identifier, or null if invalid
     */
    public String getSourceIdentifier(int n) {
        if (n == SOURCE_TEXT)
            return null;
        String[] splitted = attributes != null ? attributes.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_ATTRIBUTE1) && n >= SOURCE_ATTRIBUTE1 ? splitted[n - SOURCE_ATTRIBUTE1] : null;
    }

    /**
     * Gets the maximum source number based on the configured attributes.
     *
     * @return the maximum source number
     */
    public int getMaxSource() {
        String[] splitted = attributes != null ? attributes.split(",") : null;
        return splitted != null ? splitted.length + SOURCE_ATTRIBUTE1 - 1 : 1;
    }

    /**
     * Gets the field name for a given attribute name.
     *
     * @param old the old attribute name
     * @return the field name, or null if not found
     */
    public static String fieldNameForAttribute(String old) {
        if ("element".equals(old))
            return "path";
        return null;
    }
    
    // ========================================================================================================================
    // Controls
    // ========================================================================================================================

    /**
     * Controls for XmlLogOption.
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
         * Fills the match section of the UI.
         *
         * @throws NoSuchFieldException if field not found
         * @throws SecurityException if security issue
         */
        @Override
        protected void fillMatch() throws NoSuchFieldException, SecurityException {

            tlk().addText(container(), new TextController(editor(), clazz().getField("path")).initEmptyMessage("e.g. log4j:event or log4j:event/log4j:message"),
                    tlk().ld(cols(), TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.General_Element);
            tlk().addText(container(), new TextController(editor(), clazz().getField("attributes")).initEmptyMessage("e.g. logger,timestamp,level"),
                    tlk().ld(cols() , TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.XmlConfigurationDialog_Attributes);
        }

        /**
         * Fills the action section of the UI.
         *
         * @throws NoSuchFieldException if field not found
         * @throws SecurityException if security issue
         */
        @Override
        protected void fillAction() throws NoSuchFieldException, SecurityException {
            tlk().addButtonSet(container(), new RadioSetController(editor(), clazz().getField("action")), 2, cols(), TLK.RADIO | TLK.LABEL, XmlLogOption.ACTION_LABELS,
                    null, null, I18n.General_Action);
        }

    }
    /**
     * Returns the controls provider.
     *
     * @return the controls
     */
    public static ITlkControlProvider getControls() {
        return new Controls(XmlLogOption.class);
    }
}

package de.toem.impulse.extension.log.json;

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
 * JSON log option for configuring JSON object parsing patterns.
 *
 * This option defines a single JSON parsing configuration that can be used by
 * {@link JsonLogReader} to parse JSON objects based on path patterns and field
 * mappings. Each option specifies a JSON path pattern and a comma-separated
 * list of field names to extract as source values for domain, name, and member
 * data extraction.
 *
 * Key features:
 * - Path-based JSON object selection using hierarchical path patterns
 * - Configurable field mapping from JSON properties to source values
 * - Support for wildcard patterns (*) for flexible object matching
 * - Dynamic source validation based on configured field list
 * - Integration with JSON streaming parser for efficient processing
 *
 * Implementation notes:
 * - This class extends {@link de.toem.impulse.usecase.logging.AbstractLogOption}
 *   and follows the project property-model conventions for configuration.
 * - Path patterns support hierarchical navigation (e.g., "parent/child")
 * - Field values are comma-separated and indexed for source mapping
 * - Supports various JSON data types through string-based extraction
 * - Compatible with Jackson JSON parser token-based processing
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
@CellAnnotation(annotation = JsonLogOption.Annotation.class)
public class JsonLogOption extends AbstractLogOption {
    public static final String TYPE = Annotation.id;

    public static class Annotation {
        public static final String id = "reader.log.json.option";
        public static final String label = I18n.Log_JsonLogOption;
        public static final String iconId = I18n.Log_JsonLogOption_IconId;
        public static final String description = I18n.Log_JsonLogOption_Description;
        public static final String helpURL = I18n.Log_JsonLogOption_HelpURL;
        public static final Class<? extends IInstancer>[] instancer = new Class[] { Instancer.class };
    }

    /**
     * Instancer for JsonLogOption.
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


    // Json pattern
    // JSON path pattern for object selection
    public String path;
    @FieldAnnotation(affects = { FieldAnnotation.ALL_FIELDS })
    // Comma-separated list of field names to extract
    public String values;

    // source
    // First source value index
    public static final int SOURCE_VALUE1 = 1;

    // action
    // Action labels for JSON parsing
    public static final String[] ACTION_LABELS = { I18n.General_Ignore, I18n.JsonLogConfiguration_ActionNew, I18n.JsonLogConfiguration_ActionAdd,
            I18n.JsonLogConfiguration_ActionTerminate};
    // Whether to add record position
    public boolean addRecPos = true;

    // source
    // Constant for no source
    public static final int SOURCE_NONE = 0;
    // Source labels for field mapping
    public static final String[] SOURCE_LABELS = { I18n.General_None, I18n.JsonLogConfiguration_Source + " 1",
            I18n.JsonLogConfiguration_Source + " 2", I18n.JsonLogConfiguration_Source + " 3", I18n.JsonLogConfiguration_Source + " 4",
            I18n.JsonLogConfiguration_Source + " 5", I18n.JsonLogConfiguration_Source + " 6", I18n.JsonLogConfiguration_Source + " 7",
            I18n.JsonLogConfiguration_Source + " 8", I18n.JsonLogConfiguration_Source + " 9", I18n.JsonLogConfiguration_Source + " 10", I18n.JsonLogConfiguration_Source + " 11",
            I18n.JsonLogConfiguration_Source + " 12", I18n.JsonLogConfiguration_Source + " 13", I18n.JsonLogConfiguration_Source + " 14",
            I18n.JsonLogConfiguration_Source + " 15", I18n.JsonLogConfiguration_Source + " 16", I18n.JsonLogConfiguration_Source + " 17",
            I18n.JsonLogConfiguration_Source + " 18", I18n.JsonLogConfiguration_Source + " 19", I18n.JsonLogConfiguration_Source + " 20"  };

    // ========================================================================================================================
    // Accessors
    // ========================================================================================================================

    /**
     * Checks if the given source number is valid based on the configured values.
     *
     * @param n the source number to check
     * @return true if the source number is valid
     */
    public boolean hasValidSource(int n) {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_VALUE1) && n >= SOURCE_VALUE1 ? true : false;
    }

    /**
     * Gets the source identifier for the given source number.
     *
     * @param n the source number
     * @return the source identifier, or null if invalid
     */
    public String getSourceIdentifier(int n) {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_VALUE1) && n >= SOURCE_VALUE1 ? splitted[n - SOURCE_VALUE1] : null;
    }

    /**
     * Gets the maximum source number based on the configured values.
     *
     * @return the maximum source number
     */
    public int getMaxSource() {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null ? splitted.length : 0;
    }

    // ========================================================================================================================
    // Controls
    // ========================================================================================================================

    /**
     * Controls for JsonLogOption.
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

            tlk().addText(container(), new TextController(editor(), clazz().getField("path")).initEmptyMessage("e.g. name or parent/name"),
                    tlk().ld(cols() , TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.General_Object);
            tlk().addText(container(), new TextController(editor(), clazz().getField("values")).initEmptyMessage("e.g. logger,timestamp,level"),
                    tlk().ld(cols() , TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.JsonConfigurationDialog_Values);
        }

        /**
         * Fills the action section of the UI.
         *
         * @throws NoSuchFieldException if field not found
         * @throws SecurityException if security issue
         */
        @Override
        protected void fillAction() throws NoSuchFieldException, SecurityException {
            tlk().addButtonSet(container(), new RadioSetController(editor(), clazz().getField("action")), 2, cols(), TLK.RADIO | TLK.LABEL, JsonLogOption.ACTION_LABELS,
                    null, null, I18n.General_Action);
        }
    }
    
    /**
     * Returns the controls provider.
     *
     * @return the controls
     */
    public static ITlkControlProvider getControls() {
        return new Controls(JsonLogOption.class);
    }
}

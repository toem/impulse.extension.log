package de.toem.impulse.extension.log.yaml;

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
import de.toem.toolkits.ui.part.ITlkPart;
import de.toem.toolkits.ui.proposal.ContentProposal;
import de.toem.toolkits.ui.proposal.ContentProposalExtension;
import de.toem.toolkits.ui.tlk.ITlkControlProvider;
import de.toem.toolkits.ui.tlk.TLK;

@CellAnnotation(annotation = YamlLogOption.Annotation.class)
public class YamlLogOption extends AbstractLogOption {
    public static final String TYPE = Annotation.id;

    public static class Annotation {
        public static final String id = "reader.log.yaml.option";
        public static final String label = I18n.Log_YamlLogOption;
        public static final String iconId = I18n.Log_YamlLogOption_IconId;
        public static final String description = I18n.Log_YamlLogOption_Description;
        public static final String helpURL = I18n.Log_YamlLogOption_HelpURL;
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
    
    // ========================================================================================================================
    // Members
    // ========================================================================================================================
    
    // Yaml pattern
    public String path;
    @FieldAnnotation(affects = { FieldAnnotation.ALL_FIELDS })
    public String values;

    // source
    public static final int SOURCE_VALUE1 = 1;

    // action
    public static final String[] ACTION_LABELS = { I18n.General_Ignore, I18n.YamlLogConfiguration_ActionNew, I18n.YamlLogConfiguration_ActionAdd,
            I18n.YamlLogConfiguration_ActionTerminate};
    public boolean addRecPos = true;

    // source
    public static final int SOURCE_NONE = 0;
    public static final String[] SOURCE_LABELS = { I18n.General_None, I18n.YamlLogConfiguration_Source + " 1",
            I18n.YamlLogConfiguration_Source + " 2", I18n.YamlLogConfiguration_Source + " 3", I18n.YamlLogConfiguration_Source + " 4",
            I18n.YamlLogConfiguration_Source + " 5", I18n.YamlLogConfiguration_Source + " 6", I18n.YamlLogConfiguration_Source + " 7",
            I18n.YamlLogConfiguration_Source + " 8", I18n.YamlLogConfiguration_Source + " 9", I18n.YamlLogConfiguration_Source + " 10", I18n.YamlLogConfiguration_Source + " 11",
            I18n.YamlLogConfiguration_Source + " 12", I18n.YamlLogConfiguration_Source + " 13", I18n.YamlLogConfiguration_Source + " 14",
            I18n.YamlLogConfiguration_Source + " 15", I18n.YamlLogConfiguration_Source + " 16", I18n.YamlLogConfiguration_Source + " 17",
            I18n.YamlLogConfiguration_Source + " 18", I18n.YamlLogConfiguration_Source + " 19", I18n.YamlLogConfiguration_Source + " 20"  };

    // ========================================================================================================================
    // Accessors
    // ========================================================================================================================

    public boolean hasValidSource(int n) {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_VALUE1) && n >= SOURCE_VALUE1 ? true : false;
    }

    public String getSourceIdentifier(int n) {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null && splitted.length > (n - SOURCE_VALUE1) && n >= SOURCE_VALUE1 ? splitted[n - SOURCE_VALUE1] : null;
    }

    public int getMaxSource() {
        String[] splitted = values != null ? values.split(",") : null;
        return splitted != null ? splitted.length : 0;
    }

    // ========================================================================================================================
    // Controls
    // ========================================================================================================================

    public static class Controls extends AbstractLogOption.Controls {

        public Controls(Class<? extends ICell> clazz) {
            super(clazz);
            this.sourceLabels = SOURCE_LABELS;

        }

       
        @Override
        protected void fillMatch() throws NoSuchFieldException, SecurityException {

            tlk().addText(container(), new TextController(editor(), clazz().getField("path")).initEmptyMessage("e.g. name or parent/name"),
                    tlk().ld(cols(), TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.General_Object);
            tlk().addText(container(), new TextController(editor(), clazz().getField("values")).initEmptyMessage("e.g. logger,timestamp,level"),
                    tlk().ld(cols(), TLK.GRAB, TLK.NO_HINT, TLK.FILL, TLK.DEFAULT), TLK.LABEL | TLK.BORDER, I18n.YamlConfigurationDialog_Values);
        }

        @Override
        protected void fillAction() throws NoSuchFieldException, SecurityException {
            tlk().addButtonSet(container(), new RadioSetController(editor(), clazz().getField("action")), 2, cols(), TLK.RADIO | TLK.LABEL, YamlLogOption.ACTION_LABELS,
                    null, null, I18n.General_Action);
        }

    }
    public static ITlkControlProvider getControls() {
        return new Controls(YamlLogOption.class);
    }
}

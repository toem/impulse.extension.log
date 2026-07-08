package de.toem.impulse.extension.log.tools;

import java.util.List;

import de.toem.impulse.ImpulsePreferences;
import de.toem.toolkits.pattern.json.Json;
import de.toem.toolkits.pattern.lmtools.ILmTools.ILmTool;
import de.toem.toolkits.pattern.registry.AbstractRegistryObject;
import de.toem.toolkits.pattern.registry.RegistryAnnotation;

@RegistryAnnotation(annotation = CreateLogReader.Annotation.class)
public class CreateLogReader extends AbstractRegistryObject implements ILmTool {

    public static final String FORMAT = Annotation.id;

    public static class Annotation {
        public static final String id = "lmtool.tool.createLogReader";
        public static final String label = "createLogReader";
        public static final String iconId = null;
        public static final String description = "Create a Log Reader";
        public static final String helpURL = null;
    }

    @Override
    public boolean isPrompt() {
        return true;
    }

    @Override
    public Object getInputSchema() {
        return Json.array();
    }

    @Override
    public List<String> getCompletionSet(String argument, String typing) {
        return null;
    }

    @Override
    public Object execute(Object params) throws Exception {

        // Generate the prompt message based on the language
        return "Create a log reader for impulse. \n"
                + "1. Check if the user has attached any log source information or file, if not, ask the user. \\n"
                + "2. From the source, detect if the user needs an pattern, csv, xml, yaml, json or optionally a csv log reader. If unclear , ask the user.\\n"
                + "3. Use the impulse doc tools (getDoc) to CAREFULY to read the reader documention with its examples for the selected type of reader:\\n"
                + "   uri='help:///impulse-extension/log-reader/json-log-reader.md','help:///impulse-extension/log-reader/pattern-log-reader.md','help:///impulse-extension/log-reader/xml-log-reader.md','help:///impulse-extension/log-reader/yaml-log-reader.md' or 'help:///impulse-reference/52_csv-reader.md' \n"
                + "4. Create an configuration (xml format) for the reader based on the given source information, ask the user if something unclear. Show it in the chat as copyable source.\\n"                
                + "5. Use the impulse insertData tool to insert the xml based configuration into impulse serializer preferences at uri '"+ImpulsePreferences.serializerPreferences.getUri()+"'n"
                + "6. To be able to insert, the user need to have an impulse part (typically a viewer or preference editor) open as active part."
                + "7. The reader will be opened as dialog, the user can revert if not wanted. To use, the user need to select the reader and reload."
                + "8. If any of the steps are not successful, report, ask the user, dont try around.";
                
 
    }
}

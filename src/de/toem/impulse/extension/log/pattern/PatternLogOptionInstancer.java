package de.toem.impulse.extension.log.pattern;

import java.util.regex.Pattern;

import de.toem.impulse.samples.domain.TimeBase;
import de.toem.impulse.usecase.logging.Log;
import de.toem.toolkits.core.Utils;
import de.toem.toolkits.pattern.element.Elements;
import de.toem.toolkits.pattern.element.ICell;
import de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer;
import de.toem.toolkits.pattern.element.instancer.BaseInstancerInformation;
import de.toem.toolkits.pattern.ide.Ide;
import de.toem.toolkits.pattern.registry.IRegistry;

/**
 * Instancer for creating and configuring PatternLogOption instances.
 *
 * This instancer provides specialized functionality for creating pattern log options,
 * including automatic conversion from Log4J 1.2 pattern layouts to regular expressions.
 * It handles the complex task of parsing Log4J format strings and mapping them to
 * the structured configuration of PatternLogOption.
 *
 * Key features:
 * - Automatic Log4J pattern conversion to regex patterns
 * - Dynamic member mapping based on Log4J conversion specifiers
 * - Support for date format conversion and timestamp handling
 * - Proper escaping of meta-characters in patterns
 *
 * Implementation notes:
 * - Extends {@link de.toem.toolkits.pattern.element.instancer.AbstractDefaultInstancer}
 *   to provide custom instantiation logic
 * - Uses regex pattern compilation for date format conversion
 * - Handles various Log4J conversion characters (%c, %d, %m, %p, etc.)
 * - Maintains compatibility with Log4J 1.2 pattern layout specifications
 *
 * Copyright (c) 2013-2025 Thomas Haber
 * All rights reserved.
 *
 */
public class PatternLogOptionInstancer extends AbstractDefaultInstancer {

    /**
     * Returns the cell type for PatternLogOption.
     *
     * @return the cell type string
     */
    @Override
    public String getCellType() {
        return PatternLogOption.TYPE;
    }

//    @Override
//    public void init(IRegistry registry) {
//        super.init(registry);
//        informations.add(new BaseInstancerInformation(this, getGroups(), "forLog4j", getLabel() + " for Log4J 1.2", getIconId(), getDescription(),
//                getHelpURL()));
//    }
//
//    @Override
//    public ICell createOne(String id, ICell container) {
//        if ("forLog4j".equals(id)) {
//            ICell cell = Elements.cells.create(getCellType());
//            initOne(id, cell, null);
//            if (cell instanceof PatternLogOption) {
//                Ide.openInput(getLabel(), "Enter Log4J 1.2 pattern", "%r [%t] %p %c %x - %m%n", null, (result, value) -> {
//                    if (value != null) {
//                        readLog4jPattern(value, (PatternLogOption) cell);
//                    }
//                });
//
//            }
//            return cell;
//        } else
//            return super.createOne(id, container);
//    }

    // Default regex group pattern
    private static final String DEFAULT_GROUP = "(.*?)";
    // Greedy regex group pattern
    private static final String GREEDY_GROUP = "(.*)";
    // No-space regex group pattern
    private static final String NOSPACE_GROUP = "(\\s*?\\S*?\\s*?)";
    // Valid date format characters
    private static final String VALID_DATEFORMAT_CHARS = "GyYMwWDdFEuaHkKhmsSzZX";
    // Pattern for valid date format characters
    private static final String VALID_DATEFORMAT_CHAR_PATTERN = "[" + VALID_DATEFORMAT_CHARS + "]";
    // Integer regex group pattern
    private static final String INTEGER_GROUP = "([0-9\\-\\+]*?)";

    /**
     * Converts a Log4J 1.2 pattern layout string to a PatternLogOption configuration.
     *
     * This method parses Log4J conversion specifiers (like %c, %d, %m, %p) and
     * automatically configures the corresponding PatternLogOption fields including
     * regex pattern generation, member mapping, and domain configuration.
     *
     * @param pattern the Log4J pattern string to convert
     * @param option the PatternLogOption instance to configure
     */
    public void readLog4jPattern(String pattern, PatternLogOption option) {
        int pos = 0;
        int start = 0;
        int end = pattern.length();
        StringBuilder regular = new StringBuilder();

        int nextMember = PatternLogOption.SOURCE_MIN;

        // default fields
        option.description = "Generated from " + pattern;
        option.action = PatternLogOption.ACTION_START;

        // search for %
        while ((pos = pattern.indexOf('%', start)) != -1) {

            // append prev chars
            regular.append(replaceMetaChars(pattern.substring(start, pos)));

            // is %% ?
            pos = pos + 1;
            start = pos;
            if (start < end && pattern.charAt(start) == '%') {
                start = pos + 2;
                regular.append('%');
                continue;
            }

            // skip modifiers
            while (pos < end && !Character.isAlphabetic(pattern.charAt(pos)) && !Character.isWhitespace(pattern.charAt(pos)))
                pos++;

            // parse key
            start = pos;
            while (pos < end && Character.isAlphabetic(pattern.charAt(pos)))
                pos++;
            String key = pattern.substring(start, pos);

            // skip {...}
            String details = "";
            if (pos < end && pattern.charAt(pos) == '{') {
                int level = 0;
                while (pos < end) {
                    int c = pattern.charAt(pos++);
                    if (c == '{') {
                        if (level == 0)
                            start = pos;
                        level++;
                    } else if (c == '}')
                        level--;
                    if (level == 0) {
                        details = pattern.substring(start, pos - 1);
                        break;
                    }
                }
            }
            //Utils.log(key, details);
            switch (key) {
            case "c":
            case "logger":
                option.nameMode = PatternLogOption.NAME_SOURCE;
                option.name1Source = nextMember;
                nextMember = addMember(regular, option, nextMember, NOSPACE_GROUP, Log.LOGGER, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "C":
            case "class":
                nextMember = addMember(regular, option, nextMember, NOSPACE_GROUP, Log.CLASS, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "F":
            case "file":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, Log.FILE, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "l":
            case "location":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, "Location", PatternLogOption.SIGNAL_TEXT, null);
                break;
            case "L":
            case "line":
                nextMember = addMember(regular, option, nextMember, INTEGER_GROUP, Log.LINE, PatternLogOption.SIGNAL_INTEGER, null);
                break;
            case "m":
            case "msg":
            case "message":
                nextMember = addMember(regular, option, nextMember, GREEDY_GROUP, Log.MESSAGE, PatternLogOption.SIGNAL_TEXT, null);
                break;
            case "M":
            case "method":
                nextMember = addMember(regular, option, nextMember, NOSPACE_GROUP, Log.METHOD, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "p":
            case "level":
                option.tagSource = nextMember;
                option.fatalPattern = "FATAL";
                option.errorPattern = "ERROR";
                option.warningPattern = "WARN";
                nextMember = addMember(regular, option, nextMember, NOSPACE_GROUP, Log.LEVEL, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "sn":
            case "sequenceNumber":
                nextMember = addMember(regular, option, nextMember, INTEGER_GROUP, "Sequence", PatternLogOption.SIGNAL_INTEGER, null);
                break;
            case "t":
            case "tn":
            case "thread":
            case "threadName":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, Log.THREAD, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "T":
            case "tid":
            case "threadId":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, "ThreadId", PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "tp":
            case "threadPriority":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, "ThreadPrio", PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "x":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, Log.NDC, PatternLogOption.SIGNAL_ENUMERATION, null);
                break;
            case "X":
                nextMember = addMember(regular, option, nextMember, DEFAULT_GROUP, details, PatternLogOption.SIGNAL_TEXT, null);
                break;
            case "d":
            case "date":
                String dateFormat = getDateFormat(details);
                regular.append(convertDateFormat(dateFormat));
                if (nextMember < PatternLogOption.MEMBER_MAX) {
                    option.setValue("member" + nextMember, Log.TIMESTAMP);
                    option.setValue("s" + nextMember, PatternLogOption.SIGNAL_TEXT);
                    option.domainMode = PatternLogOption.DOMAIN_DATE;
                    option.domainSource = nextMember;
                    option.dateFormat = dateFormat;
                    nextMember++;
                }
                break;
            case "r":
            case "relative":
                option.domainMode = PatternLogOption.DOMAIN_INTEGER;
                option.domainSource = nextMember;
                option.domainUnit = TimeBase.ms.toString();
                nextMember = addMember(regular, option, nextMember, INTEGER_GROUP, Log.TIMESTAMP, PatternLogOption.SIGNAL_NONE, null);
                break;
            case "n":
                break;
            default:
                Ide.openError("Log4J", "Unknown key: %" + key);

            }
            start = pos;

        }
        regular.append(replaceMetaChars(pattern.substring(start)));

        // set regular
        option.pattern = regular.toString();

        // default domain to inc
        if (option.domainMode == PatternLogOption.DOMAIN_UNDEFINED) {
            option.domainMode = PatternLogOption.DOMAIN_RECORD_INC;
        }
    }

    /**
     * Adds a member configuration to the pattern and option.
     *
     * @param regular the StringBuilder for the regex pattern
     * @param option the PatternLogOption to configure
     * @param nextMember the next available member index
     * @param group the regex group pattern to append
     * @param name the member name
     * @param type the signal type
     * @param descriptor the member descriptor
     * @return the updated next member index
     */
    private int addMember(StringBuilder regular, PatternLogOption option, int nextMember, String group, String name, int type, String descriptor) {
        regular.append(group);
        if (nextMember < PatternLogOption.MEMBER_MAX) {
            option.setValue("member" + nextMember, name);
            option.setValue("s" + nextMember, type);
            option.setValue("d" + nextMember, descriptor);
            nextMember++;
        }
        return nextMember;
    }

    /**
     * Gets the date format string for a Log4J date pattern.
     *
     * @param datePattern the Log4J date pattern (ABSOLUTE, ISO8601, DATE, or custom)
     * @return the corresponding date format string
     */
    public static String getDateFormat(String datePattern) {

        if (datePattern.equals("ABSOLUTE")) {
            return "HH:mm:ss,SSS";
        }
        if (Utils.isEmpty(datePattern) || datePattern.equals("ISO8601")) {
            return "yyyy-MM-dd HH:mm:ss,SSS";
        }
        if (datePattern.equals("DATE")) {
            return "dd MMM yyyy HH:mm:ss,SSS";
        }
        return datePattern;
    }

    /**
     * Converts a date format string to a regex pattern.
     *
     * @param dateFormat the date format string to convert
     * @return the regex pattern for matching the date format
     */
    private String convertDateFormat(String dateFormat) {

        String result = "";
        if (dateFormat != null) {
            result = dateFormat.replaceAll(Pattern.quote("+"), "[+]");
            result = result.replaceAll(VALID_DATEFORMAT_CHAR_PATTERN, "\\\\S+");
            result = result.replaceAll(Pattern.quote("."), "\\\\.");
            result = "(" + result + ")";
        }
        return result;
    }

    /**
     * Replaces meta-characters in the input string with escaped versions for regex.
     *
     * @param input the input string to escape
     * @return the escaped string safe for use in regex patterns
     */
    private static String replaceMetaChars(String input) {

        input = input.replaceAll("\\\\", "\\\\\\");
        input = input.replaceAll(Pattern.quote("]"), "\\\\]");
        input = input.replaceAll(Pattern.quote("["), "\\\\[");
        input = input.replaceAll(Pattern.quote("^"), "\\\\^");
        input = input.replaceAll(Pattern.quote("$"), "\\\\$");
        input = input.replaceAll(Pattern.quote("."), "\\\\.");
        input = input.replaceAll(Pattern.quote("|"), "\\\\|");
        input = input.replaceAll(Pattern.quote("?"), "\\\\?");
        input = input.replaceAll(Pattern.quote("+"), "\\\\+");
        input = input.replaceAll(Pattern.quote("("), "\\\\(");
        input = input.replaceAll(Pattern.quote(")"), "\\\\)");
        input = input.replaceAll(Pattern.quote("-"), "\\\\-");
        input = input.replaceAll(Pattern.quote("{"), "\\\\{");
        input = input.replaceAll(Pattern.quote("}"), "\\\\}");
        input = input.replaceAll(Pattern.quote("#"), "\\\\#");
        return input;
    }

}

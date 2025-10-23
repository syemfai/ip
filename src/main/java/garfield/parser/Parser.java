package garfield.parser;

import garfield.core.GarfieldException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses raw user input strings into structured commands and arguments.
 */
public class Parser {

    public enum CommandType {
        BYE, LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, FIND, UNKNOWN
    }

    /**
     * Parses a full command line into a {@link Parsed} structure.
     * 
     * @param input the raw line entered by the user
     * @return the parsed command
     * @throws garfield.core.GarfieldException if the command is invalid or
     *                                               incomplete
     */
    public static final class Parsed {

        public final CommandType type;
        public final String desc, by, from, to;
        public final Integer index;

        private Parsed(CommandType t, String d, String b, String f, String to, Integer i) {
            this.type = t;
            this.desc = d;
            this.by = b;
            this.from = f;
            this.to = to;
            this.index = i;
        }

        static Parsed bye() {
            return new Parsed(CommandType.BYE, null, null, null, null, null);
        }

        static Parsed list() {
            return new Parsed(CommandType.LIST, null, null, null, null, null);
        }
    }

    private static final Pattern TODO_RE = Pattern.compile("^todo\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEADLINE_RE = Pattern.compile("^deadline\\s+(.+?)\\s*/by\\s*(.+)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_RE = Pattern.compile("^event\\s+(.+?)\\s*/from\\s*(.+?)\\s*/to\\s*(.+)$",
            Pattern.CASE_INSENSITIVE);

    private static int parseIndex(String cmd, String input) throws GarfieldException {
        Matcher m = Pattern.compile("^" + Pattern.quote(cmd) + "\\s+(\\d+)$", Pattern.CASE_INSENSITIVE).matcher(input);
        if (!m.matches()) {
            throw new GarfieldException("Usage: " + cmd + " <task-number>");
        }
        return Integer.parseInt(m.group(1));
    }

    public static Parsed parse(String input) throws GarfieldException {
        assert input != null : "Parser input must not be null";

        if (input == null || input.isEmpty()) {
            throw new GarfieldException(
                    "Unknown command. Try: todo, deadline, event, list, mark, unmark, delete, find, bye.");
        }

        if (input.equalsIgnoreCase("bye")) {
            return Parsed.bye();
        }
        if (input.equalsIgnoreCase("list")) {
            return Parsed.list();
        }

        if (input.toLowerCase().startsWith("mark")) {
            return new Parsed(CommandType.MARK, null, null, null, null, parseIndex("mark", input));
        }
        if (input.toLowerCase().startsWith("unmark")) {
            return new Parsed(CommandType.UNMARK, null, null, null, null, parseIndex("unmark", input));
        }
        if (input.toLowerCase().startsWith("delete")) {
            return new Parsed(CommandType.DELETE, null, null, null, null, parseIndex("delete", input));
        }
        if (input.toLowerCase().startsWith("find")) {
            Matcher mf = Pattern.compile("^find\\s+(.+)$", Pattern.CASE_INSENSITIVE).matcher(input);
            if (!mf.matches())
                throw new GarfieldException("Usage: find <keyword>");
            return new Parsed(CommandType.FIND, mf.group(1).trim(), null, null, null, null);
        }

        Matcher mt = TODO_RE.matcher(input);
        if (mt.matches()) {
            String desc = mt.group(1).trim();
            if (desc.isEmpty()) {
                throw new GarfieldException("Usage: todo <description>");
            }
            return new Parsed(CommandType.TODO, desc, null, null, null, null);
        }

        Matcher md = DEADLINE_RE.matcher(input);
        if (md.matches()) {
            String desc = md.group(1).trim(), by = md.group(2).trim();
            if (desc.isEmpty() || by.isEmpty()) {
                throw new GarfieldException("Usage: deadline <desc> /by <when>");
            }
            return new Parsed(CommandType.DEADLINE, desc, by, null, null, null);
        }

        Matcher me = EVENT_RE.matcher(input);
        if (me.matches()) {
            String desc = me.group(1).trim(), from = me.group(2).trim(), to = me.group(3).trim();
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new GarfieldException("Usage: event <desc> /from <start> /to <end>");
            }
            return new Parsed(CommandType.EVENT, desc, null, from, to, null);
        }

        throw new GarfieldException("Unknown command. Try: todo, deadline, event, list, mark, unmark, delete, bye.");
    }
}
package garfield.io;

import garfield.task.Task;
import garfield.task.Todo;
import garfield.task.Deadline;
import garfield.task.Event;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads and saves tasks to disk using newline-delimited JSON (NDJSON).
 *
 * <p>
 * Each line is a single JSON object, with Base64-encoded text fields to avoid
 * escaping issues.
 * The file and parent directory are created on first use if absent.
 */
public class Storage {
    private final Path file;

    public Storage(Path file) {
        assert file != null : "Storage path must not be null";
        this.file = file;
    }

    /**
     * Loads tasks from disk.
     *
     * <p>
     * Unknown or corrupted lines are skipped silently; the remainder are returned.
     *
     * @return list of tasks (possibly empty)
     * @throws IOException if the file cannot be read or created
     */
    public List<Task> load() throws IOException {
        ensureFileReady();

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.startsWith("//"))
                    .map(s -> {
                        try {
                            String type = getStr(s, "type");
                            boolean done = getBool(s, "done");
                            String desc = unb64(getStr(s, "desc_b64"));

                            Task t;
                            switch (type) {
                                case "T":
                                    t = new Todo(desc);
                                    break;
                                case "D": {
                                    String by = unb64(nvl(getStr(s, "by_b64")));
                                    t = new Deadline(desc, by);
                                    break;
                                }
                                case "E": {
                                    String from = unb64(nvl(getStr(s, "from_b64")));
                                    String to = unb64(nvl(getStr(s, "to_b64")));
                                    t = new Event(desc, from, to);
                                    break;
                                }
                                default:
                                    return null;
                            }

                            if (done) {
                                t.markAsDone();
                            }
                            return t;
                        } catch (Exception ignore) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Saves the given tasks to disk, replacing previous content.
     *
     * @param tasks tasks to serialize
     * @throws IOException if writing fails
     */
    public void save(List<Task> tasks) throws IOException {
        ensureDirReady();
        List<String> lines = new ArrayList<>();
        for (Task t : tasks)
            lines.add(serialize(t));
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        assert Files.exists(file.getParent()) : "data directory should exist after save";
        assert Files.exists(file) : "data file should exist after save";
    }

    private void ensureFileReady() throws IOException {
        ensureDirReady();
        if (!Files.exists(file))
            Files.createFile(file);
    }

    private void ensureDirReady() throws IOException {
        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent))
            Files.createDirectories(parent);
    }

    private static String serialize(Task t) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"type\":\"").append(typeOf(t)).append("\",");
        sb.append("\"done\":").append(t.isDone()).append(",");
        sb.append("\"desc_b64\":\"").append(b64(t.getDescription())).append("\"");
        if (t instanceof Deadline d) {
            sb.append(",\"by_b64\":\"").append(b64(d.getBy())).append("\"");
        } else if (t instanceof Event e) {
            sb.append(",\"from_b64\":\"").append(b64(e.getFrom())).append("\"");
            sb.append(",\"to_b64\":\"").append(b64(e.getTo())).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String typeOf(Task t) {
        if (t instanceof Todo)
            return "T";
        if (t instanceof Deadline)
            return "D";
        if (t instanceof Event)
            return "E";
        return "?";
    }

    private static final Pattern STR_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern BOOL_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(true|false)");

    private static String getStr(String json, String key) {
        Matcher m = STR_FIELD.matcher(json);
        while (m.find()) {
            if (m.group(1).equals(key))
                return m.group(2);
        }
        return null;
    }

    private static boolean getBool(String json, String key) {
        Matcher m = BOOL_FIELD.matcher(json);
        while (m.find()) {
            if (m.group(1).equals(key))
                return Boolean.parseBoolean(m.group(2));
        }
        return false;
    }

    private static String b64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String unb64(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
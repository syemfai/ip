// Credit to Tsay Yong for code inspiration.
package garfield.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import garfield.core.GarfieldException;

public class ParserTest {

    @Test
    void parseTodo_valid() throws Exception {
        Parser.Parsed p = Parser.parse("todo read book");
        assertEquals(Parser.CommandType.TODO, p.type);
        assertEquals("read book", p.desc);
    }

    @Test
    void parseDeadline_isoDate() throws Exception {
        Parser.Parsed p = Parser.parse("deadline return book /by 2019-12-02");
        assertEquals(Parser.CommandType.DEADLINE, p.type);
        assertEquals("return book", p.desc);
        assertEquals("2019-12-02", p.by);
    }

    @Test
    void parseEvent_whitespaceTolerance() throws Exception {
        Parser.Parsed p = Parser.parse("event proj mtg   /from 2019-12-02 1400   /to   2019-12-02 1600");
        assertEquals(Parser.CommandType.EVENT, p.type);
        assertEquals("proj mtg", p.desc);
        assertEquals("2019-12-02 1400", p.from);
        assertEquals("2019-12-02 1600", p.to);
    }

    @Test
    void parseMark_missingNumber_throwsUsage() {
        GarfieldException ex = assertThrows(GarfieldException.class,
                () -> Parser.parse("mark"));
        assertTrue(ex.getMessage().toLowerCase().contains("usage"));
    }

    @Test
    void parseUnknown_throws() {
        assertThrows(GarfieldException.class, () -> Parser.parse("blorp"));
    }
}
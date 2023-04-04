package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SubtaskTest {

    @Test
    public void checkConstructorWithoutId() {
        var c = new Card("cardName", "desc", null, 0);
        var s = new Subtask("subtaskTitle", c, false);
        assertEquals("subtaskTitle", s.getTitle());
        assertEquals(c, s.getCard());
        s.setId(1L);
        assertEquals(1, s.getId());
    }

    @Test
    public void equalsHashcode() {
        var c = new Card("cardName", "desc", null, 0);
        var s1 = new Subtask("subtaskTitle", c, false);
        var s2 = new Subtask("subtaskTitle", c, false);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var c = new Card("cardName", "desc", null, 0);
        var s1 = new Subtask("subtaskTitle", c, false);
        var s2 = new Subtask("differentSubtaskTitle", c, false);
        assertNotEquals(s1, s2);
        assertNotEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void hasToString() {
        var c = new Card("cardName", "desc", null, 0);
        var actual = new Subtask("subtaskTitle", c, false).toString();
        assertTrue(actual.contains(Subtask.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

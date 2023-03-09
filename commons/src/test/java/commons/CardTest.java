package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CardTest {

    @Test
    public void checkConstructorWithoutId() {
        var list = new CardList();
        var tag = new Tag("tagTitle", "green", null);
        var tags = new ArrayList<Tag>();
        tags.add(tag);
        var subtask = new Subtask("subtaskTitle", null);
        var subtasks = new ArrayList<Subtask>();
        subtasks.add(subtask);
        var c = new Card("cardTitle", "desc", "red", list);
        assertEquals("cardTitle", c.getTitle());
        assertEquals("desc", c.getDescription());
        assertEquals("red", c.getColor());
        assertEquals(list, c.getList());
        assertEquals(tags, c.getTags());
        assertEquals(subtasks, c.getSubtasks());
    }

    @Test
    public void equalsHashcode() {
        var c1 = new Card("cardTitle", "desc", "red", null);
        var c2 = new Card("cardTitle", "desc", "red", null);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var c1 = new Card("cardTitle", "desc", "red", null);
        var c2 = new Card("differentCardTitle", "desc", "red", null);
        assertNotEquals(c1, c2);
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Card("cardTitle", "desc", "red", null).toString();
        assertTrue(actual.contains(Card.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

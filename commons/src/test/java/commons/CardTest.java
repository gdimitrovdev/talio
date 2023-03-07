package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class CardTest {

    @Test
    public void checkConstructorWithId() {
        var list = new CardList();
        var tag = new Tag("tagTitle", "green", null, null);
        var tags = new ArrayList<Tag>();
        tags.add(tag);
        var subtask = new Subtask("subtaskTitle", null);
        var subtasks = new ArrayList<Subtask>();
        subtasks.add(subtask);
        var c = new Card(15, "cardTitle", "desc", "red", list, tags, subtasks);
        assertEquals(15, c.id);
        assertEquals("cardTitle", c.title);
        assertEquals("desc", c.description);
        assertEquals("red", c.color);
        assertEquals(list, c.list);
        assertEquals(tags, c.tags);
        assertEquals(subtasks, c.subtasks);
    }

    @Test
    public void checkConstructorWithoutId() {
        var list = new CardList();
        var tag = new Tag("tagTitle", "green", null, null);
        var tags = new ArrayList<Tag>();
        tags.add(tag);
        var subtask = new Subtask("subtaskTitle", null);
        var subtasks = new ArrayList<Subtask>();
        subtasks.add(subtask);
        var c = new Card("cardTitle", "desc", "red", list, tags, subtasks);
        assertEquals("cardTitle", c.title);
        assertEquals("desc", c.description);
        assertEquals("red", c.color);
        assertEquals(list, c.list);
        assertEquals(tags, c.tags);
        assertEquals(subtasks, c.subtasks);
    }

    @Test
    public void equalsHashcode() {
        var c1 = new Card("cardTitle", "desc", "red", null, null, null);
        var c2 = new Card("cardTitle", "desc", "red", null, null, null);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var c1 = new Card("cardTitle", "desc", "red", null, null, null);
        var c2 = new Card("differentCardTitle", "desc", "red", null, null, null);
        assertNotEquals(c1, c2);
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Card("cardTitle", "desc", "red", null, null, null).toString();
        assertTrue(actual.contains(Card.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void checkConstructorWithoutLists() {
        var list = new CardList();
        var c = new Card("cardTitle", "desc", "red", list);
        assertEquals("cardTitle", c.getTitle());
        assertEquals("desc", c.getDescription());
        assertEquals("red", c.getColor());
        assertEquals(list, c.getList());
        c.setId(1L);
        assertEquals(1, c.getId());
    }

    @Test
    public void checkConstructor() {
        var list = new CardList();
        var tag = new Tag("tagTitle", "green", null);
        var tags = new ArrayList<Tag>();
        tags.add(tag);
        var subtask = new Subtask("subtaskTitle", null);
        var subtasks = new ArrayList<Subtask>();
        subtasks.add(subtask);
        var c = new Card("cardTitle", "desc", "red", list, tags, subtasks);
        c.setTags(tags);
        c.setSubtasks(subtasks);
        assertEquals("cardTitle", c.getTitle());
        assertEquals("desc", c.getDescription());
        assertEquals("red", c.getColor());
        assertEquals(list, c.getList());
        assertEquals(tags, c.getTags());
        assertEquals(subtasks, c.getSubtasks());
    }

    @Test
    public void testSubtaskOperations() {
        var subtask = new Subtask("subtaskTitle", null);
        var subtasks = new ArrayList<Subtask>();
        var c = new Card("cardTitle", "desc", "red", null, null, subtasks);
        c.addSubtask(subtask);
        assertTrue(c.getSubtasks().contains(subtask));
        assertEquals(c, subtask.getCard());
        c.removeSubtask(subtask);
        assertFalse(c.getSubtasks().contains(subtask));
        assertNull(subtask.getCard());
    }

    @Test
    public void testTagOperations() {
        var tag = new Tag("tagTitle", "green", null);
        var tags = new ArrayList<Tag>();
        var c = new Card("cardTitle", "desc", "red", null, tags, null);
        c.addTag(tag);
        assertTrue(c.getTags().contains(tag));
        assertTrue(tag.getCards().contains(c));
        c.removeTag(tag);
        assertFalse(c.getTags().contains(tag));
        assertFalse(tag.getCards().contains(c));
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
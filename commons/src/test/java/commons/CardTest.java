package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class CardTest {

    @Test
    public void checkConstructorWithoutLists() {
        var list = new CardList();
        var c = new Card("cardTitle", "desc", list, 0);
        assertEquals("cardTitle", c.getTitle());
        assertEquals("desc", c.getDescription());
        assertEquals(0, c.getColorPresetNumber());
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
        var subtask = new Subtask("subtaskTitle", null, false);
        var subtasks = new ArrayList<Subtask>();
        subtasks.add(subtask);
        var c = new Card("cardTitle", "desc", 0, list, tags, subtasks);
        c.setTags(tags);
        c.setSubtasks(subtasks);
        assertEquals("cardTitle", c.getTitle());
        assertEquals("desc", c.getDescription());
        assertEquals(0, c.getColorPresetNumber());
        assertEquals(list, c.getList());
        assertEquals(tags, c.getTags());
        assertEquals(subtasks, c.getSubtasks());
    }

    @Test
    public void testSubtaskOperations() {
        var subtask = new Subtask("subtaskTitle", null, false);
        var subtasks = new ArrayList<Subtask>();
        var c = new Card("cardTitle", "desc", 0, null, null, subtasks);
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
        var c = new Card("cardTitle", "desc", 0, null, tags, null);
        c.addTag(tag);
        assertTrue(c.getTags().contains(tag));
        assertTrue(tag.getCards().contains(c));
        c.removeTag(tag);
        assertFalse(c.getTags().contains(tag));
        assertFalse(tag.getCards().contains(c));
    }

    @Test
    public void equalsHashcode() {
        var c1 = new Card("cardTitle", "desc", null, 0);
        var c2 = new Card("cardTitle", "desc", null, 0);
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var c1 = new Card("cardTitle", "desc", null, 0);
        var c2 = new Card("differentCardTitle", "desc", null, 0);
        assertNotEquals(c1, c2);
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Card("cardTitle", "desc",  null, 0).toString();
        assertTrue(actual.contains(Card.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

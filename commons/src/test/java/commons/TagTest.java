package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TagTest {

    @Test
    public void checkConstructorWithoutId() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t = new Tag("tagTitle", "red", b, cards);
        assertEquals("tagTitle", t.title);
        assertEquals("red", t.color);
        assertEquals(b, t.board);
        assertEquals(cards, t.cards);
    }

    @Test
    public void checkConstructorWithId() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t = new Tag(15, "tagTitle", "red", b, cards);
        assertEquals(15, t.id);
        assertEquals("tagTitle", t.title);
        assertEquals("red", t.color);
        assertEquals(b, t.board);
        assertEquals(cards, t.cards);
    }

    @Test
    public void equalsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t1 = new Tag("tagTitle", "red", b, cards);
        var t2 = new Tag("tagTitle", "red", b, cards);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t1 = new Tag("tagTitle", "red", b, cards);
        var t2 = new Tag("differentTagTitle", "red", b, cards);
        assertNotEquals(t1, t2);
        assertNotEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var actual = new Tag("tagTitle", "red", b, cards).toString();
        assertTrue(actual.contains(Tag.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

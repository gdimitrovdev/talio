package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void checkConstructorWithoutList() {
        var b = new Board("boardName", "password", "asdfgh", "#000000/#111111", "#000000/#222222",
                null, 0);
        var t = new Tag("tagTitle", "red", b);
        assertEquals("tagTitle", t.getTitle());
        assertEquals("red", t.getColor());
        assertEquals(b, t.getBoard());
        t.setId(1L);
        assertEquals(1, t.getId());
    }

    @Test
    public void checkConstructor() {
        var b = new Board("boardName", "password", "asdfgh", "#000000/#111111", "#000000/#222222",
                null, 0);
        var c = new Card("cardTitle", "desc", null, 0);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t = new Tag("tagTitle", "red", b, cards);
        t.setCards(cards);
        assertEquals("tagTitle", t.getTitle());
        assertEquals("red", t.getColor());
        assertEquals(b, t.getBoard());
        assertEquals(cards, t.getCards());
    }

    @Test
    public void equalsHashCode() {
        var b = new Board("boardName", "password", "asdfgh", "#000000/#111111", "#000000/#222222",
                null, 0);
        var c = new Card("cardTitle", "desc", null, 0);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t1 = new Tag("tagTitle", "red", b);
        var t2 = new Tag("tagTitle", "red", b);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var b = new Board("boardName", "password", "asdfgh", "#000000/#111111", "#000000/#222222",
                null, 0);
        var c = new Card("cardTitle", "desc", null, 0);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var t1 = new Tag("tagTitle", "red", b);
        var t2 = new Tag("differentTagTitle", "red", b);
        assertNotEquals(t1, t2);
        assertNotEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board("boardName", "password", "asdfgh", "#000000/#111111", "#000000/#222222",
                null, 0);
        var c = new Card("cardTitle", "desc", null, 0);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var actual = new Tag("tagTitle", "red", b).toString();
        assertTrue(actual.contains(Tag.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}

package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CardListTest {
    @Test
    public void checkConstructorWithoutId() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl = new CardList("listTitle", b, cards);
        assertEquals("listTitle", cl.title);
        assertEquals(b, cl.board);
        assertEquals(cards, cl.cards);
    }

    @Test
    public void checkConstructorWithId() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl = new CardList(15, "listTitle", b, cards);
        assertEquals(15, cl.id);
        assertEquals("listTitle", cl.title);
        assertEquals(b, cl.board);
        assertEquals(cards, cl.cards);
    }

    @Test
    public void equalsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl1 = new CardList("listTitle", b, cards);
        var cl2 = new CardList("listTitle", b, cards);
        assertEquals(cl2, cl1);
        assertEquals(cl2.hashCode(), cl1.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl1 = new CardList("listTitleDifferent", b, cards);
        var cl2 = new CardList("listTitle", b, cards);
        assertNotEquals(cl2, cl1);
        assertNotEquals(cl2.hashCode(), cl1.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green", null, null);
        var c = new Card("cardTitle", "desc", "green", null, null, null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var actual = new CardList("listTitleDifferent", b, cards).toString();
        assertTrue(actual.contains(CardList.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }

}

package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CardListTest {
    @Test
    public void checkConstructorWithoutId() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl = new CardList("listTitle", b);
        assertEquals("listTitle", cl.getTitle());
        assertEquals(b, cl.getBoard());
        assertEquals(cards, cl.getCards());
    }

    @Test
    public void equalsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl1 = new CardList("listTitle", b);
        var cl2 = new CardList("listTitle", b);
        assertEquals(cl2, cl1);
        assertEquals(cl2.hashCode(), cl1.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl1 = new CardList("listTitleDifferent", b);
        var cl2 = new CardList("listTitle", b);
        assertNotEquals(cl2, cl1);
        assertNotEquals(cl2.hashCode(), cl1.hashCode());
    }

    @Test
    public void hasToString() {
        var b = new Board(true, "boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var actual = new CardList("listTitleDifferent", b).toString();
        assertTrue(actual.contains(CardList.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }

}

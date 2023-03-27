package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class CardListTest {
    @Test
    public void checkConstructor() {
        var b = new Board("boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl = new CardList("listTitle", b, cards);
        cl.setCards(cards);
        assertEquals("listTitle", cl.getTitle());
        assertEquals(b, cl.getBoard());
        assertEquals(cards, cl.getCards());
    }

    @Test
    public void checkConstructorWithoutLists() {
        var b = new Board("boardName", "password", "asdfgh", "green");
        var cl = new CardList("listTitle", b);
        assertEquals("listTitle", cl.getTitle());
        assertEquals(b, cl.getBoard());
        cl.setId(1L);
        assertEquals(1, cl.getId());
    }

    @Test
    public void equalsHashCode() {
        var b = new Board("boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var cl1 = new CardList("listTitle", b);
        var cl2 = new CardList("listTitle", b);
        assertEquals(cl2, cl1);
        assertEquals(cl2.hashCode(), cl1.hashCode());
    }

    @Test
    public void testCardOperations() {
        var b = new Board("boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        var cl = new CardList("listTitle", b, cards);
        cl.addCard(c);
        assertTrue(cl.getCards().contains(c));
        assertEquals(c.getList(), cl);
        cl.removeCard(c);
        assertFalse(cl.getCards().contains(c));
        assertNull(c.getList());
    }

    @Test
    public void notEqualsHashCode() {
        var b = new Board("boardName", "password", "asdfgh", "green");
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
        var b = new Board("boardName", "password", "asdfgh", "green");
        var c = new Card("cardTitle", "desc", "green", null);
        var cards = new ArrayList<Card>();
        cards.add(c);
        var actual = new CardList("listTitleDifferent", b).toString();
        assertTrue(actual.contains(CardList.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }

}

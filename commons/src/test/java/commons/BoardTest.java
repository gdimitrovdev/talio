package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

    @Test
    public void checkConstructorWithoutId() {
        var tags = new ArrayList<Tag>();
        tags.add(new Tag("tagTitle", "red", null, null));
        var cardList = new CardList("cardListTitle", new Board(), new ArrayList<Card>());
        cardList.cards.add(new Card("cardTitle", "desc", "green", null, null, null));
        var cardlists = new ArrayList<CardList>();
        cardlists.add(cardList);
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red", cardlists, tags);
        assertEquals(true, b.readOnly);
        assertEquals("boardName", b.name);
        assertEquals("boardPassword", b.password);
        assertEquals("asdfgh", b.hash);
        assertEquals("red", b.color);
        assertEquals(cardlists, b.lists);
        assertEquals(tags, b.tags);
    }

    @Test
    public void checkConstructorWithId() {
        var tags = new ArrayList<Tag>();
        tags.add(new Tag("tagTitle", "red", null, null));
        var cardList = new CardList("cardListTitle", new Board(), new ArrayList<Card>());
        cardList.cards.add(new Card("cardTitle", "desc", "green", null, null, null));
        var cardlists = new ArrayList<CardList>();
        cardlists.add(cardList);
        var b = new Board(15,true, "boardName", "boardPassword", "asdfgh", "red", cardlists, tags);
        assertEquals(15, b.id);
        assertEquals(true, b.readOnly);
        assertEquals("boardName", b.name);
        assertEquals("boardPassword", b.password);
        assertEquals("asdfgh", b.hash);
        assertEquals("red", b.color);
        assertEquals(cardlists, b.lists);
        assertEquals(tags, b.tags);
    }

    @Test
    public void equalsHashcode() {
        var b1 = new Board(true, "boardName", "boardPassword", "asdfgh", "red", null, null);
        var b2 = new Board(true, "boardName", "boardPassword", "asdfgh", "red", null, null);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var b1 = new Board(true, "boardName", "boardPassword", "asdfgh", "red", null, null);
        var b2 = new Board(true, "differentName", "boardPassword", "asdfgh", "red", null, null);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Board(true, "boardName", "boardPassword", "asdfgh", "red", null, null).toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("name"));
    }
}

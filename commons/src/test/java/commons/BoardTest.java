package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {

    @Test
    public void checkConstructorWithoutId() {
        var tags = new ArrayList<Tag>();
        tags.add(new Tag("tagTitle", "red", null));
        var cardList = new CardList("cardListTitle", new Board());
        cardList.getCards().add(new Card("cardTitle", "desc", "green", null));
        var cardlists = new ArrayList<CardList>();
        cardlists.add(cardList);
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red");
        assertTrue(b.getReadOnly());
        assertEquals("boardName", b.getName());
        assertEquals("boardPassword", b.getPassword());
        assertEquals("asdfgh", b.getHash());
        assertEquals("red", b.getColor());
        assertEquals(cardlists, b.getLists());
        assertEquals(tags, b.getTags());
    }

    @Test
    public void equalsHashcode() {
        var b1 = new Board(true, "boardName", "boardPassword", "asdfgh", "red");
        var b2 = new Board(true, "boardName", "boardPassword", "asdfgh", "red");
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void notEqualsHashcode() {
        var b1 = new Board(true, "boardName", "boardPassword", "asdfgh", "red");
        var b2 = new Board(true, "differentName", "boardPassword", "asdfgh", "red");
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Board(true, "boardName", "boardPassword", "asdfgh", "red").toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("name"));
    }
}

package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void checkConstructorWithoutLists() {
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red");
        assertTrue(b.getReadOnly());
        assertEquals("boardName", b.getName());
        assertEquals("boardPassword", b.getPassword());
        assertEquals("asdfgh", b.getHash());
        assertEquals("red", b.getColor());
        b.setId(1L);
        assertEquals(1, b.getId());
    }

    @Test
    public void checkConstructor() {
        var tags = new ArrayList<Tag>();
        tags.add(new Tag());
        var cardList = new CardList("cardListTitle", new Board());
        cardList.getCards().add(new Card("cardTitle", "desc", "green", null));
        var cardlists = new ArrayList<CardList>();
        cardlists.add(cardList);
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red", cardlists, tags);
        b.setLists(cardlists);
        b.setTags(tags);
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
    public void testCardListOperations() {
        var cardList = new CardList("cardListTitle", new Board());
        cardList.getCards().add(new Card("cardTitle", "desc", "green", null));
        var cardlists = new ArrayList<CardList>();
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red", cardlists, null);
        b.addCardList(cardList);
        assertEquals(cardList.getBoard(), b);
        assertTrue(b.getLists().contains(cardList));
        b.removeCardList(cardList);
        assertNull(cardList.getBoard());
        assertFalse(b.getLists().contains(cardList));
    }

    @Test
    public void testTagOperations() {
        Tag tag = new Tag("tagTitle", "red", null);
        var tags = new ArrayList<Tag>();
        var b = new Board(true, "boardName", "boardPassword", "asdfgh", "red", null, tags);
        b.addTag(tag);
        assertEquals(b, tag.getBoard());
        assertTrue(b.getTags().contains(tag));
        b.removeTag(tag);
        assertNull(tag.getBoard());
        assertFalse(b.getTags().contains(tag));
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

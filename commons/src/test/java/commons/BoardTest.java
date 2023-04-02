package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class BoardTest {

    @Test
    public void checkConstructorWithoutLists() {
        var b = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#222222");
        assertEquals("boardName", b.getName());
        assertEquals("boardPassword", b.getCode());
        assertEquals("asdfgh", b.getReadOnlyCode());
        assertEquals("#000000/#111111", b.getBoardColor());
        assertEquals("#000000/#222222", b.getListsColor());
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
        var b = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#222222", cardlists, tags);
        b.setLists(cardlists);
        b.setTags(tags);
        assertEquals("boardName", b.getName());
        assertEquals("boardPassword", b.getCode());
        assertEquals("asdfgh", b.getReadOnlyCode());
        assertEquals("#000000/#111111", b.getBoardColor());
        assertEquals("#000000/#222222", b.getListsColor());
        assertEquals(cardlists, b.getLists());
        assertEquals(tags, b.getTags());
    }

    @Test
    public void equalsHashcode() {
        var b1 = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111");
        var b2 = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111");
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void testCardListOperations() {
        var cardList = new CardList("cardListTitle", new Board());
        cardList.getCards().add(new Card("cardTitle", "desc", "green", null));
        var cardlists = new ArrayList<CardList>();
        var b = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111", cardlists, null);
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
        var b = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111",  "#000000/#111111", null, tags);
        b.addTag(tag);
        assertEquals(b, tag.getBoard());
        assertTrue(b.getTags().contains(tag));
        b.removeTag(tag);
        assertNull(tag.getBoard());
        assertFalse(b.getTags().contains(tag));
    }

    @Test
    public void notEqualsHashcode() {
        var b1 = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111");
        var b2 = new Board("differentName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111");
        assertNotEquals(b1, b2);
        assertNotEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Board("boardName", "boardPassword", "asdfgh", "#000000/#111111", "#000000/#111111").toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("name"));
    }
}

package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class CardList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String title;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    public Board board;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "list", fetch = FetchType.EAGER)
    public List<Card> cards;

    public CardList(){}

    public CardList(long id, String title, Board board, List<Card> cards) {
        this.id = id;
        this.title = title;
        this.board = board;
        this.cards = cards;
    }

    public CardList(String title, Board board, List<Card> cards) {
        this.title = title;
        this.board = board;
        this.cards = cards;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}

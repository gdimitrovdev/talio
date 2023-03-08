package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String title, color;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "BOARD_ID", nullable = false)
    public Board board;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags")
    public List<Card> cards;

    public Tag(){}

    public Tag(long id, String title, String color, Board board, List<Card> cards) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.board = board;
        this.cards = cards;
    }

    public Tag(String title, String color, Board board, List<Card> cards) {
        this.title = title;
        this.color = color;
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
}

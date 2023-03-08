package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String title;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "CARD_ID", nullable = false)
    public Card card;

    public Subtask(){}

    public Subtask(long id, String title, Card card) {
        this.id = id;
        this.title = title;
        this.card = card;
    }

    public Subtask(String title, Card card) {
        this.title = title;
        this.card = card;
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

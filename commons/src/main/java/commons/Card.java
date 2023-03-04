package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String title, description, color;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "LIST_ID", nullable = false)
    public CardList list;

    @ManyToMany
    @JoinTable(name = "CARD_TAG")
    public Set<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
    public Set<Subtask> subtasks;

    public Card(){}

    public Card(long id, String title, String description, String color, CardList list, Set<Tag> tags, Set<Subtask> subtasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.color = color;
        this.list = list;
        this.tags = tags;
        this.subtasks = subtasks;
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

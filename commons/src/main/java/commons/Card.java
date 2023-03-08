package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public String title, description, color;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn(name = "LIST_ID", nullable = false)
    public CardList list;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "CARD_TAG")
    public List<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card", fetch = FetchType.EAGER)
    public List<Subtask> subtasks;

    public Card(){}

    public Card(long id, String title, String description, String color, CardList list, List<Tag> tags, List<Subtask> subtasks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.color = color;
        this.list = list;
        this.tags = tags;
        this.subtasks = subtasks;
    }

    public Card(String title, String description, String color, CardList list, List<Tag> tags, List<Subtask> subtasks) {
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

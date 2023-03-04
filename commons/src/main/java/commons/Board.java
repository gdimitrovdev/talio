package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public boolean readOnly;
    public String name, password, hash, color;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board")
    public Set<CardList> lists;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board")
    public Set<Tag> tags;

    public Board(){}

    public Board(long id, boolean readOnly, String name, String password, String hash, String color, Set<CardList> lists, Set<Tag> tags) {
        this.id = id;
        this.readOnly = readOnly;
        this.name = name;
        this.password = password;
        this.hash = hash;
        this.color = color;
        this.lists = lists;
        this.tags = tags;
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

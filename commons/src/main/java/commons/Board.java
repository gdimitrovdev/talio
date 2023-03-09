package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    private Boolean readOnly;

    @Basic(optional = false)
    private String name;

    private String password;

    private String hash;

    private String color;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "board", fetch = FetchType.EAGER)
    private List<CardList> lists;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "board", fetch = FetchType.EAGER)
    private List<Tag> tags;

    public Board(){}
    public Board(boolean readOnly, String name, String password, String hash, String color) {
        setReadOnly(readOnly);
        setName(name);
        setPassword(password);
        setHash(hash);
        setColor(color);
        lists = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<CardList> getLists() {
        return lists;
    }

    public void setLists(List<CardList> lists) {
        this.lists = lists;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
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

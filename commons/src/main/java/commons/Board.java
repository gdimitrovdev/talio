package commons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Board implements Serializable {
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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardList> lists = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public Board(){}
    public Board(boolean readOnly, String name, String password, String hash, String color) {
        setReadOnly(readOnly);
        setName(name);
        setPassword(password);
        setHash(hash);
        setColor(color);
    }

    public Board(Boolean readOnly, String name, String password, String hash, String color, List<CardList> lists, List<Tag> tags) {
        this.readOnly = readOnly;
        this.name = name;
        this.password = password;
        this.hash = hash;
        this.color = color;
        this.lists = lists;
        this.tags = tags;
    }

    public void addCardList(CardList list) {
        lists.add(list);
        list.setBoard(this);
    }

    public void removeCardList(CardList list) {
        lists.remove(list);
        list.setBoard(null);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.setBoard(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.setBoard(null);
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
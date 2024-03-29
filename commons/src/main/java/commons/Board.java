package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

//circular reference problem
@Entity
@JsonIdentityInfo(scope = Board.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Board implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    private String name;

    @Column(unique = true)
    private String code;
    
    private String readOnlyCode;

    private String listsColor;

    private String boardColor;

    @ElementCollection
    private List<String> cardColorPresets;

    private Integer defaultPresetNum;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval =
            true)
    private List<CardList> lists = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public Board() {
    }

    public Board(Board board) {
        this.id = board.id;
        this.name = board.name;
        this.code = board.code;
        this.readOnlyCode = board.readOnlyCode;
        this.boardColor = board.boardColor;
        this.listsColor = board.listsColor;
        this.cardColorPresets = board.cardColorPresets;
        this.defaultPresetNum = board.defaultPresetNum;
        if (board.lists != null) {
            this.lists = new ArrayList<CardList>(board.lists);
        }
        if (board.tags != null) {
            this.tags = new ArrayList<Tag>(board.tags);
        }
    }

    public Board(String name, String code, String readOnlyCode, String boardColor,
            String listsColor, List<String> cardColorPresets,
            Integer defaultPresetNum) {
        setDefaultPresetNum(defaultPresetNum);
        setName(name);
        setCode(code);
        setReadOnlyCode(readOnlyCode);
        setBoardColor(boardColor);
        setListsColor(listsColor);
        setCardColorPresets(cardColorPresets);

    }

    public Board(String name, String code, String readOnlyCode, String boardColor,
            String listsColor, List<CardList> lists,
            List<Tag> tags, List<String> cardColorPresets, Integer defaultPresetNum) {
        setName(name);
        setCode(code);
        setReadOnlyCode(readOnlyCode);
        setListsColor(listsColor);
        setBoardColor(boardColor);
        setLists(lists);
        setTags(tags);
        setCardColorPresets(cardColorPresets);
        setDefaultPresetNum(defaultPresetNum);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReadOnlyCode() {
        return readOnlyCode;
    }

    public void setReadOnlyCode(String readOnlyCode) {
        this.readOnlyCode = readOnlyCode;
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

    public String getListsColor() {
        return listsColor;
    }

    public void setListsColor(String listsColor) {
        this.listsColor = listsColor;
    }

    public String getBoardColor() {
        return boardColor;
    }

    public void setBoardColor(String boardColor) {
        this.boardColor = boardColor;
    }

    public List<String> getCardColorPresets() {
        return cardColorPresets;
    }

    public void setCardColorPresets(List<String> cardColorPresets) {
        this.cardColorPresets = cardColorPresets;
    }

    public Integer getDefaultPresetNum() {
        return defaultPresetNum;
    }

    public void setDefaultPresetNum(Integer defaultPresetNum) {
        this.defaultPresetNum = defaultPresetNum;
    }
}

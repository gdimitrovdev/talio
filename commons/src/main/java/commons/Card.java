package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@JsonIdentityInfo(scope = Card.class, generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Card implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic(optional = false)
    private String title;
    private String description;
    private Long listPriority;
    private Integer colorPresetNumber;

    public Long getListPriority() {
        return listPriority;
    }

    public void setListPriority(Long listPriority) {
        this.listPriority = listPriority;
    }

    @ManyToOne
    private CardList list;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks = new ArrayList<>();

    public Card() {
    }

    public Card(String title, String description, CardList list,
                Integer colorPresetNumber) {
        setColorPresetNumber(colorPresetNumber);
        setTitle(title);
        setDescription(description);
        setList(list);
        setListPriority(-1L);
    }

    public Card(String title, String description, Integer colorPresetNumber, CardList list, List<Tag> tags,
                List<Subtask> subtasks) {
        this.title = title;
        this.description = description;
        this.colorPresetNumber = colorPresetNumber;
        this.list = list;
        this.tags = tags;
        this.subtasks = subtasks;
        setListPriority(-1L);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        subtask.setCard(this);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        subtask.setCard(null);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getCards().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getCards().remove(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CardList getList() {
        return list;
    }

    public void setList(CardList list) {
        this.list = list;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
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

    public Integer getColorPresetNumber() {
        return colorPresetNumber;
    }

    public void setColorPresetNumber(Integer colorPresetNumber) {
        this.colorPresetNumber = colorPresetNumber;
    }
}

package server.services;

import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class TagServiceTest {
    @Mock
    private TagRepository tagRepositoryMock;

    @InjectMocks
    private TagService tagServiceMock;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void getMany() {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        tags.add(tag1);
        tags.add(tag2);

        when(tagRepositoryMock.findAll()).thenReturn(tags);

        List<Tag> returnedTags = tagServiceMock.getMany();
        assertEquals(tag1, returnedTags.get(0));
        assertEquals(tag2, returnedTags.get(1));

    }

    @Test
    void getOne() {
        Long tagId = 1L;
        Tag tag = new Tag();
        tag.setId(tagId);
        when(tagRepositoryMock.findById(tagId)).thenReturn(Optional.of(tag));

        Optional<Tag> returnedTag = tagServiceMock.getOne(tagId);
        assertEquals(tag, returnedTag.get());
    }

    @Test
    void createOne() {
        Tag tag = new Tag();

        when(tagRepositoryMock.save(tag)).thenReturn(tag);

        Tag returnedTag = tagServiceMock.createOne(tag);
        assertEquals(tag, returnedTag);
    }

    @Test
    void deleteOne() {
        when(tagRepositoryMock.existsById(1L)).thenReturn(true);

        tagServiceMock.deleteOne(1L);

        verify(tagRepositoryMock).deleteById(1L);
    }

    @Test
    void updateOne() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTitle("title1");

        when(tagRepositoryMock.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepositoryMock.save(tag)).thenReturn(tag);

        Tag updatedTag = new Tag();
        updatedTag.setTitle("title2");
        Tag returnedTag= tagServiceMock.updateOne(1L, updatedTag);
        assertEquals(updatedTag.getTitle(), returnedTag.getTitle());
    }
}
package server.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.services.TagService;

@RunWith(MockitoJUnitRunner.class)
class TagControllerTest {
    @Mock
    private TagService tagRepositoryMock;

    @InjectMocks
    private TagController tagControllerMock;

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

        when(tagRepositoryMock.getMany()).thenReturn(tags);

        List<Tag> returnedTags = tagControllerMock.getMany();
        assertEquals(tag1, returnedTags.get(0));
        assertEquals(tag2, returnedTags.get(1));

    }

    @Test
    void getOne() {
        Long tagId = 1L;
        Tag tag = new Tag();
        tag.setId(tagId);
        when(tagRepositoryMock.getOne(tagId)).thenReturn(Optional.of(tag));

        Tag returnedTag = tagControllerMock.getOne(tagId).getBody();
        assertEquals(tag, returnedTag);
    }

    @Test
    void createOne() {
        Tag tag = new Tag();

        when(tagRepositoryMock.createOne(tag)).thenReturn(tag);

        Tag returnedTag = tagControllerMock.createOne(tag).getBody();
        assertEquals(tag, returnedTag);
    }

    @Test
    void deleteOne() {

        tagControllerMock.deleteOne(1L);

        verify(tagRepositoryMock).deleteOne(1L);
    }

    @Test
    void updateOne() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTitle("title1");

        when(tagRepositoryMock.updateOne(1L, tag)).thenReturn(tag);

        Tag updatedTag = new Tag();
        updatedTag.setTitle("title2");
        Tag returnedTag = tagControllerMock.updateOne(1L, updatedTag).getBody();
        assertEquals(updatedTag.getTitle(), returnedTag.getTitle());
    }
}

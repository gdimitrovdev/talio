package server.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
import commons.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.services.BoardService;
import server.services.CardService;
import server.services.TagService;

@RunWith(MockitoJUnitRunner.class)
class TagControllerTest {
    @Mock
    private TagService tagServiceMock;
    @Mock
    private BoardService boardServiceMock;
    @Mock
    private CardService cardServiceMock;
    @Mock
    private SimpMessagingTemplate templateMock;
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

        when(tagServiceMock.getMany()).thenReturn(tags);

        List<Tag> returnedTags = tagControllerMock.getMany();
        assertEquals(tag1, returnedTags.get(0));
        assertEquals(tag2, returnedTags.get(1));

    }

    @Test
    void getOne() {
        Long tagId = 1L;
        Tag tag = new Tag();
        tag.setId(tagId);
        when(tagServiceMock.getOne(tagId)).thenReturn(Optional.of(tag));

        Tag returnedTag = tagControllerMock.getOne(tagId).getBody();
        assertEquals(tag, returnedTag);
    }

    @Test
    public void getOneTagNotPresent() {
        when(tagServiceMock.getOne(1L)).thenReturn(Optional.empty());

        ResponseEntity<Tag> response = tagControllerMock.getOne(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(tagServiceMock).getOne(1L);
    }

    @Test
    void createOne() {
        Tag tag = new Tag();

        when(tagServiceMock.createOne(tag)).thenReturn(tag);

        Tag returnedTag = tagControllerMock.createOne(tag).getBody();
        assertEquals(tag, returnedTag);
    }

    @Test
    public void createOneException() {
        Tag tag = new Tag();
        doThrow(new RuntimeException()).when(tagServiceMock).createOne(tag);

        ResponseEntity<Tag> response = tagControllerMock.createOne(tag);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testDeleteOne() {
        Tag tag = new Tag();
        tag.setId(1L);

        Card card1 = new Card();
        card1.setId(1L);
        card1.setTags(Collections.singletonList(tag));

        Card card2 = new Card();
        card2.setId(2L);
        card2.setTags(Collections.singletonList(tag));

        List<Card> allCards = Arrays.asList(card1, card2);

        when(cardServiceMock.getMany()).thenReturn(allCards);

        ResponseEntity response = tagControllerMock.deleteOne(tag.getId());

        verify(tagServiceMock, times(1)).deleteOne(tag.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteOneException() {
        Long invalidId = 12345L;
        doThrow(EntityNotFoundException.class).when(tagServiceMock).deleteOne(invalidId);
        var result = tagControllerMock.deleteOne(invalidId);
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    void updateOne() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTitle("title1");

        Tag updatedTag = new Tag();
        updatedTag.setTitle("title2");

        Card card1 = new Card();
        card1.setId(1L);
        card1.setTags(Collections.singletonList(tag));

        Card card2 = new Card();
        card2.setId(2L);
        card2.setTags(Collections.singletonList(tag));

        List<Card> allCards = Arrays.asList(card1, card2);

        when(cardServiceMock.getMany()).thenReturn(allCards);
        when(tagServiceMock.updateOne(1L, tag)).thenReturn(updatedTag);

        //Tag returnedTag = tagControllerMock.updateOne(1L, tag).getBody();
        ResponseEntity response = tagControllerMock.updateOne(tag.getId(), tag);

        //assertEquals(updatedTag, returnedTag);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void updateOneException() {
        Long invalidId = 123L;
        Tag tag = new Tag();
        when(tagServiceMock.updateOne(invalidId, tag)).thenThrow(new EntityNotFoundException());

        ResponseEntity<Tag> result = tagControllerMock.updateOne(invalidId, tag);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}

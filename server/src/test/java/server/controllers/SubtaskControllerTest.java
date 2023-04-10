package server.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
import commons.Subtask;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
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
import server.services.CardService;
import server.services.SubtaskService;

@RunWith(MockitoJUnitRunner.class)
class SubtaskControllerTest {

    @Mock
    private SubtaskService subtaskServiceMock;
    @Mock
    private CardService cardServiceMock;
    @Mock
    private SimpMessagingTemplate templateMock;
    @InjectMocks
    private SubtaskController subtaskControllerMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMany() {
        List<Subtask> subtasks = new ArrayList<>();
        Subtask subtask1 = new Subtask();
        Subtask subtask2 = new Subtask();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        when(subtaskServiceMock.getMany()).thenReturn(subtasks);

        List<Subtask> returnedTasks = subtaskControllerMock.getMany();
        assertEquals(subtask1, returnedTasks.get(0));
        assertEquals(subtask2, returnedTasks.get(1));

    }

    @Test
    void getOne() {
        Long taskId = 1L;
        Subtask subtask = new Subtask();
        subtask.setId(taskId);
        when(subtaskServiceMock.getOne(taskId)).thenReturn(Optional.of(subtask));

        Subtask returnedTask = subtaskControllerMock.getOne(taskId).getBody();
        assertEquals(subtask, returnedTask);
    }

    @Test
    public void getOneSubtaskNotPresent() {
        when(subtaskServiceMock.getOne(1L)).thenReturn(Optional.empty());

        ResponseEntity<Subtask> response = subtaskControllerMock.getOne(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(subtaskServiceMock).getOne(1L);
    }

    @Test
    void createOne() {
        Subtask task = new Subtask();

        when(subtaskServiceMock.createOne(task)).thenReturn(task);

        Subtask returnedSubtask = subtaskControllerMock.createOne(task).getBody();
        assertEquals(task, returnedSubtask);
    }

    @Test
    public void createOneException() {
        Subtask subtask = new Subtask();
        doThrow(new RuntimeException()).when(subtaskServiceMock).createOne(subtask);

        ResponseEntity<Subtask> response = subtaskControllerMock.createOne(subtask);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteOne() {
        Subtask s = new Subtask("Subt", null, false);
        Card c = new Card("My cards", "Desc", null, 1);

        s.setId(1L);
        c.setId(1L);

        c.addSubtask(s);

        when(subtaskServiceMock.getOne(1L)).thenReturn(Optional.of(s));
        when(cardServiceMock.getOne(1L)).thenReturn(Optional.of(c));

        subtaskControllerMock.deleteOne(1L);

        verify(subtaskServiceMock).deleteOne(1L);
    }

    @Test
    public void deleteOneException() {
        Long invalidId = 12345L;
        doThrow(EntityNotFoundException.class).when(subtaskServiceMock).deleteOne(invalidId);
        var result = subtaskControllerMock.deleteOne(invalidId);
        assertTrue(result.getStatusCode().isError());
    }

    @Test
    void updateOne() {
        Subtask subtask = new Subtask();
        subtask.setId(1L);
        subtask.setTitle("title1");

        Subtask updatedSubtask = new Subtask();
        updatedSubtask.setTitle("title2");

        when(subtaskServiceMock.updateOne(1L, subtask)).thenReturn(updatedSubtask);

        Subtask returnedSubtask = subtaskControllerMock.updateOne(1L, subtask).getBody();
        assertEquals(updatedSubtask, returnedSubtask);

    }

    @Test
    public void updateOneException() {
        Long invalidId = 123L;
        Subtask subtask = new Subtask();
        when(subtaskServiceMock.updateOne(invalidId, subtask)).thenThrow(
                new EntityNotFoundException());

        ResponseEntity<Subtask> result = subtaskControllerMock.updateOne(invalidId, subtask);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}

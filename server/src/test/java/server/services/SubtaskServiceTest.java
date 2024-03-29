package server.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import commons.Card;
import commons.Subtask;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import server.database.CardRepository;
import server.database.SubtaskRepository;

@RunWith(MockitoJUnitRunner.class)
class SubtaskServiceTest {

    @Mock
    private SubtaskRepository subtaskRepositoryMock;

    @Mock
    private CardRepository cardRepositoryMock;

    @InjectMocks
    private SubtaskService subtaskService;

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

        when(subtaskRepositoryMock.findAll()).thenReturn(subtasks);

        List<Subtask> returnedTasks = subtaskService.getMany();
        assertEquals(subtask1, returnedTasks.get(0));
        assertEquals(subtask2, returnedTasks.get(1));

    }

    @Test
    void getOne() {
        Long taskId = 1L;
        Subtask subtask = new Subtask();
        subtask.setId(taskId);
        when(subtaskRepositoryMock.findById(taskId)).thenReturn(Optional.of(subtask));

        Optional<Subtask> returnedTask = subtaskService.getOne(taskId);
        assertEquals(subtask, returnedTask.get());
    }

    @Test
    void createOne() {
        Card card = new Card();
        card.setId(1L);
        Subtask task = new Subtask("title", card, false);

        when(subtaskRepositoryMock.save(task)).thenReturn(task);
        when(cardRepositoryMock.findById(card.getId())).thenReturn(Optional.of(card));

        Subtask returnedSubtask = subtaskService.createOne(task);
        assertEquals(task, returnedSubtask);
    }

    @Test
    void createOneWithData() {
        Card card = new Card();
        card.setId(1L);

        Subtask subtask1 = new Subtask();
        subtask1.setId(1L);
        subtask1.setCard(card);
        subtask1.setPositionInCard(0L);

        Subtask subtask2 = new Subtask();
        subtask2.setId(2L);
        subtask2.setCard(card);
        subtask2.setPositionInCard(1L);

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        card.setSubtasks(subtasks);

        Subtask subtask = new Subtask();
        subtask.setCard(card);

        when(subtaskRepositoryMock.save(subtask)).thenReturn(subtask);
        when(cardRepositoryMock.findById(card.getId())).thenReturn(Optional.of(card));

        Subtask returnedSubtask = subtaskService.createOne(subtask);

        assertEquals(subtask, returnedSubtask);
    }

    @Test
    void createOneWithInvalidCard() {
        Card card = new Card();
        card.setId(1L);
        Subtask task = new Subtask("title", card, false);

        when(subtaskRepositoryMock.save(task)).thenReturn(task);
        when(cardRepositoryMock.findById(card.getId())).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> subtaskService.createOne(task));
    }

    @Test
    void createOneInCardWithOtherSubtasks() {
        Card card = new Card();
        card.setId(1L);
        Subtask otherSubtask = new Subtask("title", card, false);
        otherSubtask.setPositionInCard(0L);
        card.getSubtasks().add(otherSubtask);
        Subtask task = new Subtask("title", card, false);


        when(subtaskRepositoryMock.save(task)).thenReturn(task);
        when(cardRepositoryMock.findById(card.getId())).thenReturn(Optional.of(card));

        Subtask newTask = new Subtask("title", card, false);
        newTask.setPositionInCard(1L);

        task = subtaskService.createOne(task);

        assertEquals(task, newTask);
    }

    @Test
    void deleteOne() {
        when(subtaskRepositoryMock.existsById(1L)).thenReturn(true);

        subtaskService.deleteOne(1L);

        verify(subtaskRepositoryMock).deleteById(1L);
    }

    @Test
    void updateOne() {
        Subtask subtask = new Subtask();
        subtask.setId(1L);
        subtask.setTitle("title1");

        when(subtaskRepositoryMock.findById(1L)).thenReturn(Optional.of(subtask));
        when(subtaskRepositoryMock.save(subtask)).thenReturn(subtask);

        Subtask updatedSubtask = new Subtask();
        updatedSubtask.setTitle("title2");
        Subtask returnedSubtask = subtaskService.updateOne(1L, updatedSubtask);
        assertEquals(updatedSubtask.getTitle(), returnedSubtask.getTitle());

    }

    @Test
    void deleteMany() {
        Subtask subtask1 = new Subtask();
        Subtask subtask2 = new Subtask();

        List<Subtask> subtasks = new ArrayList<>();

        subtaskRepositoryMock.saveAll(List.of(subtask1, subtask2));

        when(subtaskRepositoryMock.findAll()).thenReturn(subtasks);

        subtaskService.deleteMany();

        verify(subtaskRepositoryMock).deleteAll();

        Assertions.assertEquals(0, subtaskRepositoryMock.count());
    }
}

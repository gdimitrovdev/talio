package server.services;

import commons.Subtask;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.SubtaskRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public List<Subtask> getMany() {
        return subtaskRepository.findAll();
    }

    public ResponseEntity<Subtask> getOne(Long id) {
        if (id < 0 || !subtaskRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(subtaskRepository.findById(id).get());
    }

    public ResponseEntity<Subtask> createOne(Subtask subtask) {
        Subtask newSubtask = subtaskRepository.save(subtask);
        return ResponseEntity.ok(newSubtask);
    }

    public void deleteOne(Long id) {
        subtaskRepository.deleteById(id);
    }

    public Subtask update(Subtask subtask)
    {
        Subtask existingSubtask = subtaskRepository.findById(subtask.getId())
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));
        existingSubtask.setTitle(subtask.getTitle());
        existingSubtask.setCard(subtask.getCard());

        return subtaskRepository.save(existingSubtask);
    }

}

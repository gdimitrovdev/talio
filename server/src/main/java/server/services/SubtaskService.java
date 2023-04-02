package server.services;

import commons.Subtask;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.SubtaskRepository;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;

    public SubtaskService(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public List<Subtask> getMany() {
        return subtaskRepository.findAll();
    }

    public Optional<Subtask> getOne(Long id) {
        return subtaskRepository.findById(id);
    }

    public Subtask createOne(Subtask subtask) {
        Subtask newSubtask = subtaskRepository.save(subtask);
        return newSubtask;
    }

    public void deleteOne(Long id) {
        if (subtaskRepository.existsById(id)) {
            subtaskRepository.deleteById(id);
        }
    }

    public Subtask updateOne(Long id, Subtask subtask) throws EntityNotFoundException {
        Subtask existingSubtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));

        existingSubtask.setTitle(subtask.getTitle());
        existingSubtask.setCompleted(subtask.getCompleted());

        return subtaskRepository.save(existingSubtask);
    }

}

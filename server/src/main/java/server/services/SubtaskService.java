package server.services;

import commons.Card;
import commons.Subtask;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.CardRepository;
import server.database.SubtaskRepository;

@Service
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final CardRepository cardRepository;

    public SubtaskService(SubtaskRepository subtaskRepository, CardRepository cardRepository) {
        this.subtaskRepository = subtaskRepository;
        this.cardRepository = cardRepository;
    }

    public List<Subtask> getMany() {
        return subtaskRepository.findAll();
    }

    public Optional<Subtask> getOne(Long id) {
        return subtaskRepository.findById(id);
    }

    /**
     * Ignores the subtask's position in the card and places it at the end
     *
     * @param subtask
     * @return
     */
    public Subtask createOne(Subtask subtask) {
        subtask.setPositionInCard(0L);

        if (subtask.getCard() != null) {
            Card card = cardRepository.findById(subtask.getCard().getId()).get();

            if (card.getSubtasks().size() == 0) {
                subtask.setPositionInCard(0L);
            } else {
                subtask.setPositionInCard(
                        card.getSubtasks().stream().max(Comparator.comparing(
                                Subtask::getPositionInCard
                        )).get().getPositionInCard() + 1L
                );
            }

            card.addSubtask(subtask);

            subtask = subtaskRepository.save(subtask);
        }

        return subtask;
    }

    public void deleteOne(Long id) {
        if (subtaskRepository.existsById(id)) {
            subtaskRepository.deleteById(id);
        }
    }

    /**
     * Ignores a subtask's card
     *
     * @param id
     * @param subtask
     * @return
     * @throws EntityNotFoundException
     */
    public Subtask updateOne(Long id, Subtask subtask) throws EntityNotFoundException {
        Subtask existingSubtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found"));
        existingSubtask.setTitle(subtask.getTitle());
        existingSubtask.setCompleted(subtask.getCompleted());
        existingSubtask.setPositionInCard(subtask.getPositionInCard());
        return subtaskRepository.save(existingSubtask);
    }

    public void deleteMany() {
        subtaskRepository.deleteAll();
    }
}

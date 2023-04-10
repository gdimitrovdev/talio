package server.services;

import commons.Board;
import commons.Tag;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final BoardRepository boardRepository;

    @Inject
    public TagService(TagRepository tagRepository, BoardRepository boardRepository) {
        this.tagRepository = tagRepository;
        this.boardRepository = boardRepository;
    }

    public List<Tag> getMany() {
        return tagRepository.findAll();
    }

    public Optional<Tag> getOne(Long id) {
        return tagRepository.findById(id);
    }

    /**
     * Ignores list of cards
     *
     * @param tag
     * @return
     */
    public Tag createOne(Tag tag) {
        tag.getCards().clear();
        tag.setBoard(boardRepository.findById(tag.getBoard().getId())
                .orElseThrow(() -> new EntityNotFoundException("Tag not found")));
        return tagRepository.save(tag);
    }

    /**
     * Removes the tag from it's board and deletes it
     *
     * @param id
     */
    public void deleteOne(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Tag not found"));
        Board b = tag.getBoard();
        b.removeTag(tag);
        boardRepository.save(b);
        tagRepository.deleteById(id);
    }

    /**
     * Ignores list of cards and board
     *
     * @param id
     * @param tag
     * @return
     * @throws EntityNotFoundException
     */
    public Tag updateOne(Long id, Tag tag) throws EntityNotFoundException {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        existingTag.setTitle(tag.getTitle());
        existingTag.setColor(tag.getColor());
        return tagRepository.save(existingTag);
    }

    public void deleteMany() {
        tagRepository.deleteAll();
    }
}

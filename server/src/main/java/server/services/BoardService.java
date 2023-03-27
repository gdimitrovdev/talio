package server.services;

import commons.Board;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getMany() {
        return boardRepository.findAll();
    }

    public Optional<Board> getOne(Long id) {
        return boardRepository.findById(id);
    }

    public Optional<Board> getOneByCode(String code) {
        return boardRepository.findByCode(code);
    }

    public Board createOne(Board board) {
        Board newBoard = boardRepository.save(board);
        return newBoard;
    }

    public void deleteOne(Long id) {
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
        }
    }

    public Board updateOne(Long id, Board board) throws EntityNotFoundException {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        existingBoard.setColor(board.getColor());
        existingBoard.setName(board.getName());
        existingBoard.setCode(board.getCode());
        existingBoard.setReadOnlyCode(board.getReadOnlyCode());
        existingBoard.setLists(board.getLists());
        existingBoard.setTags(board.getTags());

        return boardRepository.save(existingBoard);
    }

}

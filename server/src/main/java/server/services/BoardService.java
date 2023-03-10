package server.services;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> getMany() {
        return boardRepository.findAll();
    }

    public ResponseEntity<Board> getOne(Long id) {
        if(id < 0 || !boardRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(boardRepository.findById(id).get());
    }

    public ResponseEntity<Board> createOne(Board board) {
        Board newBoard = boardRepository.save(board);
        return ResponseEntity.ok(newBoard);
    }

    public void deleteOne(Long id) {
        boardRepository.deleteById(id);
    }

    public Board update(Board board)
    {
        Board existingboard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        existingboard.setReadOnly(board.isReadOnly());
        existingboard.setColor(board.getColor());
        existingboard.setName(board.getName());
        existingboard.setPassword(board.getPassword());
        existingboard.setHash(board.getHash());
        existingboard.setLists(board.getLists());
        existingboard.setTags(board.getTags());

        return boardRepository.save(existingboard);
    }

}

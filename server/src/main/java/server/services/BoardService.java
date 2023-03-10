package server.services;

import commons.Board;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

import java.util.List;

@Service
public class BoardService {

    private BoardRepository boardRepository;

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

}

package server.services;

import commons.Board;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MessageDigest messageDigest;
    private final Random random;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;

        try {
            this.messageDigest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // never thrown
        }

        this.random = new Random();
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
        updateBoardCodes(board);
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
        //        if (isCodeAlreadyUsed(board.getCode())) {
        //            throw new IllegalArgumentException("This code is already used by a board");
        //        }
        //        if (isCodeAlreadyUsed(board.getReadOnlyCode())) {
        //            throw new IllegalArgumentException("This read only code is already used by a board");
        //        }
        //        if (board.getCode().contentEquals(board.getReadOnlyCode())) {
        //            throw new IllegalArgumentException("Code and readOnlyCode must be different");
        //        }

        existingBoard.setBoardColor(board.getBoardColor());
        existingBoard.setListsColor(board.getListsColor());
        existingBoard.setName(board.getName());
        existingBoard.setCode(board.getCode());
        existingBoard.setReadOnlyCode(board.getReadOnlyCode());

        return boardRepository.save(existingBoard);
    }

    public boolean isCodeAlreadyUsed(String code) {
        return getOneByCode(code).isPresent();
    }

    public void updateBoardCodes(Board board) {
        String code = null;
        do {
            code = getNewRandomCode(5);
        } while (isCodeAlreadyUsed(code));

        String readOnlyCode = null;
        do {
            readOnlyCode = getNewRandomCode(5);
        } while (isCodeAlreadyUsed(readOnlyCode) || readOnlyCode.contentEquals(code));

        board.setCode(code);
        board.setReadOnlyCode(readOnlyCode);
    }

    public String getNewRandomCode(int length) {
        String randomNumberStr = random.nextInt() + "";
        messageDigest.update(randomNumberStr.getBytes());

        String hash = DatatypeConverter.printHexBinary(messageDigest.digest());
        return hash.substring(0, length);
    }

    public void deleteMany() {
        boardRepository.deleteAll();
    }
}

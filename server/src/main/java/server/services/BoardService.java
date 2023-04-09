package server.services;

import commons.Board;
import commons.Card;
import commons.CardList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;
import server.database.BoardRepository;
import server.database.CardRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final MessageDigest messageDigest;
    private final Random random;

    public BoardService(BoardRepository boardRepository, CardRepository cardRepository) {
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;

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
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return boardRepository.findById(id);
    }

    public Optional<Board> getOneByCode(String code) {
        return boardRepository.findByCode(code);
    }

    /**
     * Ignores codes, lists and tags
     *
     * @param board
     * @return
     */
    public Board createOne(Board board) {
        board.setTags(new ArrayList<>());
        board.setLists(new ArrayList<>());
        updateBoardCodes(board);
        return boardRepository.save(board);
    }

    public void deleteOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
        }
    }

    /**
     * Ignores lists and tags
     *
     * @param id
     * @param board
     * @return
     * @throws EntityNotFoundException
     */
    public Board updateOne(Long id, Board board) throws EntityNotFoundException {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));
        /* (isCodeAlreadyUsed(board.getCode())) {
            throw new IllegalArgumentException("This code is already used by a board");
        }
        if (isCodeAlreadyUsed(board.getReadOnlyCode())) {
            throw new IllegalArgumentException("This read only code is already used by a board");
        }
        if (board.getCode().contentEquals(board.getReadOnlyCode())) {
            throw new IllegalArgumentException("Code and readOnlyCode must be different");
        }*/
        for (CardList cardList : existingBoard.getLists()) {
            for (Card card : cardList.getCards()) {
                // If the card's color scheme has been deleted (and it's not the default one),
                // set it to the default one
                if (!card.getColorPresetNumber().equals(-1) && board.getCardColorPresets()
                        .get(card.getColorPresetNumber()).equals("")) {
                    Card newCard = cardRepository.findById(card.getId()).get();
                    newCard.setColorPresetNumber(-1);
                    cardRepository.saveAndFlush(newCard);
                }
            }
        }

        existingBoard.setBoardColor(board.getBoardColor());
        existingBoard.setListsColor(board.getListsColor());
        existingBoard.setName(board.getName());
        existingBoard.setCode(board.getCode());
        existingBoard.setReadOnlyCode(board.getReadOnlyCode());
        existingBoard.setDefaultPresetNum(board.getDefaultPresetNum());
        existingBoard.setCardColorPresets(board.getCardColorPresets());

        return boardRepository.save(existingBoard);
    }

    public boolean isCodeAlreadyUsed(String code) {
        return getOneByCode(code).isPresent();
    }

    public void updateBoardCodes(Board board) {
        String code;
        do {
            code = getNewRandomCode(5);
        } while (isCodeAlreadyUsed(code));

        String readOnlyCode;
        do {
            readOnlyCode = getNewRandomCode(5);
        } while (isCodeAlreadyUsed(readOnlyCode) || readOnlyCode.contentEquals(code));

        board.setCode(code);
        board.setReadOnlyCode(readOnlyCode);
    }

    public String getNewRandomCode(int length) {
        String randomNumberStr = "%d".formatted(random.nextInt());
        messageDigest.update(randomNumberStr.getBytes());

        String hash = DatatypeConverter.printHexBinary(messageDigest.digest());
        return hash.substring(0, length);
    }

    public void deleteMany() {
        boardRepository.deleteAll();
    }
}

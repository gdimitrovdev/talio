//package client.utils;
//import commons.Board;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//
//
//class ServerUtilsTest {
//
//    ServerUtils serverUtils;
//    @BeforeEach
//    void init(){
//        serverUtils = new ServerUtils();
//    }
//    @Test
//    void testCreateBoard() {
//        // create a Board object to use as input
//        Board inputBoard = new Board(true, "B1", "123", "456978", "red");
//
//        // call the method and get the output Board
//        Board outputBoard = serverUtils.createBoard(inputBoard);
//
//        // assert that the output Board is not null
//        assertNotNull(outputBoard);
//
//        // assert that the output Board has the expected properties
//        assertEquals(inputBoard.getHash(), outputBoard.getHash());
//        assertEquals(inputBoard.getId(), outputBoard.getId());
//        // add more assertions as needed
//    }
//
//
//
//    @Test
//    void retrieveBoard() {
//
//    }
//}
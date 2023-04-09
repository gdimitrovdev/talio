/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Subtask;
import commons.Tag;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {
    private String serverUrl;
    private String restServerUrl;
    private String websocketServerUrl;
    private Long subscribedBoard = null;
    private StompSession session;
    private Map<Object, UpdateEvent> updateEvents = new HashMap<>();
    private WebTarget webTarget;
    private Map<Long, Board> cachedBoards = new HashMap<>();
    private Map<Long, CardList> cachedLists = new HashMap<>();
    private Map<Long, Card> cachedCards = new HashMap<>();
    private Map<Long, Subtask> cachedSubtasks = new HashMap<>();
    private Map<Long, Tag> cachedTags = new HashMap<>();

    private class UpdateEvent<T> implements Consumer<T> {
        public final Class<T> type;
        public Consumer<T> consumer;

        public UpdateEvent(Class<T> type, Consumer<T> consumer) {
            this.consumer = consumer;
            this.type = type;
        }

        public void accept(T t) {
            consumer.accept(t);
        }
    }

    public ServerUtils(String serverURL) {
        setServerUrl(serverURL);
    }

    public ServerUtils() {
        setServerUrl("localhost:8080");
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public boolean setServerUrl(String serverUrl) {
        if (serverUrl.startsWith("http://")) {
            serverUrl = serverUrl.substring(7);
        } else if (serverUrl.startsWith("https://")) {
            serverUrl = serverUrl.substring(8);
        }
        try {
            unsubscribeFromBoard();
            WebTarget webTarget =
                    ClientBuilder.newClient(new ClientConfig()).target("http://" + serverUrl);
            var res = webTarget.path("/test-connection")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get();
            if (res.getStatus() == 200) {
                this.serverUrl = serverUrl;
                this.webTarget = webTarget;
                this.restServerUrl = "http://" + serverUrl;
                this.websocketServerUrl = "ws://" + serverUrl;
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getSubscribedBoardId() {
        return subscribedBoard;
    }

    /**
     * !Does not guarantee (yet) that the updates received will be only about the subscribed board!
     * Tells the server to send updates about a particular board to ServerUtils.
     * Use addUpdateEvent and removeUpdateEvent to react to particular updates.
     * You can only subscribe to one board at a time (unless you are using multilple ServerUtils).
     * Subscribing to the same board that you are currently subscribed to, will not do anything.
     * Subscribing to another board remove all update events.
     *
     * @param id The id of the board you want to subscribe to.
     */
    public void subscribeToBoard(Long id) throws ConnectException {
        if (Objects.equals(id, subscribedBoard)) {
            return;
        }
        try {
            unsubscribeFromBoard();
            subscribedBoard = id;
            connectToServerUsingSTOMPWebSockets();
        } catch (Exception e) {
            throw new ConnectException("Exception while trying to subscribe to board: " + id + "\n"
                    + e.getMessage()
            );
        }
    }

    public void unsubscribeFromBoard() throws ConnectException {
        if (subscribedBoard == null) {
            return;
        }
        try {
            session.disconnect();
            updateEvents.clear();
        } catch (Exception e) {
            throw new ConnectException(
                    "Exception while trying to unsubscribe from board: " + subscribedBoard + "\n"
                            + e.getMessage()
            );
        }
    }

    public boolean isSubscribedToBoard() {
        return subscribedBoard != null;
    }

    private void connectToServerUsingSTOMPWebSockets() throws ConnectException {
        var stomp = new WebSocketStompClient(new StandardWebSocketClient());
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            var session = stomp.connect(websocketServerUrl + "/websocket",
                    new StompSessionHandlerAdapter() {
                    }
            ).get();
            this.session = session;
            System.out.println("created session");
            /*
            var classToTopic = Map.of(
                    commons.Board.class, "boards",
                    commons.Card.class, "cards",
                    commons.CardList.class, "lists",
                    commons.Tag.class, "tags",
                    commons.Subtask.class, "subtasks"
            );

            for (var type : classToTopic.keySet()) {
                registerForMessages("/topic/" + classToTopic.get(type), type, (o) -> {
                    System.out.println(
                            "received websocket from: " + "/topic/" + classToTopic.get(type));
                    System.out.println(updateEvents);
                    System.out.println(updateEvents.keySet());
                    for (var key : updateEvents.keySet()) {
                        var updateEvent = updateEvents.get(key);
                        System.out.println(updateEvent.type + " " + type);
                        if (updateEvent.type.equals(type)) {
                            System.out.println();
                            updateEvent.accept(o);
                            System.out.println("After consumer accept");
                        }
                    }
                });
            }
            */
        } catch (Exception e) {
            throw new ConnectException(
                    "Exception while trying to create a STOMP WebSocket Session:\n"
                            + e.getMessage()
            );
        }
    }

    public <T> Object addUpdateEvent(Class<T> type, Consumer<T> consumer) {
        var key = new Object();
        updateEvents.put(key, new UpdateEvent(type, consumer));
        return key;
    }

    public void removeUpdateEvent(Object key) {
        if (key == null) {
            return;
        }
        updateEvents.remove(key);
    }

    public <T> void registerForMessages(String destination, Class<T> type, Consumer<T> consumer) {
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                crawlCurrentBoardAndRecache();
                consumer.accept((T) payload);
            }
        });

    }

    private synchronized void invalidateAllCaches() {
        cachedBoards.clear();
        cachedCards.clear();
        cachedLists.clear();
        cachedSubtasks.clear();
        cachedTags.clear();
    }

    /**
     * Invalidates all caches, gets the subscribed board from the server and populates all of the
     * caches with objects from just the board
     */
    private synchronized void crawlCurrentBoardAndRecache() {
        if (!isSubscribedToBoard()) {
            return;
            /*throw new RuntimeException("You need to be subscribed to a board, to crawl the "
                    + "current board");*/
        }
        invalidateAllCaches();
        Board board = webTarget.path("api").path("boards").path(getSubscribedBoardId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Board>() {
                });
        cachedBoards.put(board.getId(), board);
        for (CardList list : board.getLists()) {
            cachedLists.put(list.getId(), list);
            for (Card card : list.getCards()) {
                cachedCards.put(card.getId(), card);
                for (Subtask subtask : card.getSubtasks()) {
                    cachedSubtasks.put(subtask.getId(), subtask);
                }
            }
        }
        for (Tag tag : board.getTags()) {
            cachedTags.put(tag.getId(), tag);
        }
    }

    public Tag getTag(Long tagId) {
        if (cachedTags.containsKey(tagId)) {
            return cachedTags.get(tagId);
        }
        crawlCurrentBoardAndRecache();
        if (cachedTags.containsKey(tagId)) {
            return cachedTags.get(tagId);
        }
        System.out.println("Uncached tag access");
        return webTarget.path("api/tags/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Tag>() {
                });
    }

    /**
     * Ignores list of cards
     *
     * @param tag
     * @return
     */
    public Tag createTag(Tag tag) {
        Tag newTag = new Tag();
        Board board = new Board();
        board.setId(tag.getBoard().getId());
        newTag.setBoard(board);
        newTag.setCards(new ArrayList<>());
        newTag.setTitle(tag.getTitle());
        newTag.setColor(tag.getColor());
        return webTarget.path("api/tags")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newTag, APPLICATION_JSON), Tag.class);
    }

    /**
     * Ignores list of cards and board
     *
     * @param tag
     * @return
     */
    public Tag updateTag(Tag tag) {
        Tag newTag = new Tag();
        newTag.setBoard(null);
        newTag.setCards(new ArrayList<>());
        newTag.setTitle(tag.getTitle());
        newTag.setColor(tag.getColor());
        newTag.setId(tag.getId());
        return webTarget.path("api").path("tags").path(newTag.getId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newTag, APPLICATION_JSON), Tag.class);
    }

    public void deleteTag(Long tagId) {
        webTarget.path("api").path("tags").path(tagId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Subtask getSubtask(Long subtaskId) {
        if (cachedSubtasks.containsKey(subtaskId)) {
            return cachedSubtasks.get(subtaskId);
        }
        crawlCurrentBoardAndRecache();
        if (cachedSubtasks.containsKey(subtaskId)) {
            return cachedSubtasks.get(subtaskId);
        }
        System.out.println("Uncached subtask access");
        return webTarget.path("api/subtasks/" + subtaskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Subtask>() {
                });
    }

    /**
     * Ignores the position of a subtask in a card, it's always added last
     *
     * @param subtask
     */
    public Subtask createSubtask(Subtask subtask) {
        Card card = new Card();
        card.setId(subtask.getCard().getId());
        Subtask newSubtask = new Subtask(subtask.getTitle(), card, subtask.getCompleted());
        newSubtask.setPositionInCard(null);
        return webTarget.path("api").path("subtasks")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newSubtask, APPLICATION_JSON), Subtask.class);
    }

    /**
     * Ignores a subtask's card
     *
     * @param subtask
     * @return
     */
    public Subtask updateSubtask(Subtask subtask) {
        Subtask newSubtask = new Subtask(subtask.getTitle(), null, subtask.getCompleted());
        newSubtask.setPositionInCard(subtask.getPositionInCard());
        newSubtask.setId(subtask.getId());
        return webTarget.path("api").path("subtasks").path(newSubtask.getId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newSubtask, APPLICATION_JSON), Subtask.class);
    }

    public void deleteSubtask(Long subtaskId) {
        webTarget.path("api").path("subtasks").path(subtaskId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public CardList getCardList(Long listId) {
        try {
            if (cachedLists.containsKey(listId)) {
                return cachedLists.get(listId);
            }
            crawlCurrentBoardAndRecache();
            if (cachedLists.containsKey(listId)) {
                return cachedLists.get(listId);
            }
            System.out.println("Uncached list access");
            return webTarget.path("api/lists/" + listId)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println("Error in getCardList(" + listId + "):\n" + e);
            throw new RuntimeException();
        }

    }

    /**
     * Ignores a list's list of cards
     *
     * @param list
     * @return
     */
    public CardList createCardList(CardList list) {
        Board board = new Board();
        board.setId(list.getBoard().getId());
        list.setBoard(board);
        CardList newList = new CardList(list.getTitle(), board);
        newList.setCards(null);
        return webTarget.path("api").path("lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newList, APPLICATION_JSON), CardList.class);
    }

    /**
     * Ignores board and list of cards
     *
     * @param list
     * @return
     */
    public CardList updateCardList(CardList list) {
        CardList newList = new CardList(list.getTitle(), null);
        newList.setCards(null);
        newList.setId(list.getId());
        return webTarget.path("api").path("lists").path(newList.getId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newList, APPLICATION_JSON), CardList.class);
    }

    public Board deleteCardList(Long listId) {
        return webTarget.path("api").path("lists").path(listId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<Board>() {
                });
    }

    public Board getBoard(Long boardId) {
        if (cachedBoards.containsKey(boardId)) {
            return cachedBoards.get(boardId);
        }
        crawlCurrentBoardAndRecache();
        if (cachedBoards.containsKey(boardId)) {
            return cachedBoards.get(boardId);
        }
        System.out.println("Uncached board access");
        //RestTemplate restTemplate = new RestTemplate();
        //return restTemplate.getForObject(restServerUrl + "/api/boards/" + boardId, Board.class);
        return webTarget.path("api").path("boards").path(boardId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Board>() {
                });
    }

    /**
     * Ignores codes, lists and tags
     *
     * @param board
     * @return
     */
    public Board createBoard(Board board) {
        Board newBoard = new Board(board.getName(), board.getCode(), board.getReadOnlyCode(),
                board.getBoardColor(), board.getListsColor(), null, null,
                board.getCardColorPresets(), board.getDefaultPresetNum());
        return webTarget.path("api").path("boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newBoard, APPLICATION_JSON), Board.class);
    }

    /**
     * Ignores lists and tags
     *
     * @param board
     * @return
     */
    public Board updateBoard(Board board) {
        Board newBoard = new Board(board.getName(), board.getCode(), board.getReadOnlyCode(),
                board.getBoardColor(), board.getListsColor(), null, null,
                board.getCardColorPresets(), board.getDefaultPresetNum());
        newBoard.setId(board.getId());
        return webTarget.path("api").path("boards").path(newBoard.getId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newBoard, APPLICATION_JSON), Board.class);
    }

    public void deleteBoard(Long boardId) {
        webTarget.path("api").path("boards").path(boardId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Board joinBoard(String code) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(restServerUrl + "/api/boards/by-code/" + code,
                    Board.class);
        } catch (RuntimeException e) {
            return new Board("NotFoundInSystem", "", "", "", "", null, 0);
        }

    }

    public Card getCard(Long cardId) {
        if (cachedCards.containsKey(cardId)) {
            return cachedCards.get(cardId);
        }
        crawlCurrentBoardAndRecache();
        if (cachedCards.containsKey(cardId)) {
            return cachedCards.get(cardId);
        }
        System.out.println("Uncached card access");
        return webTarget.path("api/cards/" + cardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Card>() {
                });
    }

    /**
     * Ignores tags
     *
     * @param card
     */
    public void createCard(Card card) {
        CardList list = new CardList();
        list.setId(card.getList().getId());
        Card newCard = new Card(card.getTitle(), card.getDescription(),
                card.getColorPresetNumber(), list, null, null);
        webTarget.path("api").path("cards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(newCard, APPLICATION_JSON));
    }

    /**
     * Ignores list and tags
     *
     * @param card
     */
    public void updateCard(Card card) {
        Card newCard = new Card(card.getTitle(), card.getDescription(),
                card.getColorPresetNumber(), null, null, null);
        newCard.setId(card.getId());
        webTarget.path("api").path("cards").path(newCard.getId().toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(newCard, APPLICATION_JSON));
    }

    public void deleteCard(Long cardId) {
        webTarget.path("api").path("cards").path(cardId.toString())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public void moveCardToListLast(Long cardId, Long newListId) {
        Long oldListId = this.getCard(cardId).getList().getId();
        try {
            webTarget.path("/api/cards/move-to-list-last/" + cardId + "/" + newListId)
                    .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println("The expected error");
        }
        webTarget.path("/api/lists/refresh-list/" + oldListId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get(new GenericType<CardList>() {
                });
        try {
            webTarget.path("/api/cards/move-to-list-last/" + cardId + "/" + newListId)
                    .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println(
                    "Error on moveCardToListLast(" + cardId + ", " + newListId + "):\n" + e);
        }
    }

    public void moveCardToListAfterCard(Long cardId, Long newListId, Long cardAfterId) {
        Long oldListId = this.getCard(cardId).getList().getId();
        try {
            webTarget.path("/api/cards/move-to-list-after-card/" + cardId + "/" + newListId
                            + "/" + cardAfterId)
                    .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println("The expected error");
        }
        webTarget.path("/api/lists/refresh-list/" + oldListId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get(new GenericType<CardList>() {
                });
        try {
            webTarget.path("/api/cards/move-to-list-after-card/" + cardId + "/" + newListId
                            + "/" + cardAfterId)
                    .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println("Error on moveCardToListAfterCard(" + cardId + ", " + newListId
                    + ", " + cardAfterId + "):\n" + e);
        }
    }

    public void addTagToCard(Long cardId, Long tagId) {
        webTarget.path("/api/cards/add-tag-to-card/" + cardId + "/" + tagId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get();
    }

    public void removeTagFromCard(Long cardId, Long tagId) {
        webTarget.path("/api/cards/remove-tag-from-card/" + cardId + "/" + tagId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get();
    }

    public void moveUp(Subtask subtask) {
        Optional<Subtask> oneUp = subtask.getCard().getSubtasks().stream()
                .filter(s -> (s.getPositionInCard() < subtask.getPositionInCard()))
                .max(Comparator.comparing(Subtask::getPositionInCard));

        swapSubtasks(subtask, oneUp);
    }

    public void moveDown(Subtask subtask) {
        Optional<Subtask> oneDown = subtask.getCard().getSubtasks().stream()
                .filter(s -> (s.getPositionInCard() > subtask.getPositionInCard()))
                .min(Comparator.comparing(Subtask::getPositionInCard));

        swapSubtasks(subtask, oneDown);
    }

    private void swapSubtasks(Subtask subtask, Optional<Subtask> otherSubtask) {
        if (otherSubtask.isPresent()) {
            Subtask anotherSubtask = otherSubtask.get();

            Long temp = anotherSubtask.getPositionInCard();

            Subtask newOtherSubtask = new Subtask(anotherSubtask.getTitle(),
                    anotherSubtask.getCard(), anotherSubtask.getCompleted());
            newOtherSubtask.setId(anotherSubtask.getId());
            newOtherSubtask.setPositionInCard(subtask.getPositionInCard());

            Subtask newSubtask = new Subtask(subtask.getTitle(), subtask.getCard(),
                    subtask.getCompleted());
            newSubtask.setId(subtask.getId());
            newSubtask.setPositionInCard(temp);

            updateSubtask(newSubtask);
            updateSubtask(newOtherSubtask);
        }
    }
}

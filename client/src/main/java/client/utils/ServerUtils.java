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
import java.util.*;
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
            System.out.println(e);
            return false;
        }
    }

    public Long getSubscribedBoard() {
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
                consumer.accept((T) payload);
            }
        });

    }

    public Tag getTag(Long tagId) {
        return webTarget.path("api/tags/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Tag>() {
                });
    }

    public Tag createTag(Tag tag) {
        return webTarget.path("api/tags")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    public Tag updateTag(Tag tag) {
        return webTarget.path("api/tags/" + tag.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    public void deleteTag(Long tagId) {
        webTarget.path("/api/lists/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Subtask getSubtask(Long subtaskId) {
        return webTarget.path("api/subtasks/" + subtaskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Subtask>() {
                });
    }

    public Subtask createSubtask(Subtask subtask) {
        return webTarget.path("api/subtasks")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(subtask, APPLICATION_JSON), Subtask.class);
    }

    public Subtask updateSubtask(Subtask subtask) {
        return webTarget.path("api/subtasks/" + subtask.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(subtask, APPLICATION_JSON), Subtask.class);
    }

    public Card deleteSubtask(Long subtaskId) {
        return webTarget.path("/api/subtasks/" + subtaskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<Card>() {
                });
    }

    public CardList getCardList(Long listId) {
        try {
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

    public CardList createCardList(CardList list) {
        return webTarget.path("api/lists")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(list, APPLICATION_JSON), CardList.class);
    }

    public CardList updateCardList(CardList list) {
        return webTarget.path("api/lists/" + list.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(list, APPLICATION_JSON), CardList.class);
    }

    public Board deleteCardList(Long listId) {
        return webTarget.path("/api/lists/" + listId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<Board>() {
                });
    }

    public Board getBoard(Long boardId) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(restServerUrl + "/api/boards/" + boardId, Board.class);
        /*
        return webTarget.path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Board>() {
                });
                */
    }

    public Board createBoard(Board board) {
        return webTarget.path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    public Board updateBoard(Board board) {
        return webTarget.path("api/boards/" + board.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    public void deleteBoard(Long boardId) {
        webTarget.path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Board joinBoard(String code) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(restServerUrl + "/api/boards/by-code/" + code, Board.class);
        } catch (RuntimeException e) {
            return new Board("NotFoundInSystem", "", "", "", "", null, 0);
        }
          
    }

    public Card getCard(Long cardId) {
        return webTarget.path("api/cards/" + cardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Card>() {
                });
    }

    /**
     * Ignores list and tags
     * @param card
     * @return
     */
    public Card createCard(Card card) {
        return webTarget.path("api/cards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(card, APPLICATION_JSON), Card.class);
    }

    /**
     * Ignores list and tags
     * @param card
     * @return
     */
    public Card updateCard(Card card) {
        return webTarget.path("api/cards/" + card.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(card, APPLICATION_JSON), Card.class);
    }

    public CardList deleteCard(Long cardId) {
        return webTarget.path("/api/cards/" + cardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<CardList>() {
                });
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
                .get(new GenericType<CardList>() {});
        try {
            webTarget.path("/api/cards/move-to-list-last/" + cardId + "/" + newListId)
                    .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                    .get(new GenericType<CardList>() {
                    });
        } catch (Exception e) {
            System.out.println("Error on moveCardToListLast(" + cardId + ", " + newListId + "):\n" + e);
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
                .get(new GenericType<CardList>() {});
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

    public Card addTagToCard(Long cardId, Long tagId) {
        return webTarget.path("/api/cards/add-tag-to-card/" + cardId + "/" + tagId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get(new GenericType<Card>() {
                });
    }

    public Card removeTagFromCard(Long cardId, Long tagId) {
        return webTarget.path("/api/cards/remove-tag-from-card/" + cardId + "/" + tagId)
                .request(APPLICATION_JSON).accept(APPLICATION_JSON)
                .get(new GenericType<Card>() {
                });
    }

    public void moveUp(Subtask subtask) {
        Optional<Subtask> oneUp = subtask.getCard().getSubtasks().stream()
                .filter(s -> (s.getPositionInCard() < subtask.getPositionInCard()))
                .max(Comparator.comparing(Subtask::getPositionInCard));

        if (oneUp.isPresent()) {
            Subtask anotherSubtask = oneUp.get();

            Long temp = anotherSubtask.getPositionInCard();
            anotherSubtask.setPositionInCard(subtask.getPositionInCard());
            subtask.setPositionInCard(temp);

            updateSubtask(subtask);
            updateSubtask(anotherSubtask);
        }
    }

    public void moveDown(Subtask subtask) {
        Optional<Subtask> oneDown = subtask.getCard().getSubtasks().stream()
                .filter(s -> (s.getPositionInCard() > subtask.getPositionInCard()))
                .min(Comparator.comparing(Subtask::getPositionInCard));

        if (oneDown.isPresent()) {
            Subtask anotherSubtask = oneDown.get();

            Long temp = anotherSubtask.getPositionInCard();
            anotherSubtask.setPositionInCard(subtask.getPositionInCard());
            subtask.setPositionInCard(temp);

            updateSubtask(subtask);
            updateSubtask(anotherSubtask);
        }
    }
}

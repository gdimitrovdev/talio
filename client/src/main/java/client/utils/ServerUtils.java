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
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {
    private final String SERVER_URL;
    private final String REST_SERVER_URL;
    private final String WEBSOCKET_SERVER_URL;
    private Long subscribedBoard = null;
    private StompSession session;
    //private Map<Object, >

    /*private class UpdateEvent {
        public boolean valid = true;
        public Consumer
    }*/

    public ServerUtils(final String serverURL) {
        SERVER_URL = serverURL;
        REST_SERVER_URL = "http://" + SERVER_URL;
        WEBSOCKET_SERVER_URL = "ws://" + SERVER_URL;
    }

    public ServerUtils() {
        this.SERVER_URL = "localhost:8080";
        REST_SERVER_URL = "http://" + SERVER_URL;
        WEBSOCKET_SERVER_URL = "ws://" + SERVER_URL;
    }

    public Long getSubscribedBoard() {
        return subscribedBoard;
    }

    /**
     * Tells the server to send updates about a particular board to ServerUtils.
     * Use addUpdateEvent and removeUpdateEvent to react to particular updates.
     * You can only subscribe to one board at a time (unless you are using multilple ServerUtils).
     * Subscribing to the same board that you are currently subscribed to, will not do anything.
     * Subscribing to another board remove all update events.
     * @param id The id of the board you want to subscribe to.
     */
    public void subscribeToBoard(Long id) throws ConnectException {
        if(Objects.equals(id, subscribedBoard))
            return;
        try {
            unsubscribeFromBoard();
            session = connectToServerUsingSTOMPWebSockets();
        }
        catch(Exception e) {
            throw new ConnectException("Exception while trying to subscribe to board: " + id + "\n"
                    + e.getMessage()
            );
        }
    }

    public void unsubscribeFromBoard() throws ConnectException {
        if(subscribedBoard == null) {
            return;
        }
        try {
            session.disconnect();
        }
        catch(Exception e) {
            throw new ConnectException("Exception while trying to unsubscribe from board: " + subscribedBoard + "\n"
                    + e.getMessage()
            );
        }
    }

    public boolean isSubscribedToBoard() {
        return subscribedBoard != null;
    }

    private StompSession connectToServerUsingSTOMPWebSockets() throws ConnectException {
        var stomp = new WebSocketStompClient(new StandardWebSocketClient());
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(WEBSOCKET_SERVER_URL + "/websocket",
                    new StompSessionHandlerAdapter() {}
            ).get();
        }
        catch(Exception e) {
            throw new ConnectException("Exception while trying to create a STOMP WebSocket Session:\n"
                    + e.getMessage()
            );
        }
    }

    public <T> Object addUpdateEvent(Class<T> type, Consumer<T> consumer) {
        return new Object();
    }

    public void getQuotesTheHardWay() throws IOException {
        var url = new URL(REST_SERVER_URL + "/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(REST_SERVER_URL).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {
                });
    }

    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(REST_SERVER_URL).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
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

    //public void deleteBoardById can be substituted by public Response deleteCardById
    public void deleteBoardById(Long id) {
        ClientBuilder.newClient(new ClientConfig())
                .target(REST_SERVER_URL).path("/api/boards/"+id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Board retrieveBoard(String hash) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/boards/by-code/" + hash)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Board>() {
                });
    }

    public Board createBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/boards/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);

    }

    public boolean checkConnection(String server) {
        try {
            var res = ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("/test-connection/")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get();
            return res.getStatus() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setServer(String server) {
        ServerUtils.server = server;
    }

    public CardList addToEndOfList(CardList list, Card card) {
        // TODO implement this method
        return list;
    }

    public CardList addToListAfter(CardList list, Card cardToAdd, Card cardAfter) {
        // TODO implement this method
        return list;
    }

    public CardList removeFromList(CardList list, Card card) {
        // TODO implement this method
        return list;
    }
}

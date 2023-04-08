package commons;

/**
 * User for the destinations of websocket messages, so we don't mess up the strings
 */
public enum Topics {
    BOARDS("/topic/boards"),
    CARDS("/topic/cards"),
    LISTS("/topic/lists"),
    SUBTASKS("/topic/subtasks"),
    TAGS("/topic/tags");

    private final String str;

    Topics(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}

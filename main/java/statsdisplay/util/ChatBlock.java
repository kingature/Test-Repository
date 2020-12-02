package statsdisplay.util;

import java.util.ArrayList;
import java.util.Collections;

public class ChatBlock {
    /* ArrayList which holds each message */
    ArrayList<String> messages = new ArrayList<>();

    /* Key to identify the ChatBlock */
    private final String accessKey;

    /* Constructor */
    public ChatBlock(String accessKey) {
        this.accessKey = accessKey;
    }

    /* Adds a message to the ChatBlock */
    public void add(String message) {
        this.messages.add(message);
    }

    /* Return the accessKey */
    public String getAccessKey() {
        return this.accessKey;
    }

    /* Inverts the ArrayList */
    public void reverse() {
        Collections.reverse(messages);
    }

    /* Prints the ChatBlock in chat */
    public void print() {
        for (String message : messages) {
            Chat.msgClient(message, "");
        }
    }
}

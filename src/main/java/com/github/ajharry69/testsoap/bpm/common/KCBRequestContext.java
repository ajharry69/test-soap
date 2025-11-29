package com.github.ajharry69.testsoap.bpm.common;

public record KCBRequestContext(String conversationID, String messageID) {
    public KCBRequestContext(String conversationID) {
        this(conversationID, null);
    }
}

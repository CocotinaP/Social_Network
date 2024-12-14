package com.example.social_network.utils.events;

import com.example.social_network.domain.Message;

public class MessageChangeEvent implements Event{
    private ChangeEventType changeEventType;
    private Message oldValue, data;

    public MessageChangeEvent(ChangeEventType changeEventType, Message data) {
        this.changeEventType = changeEventType;
        this.data = data;
    }

    public MessageChangeEvent(ChangeEventType changeEventType, Message oldValue, Message data) {
        this.changeEventType = changeEventType;
        this.oldValue = oldValue;
        this.data = data;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    public Message getOldValue() {
        return oldValue;
    }

    public Message getData() {
        return data;
    }
}

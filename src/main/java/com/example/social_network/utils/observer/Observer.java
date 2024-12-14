package com.example.social_network.utils.observer;

import com.example.social_network.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}

package com.example.socialnetwork_1connetiondb.utils.observer;

import com.example.socialnetwork_1connetiondb.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}

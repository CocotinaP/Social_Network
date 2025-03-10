package com.example.socialnetwork_1connetiondb.utils.observer;

import com.example.socialnetwork_1connetiondb.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}

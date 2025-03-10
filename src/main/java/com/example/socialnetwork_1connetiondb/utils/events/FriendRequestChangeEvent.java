package com.example.socialnetwork_1connetiondb.utils.events;

import com.example.socialnetwork_1connetiondb.domain.FriendRequest;

public class FriendRequestChangeEvent implements Event{
    private ChangeEventType type;
    private FriendRequest oldData, data;

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest data){
        this.type = type;
        this.data = data;
    }

    public FriendRequestChangeEvent(ChangeEventType type, FriendRequest oldData, FriendRequest data) {
        this.type = type;
        this.oldData = oldData;
        this.data = data;
    }

    public ChangeEventType getType() {
        return type;
    }

    public FriendRequest getOldData() {
        return oldData;
    }

    public FriendRequest getData() {
        return data;
    }
}

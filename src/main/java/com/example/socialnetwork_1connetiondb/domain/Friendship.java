package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Friendship extends Entity<Long> {
    private User user1;
    private User user2;
    private LocalDateTime friendsFrom;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yyyy HH:mm");

    /**
     * Constructor.
     * @param user1 - the first user
     * @param user2 - the second user, who become friends.
     */
    public Friendship(User user1, User user2, LocalDateTime friendsFrom) {
        this.user1 = user1;
        this.user2 = user2;
        this.friendsFrom = friendsFrom;
    }

    /**
     *
     * @return the first friend of friendship.
     */
    public User getUser1() {
        return user1;
    }

    /**
     *
     * @return the first friend of friendship.
     */
    public User getUser2() {
        return user2;
    }

    /**
     * Modify the first friend of friendship.
     * @param p1 - new first user of friendship.
     */
    public void setUser1(User p1) {
        user1 = p1;
    }

    /**
     * Modify the second friend of friendship.
     * @param p2 - new second user of friendship.
     */
    public void setUser2(User p2) {
        user2 = p2;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + "| "+ user1.getFirstName() + " " + user1.getLastName() + " & " + user2.getFirstName() + " " + user2.getLastName()
                +"| Friends from: " + friendsFrom.format(dateTimeFormat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return user1.equals(that.user1) && user2.equals(that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}

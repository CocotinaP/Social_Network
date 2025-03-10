package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification extends Entity<Long>{
    private User sendingUser;
    private User receivingUser;
    private String message;
    private Boolean seenStatus;
    private LocalDateTime date;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yy HH:mm");

    public Notification(User sendingUser, User receivingUser, String message, Boolean seenStatus, LocalDateTime date) {
        this.sendingUser = sendingUser;
        this.receivingUser = receivingUser;
        this.message = message;
        this.seenStatus = seenStatus;
        this.date = date;
    }

    public User getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(User sendingUser) {
        this.sendingUser = sendingUser;
    }

    public User getReceivingUser() {
        return receivingUser;
    }

    public void setReceivingUser(User receivingUser) {
        this.receivingUser = receivingUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(Boolean seenStatus) {
        this.seenStatus = seenStatus;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(sendingUser, that.sendingUser) && Objects.equals(receivingUser, that.receivingUser) && Objects.equals(message, that.message) && Objects.equals(seenStatus, that.seenStatus) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendingUser, receivingUser, message, seenStatus, date);
    }

    @Override
    public String toString(){
        return "Id: " + getId() + "| Sending user: "
                + sendingUser.getFirstName() + ' ' + sendingUser.getLastName()
                + "| Receiving user: " + receivingUser.getFirstName() + receivingUser.getLastName()
                + "| Message: " + message
                + "| Seen status: " + seenStatus.toString()
                + "| Date: " + date.format(dateTimeFormat);
    }
}

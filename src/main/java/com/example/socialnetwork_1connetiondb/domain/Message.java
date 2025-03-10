package com.example.socialnetwork_1connetiondb.domain;

import com.example.socialnetwork_1connetiondb.utils.Utils;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long>{
    private User from;
    private User to;
    String message;
    private LocalDateTime date;
    private Message reply;

    public Message(User from, User to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        reply = null;
    }

    public Message(User from, User to, String message, LocalDateTime date, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message, date);
    }

    @Override
    public String toString(){
        if (reply != null) {
            return "Id: " + getId()
                    +  "| From: " + from.getFirstName() + ' ' + from.getLastName()
                    + "| To: " + to.getFirstName() + ' ' + to.getLastName()
                    + "| Message: " + message + "| Date: " + date.format(Utils.dateFormat())
                    + " | Reply message: " + reply.getMessage();
        }
        return "Id: " + getId()
                + "| From: " + from.getFirstName() + ' ' + from.getLastName()
                + "| To: " + to.getFirstName() + ' ' + to.getLastName()
                + "| Message: " + message + "| Date: " + date.format(Utils.dateFormat());
    }
}

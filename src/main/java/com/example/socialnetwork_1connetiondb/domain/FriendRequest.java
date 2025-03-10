package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendRequest extends Entity<Long>{
    private User sendingUser;
    private User receivingUser;
    private String status;
    private LocalDateTime submissionDate;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yyyy HH:mm");

    public FriendRequest(User sendingUser, User receivingUser, String status, LocalDateTime submissionDate) {
        this.sendingUser = sendingUser;
        this.receivingUser = receivingUser;
        this.status = status;
        this.submissionDate = submissionDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + "| Sending user: " + getSendingUser().getFirstName() + ' '
                + getSendingUser().getLastName() + "| Receiving user: "
                + getReceivingUser().getFirstName() + ' '
                +getReceivingUser().getLastName()
                +"| Status: " + getStatus()
                +"| Submission date: "
                + getSubmissionDate().format(dateTimeFormat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(sendingUser, that.sendingUser) && Objects.equals(receivingUser, that.receivingUser) && Objects.equals(submissionDate, that.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendingUser, receivingUser, submissionDate);
    }
}

package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendRequestDTO extends Entity<Long>{
    private Long sendingUserId;
    private Long receivingUserId;
    private String status;
    private LocalDateTime submissionDate;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yy HH:mm");

    public FriendRequestDTO(Long sendingUserId, Long receivingUserId, String status, LocalDateTime submissionDate) {
        this.sendingUserId = sendingUserId;
        this.receivingUserId = receivingUserId;
        this.status = status;
        this.submissionDate = submissionDate;
    }

    public Long getSendingUserId() {
        return sendingUserId;
    }

    public void setSendingUserId(Long sendingUserId) {
        this.sendingUserId = sendingUserId;
    }

    public Long getReceivingUserId() {
        return receivingUserId;
    }

    public void setReceivingUserId(Long receivingUserId) {
        this.receivingUserId = receivingUserId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequestDTO that = (FriendRequestDTO) o;
        return Objects.equals(sendingUserId, that.sendingUserId) && Objects.equals(receivingUserId, that.receivingUserId) && Objects.equals(status, that.status) && Objects.equals(submissionDate, that.submissionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendingUserId, receivingUserId, status, submissionDate);
    }
}

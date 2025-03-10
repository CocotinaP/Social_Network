package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class NotificationDTO extends Entity<Long>{
    private Long sendingUserId;
    private Long receivingUserId;
    private String message;
    private Boolean seenStatus;
    private LocalDateTime date;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yy HH:mm");

    public NotificationDTO(Long sendingUserId, Long receivingUserId, String message, Boolean seenStatus, LocalDateTime date) {
        this.sendingUserId = sendingUserId;
        this.receivingUserId = receivingUserId;
        this.message = message;
        this.seenStatus = seenStatus;
        this.date = date;
    }

    public Long getSendingUserId() {
        return sendingUserId;
    }

    public Long getReceivingUserId() {
        return receivingUserId;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getSeenStatus() {
        return seenStatus;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDTO that = (NotificationDTO) o;
        return Objects.equals(sendingUserId, that.sendingUserId) && Objects.equals(receivingUserId, that.receivingUserId) && Objects.equals(message, that.message) && Objects.equals(seenStatus, that.seenStatus) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sendingUserId, receivingUserId, message, seenStatus, date);
    }
}

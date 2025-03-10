package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;

public class MessageDTO extends Entity<Long>{
    private Long fromId;
    private Long toId;
    private String message;
    private LocalDateTime date;
    private Long replyId;

    public MessageDTO(Long fromId, Long toId, String message, LocalDateTime date, Long replyId) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.date = date;
        this.replyId = replyId;
    }

    public MessageDTO(Long fromId, Long toId, String message, LocalDateTime date) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.date = date;
        this.replyId = 0L;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Long getReplyId() {
        return replyId;
    }
}

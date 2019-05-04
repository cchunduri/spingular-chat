package com.spingular.chat.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ChatMessage.
 */
@Document(collection = "chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("sender")
    private Long sender;

    @Field("reciver")
    private Long reciver;

    @Field("message_text")
    private String messageText;

    @Field("message_time")
    private LocalDate messageTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public ChatMessage sender(Long sender) {
        this.sender = sender;
        return this;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReciver() {
        return reciver;
    }

    public ChatMessage reciver(Long reciver) {
        this.reciver = reciver;
        return this;
    }

    public void setReciver(Long reciver) {
        this.reciver = reciver;
    }

    public String getMessageText() {
        return messageText;
    }

    public ChatMessage messageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDate getMessageTime() {
        return messageTime;
    }

    public ChatMessage messageTime(LocalDate messageTime) {
        this.messageTime = messageTime;
        return this;
    }

    public void setMessageTime(LocalDate messageTime) {
        this.messageTime = messageTime;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatMessage)) {
            return false;
        }
        return id != null && id.equals(((ChatMessage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "id=" + getId() +
            ", sender=" + getSender() +
            ", reciver=" + getReciver() +
            ", messageText='" + getMessageText() + "'" +
            ", messageTime='" + getMessageTime() + "'" +
            "}";
    }
}

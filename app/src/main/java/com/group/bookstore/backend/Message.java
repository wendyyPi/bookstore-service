package com.group.bookstore.backend;

/**
 * @author Yusen Nian
 */
import java.io.Serializable;

public class Message implements Serializable{
    private String receiver;
    private String addresser;
    private String messageContent;
    private Status status;
    private String date;

    public Message(String receiver, String addresser, String messageContent, String date) {
        this.receiver = receiver;
        this.addresser = addresser;
        this.messageContent = messageContent;
        this.date = date;
        this.status = Status.UNREAD;
    }

    //改变未读状态
    public void openMessage(){
        this.setStatus(Status.READ);
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getAddresser() {
        return addresser;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public enum Status {
        UNREAD,
        READ;
    }
}

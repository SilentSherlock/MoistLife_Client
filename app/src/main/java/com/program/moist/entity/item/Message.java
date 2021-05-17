package com.program.moist.entity.item;

import java.io.Serializable;

/**
 * Author: SilentSherlock
 * Date: 2021/5/13
 * Description: 用于在项目中传递消息
 */
public class Message implements Serializable {
    public String sender;//若为本地发出，则sender为"this"
    public String content;//内容
    public String date;//时间戳
    public String senderId;//头像UUID

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message(String sender, String content, String date, String senderId) {
        this.sender = sender;
        this.content = content;
        this.date = date;
        this.senderId = senderId;
    }
    public static Message createByAll(String sender, String content, String date, String senderId) {
        return new Message(sender, content, date, senderId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

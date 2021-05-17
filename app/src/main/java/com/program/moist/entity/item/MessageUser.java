package com.program.moist.entity.item;

import com.program.moist.entity.User;

/**
 * Author: SilentSherlock
 * Date: 2021/5/13
 * Description: describe the class
 */
public class MessageUser {
    private Message message;
    private User user;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageUser(Message message, User user) {
        this.message = message;
        this.user = user;
    }

    public MessageUser() {
    }

    public static MessageUser createByAll(Message message, User user) {
        return new MessageUser(message, user);
    }
}

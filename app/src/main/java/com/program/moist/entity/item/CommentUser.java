package com.program.moist.entity.item;

import com.program.moist.entity.Comment;
import com.program.moist.entity.User;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class CommentUser {
    private User fromUser;
    private Comment comment;
    private User toUser;

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public CommentUser(User fromUser, Comment comment, User toUser) {
        this.fromUser = fromUser;
        this.comment = comment;
        this.toUser = toUser;
    }

    public CommentUser(User fromUser, Comment comment) {
        this.fromUser = fromUser;
        this.comment = comment;
    }

    public CommentUser() {
    }

    public static CommentUser createByAll(User fromUser, Comment comment, User toUser) {
        return new CommentUser(fromUser, comment, toUser);
    }

    public static CommentUser createByDefault(User fromUser, Comment comment) {
        return new CommentUser(fromUser, comment);
    }
}

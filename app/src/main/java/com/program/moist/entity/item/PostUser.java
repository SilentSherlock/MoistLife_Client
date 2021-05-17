package com.program.moist.entity.item;

import com.program.moist.entity.Post;
import com.program.moist.entity.User;

import java.io.Serializable;

/**
 * Author: SilentSherlock
 * Date: 2021/5/14
 * Description: describe the class
 */
public class PostUser implements Serializable {
    private Post post;
    private User user;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PostUser(){}

    public PostUser(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static PostUser createByAll(Post post, User user) {
        return new PostUser(post, user);
    }
}

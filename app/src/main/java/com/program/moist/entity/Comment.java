package com.program.moist.entity;

import java.util.Date;

/**
 * Author: SilentSherlock
 * Date: 2021/5/15
 * Description: describe the class
 */
public class Comment {
    private Integer comId;
    private Integer fromUserId;
    private Integer toUserId;
    private Integer postId;
    private String content;
    private Date comTime;
    private Integer parentComId;
    private Integer comKind;

    public Integer getComId() {
        return comId;
    }

    public void setComId(Integer comId) {
        this.comId = comId;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getComTime() {
        return comTime;
    }

    public void setComTime(Date comTime) {
        this.comTime = comTime;
    }

    public Integer getParentComId() {
        return parentComId;
    }

    public void setParentComId(Integer parentComId) {
        this.parentComId = parentComId;
    }

    public Integer getComKind() {
        return comKind;
    }

    public void setComKind(Integer comKind) {
        this.comKind = comKind;
    }
}

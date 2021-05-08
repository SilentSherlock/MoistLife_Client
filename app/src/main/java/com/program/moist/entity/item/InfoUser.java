package com.program.moist.entity.item;

import com.program.moist.entity.Information;
import com.program.moist.entity.User;

/**
 * Author: SilentSherlock
 * Date: 2021/5/5
 * Description: item包下类均为实体混合，便于卡片信息展示
 */
public class InfoUser {
    private Integer infoId;
    private java.lang.String infoTitle;
    private Float price;
    private Integer userId;
    private java.lang.String userName;
    private java.lang.String location;

    public void fill(User user, Information information) {
        if (user != null || information != null) {
            infoId = information.getInfoId();
            infoTitle = information.getInfoTitle();
            price = information.getPrice();
            userId = user.getUserId();
            userName = user.getUserName();
            location = user.getLocation();
        }

    }
    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public java.lang.String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(java.lang.String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public java.lang.String getUserName() {
        return userName;
    }

    public void setUserName(java.lang.String userName) {
        this.userName = userName;
    }

    public java.lang.String getLocation() {
        return location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }
}

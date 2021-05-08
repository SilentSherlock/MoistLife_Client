package com.program.moist.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: SilentSherlock
 * Date: 2021/5/3
 * Description: describe the class
 */
public class Information implements Serializable {
    private Integer infoId;
    private java.lang.String infoTitle;
    private Float price;
    private Integer cateId;
    private java.lang.String area;
    private java.lang.String detail;
    private java.lang.String infoPictures;
    private Integer userId;
    private Date infoTime;
    private Date updateTime;

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

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public java.lang.String getArea() {
        return area;
    }

    public void setArea(java.lang.String area) {
        this.area = area;
    }

    public java.lang.String getDetail() {
        return detail;
    }

    public void setDetail(java.lang.String detail) {
        this.detail = detail;
    }

    public java.lang.String getInfoPictures() {
        return infoPictures;
    }

    public void setInfoPictures(java.lang.String infoPictures) {
        this.infoPictures = infoPictures;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getInfoTime() {
        return infoTime;
    }

    public void setInfoTime(Date infoTime) {
        this.infoTime = infoTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

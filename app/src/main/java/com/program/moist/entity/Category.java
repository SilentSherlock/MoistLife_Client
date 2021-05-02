package com.program.moist.entity;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Author: SilentSherlock
 * Date: 2021/5/1
 * Description: describe the class
 */
public class Category {
    private Integer cateId;
    private Integer parentCateId;//父类别的id，父类别id为0，说明是一级类别
    private String cateName;
    private Date createTime;
    private Date updateTime;

    @NotNull
    @Override
    public String toString() {
        return "Category{" +
                "cateId=" + cateId +
                ", parentCateId=" + parentCateId +
                ", cateName='" + cateName + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public Integer getParentCateId() {
        return parentCateId;
    }

    public void setParentCateId(Integer parentCateId) {
        this.parentCateId = parentCateId;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

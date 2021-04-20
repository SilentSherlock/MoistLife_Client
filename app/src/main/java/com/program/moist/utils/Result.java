package com.program.moist.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: SilentSherlock
 * Date: 2021/4/17
 * Description: describe the class
 */
public class Result {
    private Status status;
    private String description;
    private Map<String, Object> resultMap;

    public Result(Status status, String msg, Map<String, Object> map) {
        this.status = status;
        this.description = msg;
        this.resultMap = map;
    }

    public Result() {
        this(Status.DEFAULT, "", new HashMap<>());
    }

    public Result(Status status) {
        this(status, "", new HashMap<>());
    }

    public Result(Status status, String msg) {
        this(status, msg, new HashMap<>());
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", description='" + description + '\'' +
                ", resultMap=" + resultMap +
                '}';
    }
}

package com.newland.model;

import java.io.Serializable;
import java.util.Date;

public class ServiceObject implements Serializable {
    private String id;

    private String parentId;

    private String objectName;

    private Date createDate;
    private String createDateString;

    //类型 1 服务 2 包 3 类 4 方法
    private Integer type;

    private Integer isCoverage;

    private String comparisonDate1;
    private String comparisonDate2;
    private String fullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsCoverage() {
        return isCoverage;
    }

    public void setIsCoverage(Integer isCoverage) {
        this.isCoverage = isCoverage;
    }

    public String getComparisonDate1() {
        return comparisonDate1;
    }

    public void setComparisonDate1(String comparisonDate1) {
        this.comparisonDate1 = comparisonDate1;
    }

    public String getComparisonDate2() {
        return comparisonDate2;
    }

    public void setComparisonDate2(String comparisonDate2) {
        this.comparisonDate2 = comparisonDate2;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
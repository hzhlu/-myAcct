package com.macrolab.myAcct.model;

public class TMyAcct {
    int id;
    String name;
    String content;
    String createDate;
    String updateDate;
    String securityKey;
    String keyVerifyCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getKeyVerifyCode() {
        return keyVerifyCode;
    }

    public void setKeyVerifyCode(String keyVerifyCode) {
        this.keyVerifyCode = keyVerifyCode;
    }

    @Override
    public String toString() {
        return "TMyAcct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", securityKey='" + securityKey + '\'' +
                ", keyVerifyCode='" + keyVerifyCode + '\'' +
                '}';
    }
}

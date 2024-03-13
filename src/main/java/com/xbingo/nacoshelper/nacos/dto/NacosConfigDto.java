package com.xbingo.nacoshelper.nacos.dto;

public class NacosConfigDto {
    private String initContent;

    private String encryptedDataKey;

    private String appName;

    private String type;

    private String content;
    private String dataId;

    private String id;

    private String tenant;

    private String group;

    private String md5;

    public String getInitContent() {
        return initContent;
    }

    public void setInitContent(String initContent) {
        this.initContent = initContent;
    }

    public String getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public void setEncryptedDataKey(String encryptedDataKey) {
        this.encryptedDataKey = encryptedDataKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}

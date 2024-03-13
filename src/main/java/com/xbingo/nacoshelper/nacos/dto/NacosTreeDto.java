package com.xbingo.nacoshelper.nacos.dto;

public class NacosTreeDto {

    private String dataId;

    private String tenant;

    private String group;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
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

    public String toString() {
        return this.dataId;
    }
}


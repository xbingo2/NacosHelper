package com.xbingo.nacoshelper.nacos.dto;

public class NacosTokenDto {

    private String nacosUrl;

    private Integer tokenTtl;

    private String accessToken;

    private Long loginTime;

    private Boolean globalAdmin;

    private String username;

    public String getNacosUrl() {
        return nacosUrl;
    }

    public void setNacosUrl(String nacosUrl) {
        this.nacosUrl = nacosUrl;
    }

    public Integer getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(Integer tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getGlobalAdmin() {
        return globalAdmin;
    }

    public void setGlobalAdmin(Boolean globalAdmin) {
        this.globalAdmin = globalAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

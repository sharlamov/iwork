package com.model;

import java.math.BigDecimal;

public class CustomUser {

    private BigDecimal id;
    private String username;
    private String password;
    private Integer scaleType;
    private Integer adminLevel;
    private String profile;
    private CustomItem defDiv;
    private CustomItem clcuser_sct;

    public String getPassword() {
        return password;
    }

    public void setPassword(String str) {
        password = str;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String str) {
        username = str;
    }

    public Integer getScaleType() {
        return scaleType;
    }

    public void setScaleType(Integer scaleType) {
        this.scaleType = scaleType;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Integer getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(Integer adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public CustomItem getDefDiv() {
        return defDiv;
    }

    public void setDefDiv(CustomItem defDiv) {
        this.defDiv = defDiv;
    }

    public CustomItem getClcuser_sct() {
        return clcuser_sct;
    }

    public void setClcuser_sct(CustomItem clcuser_sct) {
        this.clcuser_sct = clcuser_sct;
    }
}
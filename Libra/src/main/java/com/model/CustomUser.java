package com.model;

import java.math.BigDecimal;
import java.util.List;

public class CustomUser {

    private BigDecimal id;
    private String username;
    private String password;
    private List<CustomItem> elevators;
    private Integer scaleType;
    private Integer adminLevel;
    private boolean isHandEditable;

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


    public boolean isEnabled() {
        return true;
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

    public List<CustomItem> getElevators() {
        return elevators;
    }

    public void setElevators(List<CustomItem> elevators) {
        this.elevators = elevators;
    }

    public boolean isHandEditable() {
        return isHandEditable;
    }

    public void setHandEditable(boolean isHandEditable) {
        this.isHandEditable = isHandEditable;
    }
}
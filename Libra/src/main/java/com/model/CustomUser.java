package com.model;

import java.math.BigDecimal;

public class CustomUser extends AbstractModel<CustomUser> {

    private static final long serialVersionUID = -8055629994460003294L;

    private BigDecimal id;
    private String username;
    private String password;
    private CustomItem elevator;
    private CustomItem div;
    private Integer scaleType;
    private Integer adminLevel;

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

    public CustomItem getElevator() {
        return elevator;
    }

    public void setElevator(CustomItem elevator) {
        this.elevator = elevator;
    }

    public Integer getScaleType() {
        return scaleType;
    }

    public void setScaleType(Integer scaleType) {
        this.scaleType = scaleType;
    }

    public CustomItem getDiv() {
        return div;
    }

    public void setDiv(CustomItem div) {
        this.div = div;
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
}
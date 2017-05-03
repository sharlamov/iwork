package com.orders.model;

import com.dao.model.CustomItem;
import com.orders.enums.WebRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

public class CustomUser implements UserDetails{

    private static final long serialVersionUID = -8055629994460003294L;

    private BigDecimal id;
    private String username;
    private String password;
    private CustomItem elevator;
    private CustomItem div;
    private String divFilter;
    private Integer adminLevel;
    private WebRole level;
    private String email;
    private Set<GrantedAuthority> authorities;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setCustomerAuthorities(Set<GrantedAuthority> roles) {
        authorities = roles;
    }

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

    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
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

    public WebRole getLevel() {
        return level;
    }

    public void setLevel(WebRole level) {
        this.level = level;
    }

    public String getDivFilter() {
        return divFilter;
    }

    public void setDivFilter(String divFilter) {
        this.divFilter = divFilter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
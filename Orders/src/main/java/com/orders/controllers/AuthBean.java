package com.orders.controllers;

import com.dao.model.DataSet;
import com.orders.model.CustomUser;
import com.orders.utils.WebUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "authBean")
@SessionScoped
public class AuthBean extends AbstractBean implements Serializable {

    @ManagedProperty(value = "#{authenticationManager}")
    private AuthenticationManager authenticationManager;

    private String userName;
    private String pass;

    private CustomUser currentUser;
    private DataSet menu;

    public String login() {
        FacesMessage message;

        try {
            Authentication request = new UsernamePasswordAuthenticationToken(userName, WebUtil.encode(pass));
            Authentication result = authenticationManager.authenticate(request);
            currentUser = (CustomUser) result.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(result);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Добро пожаловать", userName);

            menu = getDb().exec("select * from tmdb_orders_menu order by cod");
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Вход запрещен", e.getMessage());
            e.printStackTrace();
            getMailService().dbLog(e.getMessage());
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        return menu.size() > 1 ? "home" : menu.getString("reference") + "?faces-redirect=true";
    }


    public String login1() {
        UsernamePasswordAuthenticationToken upa = new UsernamePasswordAuthenticationToken(userName, WebUtil.encode(pass));
        Authentication result = authenticationManager.authenticate(upa);

        if (result.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(result);
        }
        return "home?faces-redirect=true";
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
        SecurityContextHolder.clearContext();
        return "login?faces-redirect=true";
    }


    public CustomUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CustomUser currentUser) {
        this.currentUser = currentUser;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DataSet getMenu() {
        return menu;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}

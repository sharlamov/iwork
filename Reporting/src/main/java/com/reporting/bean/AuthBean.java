package com.reporting.bean;

import com.reporting.model.CustomUser;
import com.reporting.service.UserService;
import org.primefaces.context.RequestContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ManagedBean(name = "authBean")
@SessionScoped
public class AuthBean extends AbstractBean {

    private static List<String> languages = new ArrayList<>();//{"en","ro","ru"};

    static {
        languages.add("en");
        languages.add("ro");
        languages.add("ru");
    }

    @ManagedProperty(value = "#{authenticationManager}")
    private AuthenticationManager authenticationManager;
    @ManagedProperty(value = "#{userServiceImpl}")
    private UserService userService;
    private String userName;
    private String password;
    private CustomUser currentUser;
    private String locale = languages.get(0);

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        AuthBean.languages = languages;
    }

    @Override
    public void init() {
        countryLocaleCodeChanged();
    }

    public void countryLocaleCodeChanged() {
        FacesContext.getCurrentInstance()
                .getViewRoot().setLocale(new Locale(locale));
    }

    public void login(ActionEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message;
        boolean loggedIn;
        String page = "";

        try {
            Authentication request = new UsernamePasswordAuthenticationToken(
                    userName, password);
            Authentication result = authenticationManager.authenticate(request);
            currentUser = (CustomUser) result.getPrincipal();
            userService.updateUserDetails(currentUser);
            SecurityContextHolder.getContext().setAuthentication(result);
            loggedIn = true;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome",
                    userName);
            page = getStartPage();
        } catch (Exception e) {
            loggedIn = false;
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Loggin Error", e.getMessage());
            e.printStackTrace();
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", loggedIn);
        context.addCallbackParam("loggedPage", page);
    }

    public String logout() {
        SecurityContextHolder.clearContext();
        return "login?faces-redirect=true";
    }

    public String getStartPage() {
        return "home.xhtml";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthenticationManager(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public CustomUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CustomUser currentUser) {
        this.currentUser = currentUser;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}

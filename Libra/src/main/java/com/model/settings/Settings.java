package com.model.settings;

import com.enums.LangType;

import java.util.List;

public class Settings extends AbstractSettings {

    private LangType lang;
    private String username;
    private String password;
    private boolean auto;
    private boolean debug;
    private String connection;
    private List<ScaleSettings> scales;

    public String getConnection() {
        if(connection.contains(":"))
            connection = encodeURL(connection);
        return decodeURL(connection);
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ScaleSettings> getScales() {
        return scales;
    }

    public void setScales(List<ScaleSettings> scales) {
        this.scales = scales;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public LangType getLang() {
        return lang;
    }

    public void setLang(LangType lang) {
        this.lang = lang;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}

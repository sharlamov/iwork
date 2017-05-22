package com.orders.controllers;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.orders.dao.QueryFactory;
import com.orders.model.CustomUser;
import com.orders.services.MailService;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Blob;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

abstract class AbstractBean implements Serializable {

    private static final long serialVersionUID = -7244932139281652821L;
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    @ManagedProperty(value = "#{queryFactoryImpl}")
    private QueryFactory db;

    @ManagedProperty(value = "#{mailServiceImpl}")
    private MailService mailService;

    @ManagedProperty("#{sql}")
    private ResourceBundle bundle;

    public List<CustomItem> find(String query) {
        String sql = getAttribute("sql");
        StringBuilder bld = new StringBuilder("select * from (");
        bld.append(sql).append(") where lower(clccodt) like '%")
                .append(query.trim().toLowerCase()).append("%' and rownum < 10");
        try {
            DataSet set = db.exec(bld.toString());
            return set.stream().map(row -> (CustomItem) row[0]).collect(Collectors.toList());
        } catch (Exception e) {
            msg(e);
            return Collections.emptyList();
        }
    }

    private ExternalContext extc() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public void downloadFile(Object name, Object type, Object object) throws Exception {
        HttpServletResponse response = (HttpServletResponse) extc().getResponse();

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open file.
            long len;
            if (object instanceof Blob) {
                input = new BufferedInputStream(((Blob) object).getBinaryStream(), DEFAULT_BUFFER_SIZE);
                len = ((Blob) object).length();
            } else {
                input = new BufferedInputStream(new ByteArrayInputStream((byte[]) object), DEFAULT_BUFFER_SIZE);
                len = ((byte[]) object).length;
            }

            // Init servlet response.
            response.reset();
            response.setHeader("Content-Type", type.toString());
            response.setHeader("Content-Length", String.valueOf(len));
            response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Finalize task.
            output.flush();
        } finally {
            // Gently close streams.
            close(output);
            close(input);
        }

        // Inform JSF that it doesn't need to handle response.
        // This is very important, otherwise you will get the following exception in the logs:
        // java.lang.IllegalStateException: Cannot forward after response has been committed.
        FacesContext.getCurrentInstance().responseComplete();
    }

    // Helpers (can be refactored to public utility class) ----------------------------------------

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                mailService.dbLog(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String sql(String key) {
        return bundle.getString(key);
    }

    public CustomUser getLoggedUser() {
        return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected CustomItem getItemUser() {
        CustomUser user = getLoggedUser();
        return new CustomItem(user.getId(), user.getUsername());
    }

    public String getParam(String name) {
        return extc().getRequestParameterMap().get(name);
    }

    public <T> T getAttribute(String name) {
        Map<String, Object> map = UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()).getAttributes();
        return (T) map.get(name);
    }

    public <T> T getSessionParam(String name) {
        return (T) extc().getSessionMap().get(name);
    }

    public void setSessionParam(String name, Object value) {
        extc().getSessionMap().put(name, value);
    }

    public void goToPage(String page) {
        try {
            extc().redirect(page);
        } catch (IOException e) {
            mailService.dbLog(e.getMessage());
            e.printStackTrace();
        }
    }

    void msg(String text, boolean useFlash) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Уведомление", text);
        context.addMessage(null, message);
        if (useFlash) {
            Flash flash = context.getExternalContext().getFlash();
            flash.setKeepMessages(true);
            flash.setRedirect(true);
        }
    }

    void msg(String text) {
        msg(text, false);
    }

    void msg(Exception e) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", e.getMessage());
        context.addMessage(null, message);
        context.validationFailed();
        e.printStackTrace();
        mailService.dbLog(e.getMessage());
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setDb(QueryFactory db) {
        this.db = db;
    }

    public QueryFactory getDb() {
        return db;
    }


}
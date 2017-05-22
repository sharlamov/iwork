package com.orders.controllers;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.orders.utils.WebUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class OrderEditBean extends AbstractBean {

    private DataSet order;
    private DataSet orderFiles;
    private Integer orderId;
    private Boolean disabled;
    private DataSet history;
    private boolean disabledApprove;
    private Boolean isApprove;
    private int preLevel;
    private String commentDlg;

    @PostConstruct
    public void init() {
        try {
            String ord = getParam("orderId");
            if (ord == null) {
                order = new DataSet("ord", "pay_date", "clcdivt", "clcinitiatort", "clcdept", "clccfot", "clctype_costt", "clcclientt", "text", "suma", "val", "lvl", "offers", "comments", "clcuserIdt", "clcstatust", "contPay");
                order.add(new Object[order.getColCount()]);
                order.setObject("clcuserIdt", getItemUser());
                order.setObject("lvl", preLevel);
                orderFiles = new DataSet("ord", "fileId", "fileName", "fileType", "fileData");
                history = new DataSet("ord", "ord1", "lvl", "datetime", "text", "clcuserIdt", "clcstatust");
            } else {
                orderId = Integer.valueOf(ord);
                order = getDb().exec(sql("order"), orderId);
                orderFiles = getDb().exec(sql("orderFiles"), orderId);
                history = getDb().exec(sql("orderHistory"), orderId);
            }

            DataSet set = getDb().exec("select count(*) cnt from tms_order_fin_resp r where r.div_id =:clcdivt and r.user_id = " + getLoggedUser().getId() + " and r.authoriz_level = :lvl and (r.cfr = :clccfot or r.cfr = 0)", order);
            preLevel = order.getInt("lvl");
            disabled = preLevel != 0;
            disabledApprove = disabled && set.getInt("cnt") == 0;
        } catch (Exception e) {
            msg(e);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        orderFiles.add(new Object[]{order.getObject("ord"), null, file.getFileName(), file.getContentType(), file.getContents()});
        msg(event.getFile().getFileName() + " загружен.");
    }

    public void removeFile(int row) {
        execFile(orderFiles.remove(row), true);
        msg("Файл удален " + row);
    }

    public String save() {
        try {
            if (orderId == null) {
                orderId = getDb().exec(sql("nextId")).getDecimal("nid").intValue();
                order.setObject("ord", orderId);
                if (order.getObject("clcstatust") == null)
                    order.setObject("clcstatust", new CustomItem(1, ""));
                getDb().exec(sql("insertOrder"), order);
            } else {
                getDb().exec(sql("updateOrder"), order);
            }

            orderFiles.forEach(a -> execFile(a, false));

            if (history.isEmpty())
                getDb().exec(sql("insertHistory"), orderId, 0, "Создание заявки", order.getObject("clcuseridt"), new CustomItem(1, ""));

            int proLevel = order.getInt("lvl");
            if (preLevel != proLevel)
                getDb().exec(sql("insertHistory"), orderId, proLevel, commentDlg, getItemUser(), order.getObject("clcstatust"));

            //msg("Заявка № " + orderId + " сохранена успешно", true);
            return "orders?faces-redirect=true";
        } catch (Exception e) {
            msg(e);
            return "";
        }
    }

    public void approve() {
        try {
            String msg = "";
            if (isApprove) {
                DataSet set = getDb().exec(sql("nextLevel"), order);

                if (set.getObject("nStep") == null) {
                    order.setObject("clcstatust", new CustomItem(3, null));
                    order.setObject("lvl", 1000);
                } else {
                    order.setObject("lvl", set.getObject("nStep"));
                    order.setObject("clcstatust", new CustomItem(2, null));
                    msg = "Order No. %s is waiting for your approval!";
                }
            } else {
                DataSet set = getDb().exec(sql("previousLevel"), order);
                int lvl = set.getInt("nStep");
                order.setObject("clcstatust", new CustomItem(lvl > 0 ? 2 : 1, null));
                order.setObject("lvl", lvl);
                msg = "Order No. %s is waiting for your attention!";
            }

            save();
            goToPage("orders.xhtml");

            sendOrderNotification(String.format(msg, orderId), order);
        } catch (Exception e) {
            msg(e);
        }
    }

    private void sendOrderNotification(String msg, DataSet order) throws Exception {
        DataSet emails = getDb().exec(sql("findRecipients"), order);
        for (Object[] email : emails) {
            getMailService().sendEmail(email[0].toString(), msg);
        }
    }

    public void openApprove(Boolean isApprove) {
        this.isApprove = isApprove;
    }

    private void execFile(Object[] fileInfo, boolean isRemove) {
        try {
            if (fileInfo == null)
                return;

            if (isRemove) {
                if (fileInfo[1] != null) {
                    getDb().exec(sql("delteFile"), orderId, fileInfo[1]);
                }
            } else {
                if (fileInfo[4] instanceof byte[]) {
                    getDb().exec(sql("insertFile"), orderId, fileInfo[2], fileInfo[3], fileInfo[4]);
                }
            }
        } catch (Exception e) {
            msg(e);
        }
    }

    public String getExt(String fileName) {
        int i = fileName.lastIndexOf('.');
        String ext = fileName.trim().toLowerCase().substring(i + 1);
        switch (ext) {
            case "jpeg":
                return "jpg";
            case "doc":
                return "docx";
            case "xls":
                return "xlsx";
            default:
                return ext;
        }
    }

    public DataSet getOrder() {
        return order;
    }

    public void setOrder(DataSet order) {
        this.order = order;
    }

    public DataSet getOrderFiles() {
        return orderFiles;
    }

    public void setOrderFiles(DataSet orderFiles) {
        this.orderFiles = orderFiles;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public DataSet getHistory() {
        return history;
    }

    public void setHistory(DataSet history) {
        this.history = history;
    }

    public boolean isDisabledApprove() {
        return disabledApprove;
    }

    public void setDisabledApprove(boolean disabledApprove) {
        this.disabledApprove = disabledApprove;
    }

    public String getCommentDlg() {
        return commentDlg;
    }

    public void setCommentDlg(String commentDlg) {
        this.commentDlg = commentDlg;
    }
}

package com.orders.controllers;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import com.orders.enums.Sorting;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

@ManagedBean
@ViewScoped
public class OrdersBean extends AbstractBean {

    private DataSet orders;
    private CustomItem fDiv;
    private CustomItem fDep;
    private CustomItem fClient;
    private Sorting sSuma = Sorting.NONE;
    private Sorting sDate = Sorting.NONE;
    private Sorting sClient = Sorting.NONE;
    private DataSet filter;
    private Boolean useCreateBtn;
    private Boolean useAuthorised;

    @PostConstruct
    public void init() {
        try {
            filter = new DataSet("userid", "clcdivt", "clcclientt", "clcdept", "clcstatust", "lvl");
            filter.add(new Object[filter.getCachedNames().size()]);
            filter.setObject("userid", getLoggedUser().getId());
            loadFromApp();
            applyFilter(null);

            DataSet set = getDb().exec(sql("canCreate"), filter);
            useCreateBtn = set.getInt("cnt") > 0;
        } catch (Exception e) {
            msg(e);
        }
    }

    public void applyFilter(AjaxBehaviorEvent event) throws Exception {
        StringBuilder bld = new StringBuilder(sql("ordersList"));

        if (useAuthorised) {
            bld.append(" and r.AUTHORIZ_LEVEL = o.lvl ");
        }

        bld.append(" ) ");

        if (filter.getObject("clcdivt") != null) {
            bld.append(" and div = :clcdivt ");
        }

        if (filter.getObject("clcclientt") != null) {
            bld.append(" and client = :clcclientt ");
        }

        if (filter.getObject("clcdept") != null) {
            bld.append(" and dep = :clcdept ");
        }

        if (filter.getObject("clcstatust") != null) {
            bld.append(" and status = :clcstatust ");
        }

        bld.append(" order by 'r' ");

        if (!sSuma.equals(Sorting.NONE)) {
            bld.append(" ,suma ");
            if (sSuma.equals(Sorting.DOWN))
                bld.append("desc ");
        }

        if (!sClient.equals(Sorting.NONE)) {
            bld.append(" ,client ");
            if (sClient.equals(Sorting.DOWN))
                bld.append("desc ");
        }

        if (!sDate.equals(Sorting.NONE)) {
            bld.append(" ,pay_date ");
            if (sDate.equals(Sorting.DOWN))
                bld.append("desc ");
        }

        orders = getDb().exec(bld.toString(), filter);
        saveToApp();
    }

    public String getStatusImg(int row, String fieldName) {
        CustomItem item = (CustomItem) orders.getObject(row, fieldName);
        switch (item.getId().intValue()) {
            case 2:
                return "order_green.png";
            case 3:
                return "order_blue.png";
            case 4:
                return "order_orange.png";
            default:
                return "order.png";
        }
    }

    private void saveToApp() {
        setSessionParam("clcdivt", filter.getObject("clcdivt"));
        setSessionParam("useAuthorised", useAuthorised);
        setSessionParam("clcclientt", filter.getObject("clcclientt"));
        setSessionParam("clcstatust", filter.getObject("clcstatust"));
        setSessionParam("clcdept", filter.getObject("clcdept"));
        setSessionParam("sSuma", sSuma);
        setSessionParam("sClient", sClient);
        setSessionParam("sDate", sDate);
    }

    private void loadFromApp() {
        filter.setObject("clcdivt", getSessionParam("clcdivt"));
        useAuthorised = getSessionParam("useAuthorised");
        filter.setObject("clcclientt", getSessionParam("clcclientt"));
        filter.setObject("clcstatust", getSessionParam("clcstatust"));
        filter.setObject("clcdept", getSessionParam("clcdept"));
        sSuma = getSessionParam("sSuma") == null ? Sorting.NONE : getSessionParam("sSuma");
        sClient = getSessionParam("sClient") == null ? Sorting.NONE : getSessionParam("sClient");
        sDate = getSessionParam("sDate") == null ? Sorting.NONE : getSessionParam("sDate");
    }

    public DataSet getOrders() {
        return orders;
    }

    public CustomItem getfDiv() {
        return fDiv;
    }

    public void setfDiv(CustomItem fDiv) {
        this.fDiv = fDiv;
    }

    public CustomItem getfDep() {
        return fDep;
    }

    public void setfDep(CustomItem fDep) {
        this.fDep = fDep;
    }

    public CustomItem getfClient() {
        return fClient;
    }

    public void setfClient(CustomItem fClient) {
        this.fClient = fClient;
    }

    public void setFilter(DataSet filter) {
        this.filter = filter;
    }

    public DataSet getFilter() {
        return filter;
    }

    public Sorting getsSuma() {
        return sSuma;
    }

    public void setsSuma(Sorting sSuma) {
        this.sSuma = sSuma;
    }

    public Sorting getsDate() {
        return sDate;
    }

    public void setsDate(Sorting sDate) {
        this.sDate = sDate;
    }

    public Sorting getsClient() {
        return sClient;
    }

    public void setsClient(Sorting sClient) {
        this.sClient = sClient;
    }

    public Integer getCount() {
        return orders.size();
    }

    public Boolean getUseCreateBtn() {
        return useCreateBtn;
    }

    public Boolean getUseAuthorised() {
        return useAuthorised;
    }

    public void setUseAuthorised(Boolean useAuthorised) {
        this.useAuthorised = useAuthorised;
    }
}

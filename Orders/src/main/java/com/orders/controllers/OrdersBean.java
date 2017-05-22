package com.orders.controllers;

import com.dao.model.CustomItem;
import com.dao.model.DataSet;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class OrdersBean extends AbstractBean {

    private DataSet orders;
    private DataSet filter;
    private CustomItem user;
    private Boolean useCreateBtn;
    private Object[] selectedOrder;
    private List<Object[]> tabs;
    private Integer tabIndex;

    @PostConstruct
    public void init() {
        try {
            user = getItemUser();
            filter = getSessionParam("filter");
            if (filter == null)
                filter = DataSet.init("userid", user.getId(), "clcdivt", null, "clcclientt", null, "status", 0);

            tabIndex = getActiveTabIndex();

            tabs = new ArrayList<>();
            tabs.add(new Object[]{"Мои", 0, "podcast.png"});
            tabs.add(new Object[]{"Все", 99, "database.png"});
            tabs.add(new Object[]{"Оформление", 1, "to-do-list.png"});
            tabs.add(new Object[]{"Утверждение", 2, "folder_green_backup.png"});
            tabs.add(new Object[]{"Закрытые", 3, "folder_blue_todos.png"});

            apply();
            useCreateBtn = getDb().value(sql("canCreate"), Integer.class, user.getId()) > 0;
        } catch (Exception e) {
            msg(e);
        }
    }

    public void onRowDblClickSelect(final SelectEvent event) {
        Object[] row = (Object[]) event.getObject();
        goToPage("editOrder.xhtml?orderId=" + row[0]);
    }

    public void onTabChange(TabChangeEvent event) {
        Object[] tabData = (Object[]) event.getData();
        filter.setObject("status", tabData[1]);
        try {
            apply();
        } catch (Exception e) {
            msg(e);
        }
    }

    public int getActiveTabIndex() {
        int n = filter.getInt("status");
        switch (n) {
            case 99:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            default:
                return 0;
        }
    }

    public void rep() throws Exception {
        getMailService().createReport();
    }

    public void apply() throws Exception {
        StringBuilder bld = new StringBuilder(sql("ordersList"));

        if (filter.getInt("status") == 0) {
            bld.append(" and r.AUTHORIZ_LEVEL = o.lvl ");
        }

        bld.append(" ) ");

        if (filter.getObject("clcdivt") != null) {
            bld.append(" and div = :clcdivt ");
        }

        if (filter.getObject("clcclientt") != null) {
            bld.append(" and client = :clcclientt ");
        }

        if (filter.getInt("status") != 99 && filter.getInt("status") > 0) {
            bld.append(" and status = :status ");
        }

        bld.append(" order by 1 desc ");

        orders = getDb().exec(bld.toString(), filter);
        setSessionParam("filter", filter);
    }

    public int decimalSort(Object val1, Object val2) {
        return ((BigDecimal) val1).compareTo((BigDecimal) val2);
    }

    public void applyFilter(AjaxBehaviorEvent event) throws Exception {
        apply();
    }

    public String getStatusImg(Object status) {
        CustomItem item = (CustomItem) status;
        switch (item.getId().intValue()) {
            case 2:
                return "folder_green_backup.png";
            case 3:
                return "folder_blue_todos.png";
            default:
                return "to-do-list.png";
        }
    }

    public void setOrders(DataSet orders) {
        this.orders = orders;
    }

    public DataSet getFilter() {
        return filter;
    }

    public void setFilter(DataSet filter) {
        this.filter = filter;
    }

    public DataSet getOrders() {
        return orders;
    }

    public CustomItem getUser() {
        return user;
    }

    public void setUser(CustomItem user) {
        this.user = user;
    }

    public Integer getCount() {
        return orders.size();
    }

    public Boolean getUseCreateBtn() {
        return useCreateBtn;
    }

    public Object[] getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Object[] selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public List<Object[]> getTabs() {
        return tabs;
    }

    public void setTabs(List<Object[]> tabs) {
        this.tabs = tabs;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }
}

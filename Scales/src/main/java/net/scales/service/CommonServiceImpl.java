package net.scales.service;

import net.scales.dao.CommonDAO;
import net.scales.model.CustomItem;
import net.scales.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDAO commonDAO;

    @Override
    public CustomItem saveListItem(CustomItem selectedItem, String table, String tip, String gr1) throws Exception {
        if (selectedItem.getLabel() == null || selectedItem.getLabel().trim().isEmpty())
            throw new Exception("Introduceti va rog nume");

        if (table.equalsIgnoreCase("UNIV")) {
            selectedItem.setId(commonDAO.insertUnivItem(selectedItem, tip, gr1));
        } else {
            selectedItem.setId(commonDAO.insertSyssItem(selectedItem, tip, gr1));
        }

        if (selectedItem.getId() == null)
            throw new Exception("Errore!");

        return selectedItem;
    }

    @Override
    public List<CustomItem> completeListItem(int i, String query, String table,
                                             String tip, String gr1) {
        List<Object> items = commonDAO.completeListItem(i, query, table, tip, gr1);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<CustomItem> getTransportList(int i, String query) {
        List<Object> items = commonDAO.getTransportList(i, query);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<CustomItem> getDestinatarList(int i, String query) {
        List<Object> items = commonDAO.getDestinatarList(i, query);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<CustomItem> getPunctulList(int i, String query) {
        List<Object> items = commonDAO.getPunctulList(i, query);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<CustomItem> getTipulList(int i, String query) {
        List<Object> items = commonDAO.getTipulList(i, query);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public List<CustomItem> getDepList(int i, String query) {
        List<Object> items = commonDAO.getDepList(i, query);
        return WebUtil.toCustomItemList(items);
    }

    @Override
    public BigDecimal getDefSezon(Date fDate, long div) {
        return commonDAO.getDefSezon(fDate, div);
    }
}

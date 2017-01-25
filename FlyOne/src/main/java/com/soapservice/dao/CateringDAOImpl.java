package com.soapservice.dao;

import com.soapservice.model.Element;
import com.soapservice.model.Order;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CateringDAOImpl implements CateringDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Object[]> getItems(String sql) {
        return currentSession().createSQLQuery(sql).list();
    }

    @Override
    public Integer insertDocument(String sql, Order order) {
        Query query = currentSession().createSQLQuery(
                "CALL ycat_docs.simple_order_in(:pnr_order,:pclient,:pcoment,:pdep,:pdatetime,:ppayed,:ppayMethod,:pdelivery,:paddress,:pcallme,:pphoneNo,:psc,:pcant,:psuma)")
                .addEntity(Integer.class)
                .setParameter("pnr_order", order.getId())
                .setParameter("pclient", order.getUserId())
                .setParameter("pcoment", order.getComment())
                .setParameter("pdep", null)
                .setParameter("pdatetime", order.getDate())
                .setParameter("ppayed", order.isPayed())
                .setParameter("ppayMethod", order.getPayMethod())
                .setParameter("pdelivery", order.getDeliveryPrice())
                .setParameter("paddress", order.getDelivery().getAddress())
                .setParameter("pcallme", order.getDelivery().isCallMe())
                .setParameter("pphoneNo", order.getDelivery().getPhone());
        query.list();
        order.getElements().getElement().forEach(a -> insertElement("CALL ycat_docs.ins_element(:nrdoc,:psc,:pcant,:psuma)", 5, a));
        return 1;
    }

    @Override
    public void insertElement(String sql, Integer nrdoc, Element element) {
        Query query = currentSession().createSQLQuery(sql)
                .setParameter("nrdoc", nrdoc)
                .setParameter("psc", element.getId())
                .setParameter("pcant", element.getCount())
                .setParameter("psuma", element.getPrice());
        query.executeUpdate();
    }
}
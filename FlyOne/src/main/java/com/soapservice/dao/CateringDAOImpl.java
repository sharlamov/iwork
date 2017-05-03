package com.soapservice.dao;

import com.soapservice.model.Element;
import com.soapservice.model.Order;
import com.soapservice.model.PayMethod;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
        Query query = currentSession().createSQLQuery("select ID_TMDB_DOCS.nextval as id from dual");
        Integer nr = ((BigDecimal) query.list().iterator().next()).intValue();

        query = currentSession().createSQLQuery(
                "CALL ycat_docs.order_ins(:id, :pnr_order,:pclient,:pcoment,:pdep,:pdatetime,:ppayed,:ppayMethod,:pdelivery,:paddress,:pcallme,:pphoneNo)");
        setParam(query, "id", nr);
        setParam(query, "pnr_order", order.getId());
        setParam(query, "pclient", order.getUserId());
        setParam(query, "pcoment", order.getComment());//
        setParam(query, "pdep", null);
        setParam(query, "pdatetime", order.getDate());
        setParam(query, "ppayed", order.isPayed() ? 1 : 2);
        setParam(query, "ppayMethod", order.getPayMethod() == PayMethod.CASH ? 1 : 2);
        setParam(query, "pdelivery", 1);
        setParam(query, "paddress", order.getDelivery().getAddress());
        setParam(query, "pcallme", order.getDelivery().isCallMe() ? 1 : 0);
        setParam(query, "pphoneNo", order.getDelivery().getPhone());

        System.out.println(nr);
        query.executeUpdate();
        order.getElements().getElement().forEach(a -> insertElement(nr, a));
        return order.getId();
    }

    private void setParam(Query query, String name, Object val) {
        query.setParameter(name, val == null ? "" : val);
    }

    private void insertElement(Integer nrdoc, Element element) {
        Query query = currentSession().createSQLQuery("CALL ycat_docs.order_detail_ins(:nrdoc,:psc,:pcant,:psuma)");
        setParam(query, "nrdoc", nrdoc);
        setParam(query, "psc", element.getId());
        setParam(query, "pcant", element.getCount());
        setParam(query, "psuma", element.getPrice());
        query.executeUpdate();
    }
}
package com.soapservice.dao;

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
}
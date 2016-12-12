package com.soapservice.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
 
@Repository
public class CateringDAOImpl implements CateringDAO
{
    @Autowired
    private SessionFactory sessionFactory;

    public Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Object> getItems() {
        return currentSession().createSQLQuery("Select cod, sysfid From vmdb_docs where rownum<5").list();
    }
}
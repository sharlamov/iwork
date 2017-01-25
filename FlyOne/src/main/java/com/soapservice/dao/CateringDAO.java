package com.soapservice.dao;

import com.soapservice.model.Element;
import com.soapservice.model.Order;
import com.soapservice.model.Orders;

import java.util.List;

public interface CateringDAO
{
    List<Object[]> getItems(String sql);

    Integer insertDocument(String sql, Order order);

    void insertElement(String sql, Integer nrdoc, Element element);
}
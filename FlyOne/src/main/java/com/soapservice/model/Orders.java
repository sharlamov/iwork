package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ORDERS")
public class Orders {


    private List<Order> order;

    @XmlElement(name = "ORDER", required = true)
    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public List<Order> getOrder() {
        if (order == null) {
            order = new ArrayList<>();
        }
        return this.order;
    }

    @Override
    public String toString() {
        if(order == null)
            return "List null";
        StringBuilder sb = new StringBuilder();
        for (Order ord : order) {
            sb.append(ord.toString()).append("\r\n");
        }
        return sb.toString();
    }
}

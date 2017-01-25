package com.soapservice.endpoints;

import com.soapservice.dao.CateringService;
import com.soapservice.model.*;
import com.soapservice.utils.TestUtility;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding(style = Style.RPC)
public class FlyService {

    @Autowired
    CateringService service;

    private final TestUtility utility = new TestUtility();

    public String SET_ORDERS(@WebParam(name = "ORDERS") Orders orders) {
        return "Save success! Nrdoc: " + service.setOrders(orders);
    }

    @WebResult(name = "MENU")
    public Menu GET_MENU() {
        return service.getMenu();
    }

    @WebResult(name = "CATEGORIES")
    public Categories GET_CATEGORIES() {
        return service.getCategoriesList();
    }

    @WebResult(name = "COMPONENTS")
    public Components GET_COMPONENTS() {
        return utility.GET_COMPONENTS();
    }

    @WebResult(name = "DISCOUNTS")
    public Discounts GET_DISCOUNTS() {
        return utility.GET_DISCOUNTS();
    }

    @WebResult(name = "ELEMENTS")
    public Elements GET_ELEMENTS() {
        return service.GET_ELEMENTS();
    }

    @WebResult(name = "ORDERS")
    public Orders GET_ORDER_STATUS() {
        return utility.GET_ORDER_STATUS();
    }

    @WebResult(name = "STATUSES")
    public Statuses GET_STATUSES() {
        return service.getStatuses();
    }

}
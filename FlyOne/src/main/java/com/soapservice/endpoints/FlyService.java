package com.soapservice.endpoints;

import com.soapservice.dao.CateringService;
import com.soapservice.model.*;
import com.soapservice.utils.TestUtility;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;


@WebService
@SOAPBinding(style = Style.RPC)
public class FlyService {

    private File file = new File("FlyLog.log");
    private boolean isWork = true;

    @Autowired
    CateringService service;

    private final TestUtility utility = new TestUtility();

    @WebResult(name = "RT")
    public boolean RT() {
        isWork = true;
        return true;
    }

    @WebResult(name = "OP")
    public boolean OP() {
        isWork = false;
        return false;
    }

    public String SET_ORDERS(@WebParam(name = "ORDERS") Orders orders) {
        if (isWork) {
            service.setOrders(orders);
            log("SET_ORDERS");
            return "Saved success! ";
        } else {
            log("SET_ORDERS");
            return "Unsaved!";
        }
    }

    @WebResult(name = "MENU")
    public Menu GET_MENU() {
        log("MENU");
        return isWork ? service.getMenu() : new Menu();
    }

    @WebResult(name = "CATEGORIES")
    public Categories GET_CATEGORIES() {
        log("CATEGORIES");
        return isWork ? service.getCategoriesList() : new Categories();
    }

    @WebResult(name = "COMPONENTS")
    public Components GET_COMPONENTS() {
        log("COMPONENTS");
        return utility.GET_COMPONENTS();
    }

    @WebResult(name = "DISCOUNTS")
    public Discounts DISCOUNTS() {
        log("DISCOUNTS");
        return utility.GET_DISCOUNTS();
    }

    @WebResult(name = "ELEMENTS")
    public Elements GET_ELEMENTS() {
        log("ELEMENTS");
        return service.GET_ELEMENTS();
    }

    @WebResult(name = "ORDERS")
    public Orders GET_ORDER_STATUS() {
        log("ORDERS");
        return service.getOrderStatus();
    }

    @WebResult(name = "STATUSES")
    public Statuses GET_STATUSES() {
        log("STATUSES");
        return service.getStatuses();
    }

    public void log(String text) {
        /*try {

            FileUtils.write(file, new Date() + " - " + text + "\r\n", Charset.defaultCharset(), true);
        } catch (IOException e) {
            System.out.println(e);
        }*/
    }

}
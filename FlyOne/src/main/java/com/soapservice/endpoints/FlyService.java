package com.soapservice.endpoints;

import com.soapservice.dao.CateringService;
import com.soapservice.model.*;
import com.soapservice.utils.TestUtility;
import com.sun.xml.messaging.saaj.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;

@Service("FlyService")
@WebService
@SOAPBinding(style = Style.RPC)
public class FlyService {

    //@Resource
    WebServiceContext wsctx;

    //@Autowired
    CateringService service;

    private final String auth = "Basic " + new String(Base64.encode("fly:one".getBytes()));
    private final TestUtility utility = new TestUtility();

    public String SET_ORDERS(@WebParam(name = "orders") Orders orders){
        return checkAuth() ? orders.toString() : "Unknown user";
    }

    @WebResult(name = "Menu")
    public Menu GET_MENU() {
        return checkAuth() ? service.getMenu() : null;
    }

    @WebResult(name = "Categories")
    public Categories GET_CATEGORIES() {
        return checkAuth() ? utility.GET_CATEGORIES() : null;
    }

    @WebResult(name = "Components")
    public Components GET_COMPONENTS() {
        return checkAuth() ? utility.GET_COMPONENTS() : null;
    }

    @WebResult(name = "Discounts")
    public Discounts GET_DISCOUNTS() {
        return checkAuth() ? utility.GET_DISCOUNTS() : null;
    }

    @WebResult(name = "Elements")
    public Elements GET_ELEMENTS() {
        return checkAuth() ? utility.GET_ELEMENTS() : null;
    }

    @WebResult(name = "Orders")
    public Orders GET_ORDER_STATUS() {
        return checkAuth() ? utility.GET_ORDER_STATUS() : null;
    }

    @WebResult(name = "Statuses")
    public Statuses GET_STATUSES() {
        return checkAuth() ? utility.GET_STATUSES() : null;
    }

    private boolean checkAuth() {
        MessageContext mctx = wsctx.getMessageContext();
        //get detail from request headers
        Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
        List userList = (List) http_headers.get("Authorization");
        String username = null;
        if (userList != null) {
            //get username
            username = userList.get(0).toString();
        }
        return auth.equals(username);
    }
}

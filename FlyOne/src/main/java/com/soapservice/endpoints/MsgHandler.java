package com.soapservice.endpoints;

import com.sun.xml.messaging.saaj.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MsgHandler implements SOAPHandler {

    private final String auth = "Basic " + new String(Base64.encode("fly:one".getBytes()));

    @Override
    public boolean handleMessage(MessageContext context) {
        boolean result = true;
        boolean isResponse = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (!isResponse) {
            System.out.println("Login");
            Map http_headers = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
            List userList = (List) http_headers.get("Authorization");
            String username = null;
            if (userList != null) {
                username = userList.get(0).toString();
            }
            result = auth.equals(username);

        }

        return result;
    }

    @Override
    public boolean handleFault(MessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {
    }


    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}
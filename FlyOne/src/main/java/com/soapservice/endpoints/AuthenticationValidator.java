package com.soapservice.endpoints;

import com.sun.xml.messaging.saaj.util.Base64;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthenticationValidator implements SOAPHandler<SOAPMessageContext> {

    private final String VALID_AUTH = "Basic " + new String(Base64.encode("fly:one".getBytes()));

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        boolean outBoundProperty = (boolean) context
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // if this is an incoming message from the client
        if (!outBoundProperty) {
            try {
                System.out.println("Login");
                Map http_headers = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
                List userList = (List) http_headers.get("Authorization");
                String username = null;
                if (userList != null) {
                    username = userList.get(0).toString();
                }

                if (!VALID_AUTH.equals(username)) {
                    // Restrict the execution of the Remote Method
                    // Attach an error message as a response
                    SOAPMessage soapMsg = context.getMessage();
                    SOAPBody soapBody = soapMsg.getSOAPPart().getEnvelope().getBody();
                    SOAPFault soapFault = soapBody.addFault();
                    soapFault.setFaultString("Invalid Property");

                    throw new SOAPFaultException(soapFault);
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}
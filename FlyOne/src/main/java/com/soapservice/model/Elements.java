//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.11 at 12:07:12 AM EET 
//


package com.soapservice.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Elements {


    protected List<Element> element;

    @XmlElement
    public void setElement(List<Element> element) {
        this.element = element;
    }

    public List<Element> getElement() {
        if (element == null) {
            element = new ArrayList<Element>();
        }
        return this.element;
    }

}

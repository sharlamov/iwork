//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.11 at 12:07:12 AM EET 
//


package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MENU")
public class Menu {

    @XmlElement(name = "ITEM")
    private List<Item> item;


    public void setItem(List<Item> item) {
        this.item = item;
    }

    public List<Item> getItem() {
        if (item == null) {
            item = new ArrayList<>();
        }
        return this.item;
    }

    @Override
    public String toString() {
        if (item == null)
            return "null";
        StringBuilder sb = new StringBuilder();
        for (Item ord : item) {
            sb.append(ord.toString()).append("\r\n");
        }
        return sb.toString();
    }
}

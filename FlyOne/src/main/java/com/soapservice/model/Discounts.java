package com.soapservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Discounts {

    private List<Discount> discount;

    @XmlElement
    public void setDiscount(List<Discount> discount) {
        this.discount = discount;
    }

    public List<Discount> getDiscount() {
        if (discount == null) {
            discount = new ArrayList<Discount>();
        }
        return this.discount;
    }

}

package com.soapservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "CATEGORIES")
public class Categories {

    private List<Category> category;

    @XmlElement(name = "CATEGORY", required = true)
    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Category> getCategory() {
        if (category == null) {
            category = new ArrayList<>();
        }
        return this.category;
    }

}

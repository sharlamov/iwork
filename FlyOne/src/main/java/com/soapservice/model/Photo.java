package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PHOTO", propOrder = {
        "url"
})
public class Photo {

    @XmlElement(name = "URL", required = true)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }
}

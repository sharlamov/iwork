//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.11 at 12:07:12 AM EET 
//


package com.soapservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PHOTOS")
public class Photos {


    private List<Photo> photo;

    @XmlElement(name = "PHOTO")
    public void setPhoto(List<Photo> photo) {
        this.photo = photo;
    }

    public List<Photo> getPhoto() {
        if (photo == null) {
            photo = new ArrayList<>();
        }
        return this.photo;
    }

}

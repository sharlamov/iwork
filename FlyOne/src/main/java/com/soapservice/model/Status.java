package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "STATUS", propOrder = {
        "id",
        "name"
})
public class Status {

    @XmlElement(name = "ID")
    private int id;
    @XmlElement(name = "NAME", required = true)
    protected Label name;

    /**
     * Gets the value of the id property.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link Label }
     */
    public Label getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link Label }
     */
    public void setName(Label value) {
        this.name = value;
    }

}

package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delivery", propOrder = {
        "name", "phone", "email",
        "address",
        "callMe",
        "dateTime"
})
public class Delivery {

    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "PHONE", required = true)
    private String phone;
    @XmlElement(name = "EMAIL", required = true)
    private String email;
    @XmlElement(name = "ADDRESS", required = true)
    private String address;
    @XmlElement(name = "CALLME")
    private boolean callMe;
    @XmlElement(name = "DATETIME", required = true)
    private String dateTime;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the callMe property.
     */
    public boolean isCallMe() {
        return callMe;
    }

    /**
     * Sets the value of the callMe property.
     */
    public void setCallMe(boolean value) {
        this.callMe = value;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String value) {
        this.dateTime = value;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

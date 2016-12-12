package com.soapservice.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
public class Order {

    protected int id;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlElement(required = true)
    protected BigInteger userId;
    @XmlElement(required = true)
    protected BigInteger statusId;
    @XmlElement(required = true)
    protected BigDecimal discountCard;
    @XmlElement(required = true)
    protected BigDecimal deliveryPrice;
    @XmlElement(required = true)
    protected BigDecimal total;
    @XmlElement(required = true)
    protected PayMethod payMethod;
    protected Boolean payed;
    @XmlElement(required = true)
    protected String comment;
    @XmlElement(required = true)
    protected Delivery delivery;
    @XmlElement(required = true)
    protected Elements elements;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserId(BigInteger value) {
        this.userId = value;
    }

    /**
     * Gets the value of the statusId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatusId() {
        return statusId;
    }

    /**
     * Sets the value of the statusId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatusId(BigInteger value) {
        this.statusId = value;
    }

    /**
     * Gets the value of the discountCard property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDiscountCard() {
        return discountCard;
    }

    /**
     * Sets the value of the discountCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDiscountCard(BigDecimal value) {
        this.discountCard = value;
    }

    /**
     * Gets the value of the deliveryPrice property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    /**
     * Sets the value of the deliveryPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDeliveryPrice(BigDecimal value) {
        this.deliveryPrice = value;
    }

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTotal(BigDecimal value) {
        this.total = value;
    }

    /**
     * Gets the value of the payMethod property.
     * 
     * @return
     *     possible object is
     *     {@link PayMethod }
     *     
     */
    public PayMethod getPayMethod() {
        return payMethod;
    }

    /**
     * Sets the value of the payMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayMethod }
     *     
     */
    public void setPayMethod(PayMethod value) {
        this.payMethod = value;
    }

    /**
     * Gets the value of the payed property.
     * 
     */
    public boolean isPayed() {
        return payed;
    }

    /**
     * Sets the value of the payed property.
     * 
     */
    public void setPayed(boolean value) {
        this.payed = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the delivery property.
     * 
     * @return
     *     possible object is
     *     {@link Delivery }
     *     
     */
    public Delivery getDelivery() {
        return delivery;
    }


    public void setDelivery(Delivery value) {
        this.delivery = value;
    }


    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public Elements getElements() {
        if (elements == null) {
            elements = new Elements();
        }
        return this.elements;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", userId=" + userId +
                ", statusId=" + statusId +
                ", discountCard=" + discountCard +
                ", deliveryPrice=" + deliveryPrice +
                ", total=" + total +
                ", payMethod=" + payMethod +
                ", payed=" + payed +
                ", comment='" + comment + '\'' +
                ", delivery=" + delivery +
                ", elements=" + elements +
                '}';
    }
}

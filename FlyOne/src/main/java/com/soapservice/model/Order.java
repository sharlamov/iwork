package com.soapservice.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ORDER")
public class Order {


    @XmlElement(name = "ID", required = true)
    private int id;
    @XmlElement(name = "DATE", required = true)
    private String date;
    @XmlElement(name = "USERID", required = true)
    private BigInteger userId;
    @XmlElement(name = "STATUSID", required = true)
    private BigInteger statusId;
    @XmlElement(name = "DISCOUNTCARD", required = true)
    private BigDecimal discountCard;
    @XmlElement(name = "DELIVERYPRICE", required = true)
    private BigDecimal deliveryPrice;
    @XmlElement(name = "TOTAL", required = true)
    private BigDecimal total;
    @XmlElement(name = "PAYMETHOD", required = true)
    private PayMethod payMethod;
    @XmlElement(name = "PAYED", required = true)
    private Boolean payed;
    @XmlElement(name = "COMMENT", required = true)
    private String comment;
    @XmlElement(name = "DELIVERY", required = true)
    private Delivery delivery;
    @XmlElement(name = "ELEMENTS", required = true)
    private Elements elements;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public BigInteger getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setUserId(BigInteger value) {
        this.userId = value;
    }

    /**
     * Gets the value of the statusId property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getStatusId() {
        return statusId;
    }

    /**
     * Sets the value of the statusId property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setStatusId(BigInteger value) {
        this.statusId = value;
    }

    /**
     * Gets the value of the discountCard property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getDiscountCard() {
        return discountCard;
    }

    /**
     * Sets the value of the discountCard property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setDiscountCard(BigDecimal value) {
        this.discountCard = value;
    }

    /**
     * Gets the value of the deliveryPrice property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    /**
     * Sets the value of the deliveryPrice property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setDeliveryPrice(BigDecimal value) {
        this.deliveryPrice = value;
    }

    /**
     * Gets the value of the total property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setTotal(BigDecimal value) {
        this.total = value;
    }

    /**
     * Gets the value of the payMethod property.
     *
     * @return possible object is
     * {@link PayMethod }
     */
    public PayMethod getPayMethod() {
        return payMethod;
    }

    /**
     * Sets the value of the payMethod property.
     *
     * @param value allowed object is
     *              {@link PayMethod }
     */
    public void setPayMethod(PayMethod value) {
        this.payMethod = value;
    }

    /**
     * Gets the value of the payed property.
     */
    public boolean isPayed() {
        return payed;
    }

    /**
     * Sets the value of the payed property.
     */
    public void setPayed(boolean value) {
        this.payed = value;
    }

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the delivery property.
     *
     * @return possible object is
     * {@link Delivery }
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.12.11 at 12:07:12 AM EET 
//


package com.soapservice.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Algorithm.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Algorithm">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="standart"/>
 *     &lt;enumeration value="special"/>
 *     &lt;enumeration value="discount"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Algorithm")
@XmlEnum
public enum Algorithm {

    @XmlEnumValue("standart")
    STANDART("standart"),
    @XmlEnumValue("special")
    SPECIAL("special"),
    @XmlEnumValue("discount")
    DISCOUNT("discount");
    private final String value;

    Algorithm(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Algorithm fromValue(String v) {
        for (Algorithm c: Algorithm.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
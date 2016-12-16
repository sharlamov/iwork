package com.soapservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "COMPONENTS")
public class Components {

    private List<Component> component;

    @XmlElement(name = "COMPONENT", required = true)
    public void setComponent(List<Component> component) {
        this.component = component;
    }

    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<>();
        }
        return this.component;
    }

}

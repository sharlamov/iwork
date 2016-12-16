package com.soapservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "STATUSES")
public class Statuses {

    private List<Status> status;

    @XmlElement(name = "STATUS")
    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public List<Status> getStatus() {
        if (status == null) {
            status = new ArrayList<>();
        }
        return this.status;
    }
}

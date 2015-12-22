package com.model;

import java.math.BigDecimal;
import java.util.Date;

public class Scale extends AbstractModel<Scale> {

    private BigDecimal id;
    private String sofer;
    private String nr_vagon;
    private String auto;
    private String nr_remorca;
    private CustomItem clcdep_perevoz;
    private CustomItem clcdep_destinatt;
    private CustomItem clcprazgruz_s_12t;
    private CustomItem clcppogruz_s_12t;
    private CustomItem clcpunctto_s_12t;
    private CustomItem clcdep_postavt;
    private CustomItem clcsc_mpt;
    private CustomItem clcdep_transpt;
    private CustomItem clcsct;
    private BigDecimal sezon_yyyy;
    private String ttn_n;
    private Date ttn_data;
    private BigDecimal masa_ttn;
    private BigDecimal nr_analiz;
    private BigDecimal masa_brutto;
    private BigDecimal masa_tara;
    private BigDecimal masa_netto;
    private Date time_in;
    private Date time_out;
    private CustomItem clcelevatort;
    private BigDecimal userid;
    private BigDecimal div;

    public void calcNetto() {
        BigDecimal b = masa_brutto == null ? new BigDecimal(0) : masa_brutto;
        BigDecimal t = masa_tara == null ? new BigDecimal(0) : masa_tara;
        setMasa_netto(b.subtract(t));
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getSofer() {
        return sofer;
    }

    public void setSofer(String sofer) {
        this.sofer = sofer;
    }

    public String getNr_vagon() {
        return nr_vagon;
    }

    public void setNr_vagon(String nr_vagon) {
        this.nr_vagon = nr_vagon;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getNr_remorca() {
        return nr_remorca;
    }

    public void setNr_remorca(String nr_remorca) {
        this.nr_remorca = nr_remorca;
    }

    public CustomItem getClcdep_perevoz() {
        return clcdep_perevoz;
    }

    public void setClcdep_perevoz(CustomItem clcdep_perevoz) {
        this.clcdep_perevoz = clcdep_perevoz;
    }

    public CustomItem getClcdep_destinatt() {
        return clcdep_destinatt;
    }

    public void setClcdep_destinatt(CustomItem clcdep_destinatt) {
        this.clcdep_destinatt = clcdep_destinatt;
    }

    public CustomItem getClcprazgruz_s_12t() {
        return clcprazgruz_s_12t;
    }

    public void setClcprazgruz_s_12t(CustomItem clcprazgruz_s_12t) {
        this.clcprazgruz_s_12t = clcprazgruz_s_12t;
    }

    public CustomItem getClcpunctto_s_12t() {
        return clcpunctto_s_12t;
    }

    public void setClcpunctto_s_12t(CustomItem clcpunctto_s_12t) {
        this.clcpunctto_s_12t = clcpunctto_s_12t;
    }

    public CustomItem getClcdep_postavt() {
        return clcdep_postavt;
    }

    public void setClcdep_postavt(CustomItem clcdep_postavt) {
        this.clcdep_postavt = clcdep_postavt;
    }

    public CustomItem getClcsc_mpt() {
        return clcsc_mpt;
    }

    public void setClcsc_mpt(CustomItem clcsc_mpt) {
        this.clcsc_mpt = clcsc_mpt;
    }

    public CustomItem getClcdep_transpt() {
        return clcdep_transpt;
    }

    public void setClcdep_transpt(CustomItem clcdep_transpt) {
        this.clcdep_transpt = clcdep_transpt;
    }

    public CustomItem getClcsct() {
        return clcsct;
    }

    public void setClcsct(CustomItem clcsct) {
        this.clcsct = clcsct;
    }

    public BigDecimal getSezon_yyyy() {
        return sezon_yyyy;
    }

    public void setSezon_yyyy(BigDecimal sezon_yyyy) {
        this.sezon_yyyy = sezon_yyyy;
    }

    public String getTtn_n() {
        return ttn_n;
    }

    public void setTtn_n(String ttn_n) {
        this.ttn_n = ttn_n;
    }

    public Date getTtn_data() {
        return ttn_data;
    }

    public void setTtn_data(Date ttn_data) {
        this.ttn_data = ttn_data;
    }

    public BigDecimal getMasa_ttn() {
        return masa_ttn;
    }

    public void setMasa_ttn(BigDecimal masa_ttn) {
        this.masa_ttn = masa_ttn;
    }

    public BigDecimal getNr_analiz() {
        return nr_analiz;
    }

    public void setNr_analiz(BigDecimal nr_analiz) {
        this.nr_analiz = nr_analiz;
    }

    public BigDecimal getMasa_brutto() {
        return masa_brutto;
    }

    public void setMasa_brutto(BigDecimal masa_brutto) {
        this.masa_brutto = masa_brutto;
    }

    public BigDecimal getMasa_tara() {
        return masa_tara;
    }

    public void setMasa_tara(BigDecimal masa_tara) {
        this.masa_tara = masa_tara;
    }

    public Date getTime_in() {
        return time_in;
    }

    public void setTime_in(Date time_in) {
        this.time_in = time_in;
    }

    public Date getTime_out() {
        return time_out;
    }

    public void setTime_out(Date time_out) {
        this.time_out = time_out;
    }

    public CustomItem getClcelevatort() {
        return clcelevatort;
    }

    public void setClcelevatort(CustomItem clcelevatort) {
        this.clcelevatort = clcelevatort;
    }

    public BigDecimal getUserid() {
        return userid;
    }

    public void setUserid(BigDecimal userid) {
        this.userid = userid;
    }

    public BigDecimal getDiv() {
        return div;
    }

    public void setDiv(BigDecimal div) {
        this.div = div;
    }

    public CustomItem getClcppogruz_s_12t() {
        return clcppogruz_s_12t;
    }

    public void setClcppogruz_s_12t(CustomItem clcppogruz_s_12t) {
        this.clcppogruz_s_12t = clcppogruz_s_12t;
    }

    public BigDecimal getMasa_netto() {
        return masa_netto;
    }

    public void setMasa_netto(BigDecimal masa_netto) {
        this.masa_netto = masa_netto;
    }
}

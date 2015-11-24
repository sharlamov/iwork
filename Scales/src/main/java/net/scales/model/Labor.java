package net.scales.model;

import java.math.BigDecimal;
import java.util.Date;

public class Labor extends AbstractModel<Labor>{
	
	private BigDecimal sezon_yyyy;
	private BigDecimal nr_analiz;
	private Date data_analiz;
	private BigDecimal anlz_vlajn;
	private BigDecimal anlz_sorn;
	private BigDecimal  anlz_kislot;
	private BigDecimal anlz_zernprim;
	private BigDecimal anlz_zaraj;
	private BigDecimal  anlz_maslprim;
	private BigDecimal anlz_natura; 
	private BigDecimal anlz_kleik;
	private BigDecimal sanlz_vlajn;
	private BigDecimal  sanlz_sorn; 
	private BigDecimal sanlz_kislot; 
	private BigDecimal sanlz_zernprim;
	private BigDecimal sanlz_zaraj;
	private BigDecimal sanlz_maslprim; 
	private String txt_comment;
	private BigDecimal userid;
	private String sertificat_nr;
	private BigDecimal priznak_arm;
	private BigDecimal partida;
	private String ttn_seria; 
	private BigDecimal ttn_nr;
	private BigDecimal div;
	private CustomItem  clcsc_mpt;
	private BigDecimal anlz_pr_vih_zerna;
	private String nr_sert;
	private BigDecimal anlz_bitoe_zerno;
	private BigDecimal elevator;
	private String clcdep_postavt;
	private String clcdep_gruzootpravitt;
	private CustomItem clcskladt;

	public BigDecimal getSezon_yyyy() {
		return sezon_yyyy;
	}

	public void setSezon_yyyy(BigDecimal sezon_yyyy) {
		this.sezon_yyyy = sezon_yyyy;
	}

	public BigDecimal getNr_analiz() {
		return nr_analiz;
	}

	public void setNr_analiz(BigDecimal nr_analiz) {
		this.nr_analiz = nr_analiz;
	}

	public Date getData_analiz() {
		return data_analiz;
	}

	public void setData_analiz(Date data_analiz) {
		this.data_analiz = data_analiz;
	}

	public BigDecimal getAnlz_vlajn() {
		return anlz_vlajn;
	}

	public void setAnlz_vlajn(BigDecimal anlz_vlajn) {
		this.anlz_vlajn = anlz_vlajn;
	}

	public BigDecimal getAnlz_sorn() {
		return anlz_sorn;
	}

	public void setAnlz_sorn(BigDecimal anlz_sorn) {
		this.anlz_sorn = anlz_sorn;
	}

	public BigDecimal getAnlz_kislot() {
		return anlz_kislot;
	}

	public void setAnlz_kislot(BigDecimal anlz_kislot) {
		this.anlz_kislot = anlz_kislot;
	}

	public BigDecimal getAnlz_zernprim() {
		return anlz_zernprim;
	}

	public void setAnlz_zernprim(BigDecimal anlz_zernprim) {
		this.anlz_zernprim = anlz_zernprim;
	}

	public BigDecimal getAnlz_zaraj() {
		return anlz_zaraj;
	}

	public void setAnlz_zaraj(BigDecimal anlz_zaraj) {
		this.anlz_zaraj = anlz_zaraj;
	}

	public BigDecimal getAnlz_maslprim() {
		return anlz_maslprim;
	}

	public void setAnlz_maslprim(BigDecimal anlz_maslprim) {
		this.anlz_maslprim = anlz_maslprim;
	}

	public BigDecimal getAnlz_natura() {
		return anlz_natura;
	}

	public void setAnlz_natura(BigDecimal anlz_natura) {
		this.anlz_natura = anlz_natura;
	}

	public BigDecimal getAnlz_kleik() {
		return anlz_kleik;
	}

	public void setAnlz_kleik(BigDecimal anlz_kleik) {
		this.anlz_kleik = anlz_kleik;
	}

	public BigDecimal getSanlz_vlajn() {
		return sanlz_vlajn;
	}

	public void setSanlz_vlajn(BigDecimal sanlz_vlajn) {
		this.sanlz_vlajn = sanlz_vlajn;
	}

	public BigDecimal getSanlz_sorn() {
		return sanlz_sorn;
	}

	public void setSanlz_sorn(BigDecimal sanlz_sorn) {
		this.sanlz_sorn = sanlz_sorn;
	}

	public BigDecimal getSanlz_kislot() {
		return sanlz_kislot;
	}

	public void setSanlz_kislot(BigDecimal sanlz_kislot) {
		this.sanlz_kislot = sanlz_kislot;
	}

	public BigDecimal getSanlz_zernprim() {
		return sanlz_zernprim;
	}

	public void setSanlz_zernprim(BigDecimal sanlz_zernprim) {
		this.sanlz_zernprim = sanlz_zernprim;
	}

	public BigDecimal getSanlz_zaraj() {
		return sanlz_zaraj;
	}

	public void setSanlz_zaraj(BigDecimal sanlz_zaraj) {
		this.sanlz_zaraj = sanlz_zaraj;
	}

	public BigDecimal getSanlz_maslprim() {
		return sanlz_maslprim;
	}

	public void setSanlz_maslprim(BigDecimal sanlz_maslprim) {
		this.sanlz_maslprim = sanlz_maslprim;
	}

	public String getTxt_comment() {
		return txt_comment;
	}

	public void setTxt_comment(String txt_comment) {
		this.txt_comment = txt_comment;
	}

	public BigDecimal getUserid() {
		return userid;
	}

	public void setUserid(BigDecimal userid) {
		this.userid = userid;
	}

	public String getSertificat_nr() {
		return sertificat_nr;
	}

	public void setSertificat_nr(String sertificat_nr) {
		this.sertificat_nr = sertificat_nr;
	}

	public BigDecimal getPriznak_arm() {
		return priznak_arm;
	}

	public void setPriznak_arm(BigDecimal priznak_arm) {
		this.priznak_arm = priznak_arm;
	}

	public BigDecimal getPartida() {
		return partida;
	}

	public void setPartida(BigDecimal partida) {
		this.partida = partida;
	}

	public String getTtn_seria() {
		return ttn_seria;
	}

	public void setTtn_seria(String ttn_seria) {
		this.ttn_seria = ttn_seria;
	}

	public BigDecimal getTtn_nr() {
		return ttn_nr;
	}

	public void setTtn_nr(BigDecimal ttn_nr) {
		this.ttn_nr = ttn_nr;
	}

	public BigDecimal getDiv() {
		return div;
	}

	public void setDiv(BigDecimal div) {
		this.div = div;
	}

	public CustomItem getClcsc_mpt() {
		return clcsc_mpt;
	}

	public void setClcsc_mpt(CustomItem clcsc_mpt) {
		this.clcsc_mpt = clcsc_mpt;
	}

	public BigDecimal getAnlz_pr_vih_zerna() {
		return anlz_pr_vih_zerna;
	}

	public void setAnlz_pr_vih_zerna(BigDecimal anlz_pr_vih_zerna) {
		this.anlz_pr_vih_zerna = anlz_pr_vih_zerna;
	}

	public String getNr_sert() {
		return nr_sert;
	}

	public void setNr_sert(String nr_sert) {
		this.nr_sert = nr_sert;
	}

	public BigDecimal getAnlz_bitoe_zerno() {
		return anlz_bitoe_zerno;
	}

	public void setAnlz_bitoe_zerno(BigDecimal anlz_bitoe_zerno) {
		this.anlz_bitoe_zerno = anlz_bitoe_zerno;
	}

	public BigDecimal getElevator() {
		return elevator;
	}

	public void setElevator(BigDecimal elevator) {
		this.elevator = elevator;
	}

	public String getClcdep_postavt() {
		return clcdep_postavt;
	}

	public void setClcdep_postavt(String clcdep_postavt) {
		this.clcdep_postavt = clcdep_postavt;
	}

	public String getClcdep_gruzootpravitt() {
		return clcdep_gruzootpravitt;
	}

	public void setClcdep_gruzootpravitt(String clcdep_gruzootpravitt) {
		this.clcdep_gruzootpravitt = clcdep_gruzootpravitt;
	}

	public CustomItem getClcskladt() {
		return clcskladt;
	}

	public void setClcskladt(CustomItem clcskladt) {
		this.clcskladt = clcskladt;
	}
}

package net.scales.bean;

import net.scales.model.CustomItem;
import net.scales.model.Scale;
import net.scales.service.CommonService;
import net.scales.service.ScaleService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "scalesBean")
@ViewScoped
public class ScalesBean extends AbstractBean {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{scaleServiceImpl}")
	private ScaleService scaleService;

	@ManagedProperty(value = "#{commonServiceImpl}")
	private CommonService commonService;

	private List<Scale> scalesList;

	private Scale selectedScale;

	private CustomItem selectedItem;

	private int limit;

	private String type;

	@Override
	public void init() {
		type = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("type");

		setStartDate(nvl(getStartDate()));
		setEndDate(nvl(getEndDate()));
		dateChangeHandler();
		selectedScale = new Scale();
		selectedItem = new CustomItem();

		if (type.equalsIgnoreCase("IN")) {
			limit = scaleService.getLimit();
		}
	}

	public void dateChangeHandler() {
		scalesList = scaleService.getScales(type, getStartDate(), getEndDate(),
				getLoggedUser());
	}

	public String bgcolor(Scale sd) {
		BigDecimal mNetto = sd.getMasa_netto() == null ? new BigDecimal(0) : sd
				.getMasa_netto();
		BigDecimal mTtn = sd.getMasa_ttn() == null ? new BigDecimal(0) : sd
				.getMasa_ttn();

		switch (mNetto.compareTo(mTtn)) {
		case 0:
			return "rowred";
		case 1:
			return "rowstronggreen";
		case -1:
			return mNetto.subtract(mTtn).intValue() < limit ? "rowstrongred"
					: "rowred";
		default:
			return "";
		}
	}

	public void saveScale() {
		try {
			Date cDate = nvl(getStartDate());
			selectedScale.calcNetto();

			if (selectedScale.getId() == null) {
				selectedScale.setTime_in(cDate);
				scaleService.insertScale(type, selectedScale, getLoggedUser());
				dateChangeHandler();
				selectedScale = null;
			} else {
				scaleService.updateScale(type, selectedScale, getLoggedUser());
			}
		} catch (Exception e) {
			facesException(e);
		}
	}

	public Scale getNewScale() {
		Scale sd = new Scale();
		BigDecimal sezon = commonService.getDefSezon(getStartDate(),
				getLoggedUser().getDiv().getId().longValue());
		sd.setSezon_yyyy(sezon);
		return sd;
	}

	public void saveListItem(CustomItem ci, String table, String tip, String gr1) {
		try {
			commonService.saveListItem(selectedItem, table, tip, gr1);
			facesMessage("Datele au fost salvat cu succes");
			selectedItem = new CustomItem();
		} catch (Exception e) {
			facesException(e);
		}
	}

	public List<CustomItem> completeListItem(String query) {
		Map<String, Object> map = UIComponent.getCurrentComponent(
				FacesContext.getCurrentInstance()).getAttributes();
		String table = (String) map.get("table");
		String tip = (String) map.get("tip");
		String gr1 = (String) map.get("gr1");
		return commonService.completeListItem(10, query, table, tip, gr1);
	}

	public CustomItem copyCustomItem(CustomItem ci) {
		CustomItem newItem = new CustomItem();
		if (ci != null && ci.getId() == null)
			newItem.setLabel(ci.getLabel());
		return newItem;
	}

	public List<CustomItem> completeTransportList(String query) {
		return commonService.getTransportList(10, query);
	}

	public List<CustomItem> completeDestinatarList(String query) {
		return commonService.getDestinatarList(10, query);
	}

	public List<CustomItem> completePunctulList(String query) {
		return commonService.getPunctulList(10, query);
	}

	public List<CustomItem> completeTipulList(String query) {
		return commonService.getTipulList(10, query);
	}

	public void setScaleService(ScaleService scaleService) {
		this.scaleService = scaleService;
	}

	public List<Scale> getScalesList() {
		return scalesList;
	}

	public void setScalesList(List<Scale> scalesList) {
		this.scalesList = scalesList;
	}

	public CustomItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CustomItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Scale getSelectedScale() {
		return selectedScale;
	}

	public void setSelectedScale(Scale selectedScale) {
		this.selectedScale = selectedScale;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

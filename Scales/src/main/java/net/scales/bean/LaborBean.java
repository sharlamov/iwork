package net.scales.bean;

import net.scales.model.CustomItem;
import net.scales.model.Labor;
import net.scales.service.CommonService;
import net.scales.service.LaborService;
import net.scales.service.ScaleService;
import net.scales.util.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "laborBean")
@ViewScoped
public class LaborBean extends AbstractBean {

    private static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{laborServiceImpl}")
    private LaborService laborService;

    @ManagedProperty(value = "#{commonServiceImpl}")
    private CommonService commonService;

    private List<Labor> laborList;

    private Labor selectedLabor;

    private Labor oldLabor;

    private String type;

    @Override
    public void init() {
        type = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("type");
        setStartDate(nvl(getStartDate()));
        dateChangeHandler();
    }

    public void dateChangeHandler() {
        laborList = laborService.getLabors(type.equalsIgnoreCase("IN"), getStartDate(), getLoggedUser());
    }

    public Labor getNewLabor() {
        setOldLabor(null);
        Labor l = new Labor();
        BigDecimal sezon = commonService.getDefSezon(getStartDate(),
                getLoggedUser().getDiv().getId().longValue());
        l.setSezon_yyyy(sezon);
        l.setData_analiz(getStartDate());
        return l;
    }

    public void openEditForm(Labor labor) {
        setOldLabor(WebUtil.copy(labor));
        setSelectedLabor(labor);
    }

    public void saveLabor() {
        try {
            if (oldLabor == null) {
                laborService.insertLabor(type.equalsIgnoreCase("in"), selectedLabor, getLoggedUser());
                dateChangeHandler();
                selectedLabor = null;
            } else {
                laborService.updateLabor(selectedLabor, oldLabor,
                        getLoggedUser());
                setOldLabor(null);
            }
        } catch (Exception e) {
            facesException(e);
        }
    }

    public List<CustomItem> completeTipulList(String query) {
        return commonService.getTipulList(10, query);
    }

    public List<CustomItem> completeDepList(String query) {
        return commonService.getDepList(10, query);
    }

    public void setLaborService(LaborService laborService) {
        this.laborService = laborService;
    }

    public List<Labor> getLaborList() {
        return laborList;
    }

    public void setLaborList(List<Labor> laborList) {
        this.laborList = laborList;
    }

    public Labor getSelectedLabor() {
        return selectedLabor;
    }

    public void setSelectedLabor(Labor selectedLabor) {
        this.selectedLabor = selectedLabor;
    }

    public Labor getOldLabor() {
        return oldLabor;
    }

    public void setOldLabor(Labor oldLabor) {
        this.oldLabor = oldLabor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }
}

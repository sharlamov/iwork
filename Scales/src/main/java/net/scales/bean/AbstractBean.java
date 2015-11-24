package net.scales.bean;

import net.scales.model.CustomUser;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public abstract class AbstractBean implements Serializable {

	private static final long serialVersionUID = -7244932139281652821L;

	private Date startDate;

	private Date endDate;

	private CustomUser loggedUser;

	private boolean isInitialized = false;

	public void initController(ComponentSystemEvent componentSystemEvent)
			throws Exception {
		if (!isInitialized) {
			Object principal = SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			if (principal instanceof UserDetails) {
				setLoggedUser((CustomUser) principal);
				init();
				isInitialized = true;
			}
		}
	}

	public abstract void init() throws Exception;

	void facesMessage(String text){
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", text);
		context.addMessage(null, message);
	}
	
	void facesException(Exception e) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"Operation error", e.getMessage());
		context.addMessage(null, message);
		context.validationFailed();
		e.printStackTrace();
	}

	public StreamedContent getHelpFile() throws IOException {
		String path = "/resources/files/Instructiune_RO.docx";
		String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(path);
		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(path);
		return new DefaultStreamedContent(stream, contentType, "Instructiune.docx");
	}

	Date nvl(Date d) {
		return d == null ? new Date() : d;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CustomUser getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(CustomUser loggedUser) {
		this.loggedUser = loggedUser;
	}
}
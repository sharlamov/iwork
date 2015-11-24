package net.scales.model;

import net.scales.enums.WebRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

public class CustomUser extends AbstractModel<CustomUser> implements UserDetails {

	private static final long serialVersionUID = -8055629994460003294L;

	private BigDecimal id;
	private String username;
	private String password;
	private CustomItem elevator;
	private CustomItem div;
	private Integer scaleType;
	private Integer adminLevel;
	private WebRole level;
	private Set<GrantedAuthority> authorities;

	public void setUsername(String str) {
		username = str;
	}

	public void setPassword(String str) {
		password = str;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setCustomerAuthorities(Set<GrantedAuthority> roles) {
		authorities = roles;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public CustomItem getElevator() {
		return elevator;
	}

	public void setElevator(CustomItem elevator) {
		this.elevator = elevator;
	}

	public Integer getScaleType() {
		return scaleType;
	}

	public void setScaleType(Integer scaleType) {
		this.scaleType = scaleType;
	}

	public CustomItem getDiv() {
		return div;
	}

	public void setDiv(CustomItem div) {
		this.div = div;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public Integer getAdminLevel() {
		return adminLevel;
	}

	public void setAdminLevel(Integer adminLevel) {
		this.adminLevel = adminLevel;
	}

	public WebRole getLevel() {
		return level;
	}

	public void setLevel(WebRole level) {
		this.level = level;
	}
}
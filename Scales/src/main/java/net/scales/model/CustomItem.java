package net.scales.model;

import net.scales.util.WebUtil;

import java.math.BigDecimal;

public class CustomItem extends AbstractModel<CustomItem>{

	private BigDecimal id;
	private String name;
	private String label;
	
	public CustomItem() {
		super();
	}

	public CustomItem(BigDecimal id, String name, String label) {
		super();
		this.id = id;
		this.name = name;
		this.label = label;
	}

	public CustomItem(Object obj0, Object obj1, Object obj2) {
		super();
		this.id = (BigDecimal) obj0;
		this.name = WebUtil.parse(obj1, String.class);
		this.label = WebUtil.parse(obj2, String.class);
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return (id == null ? "" : id) + "#" + (label == null ? "" : label);
	}
}

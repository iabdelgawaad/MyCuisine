package com.cooking.mycuisine.model;

public class Ingredient {

	private String amount;
	private String fraction;
	private String unit;
	private String component;
	private String group;

	public Ingredient() {
		this.amount = "";
		this.fraction = "";
		this.unit = "";
		this.component = "";
		this.group = "";
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFraction() {
		return fraction;
	}

	public void setFraction(String fraction) {
		this.fraction = fraction;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}

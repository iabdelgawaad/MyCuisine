package com.cooking.mycuisine.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements java.io.Serializable {

	private String name;
	private String image_string;
	private int numOfMinutes;
	private int numofServings;
	private boolean isDraft; // = flase-->means that the Recipe is finished
	private Category category;
	private List<Ingredient> ingredients;
	private List<Direction> directions;

	public Recipe() {
		this.name = "";
		this.image_string = "";
		this.numOfMinutes = 0;
		this.numofServings = 0;
		this.isDraft = false;
	}

	public Recipe(String name, String image_string, int numOfMinutes,
			int numofServings, boolean isDraft) {

		this.name = name;
		this.image_string = image_string;
		this.numOfMinutes = numOfMinutes;
		this.numofServings = numofServings;
		this.isDraft = isDraft;

	}

	// Setters & getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage_string() {
		return image_string;
	}

	public void setImage_string(String image_string) {
		this.image_string = image_string;
	}

	public int getNumOfMinutes() {
		return numOfMinutes;
	}

	public void setNumOfMinutes(int numOfMinutes) {
		this.numOfMinutes = numOfMinutes;
	}

	public int getNumofServings() {
		return numofServings;
	}

	public void setNumofServings(int numofServings) {
		this.numofServings = numofServings;
	}

	public boolean isDraft() {
		return isDraft;
	}

	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	public Category getCategory() {

		if (category == null) {
			category = new Category();
		}
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Ingredient> getIngredients() {

		if (ingredients == null) {
			ingredients = new ArrayList<Ingredient>();
		}
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public List<Direction> getDirections() {

		if (directions == null) {
			directions = new ArrayList<Direction>();
		}
		return directions;
	}

	public void setDirections(List<Direction> directions) {
		this.directions = directions;
	}

}

package com.example.mysheetreader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Block implements Serializable {
	private String blockId;
	private List<Category> categories;

	public Block(String id) {
		this.categories = new ArrayList<>();
		this.blockId = id;
	}

	public String getBlockId(){
		return this.blockId;
	}

	public Integer getCategoresSize(){
		return this.categories.size();
	}

	public List<Category> getCategories(){
		return this.categories;
	}

	public void createCategory(String name) {
		Category category = new Category(name);
		categories.add(category);
	}

	public Category getCategory(int position) {
		return categories.get(position);
	}

	public void addRow(int categoryPosition, String subCategory, String data) {
		Category category = this.getCategory(categoryPosition);
		category.createRow(subCategory, data);
	}

	public Category.Row getRow(int categoryPosition, int rowPosition) {
		return this.getCategory(categoryPosition).getRow(rowPosition);
	}

	public void addData(String newData, int categoryPosition, int rowPosition) {
		Category category = categories.get(categoryPosition);
		category.addData(newData, rowPosition);
	}

	public Integer getDataInteger(int categoryPosition, int rowPosotion) {
		return getCategory(categoryPosition).getDataInteger(rowPosotion);
	}

	public String getData(int categoryPosition, int rowPosotion) {
		return getCategory(categoryPosition).getData(rowPosotion);
	}


	public class Category implements  Serializable {
		private String name;
		private List<Row> rows;

		private Category(String name) {
			this.name = name;
			this.rows = new ArrayList<>();
		}

		public String getName(){
			return this.name;
		}

		public List<Block.Category.Row> getRows(){
			return this.rows;
		}

		private void createRow(String subCategory, String data) {
			Row row = new Row(subCategory, data);
			rows.add(row);
		}

		private Row getRow(int position){
			return rows.get(position);
		}

		private void addData(String newdata, int rowPosition) {
			Row row = getRow(rowPosition);
			row.addData(newdata);
		}

		private Integer getDataInteger(int rowPosition){
			return getRow(rowPosition).getDataInteger();
		}

		private String getData(int rowPosition){
			return getRow(rowPosition).getData();
		}

		public class Row implements  Serializable {
			private String subCategory;
			private String data;
			private Integer dataInteger;
			private String add;
			private Boolean hasChanged = false;
			private String rowRow;

			private Row(String subCategory, String data) {
				this.subCategory = subCategory;
				this.data = data;
				this.dataInteger = 0;
				createDataInteger(data);
				this.add = "";
			}
			public Integer getDataInteger() {
				return this.dataInteger;
			}

			public String getData() {
				return this.data;
			}

			public String getSubCategory(){
				return this.subCategory;
			}

			public String getAdd() {
				return this.add;
			}

			public void setAdd(String add) {
				this.add = add;
			}

			public void setData(String data) {
				this.data = data;
				createDataInteger(data);
			}

			public Boolean getHasChanged() {
				return hasChanged;
			}

			public void setHasChanged() {
				this.hasChanged = !this.hasChanged;
			}

			public String getRowRow() {
				return rowRow;
			}

			public void addData(String newData) {
				this.data += "+" + newData;
				addDataInteger(newData);
			}

			private void addDataInteger(String newdata) {
				this.dataInteger += Integer.valueOf(newdata);
			}

			private void createDataInteger(String data) {
				String[] subStrings = data.split("\\+");
				// format of data is =number + number + number ... with possible whitespaces.
				subStrings[0] = subStrings[0].substring(subStrings[0].indexOf("=") + 1);
				for (String substring:subStrings) {
					//remove all whitespaces
					substring = substring.replaceAll("\\s+", "");
					this.dataInteger += Integer.valueOf(substring);
				}
			}
		}
	}
}

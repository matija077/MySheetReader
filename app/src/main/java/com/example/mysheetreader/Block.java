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

	public void setcategory(int position, Block.Category category) {
		this.categories.set(position, category);
	}

	public void addRow(int categoryPosition, String subCategory, String data, Double dataDouble, String rowRow) {
		Category category = this.getCategory(categoryPosition);
		category.createRow(subCategory, data, dataDouble, rowRow);
	}

	public Category.Row getRow(int categoryPosition, int rowPosition) {
		return this.getCategory(categoryPosition).getRow(rowPosition);
	}

	public void addData(String newData, int categoryPosition, int rowPosition) {
		Category category = categories.get(categoryPosition);
		category.addData(newData, rowPosition);
	}

	public Double getDataInteger(int categoryPosition, int rowPosotion) {
		return getCategory(categoryPosition).getDataDouble(rowPosotion);
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

		private void createRow(String subCategory, String data, Double dataDouble, String rowRow) {
			Row row = new Row(subCategory, data, dataDouble, rowRow);
			rows.add(row);
		}

		private Row getRow(int position){
			return rows.get(position);
		}

		private void addData(String newdata, int rowPosition) {
			Row row = getRow(rowPosition);
			row.addData(newdata);
		}

		public void resetHasChanged() {
			for (Row row: this.rows) {
				row.resetHasChanged();
			}
		}

		private Double getDataDouble(int rowPosition){
			return getRow(rowPosition).getDataDouble();
		}

		private String getData(int rowPosition){
			return getRow(rowPosition).getData();
		}

		public class Row implements  Serializable {
			private String subCategory;
			private String data;
			private Double dataDouble;
			private String add;
			private Boolean hasChanged = false;
			private String rowRow;

			private Row(String subCategory, String data, Double dataDouble, String rowRow) {
				this.subCategory = subCategory;
				this.data = data;
				this.dataDouble = dataDouble;
				//reateDataInteger(data);
				this.add = "";
				this.rowRow = rowRow;
			}
			public Double getDataDouble() {
				return this.dataDouble;
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
				//resetting dataInteger so we don't have to change createDatatInteger
				this.dataDouble = 0.0;
				createDataInteger(data);
			}

			public Boolean getHasChanged() {
				return hasChanged;
			}

			public void setHasChanged() {
				this.hasChanged = Boolean.TRUE;
			}

			public void resetHasChanged(){
				this.hasChanged = Boolean.FALSE;
			}

			public String getRowRow() {
				return rowRow;
			}

			public void addData(String newData) {
				this.data += "+" + newData;
				addDataDouble(newData);
			}

			private void addDataDouble(String newdata) {
				this.dataDouble += Double.valueOf(newdata);
			}

			private void createDataInteger(String data) {
				String[] subStrings = data.split("\\+");
				// format of data is =number + number + number ... with possible whitespaces.
				subStrings[0] = subStrings[0].substring(subStrings[0].indexOf("=") + 1);
				for (String substring:subStrings) {
					//remove all whitespaces
					substring = substring.replaceAll("\\s+", "");
					this.dataDouble += Integer.valueOf(substring);
				}
			}
		}
	}
}

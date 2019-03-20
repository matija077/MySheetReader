package com.example.mysheetreader;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;


public class ParseValueRange {

	public ParseValueRange(){
	}

	public void parseRange(ValueRange valueRange, Block block) {
		List<List<Object>> values = valueRange.getValues();
		String range = valueRange.getRange();
		range = calculateRowRange(range, 0);

		//for variableLengthCategory last one is empty
		if (values==null) {
			return;
		}

		for (int i = 0; i < values.size(); i++) {
			List<Object> row = values.get(i);

			String value1 = String.valueOf(row.get(0));
			String value2 = String.valueOf(row.get(1));
			String value3 = String.valueOf(row.get(2));

			if (block.getCategoresSize() == 0 || value1!="") {
				block.createCategory(value1);
			}
			block.addRow(block.getCategoresSize() - 1, value2, value3, range);
			range = calculateRowRange(range, 1);
			/*if (!block.getCategoryName(block.getCategoresSize() - 1).equals(value1)) {
				block.createCategory(value1);
			} else {
				block.addRow(block.getCategoresSize() - 1, value2, value3);
			}*/

		}

	}

	public String calculateRowRange(String range, int offset){
		String sheetName = range.substring(0, range.indexOf("!") + 1);
		String temp = range.substring(range.indexOf("!") + 1);

		int i = 0;
		while (i < temp.length() && !Character.isDigit(temp.charAt(i))) i++;
		int j = i;
		while (j < temp.length() && Character.isDigit(temp.charAt(j))) j++;

		int rowRow = Integer.valueOf(temp.substring(i, j));
		rowRow += offset;

		String rowRange;
		rowRange = sheetName + "C" + String.valueOf(rowRow) + ":" + "C" + String.valueOf(rowRow);

		return rowRange;
	}

	public List<List<Object>> createValueRange(Block.Category category) {
		List<List<Object>> data = new ArrayList<>();

		for (int i=0; i < category.getRows().size(); i++) {
			List<Object> row = new ArrayList<>();
			row.add(category.getName());
			row.add(category.getRows().get(i).getSubCategory());
			row.add(category.getRows().get(i).getData());
			data.add(row);
		}

		return data;
	}
}

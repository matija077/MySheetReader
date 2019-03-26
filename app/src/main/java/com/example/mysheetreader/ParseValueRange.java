package com.example.mysheetreader;

import android.util.Log;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;


public class ParseValueRange {

	public ParseValueRange(){
	}

	public List<Block> parseListOfRangesGetData(List<ValueRange> valueRanges, String numberOfBlocks) {
		List<List<Object>> valuesData = valueRanges.get(0).getValues();
		List<List<Object>> valuesDataDouble = valueRanges.get(1).getValues();
		String range = valueRanges.get(0).getRange();
		// beginign range is one lower than first row and then we always udpate for one row eacth time
		// we enter the loop
		range = calculateRowRange(range, -1);
		List<Block> blocks = new ArrayList<>();
		int blockCounter = 1;
		int numberOfBlocksInt = Integer.valueOf(numberOfBlocks);
		final String blockTemplate = "Block";
		Block block = new Block(blockTemplate + String.valueOf(blockCounter));

		Boolean newBlock = Boolean.FALSE;
		Boolean header = Boolean.FALSE;

		try {

			for (int i = 0; i < valuesData.size(); i++) {
				List<Object> rowData = null;
				List<Object> rowDataDouble = null;
				rowData = valuesData.get(i);
				rowDataDouble = valuesDataDouble.get(i);
				range = calculateRowRange(range, 1);
				// and not empty
				if (newBlock == Boolean.TRUE && rowData.size() > 0) {
					if (header == Boolean.TRUE) {
						header = Boolean.FALSE;
						continue;
					}
					blocks.add(block);
					blockCounter += 1;
					block = new Block(blockTemplate + String.valueOf(blockCounter));
					newBlock = Boolean.FALSE;
				}

				if (rowData.size() == 0) {
					newBlock = Boolean.TRUE;
					header = Boolean.TRUE;
					if (blocks.size() + 1 >= numberOfBlocksInt) {
						break;
					} else {
						continue;
					}
				}


				String value1 = "";
				String value2 = "";
				String value3 = "";
				String value3Double = "";
				try {
					value1 = String.valueOf(rowData.get(0));
					value2 = String.valueOf(rowData.get(1));
					value3 = String.valueOf(rowData.get(2));
					value3Double = String.valueOf(rowDataDouble.get(2));
				} catch (Exception e) {
					Log.e("a", "a");
				}

				if (block.getCategoresSize() == 0 || value1 != "") {
					block.createCategory(value1);
				}

				block.addRow(block.getCategoresSize() - 1, value2, value3,
						Double.valueOf(value3Double), range);
			}
		} catch (Exception exception) {
			Log.d("a", "a");
		}
		blocks.add(block);

		return blocks;
	}

	public void parseListOfRanges(List<ValueRange> valueRanges, Block block) {
		for (ValueRange valueRange:valueRanges) {
			parseRange(valueRange, block);
		}

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
			//block.addRow(block.getCategoresSize() - 1, value2, value3, range);
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

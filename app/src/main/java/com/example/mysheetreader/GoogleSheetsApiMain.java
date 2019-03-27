package com.example.mysheetreader;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GoogleSheetsApiMain extends GoogleSheetApiHelper {
	private Context context;
	private String spreadsheetID;
	private final int numberOfRowsFixedLenght = 27;
	private final String TAG = "GoogleSheetsApiMain";
	private List<Block> blocks;
	private TaskTracer taskTracer;
	private Exception exception;
	private Double dataDouble;

	public GoogleSheetsApiMain(TaskTracer taskTracer) {
		this.taskTracer = taskTracer;
	}

	public GoogleSheetsApiMain() {

	}

	public String getSpreadsheetID() {
		return spreadsheetID;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public Exception getException() {
		return exception;
	}

	public Double getDataDouble() {
		return dataDouble;
	}

	@Override
	public Boolean getData(Object... params) {
		Map map = (Map) params[0];
		try {
			String url = (String) map.get("url");
			String maxRows = (String) String.valueOf(map.get("maxRows"));
			String numberOfBlocks = String.valueOf(map.get("numberOfBlocks"));
			String sheetIDPreference = (String) map.get("sheetIDUsed");
			context = (Context) params[1];
			GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

			String sheetId;
			if (sheetIDPreference.equals("")) {
				 sheetId = url.substring(url.indexOf('=') + 1);
			} else {
				sheetId = sheetIDPreference;
			}

			// url for spreadsheet wihtotu sheetgid
			String urlToSave = url.substring(0, url.indexOf('=') + 1);
			this.setSpreadsheetURL(urlToSave);
			// 3 for 3 characters in '/d/'
			// spreadsheetID to be saved in a file for saveData
			spreadsheetID = url.substring(url.indexOf("/d/") + 3, url.indexOf("/edit"));
			String sheetName = "";

			String columnStart = "A";
			String columnEnd = "C";
			String startRow = "3";

			String endRowFixedLenght = String.valueOf(Integer.parseInt(startRow) +
					numberOfRowsFixedLenght);
			//String dataRange = columnStart + startRow + ":" + columnEnd + endRowFixedLenght;
			String dataRange = "A2" + ":" + "C" + maxRows;

			prepare(context);
			Sheets sheets = getSheets();
			this.setDataRange(dataRange);
			this.setSheetId(sheetId);
			this.setSpreadsheetID(spreadsheetID);
			prepareSheets();
			sheetName = this.getSheetName();
			dataRange = this.getDataRange();

			List<ValueRange> valueRanges = new ArrayList<>();
			ValueRange result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
					.setValueRenderOption("FORMULA")
					.execute();
			valueRanges.add(result);
			result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
					.setValueRenderOption("UNFORMATTED_VALUE")
					.execute();
			valueRanges.add(result);

			ParseValueRange parseValueRange = new ParseValueRange();
			blocks = parseValueRange.parseListOfRangesGetData(valueRanges, numberOfBlocks);
			//taskTracer.onTaskCompleted(new Object());
			saveToFile();
			return Boolean.TRUE;
	} catch (Exception e) {
			Log.e(TAG, "ola");
			//taskTracer.onTaskFailed(e);
			exception = e;
			return Boolean.FALSE;
		}
}

	@Override
	public Boolean savedata(Object...params) {
		try {
			Map map = (Map) params[0];
			context = (Context) params[1];
			Block.Category category = (Block.Category) map.get(context.getResources().getString(R.string.row_key));
			ParseValueRange parseValueRange = new ParseValueRange();

			prepare(context);
			Sheets sheets = getSheets();

			List<ValueRange> data = new ArrayList<>();
			for (Block.Category.Row row : category.getRows()) {
				if (!row.getAdd().equals("")) {
					List<List<Object>> values = new ArrayList<>();
					Block block = new Block("temp");
					block.createCategory("temp");
					block.addRow(0, row.getSubCategory(), row.getData(),
							row.getDataDouble(), row.getRowRow());
					Block.Category.Row rowToSave = block.getRow(0, 0);
					rowToSave.addData(row.getAdd());
					List<Object> value = prepareRow(rowToSave);
					values.add(value);
					data.add(new ValueRange()
							.setRange(row.getRowRow())
							.setValues(values));
				}
			}
			String spreadSheetId = readFromFile();


			BatchUpdateValuesRequest batchUpdateValuesRequest = new BatchUpdateValuesRequest()
					.setValueInputOption("USER_ENTERED")
					.setData(data);

			BatchUpdateValuesResponse response = sheets.spreadsheets().values().batchUpdate(
					spreadSheetId, batchUpdateValuesRequest).execute();
			return Boolean.TRUE;

		} catch (Exception e) {
			Log.e(TAG, "a");
			exception = e;
			return Boolean.FALSE;
		}
	}

	@Override
	public void prepare(Context context) {
		super.prepare(context);
	}

	@Override
	public Sheets getSheets() {
		return super.getSheets();
	}

	public Boolean saveOneRow(Block.Category.Row row, String data, Context context) {
		try {
			prepare(context);
			Sheets sheets = getSheets();
			this.context = context;

			List<List<Object>> values = new ArrayList<>();
			Block block = new Block("temp");
			block.createCategory("temp");
			block.addRow(0, row.getSubCategory(), data,
					row.getDataDouble(), row.getRowRow());
			List<Object> value = prepareRow(block.getRow(0, 0)
			);
			values.add(value);
			ValueRange valueRange = new ValueRange()
					.setValues(values);

			String spreadSheetId = readFromFile();
			UpdateValuesResponse result = sheets.spreadsheets().values().update(spreadSheetId,
					row.getRowRow(), valueRange)
					.setValueInputOption("USER_ENTERED")
					.execute();
			return Boolean.TRUE;
		}catch (Exception e) {
			Log.e(TAG, "ola");
			exception = e;
			return  Boolean.FALSE;
		}
	}

	public Boolean getOneRow(Block.Category.Row row, Context context) {
		try {
			prepare(context);
			Sheets sheets = getSheets();
			this.context = context;

			String spreadSheetId = readFromFile();
			ValueRange valueRange = sheets.spreadsheets().values().get(spreadSheetId,
					row.getRowRow())
					.setValueRenderOption("UNFORMATTED_VALUE")
					.execute();

			List<List<Object>> valuesData = valueRange.getValues();
			List<Object> rowData = valuesData.get(0);
			String dataDouble = String.valueOf(rowData.get(0));
			this.dataDouble = Double.valueOf(dataDouble);

			return Boolean.TRUE;
		} catch (Exception e) {
			Log.e(TAG, "ola");
			exception = e;
			return  Boolean.FALSE;
		}
	}

	protected void saveToFile() {
		FileOutputStream fileOutputStream;
		String filename = context.getResources().getString(R.string.file_name);
		File file = new File(context.getFilesDir(), filename);

		try {
			fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fileOutputStream.write(spreadsheetID.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			//taskTracer.onTaskFailed(e);
		} catch (IOException e) {
			e.printStackTrace();
			//taskTracer.onTaskFailed(e);
		}
	}

		protected String readFromFile(){
			// https://www.journaldev.com/9383/android-internal-storage-example-tutorial
			String filename = context.getResources().getString(R.string.file_name);
			File file = new File(context.getFilesDir(), filename);
			FileInputStream fileInputStream;
			String spreadsheetId = "";

			try {
				fileInputStream = context.openFileInput(filename);
				int character;
				while ( (character = fileInputStream.read()) != -1 ) {
					spreadsheetId += Character.toString((char)character);
				}
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				exception = e;
			} catch (IOException e) {
				e.printStackTrace();
				exception = e;
			}

			return spreadsheetId;
		}

		protected List<Object> prepareRow(Block.Category.Row row) {
			List<Object> value = new ArrayList<>();
			value.add(row.getData());
			return value;
		}
}

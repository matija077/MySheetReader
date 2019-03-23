package com.example.mysheetreader;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
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

	@Override
	public Boolean getData(Object... params) {
		Map map = (Map) params[0];
		try {
			String url = (String) map.get("url");
			String maxRows = (String) String.valueOf(map.get("maxRows"));
			context = (Context) params[1];
			GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

			String sheetId = url.substring(url.indexOf('=') + 1);
			// 3 for 3 characters in '/d/'
			spreadsheetID = url.substring(url.indexOf("/d/") + 3, url.indexOf("/edit"));
			String sheetName = "";

			String columnStart = "A";
			String columnEnd = "C";
			String startRow = "3";

			String endRowFixedLenght = String.valueOf(Integer.parseInt(startRow) +
					numberOfRowsFixedLenght);
			//String dataRange = columnStart + startRow + ":" + columnEnd + endRowFixedLenght;
			String dataRange = "A3" + ":" + "C" + maxRows;

			prepare(context);
			Sheets sheets = getSheets();

			// don't forget execute. In URL there is an sheetId but not Title. And Title is need
			// for Range A1 notaion. So we get the whoel spreadhseet object and compare ids until we
			// find a correct sheet and extract title from it and add to ranges.
			Spreadsheet requuestSpreadsheet = sheets.spreadsheets().get(spreadsheetID).execute();
			ArrayList sheetsTemp = (ArrayList) requuestSpreadsheet.getSheets();
			for (int i=0; i<sheetsTemp.size(); i++) {
				Sheet sheet = (Sheet) sheetsTemp.get(i);
				SheetProperties sheetProperties = sheet.getProperties();
				int tempSheetId = sheetProperties.getSheetId();
				if (String.valueOf(tempSheetId).equals(sheetId)) {
					sheetName = sheetProperties.getTitle();
					dataRange = sheetName + "!" + dataRange;

				}
			}
			sheetsTemp = null;
			requuestSpreadsheet = null;


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
			blocks = parseValueRange.parseListOfRangesGetData(valueRanges);
			//taskTracer.onTaskCompleted(new Object());
			return Boolean.TRUE;
	} catch (Exception e) {
			Log.e(TAG, "ola");
			//taskTracer.onTaskFailed(e);
			exception = e;
			return Boolean.FALSE;
		}
}

	@Override
	public Boolean savedata() {
	return Boolean.TRUE;
	}

	@Override
	public void prepare(Context context) {
		super.prepare(context);
	}

	@Override
	public Sheets getSheets() {
		return super.getSheets();
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
}

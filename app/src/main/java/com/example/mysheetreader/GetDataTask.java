package com.example.mysheetreader;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ImmutableList;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetDataTask extends AsyncTask {

	private getDataTaskTracer getDataTaskTracer;
	private TaskTracer taskTracer;
	private Context context;
	private HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TAG = "GetDataTask";
	private final int numberOgBlocks = 5;
	private final int numberOfRowsFixedLenght = 27;
	List<List<String>> block1;
	List<List<String>> block2;
	List<List<String>> block3;
	List<List<String>> block4;
	List<List<String>> block5;
	Exception exception;
	List<Block> blocks;
	String spreadsheetID;


	public interface getDataTaskTracer {
		void onTaskCompleted();
		void onTaskInProgress();
		void onTaskFailed(Exception exception);
	}

	GetDataTask(getDataTaskTracer getDataTaskTracer) {
		this.getDataTaskTracer = getDataTaskTracer;
	}

	GetDataTask(TaskTracer taskTracer) {
		this.taskTracer = taskTracer;
	}

	@Override
	protected Object doInBackground(Object... params) {
		Map map = (Map) params[0];
		try {
			String url = (String) map.get("url");
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
			String dataRange = columnStart + startRow + ":" + columnEnd + endRowFixedLenght;


			GoogleAccountCredential credential =
					GoogleAccountCredential.usingOAuth2(context,
							ImmutableList.of(
									"https://www.googleapis.com/auth/spreadsheets"
							));
			Account account = googleSignInAccount.getAccount();
			credential.setSelectedAccount(account);

			Sheets sheets = new Sheets(HTTP_TRANSPORT, JSON_FACTORY, credential);

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

			String rowStartVariable = endRowFixedLenght;
			blocks = new ArrayList<>();
			ParseValueRange parseValueRange = new ParseValueRange();
			// for fixedSize we fetch everything and parse it
			//TODO add rowRow data if this option is requeired. otherwise add Block or Categroy range
			for (int i=0; i<numberOgBlocks; i++) {
				ValueRange result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
						.setValueRenderOption("FORMULA")
						.execute();
				blocks.add(new Block("Block" + String.valueOf(i)));
				//some problems with size
				Integer size =  blocks.size() - 1;
				Block randomBlock = blocks.get(size);
				parseValueRange.parseRange(result, randomBlock);
				do {
					//variableLength we take one row after the last one and increase by one until
					//we return from parserange or size of result is 2 beacuse there will be no values.
					dataRange = dataRangeBuilder(sheetName, columnStart, rowStartVariable, columnEnd,
							rowStartVariable, 1, 1);
					result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
							.setValueRenderOption("FORMULA")
							.execute();
					size =  blocks.size() - 1;
					randomBlock = blocks.get(size);
					parseValueRange.parseRange(result, randomBlock);
					rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + 1);
				}while(result.size()==3);

				//mwe are now on the first empty row. we move to second.
				rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + 1);
				dataRange = dataRangeBuilder(sheetName, columnStart, rowStartVariable, columnEnd,
						rowStartVariable, 1, numberOfRowsFixedLenght + 1);
				// after setting dataRange we set rowStartVariable to last row in fixedLength
				rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + numberOfRowsFixedLenght + 1);
			}


			Log.d(TAG, "opa");
			saveToFile();

		} catch (Exception _exception) {
			exception = _exception;
			Log.d(TAG, "opa");
			cancel(true);
		}

			return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);

		//getDataTaskTracer.onTaskCompleted();
		taskTracer.onTaskCompleted(blocks);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		taskTracer.onTaskFailed(exception);
	}

	protected String dataRangeBuilder(String sheetName, String columnStart, String rowStart,
									  String columnEnd, String rowEnd, int rowStartOffset,
									  int rowEndOffset) {
		String dataRange;
		rowStart = String.valueOf(Integer.parseInt(rowStart) + rowStartOffset);
		rowEnd = String.valueOf(Integer.parseInt(rowEnd) + rowEndOffset);
		dataRange = sheetName + "!" + columnStart + rowStart + ":" + columnEnd + rowEnd;

		return dataRange;
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
			cancel(true);
		} catch (IOException e) {
			e.printStackTrace();
			cancel(true);
		}
	}
}

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
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			GoogleSheetApiHelper googleSheetApiHelper = new GoogleSheetsApiMain();
			if (googleSheetApiHelper.getData(params)) {
				spreadsheetID = ((GoogleSheetsApiMain) googleSheetApiHelper).getSpreadsheetID();
				blocks = ((GoogleSheetsApiMain) googleSheetApiHelper).getBlocks();
			} else {
				exception = ((GoogleSheetsApiMain) googleSheetApiHelper).getException();
				cancel(true);
			}

			/*String rowStartVariable = endRowFixedLenght;
			blocks = new ArrayList<>();
			ParseValueRange parseValueRange = new ParseValueRange();
			// for fixedSize we fetch everything and parse it
			//TODO add rowRow data if this option is requeired. otherwise add Block or Categroy range
			for (int i=0; i<numberOgBlocks; i++) {
				ValueRange result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
						.setValueRenderOption("FORMULA")
						.execute();
				// blocks ids start from 1
				blocks.add(new Block("Block" + String.valueOf(i+1)));
				//some problems with size
				Integer size =  blocks.size() - 1;
				Block randomBlock = blocks.get(size);
				parseValueRange.parseRange(result, randomBlock);
				List<ValueRange> ranges = new ArrayList<>();
				do {
					//variableLength we take one row after the last one and increase by one until
					//we return from parserange or size of result is 2 beacuse there will be no values.
					dataRange = dataRangeBuilder(sheetName, columnStart, rowStartVariable, columnEnd,
							rowStartVariable, 1, 1);
					result = sheets.spreadsheets().values().get(spreadsheetID, dataRange)
							.setValueRenderOption("FORMULA")
							.execute();
					ranges.add(result);
					rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + 1);
				}while(result.size()==3);

				size =  blocks.size() - 1;
				randomBlock = blocks.get(size);
				parseValueRange.parseListOfRanges(ranges, randomBlock);

				//mwe are now on the first empty row. we move to second.
				rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + 1);
				dataRange = dataRangeBuilder(sheetName, columnStart, rowStartVariable, columnEnd,
						rowStartVariable, 1, numberOfRowsFixedLenght + 1);
				// after setting dataRange we set rowStartVariable to last row in fixedLength
				rowStartVariable = String.valueOf(Integer.parseInt(rowStartVariable) + numberOfRowsFixedLenght + 1);
			}*/


			Log.d(TAG, "opa");

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
}

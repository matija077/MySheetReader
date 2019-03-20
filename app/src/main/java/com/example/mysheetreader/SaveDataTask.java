package com.example.mysheetreader;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.renderscript.Sampler;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Validator;


public class SaveDataTask extends AsyncTask {

	private TaskTracer taskTracer;
	private HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private Block.Category category;
	private Context context;
	private ParseValueRange parseValueRange;
	private Exception exception;

	public SaveDataTask(TaskTracer taskTracer) {
		this.taskTracer = taskTracer;
	}

	@Override
	protected Object doInBackground(Object... objects) {
		try {
			Map map = (Map) objects[0];
			category = (Block.Category) map.get(R.string.row_key);
			context = (Context) objects[1];
			parseValueRange = new ParseValueRange();

			GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

			GoogleAccountCredential credential =
					GoogleAccountCredential.usingOAuth2(context,
							ImmutableList.of(
									"https://www.googleapis.com/auth/spreadsheets"
							));
			Account account = googleSignInAccount.getAccount();
			credential.setSelectedAccount(account);

			Sheets sheets = new Sheets(HTTP_TRANSPORT, JSON_FACTORY, credential);

			/*List<Request> requests = new ArrayList<>();
			for (Block.Category.Row row:category.getRows()) {
				if (row.getHasChanged() == Boolean.TRUE) {
					requests.add(new Request()
							.setUpdateCells(new UpdateCellsRequest()
								.set)
				}
			}*/

			String spreadSheetId = readFromFile();

			List<List<Object>> values = parseValueRange.createValueRange(category);
			ValueRange data = new ValueRange()
					.setValues(values);

			//TODO either category range and all rows, or row ranges and hasChanged and cellUpdate
			//UpdateValuesResponse response = sheets.spreadsheets().values().update()
		} catch (Exception e) {
			exception = e;
			cancel(true);
		}


		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);

		taskTracer.onTaskCompleted(o);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		taskTracer.onTaskFailed(exception);
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
			cancel(true);
		} catch (IOException e) {
			e.printStackTrace();
			exception = e;
			cancel(true);
		}

		return spreadsheetId;
	}
}

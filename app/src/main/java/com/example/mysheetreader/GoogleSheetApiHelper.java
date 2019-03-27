package com.example.mysheetreader;

import android.accounts.Account;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class GoogleSheetApiHelper {
	private HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private Sheets sheets;
	private String spreadsheetURL;
	private String spreadsheetID;
	private String sheetId;
	private String sheetName;
	private String dataRange;
	private List<String> sheetNames;
	private List<String> sheetIds;

	public GoogleSheetApiHelper() {
		this.sheetNames = new ArrayList<>();
		this.sheetIds = new ArrayList<>();
	}

	public abstract Boolean  getData(Object... params);

	public abstract Boolean savedata(Object... params);

	public Sheets getSheets() {
		return sheets;
	}

	public void setDataRange(String dataRange) {
		this.dataRange = dataRange;
	}

	public void setSpreadsheetID(String spreadsheetID) {
		this.spreadsheetID = spreadsheetID;
	}

	public void setSheetId(String sheetId) {
		this.sheetId = sheetId;
	}

	public void setSpreadsheetURL(String url) {
		this.spreadsheetURL = url;
	}

	public String getSheetName() {
		return sheetName;
	}

	public String getSpreadsheetURL() {
		return spreadsheetURL;
	}

	public String getDataRange() {
		return dataRange;
	}

	public List<String> getSheetNames() {
		return sheetNames;
	}

	public List<String> getSheetIds() {
		return sheetIds;
	}

	public void prepare(Context context) {
		GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);

		GoogleAccountCredential credential =
				GoogleAccountCredential.usingOAuth2(context,
						ImmutableList.of(
								"https://www.googleapis.com/auth/spreadsheets"
						));
		Account account = googleSignInAccount.getAccount();
		credential.setSelectedAccount(account);

		sheets = new Sheets(HTTP_TRANSPORT, JSON_FACTORY, credential);
	}

	public void prepareSheets() {
		// don't forget execute. In URL there is an sheetId but not Title. And Title is need
		// for Range A1 notaion. So we get the whoel spreadhseet object and compare ids until we
		// find a correct sheet and extract title from it and add to ranges.
		try {
			Spreadsheet requuestSpreadsheet = sheets.spreadsheets().get(spreadsheetID).execute();
			ArrayList sheetsTemp = (ArrayList) requuestSpreadsheet.getSheets();
			for (int i = 0; i < sheetsTemp.size(); i++) {
				Sheet sheet = (Sheet) sheetsTemp.get(i);
				SheetProperties sheetProperties = sheet.getProperties();
				int tempSheetId = sheetProperties.getSheetId();
				if (String.valueOf(tempSheetId).equals(sheetId)) {
					sheetName = sheetProperties.getTitle();
					dataRange = sheetName + "!" + dataRange;

				}
				String temp = sheetProperties.getTitle();
				sheetNames.add(temp);
				temp = String.valueOf(sheetProperties.getSheetId());
				sheetIds.add(temp);
			}
			sheetsTemp = null;
			requuestSpreadsheet = null;
		} catch (Exception e) {

		}
	}

}

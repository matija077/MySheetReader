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
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Map;


public abstract class GoogleSheetApiHelper {
	private HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private Sheets sheets;

	public GoogleSheetApiHelper() {

	}

	public abstract Boolean  getData(Object... params);

	public abstract Boolean savedata(Object... params);

	public Sheets getSheets() {
		return sheets;
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

}

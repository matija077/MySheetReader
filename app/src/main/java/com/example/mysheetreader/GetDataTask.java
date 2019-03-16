package com.example.mysheetreader;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Map;


public class GetDataTask extends AsyncTask {

	private getDataTaskTracer getDataTaskTracer;
	private Context context;


	public interface getDataTaskTracer {
		void onTaskCompleted();
		void onTaskInProgress();
		void onTaskFailed(Exception exception);
	}

	GetDataTask(getDataTaskTracer getDataTaskTracer) {
		this.getDataTaskTracer = getDataTaskTracer;
	}

	@Override
	protected Object doInBackground(Object... params) {
		Map map = (Map) params[0];
		String url = (String) map.get("url");
		context = (Context) params[1];
		GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}

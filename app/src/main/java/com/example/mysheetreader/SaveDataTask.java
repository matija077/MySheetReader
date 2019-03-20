package com.example.mysheetreader;

import android.os.AsyncTask;


public class SaveDataTask extends AsyncTask {

	private TaskTracer taskTracer;

	public SaveDataTask(TaskTracer taskTracer) {
		this.taskTracer = taskTracer;
	}

	@Override
	protected Object doInBackground(Object[] objects) {
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

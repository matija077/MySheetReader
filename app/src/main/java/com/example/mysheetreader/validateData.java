package com.example.mysheetreader;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Map;


public class validateData extends AsyncTask {
	private TaskTracer taskTracer;
	private Exception exception;

	public validateData(TaskTracer taskTracer) {
		this.taskTracer = taskTracer;
	}

	@Override
	protected Object doInBackground(Object... objects) {
		Map map = (Map) objects[0];
		Context context = (Context) objects[1];
		Block.Category.Row row = (Block.Category.Row) map.get("row");
		String data = (String) map.get("data");

		GoogleSheetApiHelper googleSheetApiHelper = new GoogleSheetsApiMain();
		if (((GoogleSheetsApiMain) googleSheetApiHelper).saveOneRow(row, data, context)) {
			if (((GoogleSheetsApiMain) googleSheetApiHelper).getOneRow(row, context)){
				row.setData(data);
				row.setDataDouble(((GoogleSheetsApiMain) googleSheetApiHelper).getDataDouble());
			} else {
				((GoogleSheetsApiMain) googleSheetApiHelper).saveOneRow(row, row.getData(), context);
				exception = ((GoogleSheetsApiMain) googleSheetApiHelper).getException();
				cancel(true);
			}
		} else {
			exception = ((GoogleSheetsApiMain) googleSheetApiHelper).getException();
			cancel(true);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object o) {
		super.onPostExecute(o);
		taskTracer.onTaskCompleted(new Object());
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		taskTracer.onTaskFailed(exception);
	}
}

package com.example.mysheetreader;

public interface TaskTracer {
	void onTaskCompleted();
	void onTaskInProgress();
	void onTaskFailed(Exception exception);

}

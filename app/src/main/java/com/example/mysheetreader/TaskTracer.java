package com.example.mysheetreader;

public interface TaskTracer<T> {
	void onTaskCompleted(T object);
	void onTaskInProgress();
	void onTaskFailed(Exception exception);
	void onMultipleTaskCompleted(T... objects);

}

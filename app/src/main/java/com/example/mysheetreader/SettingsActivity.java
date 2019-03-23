package com.example.mysheetreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


// https://google-developer-training.gitbooks.io/android-developer-fundamentals-course-practicals/content/en/Unit%204/92_p_adding_settings_to_an_app.html
public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, new SettingsFragment())
					.commit();
		} catch (Exception exception) {
			Log.e("a", "a");
		}

	}


}

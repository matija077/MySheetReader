package com.example.mysheetreader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private String OAuthClientID = "809979036613-ah2ri993ks6ptukvt7eofmj0vtjf5h0p.apps.googleusercontent.com";
	private static final String TAG = "MainActivity";
	private static final int RC_SIGN_IN = 443;
	GoogleSignInClient mGoogleSignInClient;
	GoogleSignInAccount account;
	SignInButton signInButton;
	TextView test;
	CoordinatorLayout coordinatorLayout;
	ProgressBar progressBar;
	SharedPreferences sharedPreferences;
	static String urlToSave;
	static String urlDefault;
	List<String> sheetNames;
	List<String> sheetIDs;
	String spreadsheetURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		signInButton = findViewById(R.id.sign_in_button);
		test = findViewById(R.id.test);
		coordinatorLayout = findViewById(R.id.mainActivityCordinatorLayout);
		progressBar = findViewById(R.id.progress_bar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(this);
		signInButton.setOnClickListener(this);

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestScopes(new Scope(SheetsScopes.SPREADSHEETS))
				.requestEmail()
				.build();

		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		/*SharedPreferences sharedPreferences = this.getSharedPreferences(getString(
				R.string.preference_file_key), this.MODE_PRIVATE);
		urlSharedPreferences = getResources().getString(R.string.preference_url_key);
		maxRows = getResources().getString(R.string.preference_max_rowss_key);*/

		//add default defaultPreferences in settings and initialize sharedPreferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference_file_key), MODE_PRIVATE);
		sheetNames = new ArrayList<>();
		sheetIDs = new ArrayList<>();
	}

	@Override
	protected void onStart() {
		super.onStart();

		//Check for existing Google Sign In account, if the user is already signed in
		// the GoogleSignInAccount will be non-null.
		account = GoogleSignIn.getLastSignedInAccount(this);

		if (account != null) {
			signInButton.setVisibility(View.GONE);
			test.setText(account.getEmail());
		} else {
			ListView listView = findViewById(R.id.main_fragment_list_view);
			listView.setVisibility(View.GONE);
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.INTERNET},
									1);
		}
		/*if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
					2);
		}*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		urlDefault = sharedPreferences.getString(getResources().getString
				(R.string.preference_url_key), "");
		spreadsheetURL = sharedPreferences.getString(getResources().getString(R.string.preference_spreadsheetURL_key), "");
		int sheetNamesSize = Integer.valueOf(sharedPreferences.getString(getResources().getString
				(R.string.preference_sheetNames_size_key), "0"));
		if (sheetNames.size() > 0) {
			sheetNames = new ArrayList<>();
		}
		if (sheetIDs.size() > 0) {
			sheetIDs = new ArrayList<>();
		}
		for (int i=1; i<=sheetNamesSize; i++) {
			String temp = sharedPreferences.getString(getResources().getString
					(R.string.preference_sheetName_key) + String.valueOf(i), "");
			sheetNames.add(temp);
			temp = sharedPreferences.getString(getResources().getString(R.string.preference_sheetID_key) +
					String.valueOf(i), "");
			sheetIDs.add(temp);
		}

		account = GoogleSignIn.getLastSignedInAccount(this);
		if (account != null) {
			displaySheetNamesFragemntt();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			try {
				Intent intent = new Intent(this, SettingsActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				/*getSupportFragmentManager()
						.beginTransaction()
						.replace(android.R.id.content, new SettingsFragment())
						.commit();*/
			} catch(Exception exception) {
				Log.e(TAG, "ola");

			}
			return true;
		} else if (id == R.id.action_logout) {
			signOut();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.sign_in_button:
				signIn();
				break;
			case R.id.fab:
				// if we fetch data then we check url deafautl preference and see if we need to save
				// url preferences
				FragmentManager fragmentManager = getSupportFragmentManager();
				UrlDialogFragment urlDialogFragment = UrlDialogFragment.newInstance(new TaskTracer() {
					@Override
					public void onTaskCompleted(Object object) {
						Snackbar snackbar = Snackbar.make(coordinatorLayout,
								R.string.snackbar_main_activity_get, Snackbar.LENGTH_LONG);
						snackbar.show();
						SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Boolean urlBoolean = sharedDefaultPreferences.getBoolean("switch_preference_url", Boolean.FALSE);
						if (urlBoolean == Boolean.TRUE) {
							saveUrl();
						} /*else {
							clearUrl();
						}*/
						showData(object);
					}

					@Override
					public void onTaskInProgress() {

					}

					@Override
					public void onTaskFailed(Exception exception) {
						Snackbar snackbar = Snackbar.make(coordinatorLayout,
								R.string.snackbar_main_activity_error, Snackbar.LENGTH_LONG);
						snackbar.show();

					}

					// (blocks, sheetNames, spreadsheetURL)
					@Override
					public void onMultipleTaskCompleted(Object[] objects) {
						Snackbar snackbar = Snackbar.make(coordinatorLayout,
								R.string.snackbar_main_activity_get, Snackbar.LENGTH_LONG);
						snackbar.show();
						SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Boolean urlBoolean = sharedDefaultPreferences.getBoolean("switch_preference_url", Boolean.FALSE);
						if (urlBoolean == Boolean.TRUE) {
							saveUrl();
						} /*else {
							clearUrl();
						}*/
						sheetNames = (List<String>) objects[1];
						spreadsheetURL = (String) objects[2];
						sheetIDs = (List<String>) objects[3];
						saveSheetNamesAndUSpreadsheetURL();
						showData(objects[0]);
					}
				}, progressBar);
				urlDialogFragment.show(fragmentManager, "name");
				break;
		}


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void signIn(){
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			account = completedTask.getResult(ApiException.class);
			signInButton.setVisibility(View.GONE);
			test.setText(account.getEmail());

		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
		}
	}

	public static class UrlDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
		View view;
		static TaskTracer taskTracer;
		static ProgressBar progressBar;
		static String maxRowsDIalog;
		static Boolean urlBoolean;
		static String numberOfBlocks;

		public UrlDialogFragment() {

		}

		public static UrlDialogFragment newInstance(TaskTracer _taskTracer, ProgressBar _progressBar){
			UrlDialogFragment urlDialogFragment = new UrlDialogFragment();
			taskTracer = _taskTracer;
			progressBar = _progressBar;
			return urlDialogFragment;
		}


		@Nullable
		@Override
		public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();

			view = inflater.inflate(R.layout.dialog_url, null);
			SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			/*urlSharedPreferences = sharedPreferences.getString(getResources().getString(R.string.preference_url_key), "");
			maxRowsDIalog = sharedPreferences.getInt(getResources().getString(R.string.preference_max_rowss_key), 400);*/
			urlBoolean = sharedDefaultPreferences.getBoolean("switch_preference_url", Boolean.FALSE);
			numberOfBlocks = sharedDefaultPreferences.getString("edit_text_preference_number_of_blocks", "10");
			maxRowsDIalog = sharedDefaultPreferences.getString("edit_text_preference_max_rows", "400");
			if (urlBoolean) {
				if (!urlDefault.equals("")) {
					EditText urlView = view.findViewById(R.id.text_dialog_url);
					urlView.setText(urlDefault);
				}
			}

			builder
					.setView(view)
					.setTitle(R.string.url_fragment_title)
					.setPositiveButton("Get",this)
					.setNegativeButton("Cancel", this);

			return builder.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Map params = new HashMap();
			final EditText urlView = view.findViewById(R.id.text_dialog_url);
			//text is actually spanable string builder
			params.put("url", urlView.getText().toString());
			params.put("maxRows", maxRowsDIalog);
			params.put("numberOfBlocks", numberOfBlocks);
			params.put("sheetIDUsed", "");

			if (which == DialogInterface.BUTTON_POSITIVE) {
				progressBar.setVisibility(View.VISIBLE);
				new GetDataTask(new TaskTracer() {
					@Override
					public void onTaskCompleted(Object object) {
						/*FragmentActivity activity = (FragmentActivity) view.getContext();
						Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.mainActivityCordinatorLayout),
								R.string.snackbar_main_activity_get, Snackbar.LENGTH_LONG);
						snackbar.show();*/
						progressBar.setVisibility(View.GONE);
						//object = urlView.getText().toString();
						urlToSave = urlView.getText().toString();
						taskTracer.onTaskCompleted(object);
					}

					@Override
					public void onTaskInProgress() {

					}

					@Override
					public void onTaskFailed(Exception exception) {
						progressBar.setVisibility(View.GONE);
						taskTracer.onTaskFailed(exception);
					}

					// (blocks, sheetNames, spreadsheetURL)
					@Override
					public void onMultipleTaskCompleted(Object[] objects) {
						progressBar.setVisibility(View.GONE);
						urlToSave = urlView.getText().toString();
						taskTracer.onMultipleTaskCompleted(objects);
					}
				}).execute(params, getActivity());
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				UrlDialogFragment.this.getDialog().cancel();
			}
		}
	}

	public void saveUrl() {
		SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
		preferencesEditor.putString(getResources().getString(R.string.preference_url_key),
				String.valueOf(urlToSave));
		preferencesEditor.apply();
	}

	public void clearUrl() {
		SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
		preferencesEditor.clear();
		preferencesEditor.apply();
	}

	public void saveSheetNamesAndUSpreadsheetURL() {
		SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
		preferencesEditor.putString(getResources().getString(R.string.preference_spreadsheetURL_key),
				String.valueOf(spreadsheetURL));

		preferencesEditor.putString(getResources().getString(R.string.preference_sheetNames_size_key),
				String.valueOf(sheetNames.size()));

		for (int i=1; i<=sheetNames.size(); i++) {
			preferencesEditor.putString(getResources().getString(R.string.preference_sheetName_key)
							+ String.valueOf(i), String.valueOf(sheetNames.get(i-1)));
			preferencesEditor.putString(getResources().getString(R.string.preference_sheetID_key) +
					String.valueOf(i), String.valueOf(sheetIDs.get(i-1)));
		}
		preferencesEditor.apply();
	}

	public void displaySheetNamesFragemntt() {
		if (spreadsheetURL == "" || sheetNames == null || sheetIDs == null) {
			return;
		}
		/*Map params = new HashMap<>();
		params.put("spreadsheetURL", spreadsheetURL);
		params.put("sheetNames", sheetNames);*/


		final FragmentSheetNamesArrayAdapter fragmentSheetNamesArrayAdapter = new
				FragmentSheetNamesArrayAdapter(this, R.layout.row, sheetNames,
				spreadsheetURL, new FragmentSheetNamesArrayAdapter.SheetNameInterface() {
			@Override
			public void onClicked(View v) {
				String maxRowsDIalog;
				String numberOfBlocks;
				SharedPreferences sharedDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
				numberOfBlocks = sharedDefaultPreferences.getString("edit_text_preference_number_of_blocks", "10");
				maxRowsDIalog = sharedDefaultPreferences.getString("edit_text_preference_max_rows", "400");

				ListView listView = findViewById(R.id.main_fragment_list_view);
				int position = listView.getPositionForView(v);
				String sheetname = sheetNames.get(position);
				String sheetID = sheetIDs.get(position);

				Map params = new HashMap();
				params.put("url", spreadsheetURL);
				params.put("maxRows", maxRowsDIalog);
				params.put("numberOfBlocks", numberOfBlocks);
				params.put("sheetIDUsed", sheetID);

				progressBar.setVisibility(View.VISIBLE);
				new GetDataTask(new TaskTracer() {
					@Override
					public void onTaskCompleted(Object object) {

					}

					@Override
					public void onTaskInProgress() {

					}

					@Override
					public void onTaskFailed(Exception exception) {

					}

					@Override
					public void onMultipleTaskCompleted(Object[] objects) {
						progressBar.setVisibility(View.GONE);
						Snackbar snackbar = Snackbar.make(coordinatorLayout,
								R.string.snackbar_main_activity_get, Snackbar.LENGTH_LONG);
						snackbar.show();

						sheetNames = (List<String>) objects[1];
						spreadsheetURL = (String) objects[2];
						saveSheetNamesAndUSpreadsheetURL();

						showData(objects[0]);
					}
				}).execute(params, getApplication());
			}
		});
		ListView listView = findViewById(R.id.main_fragment_list_view);
		listView.setAdapter(fragmentSheetNamesArrayAdapter);
		listView.setVisibility(View.VISIBLE);
		/*FragmentManager fragmentManager = getSupportFragmentManager();
		SheetNamesFragment sheetNamesFragment = SheetNamesFragment.newInstance(params);
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		try {
			fragmentTransaction.add(sheetNamesFragment, "ola");
			fragmentTransaction.commit();
		} catch (Exception e) {
			Log.e(TAG, "ola");
		}*/
	}

	// https://stackoverflow.com/questions/3053761/reload-activity-in-android
	private void signOut() {
		mGoogleSignInClient.signOut();
		finish();
		startActivity(getIntent());
	}

	public void showData(Object object){
		Intent intent = new Intent(MainActivity.this, BlockList.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(getResources().getString(R.string.blocks_Key), (Serializable) object);
		intent.putExtras(bundle);
		try {
			startActivity(intent);
		} catch(Exception exception) {
			Log.e(TAG, String.valueOf(exception));
		}
	}
}

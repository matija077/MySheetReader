package com.example.mysheetreader;

import android.Manifest;
import android.app.Activity;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
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
	static String urlSharedPreferences;
	static int maxRows;


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

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		urlSharedPreferences = sharedPreferences.getString(getResources().getString(R.string.preference_url_key), "");
		maxRows = sharedPreferences.getInt(getResources().getString(R.string.preference_max_rowss_key), 400);
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
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.INTERNET},
									1);
		}
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
				!= PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
					2);
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
			Intent intent = new Intent(this, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
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
				FragmentManager fragmentManager = getSupportFragmentManager();
				UrlDialogFragment urlDialogFragment = UrlDialogFragment.newInstance(new TaskTracer() {
					@Override
					public void onTaskCompleted(Object object) {
						Snackbar snackbar = Snackbar.make(coordinatorLayout,
								R.string.snackbar_main_activity_get, Snackbar.LENGTH_LONG);
						snackbar.show();
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
			EditText urlView = view.findViewById(R.id.text_dialog_url);
			urlView.setText(urlSharedPreferences);

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
			EditText urlView = view.findViewById(R.id.text_dialog_url);
			//text is actually spanable string builder
			params.put("url", urlView.getText().toString());
			params.put("maxRows", maxRows);

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
				}).execute(params, getActivity());
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				UrlDialogFragment.this.getDialog().cancel();
			}
		}
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

package com.example.mysheetreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class RowList extends AppCompatActivity implements View.OnClickListener {
	private Block.Category category;
	private static final String TAG = "RowList";
	private Button applyButton;
	private ListView listView;
	private static CoordinatorLayout coordinatorLayout;
	public static int positionInParentActivity;
	public static final int CATEGROY_REQUEST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_row_list);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		applyButton = findViewById(R.id.apply);
		coordinatorLayout = findViewById(R.id.activity_row_list_cordinator_layout);

		applyButton.setOnClickListener(this);
		Intent intent = getIntent();
		IntentHelper intentHelper = new IntentHelper(getApplication());
		List<Object> objects = intentHelper.readFromIntentBlockList(intent, getResources()
				.getString(R.string.row_key));
		category = (Block.Category) objects.get(0);
		positionInParentActivity = (int) objects.get(1);
		getSupportActionBar().setTitle(category.getName());

		Log.d(TAG, "ola");

		try {
			RowArrayAdapter rowArrayAdapter = new RowArrayAdapter(this, R.layout.data_row,
					category.getRows(), new RowArrayAdapter.DataString() {
				@Override
				public void onClicked(View v) {
					// we get position from listView and than get data from rowArrayAdapter
					final RowArrayAdapter rowArrayAdapter = (RowArrayAdapter) listView.getAdapter();
					int position = listView.getPositionForView(v);
					Block.Category.Row row = (Block.Category.Row) rowArrayAdapter.getItem(position);
					//we don't need tasktracer here!
					ChangeDataFragment changeDataFragment = ChangeDataFragment.newInstance(row, new TaskTracer() {
						@Override
						public void onTaskCompleted(Object object) {
							rowArrayAdapter.notifyDataSetChanged();
						}

						@Override
						public void onTaskInProgress() {

						}

						@Override
						public void onTaskFailed(Exception exception) {

						}
					});
					FragmentManager fragmentManager = getSupportFragmentManager();
					changeDataFragment.show(fragmentManager, "name");
					Log.d(TAG, "ola");
				}
			});
			listView = findViewById(R.id.row_list_view);
			listView.setAdapter(rowArrayAdapter);
		} catch(Exception exception) {
			Log.e(TAG, "ola");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.apply:
				applyChanges();
				break;
		}
	}

	public static class ChangeDataFragment extends DialogFragment implements DialogInterface.OnClickListener{
		// view is here because we have class implementation of onClickListener and we need view there.
		View view;
		static Block.Category.Row row;
		static TaskTracer taskTracer;

		public ChangeDataFragment() {

		}

		public static ChangeDataFragment newInstance(Block.Category.Row _row, TaskTracer _taskTracer) {
			row = _row;
			taskTracer = _taskTracer;
			Boolean isUpdated = Boolean.FALSE;
			ChangeDataFragment changeDataFragment = new ChangeDataFragment();
			return changeDataFragment;
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
			LayoutInflater layoutInflater = getActivity().getLayoutInflater();

			view = layoutInflater.inflate(R.layout.dialog_data_change, null);
			infllate(view);

			builder
					.setView(view)
					.setTitle(R.string.data_change_fragment_title)
					.setPositiveButton(R.string.data_change_fragmebnt_positive_button, this)
					.setNegativeButton(R.string.data_change_fragment_negative_button, this);

			return builder.create();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which==DialogInterface.BUTTON_POSITIVE) {
				/*if (changeData(view)) {
					taskTracer.onTaskCompleted(Boolean.TRUE);
				} else {
					ChangeDataFragment.this.getDialog().cancel();
				}*/
				changeData(view);
			} else if (which == DialogInterface.BUTTON_NEGATIVE){
				ChangeDataFragment.this.getDialog().cancel();
			}
		}

		private void infllate(View view) {
			EditText editText = view.findViewById(R.id.dialog_data_change);
			editText.setText(row.getData());
		}

		private void changeData(View view){
			EditText editText = view.findViewById(R.id.dialog_data_change);
			String data = String.valueOf(editText.getText());

			/*if (!checkDataRegex(data)) {
				return Boolean.FALSE;
			}*/

			Map map = new HashMap();
			map.put("data", data);
			map.put("row", row);
			new validateData(new TaskTracer() {
				@Override
				public void onTaskCompleted(Object object) {
					Snackbar snackbar = Snackbar.make(coordinatorLayout,
							R.string.snackbar_row_list_row_update, Snackbar.LENGTH_LONG);
					snackbar.show();
					taskTracer.onTaskCompleted(new Object());
					//ChangeDataFragment.this.getDialog().cancel();
				}

				@Override
				public void onTaskInProgress() {

				}

				@Override
				public void onTaskFailed(Exception exception) {
					Snackbar snackbar = Snackbar.make(coordinatorLayout,
							R.string.snakcbar_row_list_row_error, Snackbar.LENGTH_LONG);
					snackbar.show();
					taskTracer.onTaskFailed(exception);
					//ChangeDataFragment.this.getDialog().cancel();
				}
			}).execute(map, getContext());
			//row.setHasChanged();
		}

		/*private Boolean checkDataRegex(String data) {
		}*/
	}

	// iterate through rowArrayAdapter, find if addData editRext is not empty. If it's not emppty
	// than add that data to existing one and clean addData editText. Notify for changes otherwise
	// it's gonna chahge only on editText clicked again
	private void applyChanges() {
		RowArrayAdapter rowArrayAdapter = (RowArrayAdapter) listView.getAdapter();
		for (int i=0; i<rowArrayAdapter.getCount(); i++) {
			View view = rowArrayAdapter.getView(i, null, listView);
			EditText editText = view.findViewById(R.id.data_row_add);
			String data = String.valueOf(editText.getText());
			if (!data.equals("")) {
				rowArrayAdapter.updateItem(view, i,data);
			}
			Block.Category.Row row = (Block.Category.Row) rowArrayAdapter.getItem(i);
			Log.d(TAG, "ola");


		}
		//TODO move to the inside of the loop ?
		rowArrayAdapter.notifyDataSetChanged();

		Map map = new HashMap();
		map.put(getResources().getString(R.string.row_key), category);
		new SaveDataTask(new TaskTracer(){
			@Override
			public void onTaskCompleted(Object object) {
				Snackbar snackbar = Snackbar.make(coordinatorLayout,
						R.string.snkacbar_row_list_activity_update, Snackbar.LENGTH_LONG);
				snackbar.show();
				resetHasChanged();;
			}

			@Override
			public void onTaskInProgress() {

			}

			@Override
			public void onTaskFailed(Exception exception) {
				Snackbar snackbar = Snackbar.make(coordinatorLayout,
						R.string.snkacbar_row_list_activity_error, Snackbar.LENGTH_LONG);
				snackbar.show();
			}
		}).execute(map, this);
	}

	private void resetHasChanged(){
		this.category.resetHasChanged();
	}

	// https://stackoverflow.com/questions/2679250/setresult-does-not-work-when-back-button-pressed
	@Override
	public void onBackPressed() {
		//call super last so it's not actually finished before you can setResult
		Intent intent = new Intent();
		IntentHelper intentHelper = new IntentHelper(getApplication());
		Bundle bundle = intentHelper.prepareIntentCategory(category, positionInParentActivity);
		intent.putExtras(bundle);

		setResult(CATEGROY_REQUEST, intent);
		super.onBackPressed();
		//finish();
	}
}

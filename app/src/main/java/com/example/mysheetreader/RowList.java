package com.example.mysheetreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
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


public class RowList extends AppCompatActivity implements View.OnClickListener {
	private Block.Category category;
	private static final String TAG = "RowList";
	private Button applyButton;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_row_list);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		applyButton = findViewById(R.id.apply);

		applyButton.setOnClickListener(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		Object object = bundle.getSerializable(getResources().getString(R.string.row_key));
		category = (Block.Category) object;
		Log.d(TAG, "ola");

		try {
			RowArrayAdapter rowArrayAdapter = new RowArrayAdapter(this, R.layout.data_row,
					category.getRows(), new RowArrayAdapter.DataString() {
				@Override
				public void onClicked(View v) {
					// we get position from listView and than get data from rowArrayAdapter
					RowArrayAdapter rowArrayAdapter = (RowArrayAdapter) listView.getAdapter();
					int position = listView.getPositionForView(v);
					Block.Category.Row row = (Block.Category.Row) rowArrayAdapter.getItem(position);
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

	public static class ChangeDataFragment extends DialogFragment implements DialogInterface.OnClickListener {
		// view is here because we have class implementation of onClickListener and we need view there.
		View view;
		static Block.Category.Row row;

		public ChangeDataFragment() {

		}

		public static ChangeDataFragment newInstance(Block.Category.Row _row) {
			row = _row;
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
			row.setData(data);
		}
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
			//TODO move to the outside of the loop
			rowArrayAdapter.notifyDataSetChanged();
		}
	}
}

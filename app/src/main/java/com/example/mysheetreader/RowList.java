package com.example.mysheetreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
			rowArrayAdapter.notifyDataSetChanged();
		}
	}
}

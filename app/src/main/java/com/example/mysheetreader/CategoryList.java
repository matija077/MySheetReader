package com.example.mysheetreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CategoryList extends AppCompatActivity {
	private Block block;
	private static final String TAG = "CategoryList";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		Object object = bundle.getSerializable(getResources().getString(R.string.category_key));
		block = (Block) object;
		Log.d(TAG, "ola");
		try {
			CategoryListAdapter categoryListAdapter = new CategoryListAdapter(this, R.layout.row,
					block.getCategories());
			ListView listView = findViewById(R.id.category_list_view);
			listView.setAdapter(categoryListAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(CategoryList.this, RowList.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(getResources().getString(R.string.row_key),
							block.getCategory(position));
					intent.putExtras(bundle);
					try {
						startActivity(intent);
					} catch(Exception exception) {
						Log.e(TAG, String.valueOf(exception));
					}
				}
			});
		} catch (Exception exception) {
			Log.e(TAG, "ola");
		}

	}

}

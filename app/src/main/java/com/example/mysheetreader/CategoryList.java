package com.example.mysheetreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
	public static final int BLOCK_REQUEST = 1;
	public static int positionInParentActivity;
	public static final int CATEGROY_REQUEST = 2;
	private CoordinatorLayout coordinatorLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		coordinatorLayout = findViewById(R.id.activity_category_list_cordinator_layout);

		Intent intent = getIntent();
		final IntentHelper intentHelper = new IntentHelper(getApplication());
		List<Object> objects = intentHelper.readFromIntentBlockList(intent, getResources()
				.getString(R.string.category_key));
		block = (Block) objects.get(0);
		positionInParentActivity = (int) objects.get(1);
		Log.d(TAG, "ola");
		// https://stackoverflow.com/questions/26486730/in-android-app-toolbar-settitle-method-has-no-effect-application-name-is-shown/32582780
		getSupportActionBar().setTitle(block.getBlockId());

		toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		try {
			CategoryListAdapter categoryListAdapter = new CategoryListAdapter(this, R.layout.row,
					block.getCategories());
			ListView listView = findViewById(R.id.category_list_view);
			listView.setAdapter(categoryListAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(CategoryList.this, RowList.class);
					IntentHelper intentHelper = new IntentHelper(getApplication());
					Bundle bundle = intentHelper.prepareIntentCategory(block.getCategory(position),
							position);
					intent.putExtras(bundle);
					try {
						startActivityForResult(intent, CATEGROY_REQUEST);
					} catch(Exception exception) {
						Log.e(TAG, String.valueOf(exception));
					}
				}
			});
		} catch (Exception exception) {
			Log.e(TAG, "ola");
		}

	}

	@Override
	public void onBackPressed() {
		//call super last so it's not actually finished before you can setResult

		Intent intent = new Intent();
		IntentHelper intentHelper = new IntentHelper(getApplication());
		Bundle bundle = intentHelper.prepareIntentBlock(block, positionInParentActivity);
		intent.putExtras(bundle);

		setResult(BLOCK_REQUEST, intent);
		super.onBackPressed();
		//finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CATEGROY_REQUEST) {
			if (resultCode ==CATEGROY_REQUEST) {
				IntentHelper intentHelper = new IntentHelper(getApplication());
				List<Object> objects = intentHelper.readFromIntentBlockList(data, getResources()
						.getString(R.string.row_key));
				int position = (int) objects.get(1);
				Block.Category category = (Block.Category) objects.get(0);
				this.block.setcategory(position, category);
			}
		}
	}
}

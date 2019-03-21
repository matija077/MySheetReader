package com.example.mysheetreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


public class BlockList extends AppCompatActivity {
	List<Block> blocks;
	private static final String TAG = "BlockList";
	public static final int BLOCK_REQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_block_list);
			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			Object object = bundle.getSerializable(getResources().getString(R.string.blocks_Key));
			blocks = (List<Block>) object;
			Log.d(TAG, "ola");

			BlockArrayAdapter blockListAdapter = new BlockArrayAdapter(this,
					R.layout.row, blocks);
			//BlockArrayAdapter blockListAdapter = new BlockArrayAdapter(this, blocks);
			ListView listView = findViewById(R.id.block_list_view);
			listView.setAdapter(blockListAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.d(TAG, "ola");
					Intent intent = new Intent(BlockList.this, CategoryList.class);
					IntentHelper intentHelper = new IntentHelper(getApplication());
					Bundle bundle = intentHelper.prepareIntentBlockList(blocks, position);
					intent.putExtras(bundle);
					try {
						startActivityForResult(intent, BLOCK_REQUEST);
					} catch(Exception exception) {
						Log.e(TAG, String.valueOf(exception));
					}
				}
			});
		} catch (Exception exception) {
			Log.e(TAG, String.valueOf(exception));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == BLOCK_REQUEST) {
			if (resultCode ==RESULT_OK) {
				IntentHelper intentHelper = new IntentHelper(getApplication());
				List<Object> objects = intentHelper.readFromIntentBlockList(data, getResources().
						getString(R.string.category_key));
				int position = (int) objects.get(1);
				Block block = (Block) objects.get(0);
				this.blocks.set(position, block);
			}
		}
	}
}

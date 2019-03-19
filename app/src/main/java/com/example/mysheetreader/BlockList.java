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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class BlockList extends AppCompatActivity {
	List<Block> blocks;
	private static final String TAG = "BlockList";

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
					R.layout.block_row, blocks);
			//BlockArrayAdapter blockListAdapter = new BlockArrayAdapter(this, blocks);
			ListView listView = findViewById(R.id.block_list_view);
			listView.setAdapter(blockListAdapter);

			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Log.d(TAG, "ola");
					Log.d(TAG, "ola");
				}
			});
		} catch (Exception exception) {
			Log.e(TAG, String.valueOf(exception));
		}
	}

}

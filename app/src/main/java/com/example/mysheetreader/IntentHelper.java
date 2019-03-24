package com.example.mysheetreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class IntentHelper {
	public Bundle bundle;
	public Context context;

	public IntentHelper(Context context){
		this.bundle = new Bundle();
		this.context = context;
	}

	public Bundle prepareIntentBlockList(List<Block> blocks, int position) {
		bundle.putSerializable(this.context.getResources().getString(R.string.category_key),
				blocks.get(position));
		bundle.putSerializable(this.context.getResources().getString(R.string.bundle_position),
				position);
		return this.bundle;
	}

	public Bundle prepareIntentBlock(Block block, int position) {
		bundle.putSerializable(this.context.getResources().getString(R.string.category_key),
				block);
		bundle.putSerializable(this.context.getResources().getString(R.string.bundle_position),
				position);
		return this.bundle;
	}

	public Bundle prepareIntentCategory(Block.Category category, int position) {
		bundle.putSerializable(this.context.getResources().getString(R.string.row_key),
				category);
		bundle.putSerializable(this.context.getResources().getString(R.string.bundle_position),
				position);
		return this.bundle;
	}


	public List<Object> readFromIntentBlockList(Intent data, String key) {
		List<Object> objects = new ArrayList<>();

		Bundle bundle = data.getExtras();
		Object object = bundle.getSerializable(key);
		objects.add(object);
		object = bundle.getSerializable(this.context.getResources().getString(R.string.bundle_position));
		objects.add(object);

		return objects;
	}

}

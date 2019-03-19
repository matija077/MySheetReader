package com.example.mysheetreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;


public class CategoryListAdapter extends ArrayAdapter {

	private Context context;
	private List<Block.Category> categories;

	public CategoryListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
		super(context, resource, objects);
		this.context = context;
		this.categories = objects;
	}


	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
		}

		TextView textView = view.findViewById(R.id.row_text_view);
		textView.setText(categories.get(position).getName());

		return view;
	}
}

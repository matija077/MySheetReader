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


public class BlockArrayAdapter extends ArrayAdapter {
	private Context context;
	private List<Block> blocks;

	public BlockArrayAdapter(@NonNull Context context, int resource, @NonNull List<Block> blocks) {
		super(context, resource, blocks);
		this.context = context;
		this.blocks = blocks;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
		}

		Block block = blocks.get(position);
		TextView textView = view.findViewById(R.id.row_text_view);
		textView.setText(block.getBlockId());

		return view;
	}
}

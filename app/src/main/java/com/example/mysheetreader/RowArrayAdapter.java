package com.example.mysheetreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class RowArrayAdapter extends ArrayAdapter {
	private Context context;
	private List<Block.Category.Row> rows;

	public RowArrayAdapter(@NonNull Context context, int resource, @NonNull List objects) {
		super(context, resource, objects);
		this.context = context;
		this.rows = objects;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.data_row, parent, false);
		}

		TextView textViewSubCategory = view.findViewById(R.id.data_row_sub_category);
		textViewSubCategory.setText(rows.get(position).getSubCategory());

		TextView textViewDataInteger = view.findViewById(R.id.data_row_data_integer);
		textViewDataInteger.setText(String.valueOf(rows.get(position).getDataInteger()));
		textViewDataInteger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("weird", "da");
			}
		});

		EditText editText = view.findViewById(R.id.data_row_add);
		editText.setText(rows.get(position).getAdd());
		editText.addTextChangedListener(new CustomEditTextListener(position));

		return view;
	}

	public void updateItem(View view, int position, String data) {
		TextView textViewDataInteger = view.findViewById(R.id.data_row_data_integer);
		rows.get(position).addData(data);
		textViewDataInteger.setText(String.valueOf(rows.get(position).getDataInteger()));

		EditText editText = view.findViewById(R.id.data_row_add);
		editText.setText("");
	}

	private class CustomEditTextListener implements TextWatcher {
		private int position;

		public CustomEditTextListener(int position) {
			this.position = position;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();
			rows.get(this.position).setAdd(text);
		}
	}
}

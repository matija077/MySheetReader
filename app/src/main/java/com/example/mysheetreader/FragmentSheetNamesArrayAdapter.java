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


public class FragmentSheetNamesArrayAdapter extends ArrayAdapter {
	private List<String> sheetNames;
	private String spreadsheetURL;
	private Context context;
	private SheetNameInterface sheetNameInterface;

	public FragmentSheetNamesArrayAdapter(@NonNull Context context, int resource,
										  @NonNull List objects, @NonNull String spreadsheetURL,
										  @NonNull SheetNameInterface sheetNameInterface) {
		super(context, resource, objects);
		this.sheetNames = objects;
		this.spreadsheetURL = spreadsheetURL;
		this.context = context;
		this.sheetNameInterface = sheetNameInterface;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
		}

		TextView textView = view.findViewById(R.id.row_text_view);
		textView.setText(sheetNames.get(position));
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sheetNameInterface.onClicked(v);
			}
		});

		return view;
	}

	public interface SheetNameInterface {
		void onClicked(View v);
	}
}

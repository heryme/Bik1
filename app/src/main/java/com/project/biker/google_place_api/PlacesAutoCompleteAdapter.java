package com.project.biker.google_place_api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.project.biker.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

	ArrayList<HashMap<String, Object>> resultList;

	Context mContext;
	int mResource;
	AlertDialog dialog;
	Boolean isProfile;

	GooglePlaceApi mPlaceAPI = new GooglePlaceApi();

	public PlacesAutoCompleteAdapter(Context context, int resource,Boolean isProfile) {
		super(context, resource);

		mContext = context;
		mResource = resource;
		dialog =  AlertDialogUtility(mContext,context.getString(R.string.location_limit));
		this.isProfile = isProfile;
	}

	@Override
	public int getCount() {
		// Last item will be the footer
		return resultList.size();
	}

	@Override
	public String getItem(int position) {
		return resultList.get(position).toString();
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();

				if (constraint != null) {
					resultList = mPlaceAPI.autocomplete(constraint.toString(),dialog,mContext,isProfile);
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}

				return filterResults;

			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@SuppressWarnings("unused")
			public boolean onLoadClass(@SuppressWarnings("rawtypes") Class clazz) {
				return false;
			}
		};

		return filter;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		// if (convertView == null) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (position != (resultList.size() - 1))
			view = inflater.inflate(R.layout.row_google_autocomplete_text, null);
		else
			view = inflater.inflate(R.layout.row_google_autocomplete_text, null);
		// }
		// else {
		// view = convertView;
		// }

		if (position != (resultList.size() - 1)) {
			TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);

			HashMap<String, Object> mapDescription = resultList.get(position);

			autocompleteTextView.setText(mapDescription.get("DESCRIPTION").toString());

		} else {
			// ImageView imageView = (ImageView)
			// view.findViewById(R.id.imageView);
			// not sure what to do <img
			// src="http://s.w.org/images/core/emoji/72x72/1f600.png" alt="😀"
			// draggable="false" class="emoji">
		}

		return view;
	}

	private AlertDialog AlertDialogUtility(Context context, String message) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setMessage(message);
		builder1.setCancelable(true);
		builder1.setPositiveButton(
				"Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert11 = builder1.create();
		return  alert11;

	}
}
package com.emgeesons.crime_stoppers.vehicle_security;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	private Context context;
	private String[] mtitle;

	private int[] micon;
	private LayoutInflater inflater;

	public MenuListAdapter(Context context, String title[], int icon[]) {
		this.context = context;
		this.mtitle = title;

		this.micon = icon;

	}

	@Override
	public int getCount() {
		return mtitle.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup parent) {

		TextView title;
		ImageView icon;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.drawer_list_item, parent,
				false);
		title = (TextView) itemView.findViewById(R.id.title);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		// show line at pos 2 and 6
		if (position == 2 || position == 6) {

			RelativeLayout s = (RelativeLayout) itemView
					.findViewById(R.id.relativeLayout1);
			s.setVisibility(View.VISIBLE);
			title.setVisibility(View.GONE);
			icon.setVisibility(View.GONE);

		}

		title.setText(mtitle[position]);

		icon.setImageResource(micon[position]);

		return itemView;

	}

}

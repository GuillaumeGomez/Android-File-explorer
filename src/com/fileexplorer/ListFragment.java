package com.fileexplorer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListFragment extends Fragment {
	public  ArrayList<DataHandler> listViewValues = new ArrayList<DataHandler>();

	private View view;
	private ArrayList<Integer>	ar = new ArrayList<Integer>();
	
	public static final String[] tables = {"Search", "Results"};
	public static int pos = 0;

	public static String getTableName() {
		if (pos == 1)
			pos = 0;
		else
			pos = 1;
		return tables[pos];
	}

	public int addCheck(int pos) {
		ar.add(pos);
		return ar.size();
	}

	public int removeCheck(int pos) {
		ar.remove(Integer.valueOf(pos));
		return ar.size();
	}

	public int getChecked() {
		return ar.size();
	}

	public ArrayList<DataHandler>	getCheckedItem() {
		ArrayList<DataHandler> list = new ArrayList<DataHandler>();

		for (Integer i : ar) {
			list.add(listViewValues.get(i));
		}
		ar.clear();
		return list;
	}

	public void removeItem(DataHandler d) {
		//db.deleteData(d.id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//db = new DatabaseHandler(this.getActivity(), ListFragment.getTableName());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.search, container, false);

		return view;
	}

	public void onItemClick(int pos){

	}

	public void onItemLongClick(int pos){
	}
}

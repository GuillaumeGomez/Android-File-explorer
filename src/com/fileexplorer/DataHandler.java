package com.fileexplorer;

public class DataHandler {
	public String 		name = "";
	public boolean		is_dir = false;
	public boolean		is_selected = false;
	public String		size = "";

	DataHandler(){	
	}
	
	DataHandler(String name, boolean is_dir, long size){
		this.is_dir = is_dir;
		this.name = name;
		if (size >= 1000000000)
			this.size = Long.toString(size / 1000000000) + " GB";
		else if (size >= 1000000)
			this.size = Long.toString(size / 1000000) + " MB";
		else if (size >= 1000)
			this.size = Long.toString(size / 1000) + " KB";
		else
			this.size = Long.toString(size) + " B";
	}
	
	DataHandler(DataHandler d){
		this.name = d.name;
		this.is_dir = d.is_dir;
		this.is_selected = d.is_selected;
		this.size = d.size;
	}
}

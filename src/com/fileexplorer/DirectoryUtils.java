package com.fileexplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

public class DirectoryUtils {
	public ArrayList<DataHandler>	dataList = new ArrayList<DataHandler>();

	private class CustomComparator implements Comparator<DataHandler> {
		@Override
		public int compare(DataHandler o1, DataHandler o2) {
			return o1.name.toLowerCase(Locale.ENGLISH).compareTo(o2.name.toLowerCase(Locale.ENGLISH));
		}
	}

	DirectoryUtils() {
		readDir(Environment.getExternalStorageDirectory().toString());
	}

	DirectoryUtils(String path) {
		readDir(path);
	}

	ArrayList<DataHandler>	getDatas() {
		return dataList;
	}

	ArrayList<DataHandler>	readDir() {
		return readDir(Environment.getExternalStorageDirectory().toString());
	}

	ArrayList<DataHandler>	readDir(String path) {
		File f = new File(path);
		File file[] = f.listFiles();

		if (file == null)
			return null;
		ArrayList<DataHandler> fileList = new ArrayList<DataHandler>();

		dataList.clear();
		for (int i = 0; i < file.length; ++i){
			if (file[i].isDirectory())
				dataList.add(new DataHandler(file[i].getName(), file[i].isDirectory(), file[i].length()));
			else
				fileList.add(new DataHandler(file[i].getName(), file[i].isDirectory(), file[i].length()));
		}
		Collections.sort(dataList, new CustomComparator());
		Collections.sort(fileList, new CustomComparator());
		dataList.addAll(fileList);
		return dataList;
	}

	public static boolean copy(String src, String dst, Context t) {
		File	f = new File(src);

		if (f.isDirectory())
			return copyDirectory(f, new File(dst), t);
		return copyFile(f, new File(dst), t);
	}

	static public boolean copyDirectory(File sourceLocation, File targetLocation, Context t) {
		if (sourceLocation.isDirectory()) {
			String[] children = sourceLocation.list();
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]), t);
			}
		} else {
			copyFile(sourceLocation, targetLocation, t);
		}
		return true;
	}

	static public boolean copyFile(File sourceLocation, File targetLocation, Context t) {
		try {
			if (targetLocation.exists()) {
				String filename = targetLocation.getName();
				final File f1 = sourceLocation;
				final File f2 = targetLocation;
				AlertDialog.Builder alert = new AlertDialog.Builder(t);

				alert.setTitle("Problem");
				alert.setMessage("The file " + filename + " already exists. Do you want to replace it ?");
				alert.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						FileChannel inputChannel = null;
						FileChannel outputChannel = null;
						try {
							inputChannel = new FileInputStream(f1).getChannel();
							outputChannel = new FileOutputStream(f2).getChannel();
							outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
							inputChannel.close();
							outputChannel.close();
						} catch (Exception e) {
						}
					}
				});
				alert.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
					}
				});
				alert.show();
				return true;
			} else {
				FileChannel inputChannel = null;
				FileChannel outputChannel = null;
				try {
					inputChannel = new FileInputStream(sourceLocation).getChannel();
					outputChannel = new FileOutputStream(targetLocation).getChannel();
					outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
					inputChannel.close();
					outputChannel.close();
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	static public void delete(ArrayList<String> s) {
		for (String t : s) {
			DirectoryUtils.delete(t);
		}
	}

	static public boolean delete(String s) {
		File f = new File(s);

		if (f == null || !f.exists()) {
			return false;
		}
		if (f.isDirectory()) {
			return deleteDirectory(f);
		}
		return f.delete();
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	static public String createFolder(String t) {
		File d = new File(t);

		if (d.exists()) {
			return "Folder already exists";
		}
		if (!d.mkdir()) {
			return "Folder couldn't been created";
		}
		return "";
	}

	static public String createFile(String t) {
		File d = new File(t);

		if (d.exists()) {
			return "File already exists";
		}
		try {
			if (!d.createNewFile()) {
				return "Folder couldn't been created";
			}
		} catch (IOException e) {
			return "Folder couldn't been created";
		}
		return "";
	}

	//both old_name and new_name need the entire path !
	static public boolean rename(String old_name, String new_name) {
		File f = new File(old_name);

		if (f == null || !f.exists()) {
			return false;
		}
		return f.renameTo(new File(new_name));
	}
}

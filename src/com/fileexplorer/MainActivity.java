package com.fileexplorer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

// TODO
// add favorite
// add home
// add search

public class MainActivity extends Activity {
	public ArrayList<DataHandler> listViewValues = new ArrayList<DataHandler>();
	public ArrayList<BitmapWorkerTask> backWorks = new ArrayList<BitmapWorkerTask>();
	private ArrayList<View> selected = new ArrayList<View>();
	private ArrayList<String> cutFiles = new ArrayList<String>();
	private ArrayList<String> copyFiles = new ArrayList<String>();
	private DirectoryUtils dir = new DirectoryUtils();
	public static final int MY_CHILD_INTENT = 9787456;
	private String currentDir = "./";
	private String prevDir = "";
	private String drawWay = "classic";
	private int resId = 0;
	private boolean selectMode = false;
	private TextView ac_copy = null;
	private TextView ac_cut = null;
	private TextView ac_paste = null;
	private TextView ac_delete = null;
	private TextView ac_rename = null;
	private MenuItem show_options = null;
	private ActionBarDrawerToggle mDrawerToggle = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MainActivity ac = this;

		ac_copy = (TextView) findViewById(R.id.ac_copy);
		ac_cut = (TextView) findViewById(R.id.ac_cut);
		ac_paste = (TextView) findViewById(R.id.ac_paste);
		ac_delete = (TextView) findViewById(R.id.ac_delete);
		ac_rename = (TextView) findViewById(R.id.ac_rename);

		setTextViewEnabled(ac_copy, false);
		setTextViewEnabled(ac_cut, false);
		setTextViewEnabled(ac_paste, false);
		setTextViewEnabled(ac_delete, false);
		setTextViewEnabled(ac_rename, false);
		ac_copy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.isEnabled())
					ac.copyClicked();
			}
		});
		ac_cut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.isEnabled())
					ac.cutClicked();
			}
		});
		ac_paste.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.isEnabled())
					ac.pasteClicked();
			}
		});
		ac_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.isEnabled())
					ac.deleteClicked();
			}
		});
		ac_rename.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0.isEnabled())
					ac.renameClicked();
			}
		});

		DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);

		final MainActivity ft = this;
		RelativeLayout lay = (RelativeLayout)findViewById(R.id.search);
		lay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ft, SearchActivity.class);
				startActivity(intent);
			}
			
		});
		((ImageView)lay.findViewById(R.id.image)).setBackgroundResource(R.drawable.search);
		((TextView)lay.findViewById(R.id.element_name)).setText("search");
		((TextView)lay.findViewById(R.id.element_name)).setTextColor(Color.parseColor("#ffdddddd"));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
				R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {

			}

			public void onDrawerOpened(View drawerView) {

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		this.drawAct();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		show_options = menu.findItem(R.id.show_options);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.change_view:
			drawWay = ("classic".equals(drawWay) ? "horizontal" : "classic");
			this.drawAct();
			break;
			/*case R.id.add:
			this.addElement();
			break;*/
		case R.id.credits:
			Intent intent = new Intent(this, CreditsActivity.class);
			startActivity(intent);
			break;
		case R.id.create_file:
			this.addElement(false);
			break;
		case R.id.create_folder:
			this.addElement(true);
			break;
		case R.id.show_options:
			if (!selectMode) {
				item.setIcon(R.drawable.list_activated);
				findViewById(R.id.button_line).setVisibility(View.VISIBLE);
				selectMode = true;
				((ScrollView) findViewById(R.id.scrollview))
				.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 0, 0.91f));
			} else {
				this.onBackPressed();
			}
			break;
		}
		return true;
	}

	public void setTextViewEnabled(TextView t, boolean enable) {
		if (!enable) {
			t.setTextColor(Color.argb(255, 160, 160, 160));
		} else {
			t.setTextColor(Color.argb(255, 255, 255, 255));
		}
		t.setEnabled(enable);
	}

	public boolean createFolder(String t) {
		String res = DirectoryUtils.createFolder(currentDir + t);

		if ("".equals(res)) {
			this.drawAct();
			return true;
		}
		this.displayBox("Error", res);
		return false;
	}

	public boolean createFile(String t) {
		String res = DirectoryUtils.createFile(currentDir + t);

		if ("".equals(res)) {
			this.drawAct();
			return true;
		}
		this.displayBox("Error", res);
		return false;
	}

	public void addElement(boolean isDir) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final boolean dir = isDir;
		if (isDir) {
			alert.setTitle("Create new folder");
			alert.setMessage("Enter folder's name");
		} else {
			alert.setTitle("Create new file");
			alert.setMessage("Enter file's name");
		}

		final EditText input = new EditText(this);
		final MainActivity main = this;
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if ("".equals(value))
					return;
				if (dir) {
					main.createFolder(value);
				} else {
					main.createFile(value);
				}
			}
		});
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}

	/*public void addElement() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.choice, null);

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.setView(layout);
		alert.setTitle("Choose an action");
		final AlertDialog dial = alert.show();

		final MainActivity act = this;

		((LinearLayout)layout.findViewById(R.id.file)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dial.dismiss();
				act.addElement(false);
			}
		});
		((LinearLayout)layout.findViewById(R.id.folder)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dial.dismiss();
				act.addElement(true);
			}
		});
	}*/

	public ImageView setImageToImage(DataHandler data, ImageView img, View v) {
		if (data.is_dir) {
			img.setImageResource(R.drawable.directory);
			return img;
		}
		String filePath = currentDir + data.name;
		if (data.name.endsWith(".apk")) {
			PackageInfo packageInfo = this.getPackageManager()
					.getPackageArchiveInfo(filePath,
							PackageManager.GET_ACTIVITIES);
			if (packageInfo != null) {
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				appInfo.sourceDir = filePath;
				appInfo.publicSourceDir = filePath;
				Drawable icon = appInfo.loadIcon(this.getPackageManager());
				img.setImageBitmap(((BitmapDrawable) icon).getBitmap());
				return img;
			}
		}
		String type = getFileType(data.name);

		if (type.contains("audio")) {
			img.setImageResource(R.drawable.music_file);
		} else if (type.contains("image") || type.contains("video")) {
			BitmapWorkerTask task = new BitmapWorkerTask(new Container(this, img), filePath,
					type.contains("video") ? "video" : "");
			backWorks.add(task);
			task.execute(++resId);
			img.setImageResource(R.drawable.picture_file);
		} else if (type.contains("text")) {
			img.setImageResource(R.drawable.text_file);
		} else {
			img.setImageResource(R.drawable.file);
		}
		return img;
	}

	public View createView(DataHandler tmp, int tmp_size) {
		View v = null;
		TextView t_h = null;

		if ("classic".equals(drawWay)) {
			v = getLayoutInflater().inflate(R.layout.element, null);
		} else if ("horizontal".equals(drawWay)) {
			v = getLayoutInflater().inflate(R.layout.horizontal_element, null);
			t_h = (TextView) v.findViewById(R.id.element_size);
		}
		final MainActivity act = this;

		if ("classic".equals(drawWay)) {
			((LinearLayout) v.findViewById(R.id.sub_lay))
			.setLayoutParams(new LayoutParams(tmp_size,
					LayoutParams.WRAP_CONTENT));
		}
		TextView t = (TextView) v.findViewById(R.id.element_name);
		setImageToImage(tmp, (ImageView) v.findViewById(R.id.image), v);

		t.setText(tmp.name);
		if (t_h != null)
			t_h.setText(tmp.size);
		if (tmp.is_dir) {
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					act.changeDirectory(arg0);
				}
			});
		} else {
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					act.onItemClicked(v);
				}
			});
		}
		v.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				act.onItemLongClick(arg0);
				return false;
			}
		});
		return v;
	}

	public void cancelAllBackWorks() {
		for (BitmapWorkerTask tmp : backWorks) {
			tmp.cancel(true);
		}
		backWorks.clear();
	}

	@SuppressWarnings("deprecation")
	public void overrideGetDisplaySize(Display display, Point outSize) {
		try {
			@SuppressWarnings("rawtypes")
			Class pointClass = Class.forName("android.graphics.Point");
			Method newGetSize = Display.class.getMethod("getSize", new Class[]{ pointClass });

			newGetSize.invoke(display, outSize);
		} catch(Exception ex) {
			outSize.x = display.getWidth();
			outSize.y = display.getHeight();
		}
	}

	public int convertDpToPx(int dp) {
		return (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp, 
				getResources().getDisplayMetrics());
	}

	public boolean drawAct() {
		ArrayList<DataHandler> tmpList = dir.readDir(currentDir);
		resId = 0;

		if (tmpList == null) {
			displayBox("Error", "You don't have access to this folder:\n"
					+ currentDir);
			currentDir = prevDir;
			return false;
		}

		ArrayList<String> selectedString = new ArrayList<String>();

		for (View t : selected) {
			selectedString.add(((TextView)t.findViewById(R.id.element_name)).getText().toString());
		}

		cancelAllBackWorks();
		listViewValues = tmpList;
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		overrideGetDisplaySize(display, size);

		int lay_width = 135;
		int number = 1;
		if ("classic".equals(drawWay)) {
			number = size.x / lay_width;
		}
		int tmp_size = (size.x % lay_width) / number + lay_width;

		LinearLayout lay = (LinearLayout) findViewById(R.id.mainLay);

		lay.removeAllViews();
		if (listViewValues.size() < 1)
			return true;
		boolean is_classic = "classic".equals(drawWay);

		for (int x = 0; x < listViewValues.size();) {
			if (is_classic == false && x > 0) {
				View v = new View(this);
				LinearLayout.LayoutParams lay_param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, convertDpToPx(1));
				lay_param.setMargins(0, convertDpToPx(5), 0, convertDpToPx(5));
				v.setLayoutParams(lay_param);
				v.setBackgroundColor(Color.parseColor("#ffaaaaaa"));
				lay.addView(v);
			}
			LinearLayout hlay = new LinearLayout(this);

			hlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			hlay.setOrientation(LinearLayout.HORIZONTAL);
			for (int y = 0; y < number && x < listViewValues.size(); ++y) {
				DataHandler tmp_data = listViewValues.get(x++);
				View new_view = createView(tmp_data, tmp_size);

				if (selectedString.contains(tmp_data.name)) {
					changeSelectState(new_view);
					selectedString.remove(tmp_data.name);
				}
				hlay.addView(new_view);
			}
			lay.addView(hlay);
		}
		return true;
	}

	public void displayBox(String title, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(msg);
		builder.setTitle(title);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		builder.create().show();
	}

	public String getFileType(String s) {
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		s = s.toLowerCase(Locale.ENGLISH);
		String ext = s.substring(s.lastIndexOf(".") + 1);
		String ret = mime.getMimeTypeFromExtension(ext);
		if (ret == null)
			return "";
		return ret;
	}

	public void onItemLongClick(View v) {
		findViewById(R.id.button_line).setVisibility(View.VISIBLE);
		//changeSelectState(v);
		if (!selectMode) {
			selectMode = true;
			((ScrollView) findViewById(R.id.scrollview))
			.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 0.91f));
		}
	}

	public void deselectAll() {
		for (View tmp : selected) {
			tmp.setBackgroundColor(Color.argb(0, 255, 255, 255));
		}
		clearSelection();
	}

	public void changeSelectState(View v) {
		if (selected.contains(v)) {
			selected.remove(v);
			v.setBackgroundColor(Color.argb(0, 255, 255, 255));
			setTextViewEnabled(ac_cut, selected.size() > 0);
			setTextViewEnabled(ac_copy, selected.size() > 0);
			setTextViewEnabled(ac_delete, selected.size() > 0);
			setTextViewEnabled(ac_rename, selected.size() > 0);
		} else {
			selected.add(v);
			v.setBackgroundColor(Color.argb(255, 255, 186, 0));
			setTextViewEnabled(ac_cut, true);
			setTextViewEnabled(ac_copy, true);
			setTextViewEnabled(ac_delete, true);
			setTextViewEnabled(ac_rename, true);
		}
	}

	public void changeDirectory(View v) {
		if (selectMode) {
			changeSelectState(v);
			return;
		}
		String tmp = currentDir;
		currentDir += ((TextView) v.findViewById(R.id.element_name)).getText()
				.toString() + "/";
		if (this.drawAct() == false) {
			currentDir = tmp;
		}
	}

	public void onItemClicked(View v) {
		if (selectMode) {
			changeSelectState(v);
			return;
		}
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(currentDir
				+ ((TextView) v.findViewById(R.id.element_name)).getText()
				.toString());

		intent.setDataAndType(Uri.fromFile(file), getFileType(file.getName()));

		try {
			startActivity(intent);
		} catch (Exception e) {
			this.displayBox("Error", "No application found to open this file");
		}
	}

	@Override
	public void onBackPressed() {
		if (selectMode) {
			selectMode = false;
			findViewById(R.id.button_line).setVisibility(View.INVISIBLE);
			show_options.setIcon(R.drawable.list);
			deselectAll();
			((ScrollView) findViewById(R.id.scrollview))
			.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			return;
		}
		if (currentDir.equals("./"))
			return;
		String[] s = currentDir.split("/");
		prevDir = currentDir;
		currentDir = "";
		for (int x = 0; x < s.length - 1; ++x) {
			currentDir += s[x] + "/";
		}
		this.drawAct();
	}

	public void copyClicked() {
		cutFiles.clear();
		copyFiles.clear();
		for (View v : selected) {
			copyFiles.add(currentDir
					+ ((TextView) v.findViewById(R.id.element_name)).getText()
					.toString());
		}
		this.setTextViewEnabled(ac_paste, copyFiles.size() > 0);
		if (copyFiles.size() > 0) {
			this.displayLittleInfoMessage("You can now paste the copied elements");
		}
	}

	public void cutClicked() {
		cutFiles.clear();
		copyFiles.clear();
		for (View v : selected) {
			cutFiles.add(currentDir
					+ ((TextView) v.findViewById(R.id.element_name)).getText()
					.toString());
		}
		this.setTextViewEnabled(ac_paste, cutFiles.size() > 0);
		if (cutFiles.size() > 0) {
			this.displayLittleInfoMessage("You can now paste the cut elements");
		}
	}

	public String getName(String s) {
		String[] tmp = s.split("/");

		return tmp[tmp.length - 1];
	}

	public void pasteClicked() {
		if (copyFiles.size() > 0) {
			for (String tmp : copyFiles) {
				if (!DirectoryUtils.copy(tmp, currentDir + getName(tmp), this)) {
					this.displayBox("Error", "Cannot copy:" + getName(tmp));
				}
			}
			copyFiles.clear();
		} else if (cutFiles.size() > 0) {
			for (String tmp : cutFiles) {
				if (!DirectoryUtils.copy(tmp, currentDir + getName(tmp), this)) {
					this.displayBox("Error", "Cannot copy:" + getName(tmp));
				} else {
					File f = new File(tmp);

					if (!f.delete()) {
						this.displayBox("Error", "Cannot delete after copy:"
								+ tmp);
					}
				}
			}
			cutFiles.clear();
		}
		this.setTextViewEnabled(ac_paste, false);
		this.drawAct();
		this.displayLittleInfoMessage("Done");
	}

	public void deleteClicked() {
		final MainActivity act = this;
		if (selected.size() == 1) {
			final String filename = currentDir
					+ ((TextView) selected.get(0).findViewById(
							R.id.element_name)).getText().toString();
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Delete");
			alert.setMessage("Are you sure you want to delete "
					+ ((TextView) selected.get(0).findViewById(
							R.id.element_name)).getText().toString() + " ?");
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					DirectoryUtils.delete(filename);
					act.clearSelection();
					act.drawAct();
				}
			});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
				}
			});
			alert.show();
		} else {
			final ArrayList<String> filenames = new ArrayList<String>();
			for (View v : selected) {
				filenames.add(currentDir
						+ ((TextView) v.findViewById(R.id.element_name))
						.getText().toString());
			}
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Delete");
			alert.setMessage("Are you sure you want to delete these "
					+ Integer.toString(filenames.size()) + " items ?");
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					DirectoryUtils.delete(filenames);
					act.clearSelection();
					act.drawAct();
				}
			});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
				}
			});
			alert.show();
		}
	}

	public void renameClicked() {
		for (View v : selected) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Rename");
			alert.setMessage("Enter new name for: "
					+ ((TextView) v.findViewById(R.id.element_name)).getText()
					.toString());

			final MainActivity act = this;
			final EditText input = new EditText(this);
			final String path = currentDir;
			final String origin = ((TextView) v.findViewById(R.id.element_name))
					.getText().toString();

			input.setText(origin);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					if (origin.equals(input.getText().toString()))
						return;
					DirectoryUtils.rename(path + origin, path
							+ input.getText().toString());
					act.drawAct();
				}
			});
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
				}
			});
			alert.show();
		}
		clearSelection();
		this.drawAct();
	}

	public void clearSelection() {
		selected.clear();
		setTextViewEnabled(ac_copy, false);
		setTextViewEnabled(ac_cut, false);
		//setTextViewEnabled(ac_paste, false);
		setTextViewEnabled(ac_delete, false);
		setTextViewEnabled(ac_rename, false);
	}

	public void displayLittleInfoMessage(String msg, int duration) {
		Toast toast = Toast.makeText(this, msg, duration);
		toast.show();
	}

	public void displayLittleInfoMessage(String msg) {
		displayLittleInfoMessage(msg, Toast.LENGTH_SHORT);
	}
	
	public void doNothing(View v) {
	
	}
	
	public void	searchClicked(View v) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}
}

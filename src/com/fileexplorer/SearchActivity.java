package com.fileexplorer;

import java.util.ArrayList;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;

public class SearchActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener {
	private MyAdapter mAdapter = null;
	private ViewPager mPager = null;
	
	public static class MyAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment>	lf = new ArrayList<Fragment>();
		SearchActivity ma;

		public MyAdapter(FragmentManager fm, SearchActivity m) {
			super(fm);
			ma = m;
			lf.clear();
			lf.add(new ListFragment());
			lf.add(new ListFragment());
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			if (position >= lf.size() || position < 0)
				return null;
			return lf.get(position);
		}

		public ArrayList<Fragment>	getPageList() {
			return lf;
		}
	}
	
	private class PageListener extends ViewPager.SimpleOnPageChangeListener {
		SearchActivity ma;

		public PageListener(SearchActivity m) {
			ma = m;
		}

		@Override
		public void onPageSelected(int position) {
			ma.tabChanged(position);
			//getActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		mAdapter = new MyAdapter(getSupportFragmentManager(), this);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mPager.setOnPageChangeListener(new PageListener(this));

		android.support.v7.app.ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
		addTab(bar, "Search parameters");
		addTab(bar, "Results");
	}
	
	public void addTab(android.support.v7.app.ActionBar bar, String s) {
		Tab tab = bar.newTab();
        tab.setText(s);
        tab.setTabListener(this);
        bar.addTab(tab);
	}
	
	public void tabChanged(int pos) {
		//ListFragment l = (ListFragment)mAdapter.getItem(pos);
		//changeMenuEnable(l.getChecked() > 0);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
}

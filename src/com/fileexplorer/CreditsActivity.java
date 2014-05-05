package com.fileexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class CreditsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credits);
		
		TextView t = (TextView)findViewById(R.id.description_text);
	    t.setMovementMethod(LinkMovementMethod.getInstance());
	}
}

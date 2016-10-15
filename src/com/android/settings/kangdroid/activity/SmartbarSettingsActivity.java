package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.crdroid.settings.fragments.SmartbarSettings;

public class SmartbarSettingsActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SmartbarSettings()).commit();
 }

}
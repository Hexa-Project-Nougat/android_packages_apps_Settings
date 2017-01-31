package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.crdroid.settings.fragments.PulseSettings;

public class PulseSettingsActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PulseSettings()).commit();
 }

}
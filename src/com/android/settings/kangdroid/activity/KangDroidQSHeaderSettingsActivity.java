package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.android.settings.kangdroid.KangDroidQSHeaderSettings;

public class KangDroidQSHeaderSettingsActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new KangDroidQSHeaderSettings()).commit();
 }

}
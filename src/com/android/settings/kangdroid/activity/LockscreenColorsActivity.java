package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.android.settings.rr.LockscreenColors;

public class LockscreenColorsActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new LockscreenColors()).commit();
 }

}
package com.android.settings.hydrogen.fingerprint;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.widget.Button;
import java.util.List;

public class FingerprintEnrollFinish
  extends FingerprintEnrollBase
{
  private boolean mLaunchingEnroll;
  
  private void setFingerprintEnrolling(boolean paramBoolean)
  {
    int i = 1;
    if (this.mLaunchingEnroll) {
      return;
    }
    boolean bool;
    ContentResolver localContentResolver;
    if (Settings.System.getInt(getApplicationContext().getContentResolver(), "oem_acc_fingerprint_enrolling", 0) != 0)
    {
      bool = true;
      if (paramBoolean != bool)
      {
        localContentResolver = getApplicationContext().getContentResolver();
        if (!paramBoolean) {
          break label61;
        }
      }
    }
    for (;;)
    {
      Settings.System.putInt(localContentResolver, "oem_acc_fingerprint_enrolling", i);
      return;
      bool = false;
      break;
      label61:
      i = 0;
    }
  }
  
  private void showAnimation()
  {
    overridePendingTransition(2131034142, 2131034143);
  }
  
  protected int getMetricsCategory()
  {
    return 242;
  }
  
  public void onClick(View paramView)
  {
    if (paramView.getId() == 2131362150)
    {
      this.mLaunchingEnroll = true;
      Intent localIntent = getEnrollingIntent();
      localIntent.addFlags(33554432);
      startActivity(localIntent);
      finish();
    }
    super.onClick(paramView);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130969152);
    setHeaderText(2131691094);
    setHeaderTextColor(2131493771);
    paramBundle = (Button)findViewById(2131362150);
    if (((FingerprintManager)getSystemService("fingerprint")).getEnrolledFingerprints(this.mUserId).size() >= getResources().getInteger(17694881))
    {
      paramBundle.setVisibility(4);
      return;
    }
    paramBundle.setOnClickListener(this);
  }
  
  protected void onNextButtonClick()
  {
    setResult(1);
    finish();
  }
  
  public void onPause()
  {
    setFingerprintEnrolling(false);
    super.onPause();
  }
  
  public void onResume()
  {
    this.mLaunchingEnroll = false;
    setFingerprintEnrolling(true);
    super.onResume();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\FingerprintEnrollFinish.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.android.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.util.Log;
import android.view.View;
import android.view.Window;
import java.util.Objects;

public class FallbackHome
  extends Activity
{
  private static final String TAG = "FallbackHome";
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      FallbackHome.-wrap0(FallbackHome.this);
    }
  };
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      FallbackHome.-wrap0(FallbackHome.this);
    }
  };
  
  private void maybeFinish()
  {
    if (((UserManager)getSystemService(UserManager.class)).isUserUnlocked())
    {
      Object localObject = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
      localObject = getPackageManager().resolveActivity((Intent)localObject, 0);
      if (Objects.equals(getPackageName(), ((ResolveInfo)localObject).activityInfo.packageName))
      {
        Log.d("FallbackHome", "User unlocked but no home; let's hope someone enables one soon?");
        this.mHandler.sendEmptyMessageDelayed(0, 500L);
      }
    }
    else
    {
      return;
    }
    Log.d("FallbackHome", "User unlocked and real home found; let's go!");
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) == 0) {
      setTheme(16973834);
    }
    getWindow().getDecorView().setSystemUiVisibility(6);
    registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    maybeFinish();
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mReceiver);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\FallbackHome.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
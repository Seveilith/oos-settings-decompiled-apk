package com.oneplus.settings.faceunlock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.util.Log;
import com.android.internal.policy.IOPFaceSettingService;
import com.android.internal.policy.IOPFaceSettingService.Stub;
import com.android.settings.SettingsPreferenceFragment;

public class OPFaceUnlockSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
  private static final String KEY_ADD_FACE = "key_add_face";
  private static final String KEY_AUTO_FACE_UNLOCK_ENABLE = "key_auto_face_unlock_enable";
  private static final String KEY_FACEUNLOCK_CATEGORY = "key_faceunlock_category";
  private static final String KEY_FACEUNLOCK_MANAGEMENT_CATEGORY = "key_faceunlock_management_category";
  private static final String KEY_FACE_UNLOCK_ASSISTIVE_LIGHTING = "key_face_unlock_assistive_lighting";
  private static final String KEY_FACE_UNLOCK_ENABLE = "key_face_unlock_enable";
  private static final String KEY_REMOVE_FACE = "key_remove_face";
  private static final String ONEPLUS_AUTO_FACE_UNLOCK_ENABLE = "oneplus_auto_face_unlock_enable";
  private static final String ONEPLUS_FACE_UNLOCK_ASSISTIVE_LIGHTING_ENABLE = "oneplus_face_unlock_assistive_lighting_enable";
  private static final String ONEPLUS_FACE_UNLOCK_ENABLE = "oneplus_face_unlock_enable";
  private static final String ONEPLUS_FACE_UNLOCK_ENROLL_ACTION = "com.oneplus.faceunlock.FaceUnlockActivity";
  private static final int REFRESH_UI = 100;
  private static final int RESULT_FAIL = 1;
  private static final int RESULT_NOT_FOUND = 2;
  private static final int RESULT_OK = 0;
  private static final String TAG = "OPFaceUnlockSettings";
  private Preference mAddFace;
  private SwitchPreference mAutoFaceUnlock;
  private IOPFaceSettingService mFaceSettingService;
  private SwitchPreference mFaceUnlock;
  private SwitchPreference mFaceUnlockAssistiveLighting;
  private PreferenceCategory mFaceUnlockCategory;
  private ServiceConnection mFaceUnlockConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.i("OPFaceUnlockSettings", "Oneplus face unlock service connected");
      OPFaceUnlockSettings.-set0(OPFaceUnlockSettings.this, IOPFaceSettingService.Stub.asInterface(paramAnonymousIBinder));
      OPFaceUnlockSettings.-get0(OPFaceUnlockSettings.this).sendEmptyMessage(100);
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.i("OPFaceUnlockSettings", "Oneplus face unlock service disconnected");
      OPFaceUnlockSettings.-set0(OPFaceUnlockSettings.this, null);
    }
  };
  private PreferenceCategory mFaceUnlockManagerCategory;
  private AlertDialog mRemoveDialog;
  private Preference mRemoveFace;
  private Handler mUIHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      OPFaceUnlockSettings.-wrap0(OPFaceUnlockSettings.this);
    }
  };
  
  private void bindFaceUnlockService()
  {
    try
    {
      Intent localIntent = new Intent();
      localIntent.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceSettingService");
      getActivity().bindService(localIntent, this.mFaceUnlockConnection, 1);
      Log.i("OPFaceUnlockSettings", "Start bind oneplus face unlockservice");
      return;
    }
    catch (Exception localException)
    {
      Log.i("OPFaceUnlockSettings", "Bind oneplus face unlockservice exception");
    }
  }
  
  private void disableAutoUnlockSettings(boolean paramBoolean)
  {
    if (this.mAutoFaceUnlock != null) {
      this.mAutoFaceUnlock.setEnabled(paramBoolean);
    }
  }
  
  private void disableFaceUnlockAssistiveLightingSettings(boolean paramBoolean)
  {
    if (this.mFaceUnlockAssistiveLighting != null) {
      this.mFaceUnlockAssistiveLighting.setEnabled(paramBoolean);
    }
  }
  
  public static Intent getSetupFaceUnlockIntent(Context paramContext)
  {
    paramContext = new Intent();
    paramContext.setClassName("com.oneplus.faceunlock", "com.oneplus.faceunlock.FaceUnlockActivity");
    paramContext.putExtra("FaceUnlockActivity.StartMode", 1);
    return paramContext;
  }
  
  /* Error */
  private void gotoAddFaceData()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: new 112	android/content/Intent
    //   5: dup
    //   6: invokespecial 113	android/content/Intent:<init>	()V
    //   9: astore_2
    //   10: aload_2
    //   11: ldc 115
    //   13: ldc 50
    //   15: invokevirtual 121	android/content/Intent:setClassName	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   18: pop
    //   19: aload_2
    //   20: ldc -99
    //   22: iconst_0
    //   23: invokevirtual 161	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   26: pop
    //   27: aload_0
    //   28: invokevirtual 125	com/oneplus/settings/faceunlock/OPFaceUnlockSettings:getActivity	()Landroid/app/Activity;
    //   31: aload_2
    //   32: invokevirtual 168	android/app/Activity:startActivity	(Landroid/content/Intent;)V
    //   35: return
    //   36: astore_2
    //   37: ldc 62
    //   39: new 170	java/lang/StringBuilder
    //   42: dup
    //   43: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   46: ldc -83
    //   48: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_1
    //   52: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   55: invokevirtual 184	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   58: invokestatic 187	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   61: pop
    //   62: return
    //   63: astore_1
    //   64: aload_2
    //   65: astore_1
    //   66: goto -29 -> 37
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	69	0	this	OPFaceUnlockSettings
    //   1	51	1	localObject1	Object
    //   63	1	1	localActivityNotFoundException1	android.content.ActivityNotFoundException
    //   65	1	1	localObject2	Object
    //   9	23	2	localIntent	Intent
    //   36	29	2	localActivityNotFoundException2	android.content.ActivityNotFoundException
    // Exception table:
    //   from	to	target	type
    //   2	10	36	android/content/ActivityNotFoundException
    //   10	35	63	android/content/ActivityNotFoundException
  }
  
  /* Error */
  public static void gotoFaceUnlockSettings(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: getstatic 195	java/lang/System:out	Ljava/io/PrintStream;
    //   5: ldc -59
    //   7: invokevirtual 203	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   10: new 112	android/content/Intent
    //   13: dup
    //   14: invokespecial 113	android/content/Intent:<init>	()V
    //   17: astore_2
    //   18: aload_2
    //   19: ldc -51
    //   21: ldc -49
    //   23: invokevirtual 121	android/content/Intent:setClassName	(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    //   26: pop
    //   27: aload_0
    //   28: aload_2
    //   29: invokevirtual 210	android/content/Context:startActivity	(Landroid/content/Intent;)V
    //   32: return
    //   33: astore_0
    //   34: aload_1
    //   35: astore_0
    //   36: ldc 62
    //   38: new 170	java/lang/StringBuilder
    //   41: dup
    //   42: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   45: ldc -83
    //   47: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: aload_0
    //   51: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   54: invokevirtual 184	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   57: invokestatic 187	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   60: pop
    //   61: return
    //   62: astore_0
    //   63: aload_2
    //   64: astore_0
    //   65: goto -29 -> 36
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	paramContext	Context
    //   1	34	1	localObject	Object
    //   17	47	2	localIntent	Intent
    // Exception table:
    //   from	to	target	type
    //   2	18	33	android/content/ActivityNotFoundException
    //   18	32	62	android/content/ActivityNotFoundException
  }
  
  private void initView()
  {
    this.mFaceUnlockCategory = ((PreferenceCategory)findPreference("key_faceunlock_category"));
    this.mFaceUnlockManagerCategory = ((PreferenceCategory)findPreference("key_faceunlock_management_category"));
    this.mAddFace = findPreference("key_add_face");
    this.mAddFace.setOnPreferenceClickListener(this);
    this.mRemoveFace = findPreference("key_remove_face");
    this.mRemoveFace.setOnPreferenceClickListener(this);
    this.mFaceUnlock = ((SwitchPreference)findPreference("key_face_unlock_enable"));
    this.mFaceUnlock.setOnPreferenceChangeListener(this);
    this.mAutoFaceUnlock = ((SwitchPreference)findPreference("key_auto_face_unlock_enable"));
    this.mAutoFaceUnlock.setOnPreferenceChangeListener(this);
    this.mFaceUnlockAssistiveLighting = ((SwitchPreference)findPreference("key_face_unlock_assistive_lighting"));
    this.mFaceUnlockAssistiveLighting.setOnPreferenceChangeListener(this);
  }
  
  private boolean isAutoFaceUnlockEnabled()
  {
    return Settings.System.getInt(getContentResolver(), "oneplus_auto_face_unlock_enable", 1) == 1;
  }
  
  private boolean isFaceAdded()
  {
    boolean bool = false;
    if (this.mFaceSettingService == null) {
      return false;
    }
    int i = 2;
    try
    {
      int j = this.mFaceSettingService.checkState(0);
      i = j;
      Log.i("OPFaceUnlockSettings", "Start check face state:" + j);
      i = j;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.i("OPFaceUnlockSettings", "Start check face State RemoteException:" + localRemoteException);
      }
    }
    if (i == 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isFaceUnlockAssistiveLightingEnabled()
  {
    return Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_assistive_lighting_enable", 0) == 1;
  }
  
  private boolean isFaceUnlockEnabled()
  {
    return Settings.System.getInt(getContentResolver(), "oneplus_face_unlock_enable", 1) == 1;
  }
  
  private void refreshUI()
  {
    if (isFaceAdded())
    {
      this.mFaceUnlockCategory.addPreference(this.mRemoveFace);
      this.mFaceUnlockCategory.removePreference(this.mAddFace);
      this.mFaceUnlock.setEnabled(true);
      disableAutoUnlockSettings(this.mFaceUnlock.isChecked());
      disableFaceUnlockAssistiveLightingSettings(this.mFaceUnlock.isChecked());
    }
    for (;;)
    {
      this.mFaceUnlock.setChecked(isFaceUnlockEnabled());
      this.mAutoFaceUnlock.setChecked(isAutoFaceUnlockEnabled());
      if (this.mFaceUnlockAssistiveLighting != null) {
        this.mFaceUnlockAssistiveLighting.setChecked(isFaceUnlockAssistiveLightingEnabled());
      }
      return;
      this.mFaceUnlockCategory.addPreference(this.mAddFace);
      this.mFaceUnlockCategory.removePreference(this.mRemoveFace);
      this.mFaceUnlock.setEnabled(false);
      this.mAutoFaceUnlock.setEnabled(false);
      if (this.mFaceUnlockAssistiveLighting != null) {
        this.mFaceUnlockAssistiveLighting.setEnabled(false);
      }
    }
  }
  
  private void removeFaceData()
  {
    if (this.mFaceSettingService == null) {
      return;
    }
    try
    {
      this.mFaceSettingService.removeFace(0);
      this.mUIHandler.sendEmptyMessage(100);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.i("OPFaceUnlockSettings", "Start remove face RemoteException:" + localRemoteException);
    }
  }
  
  private void switchAutoFaceUnlock(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "oneplus_auto_face_unlock_enable", i);
      return;
    }
  }
  
  private void switchFaceUnlock(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "oneplus_face_unlock_enable", i);
      disableAutoUnlockSettings(paramBoolean);
      disableFaceUnlockAssistiveLightingSettings(paramBoolean);
      return;
    }
  }
  
  private void switchFaceUnlockAssistiveLighting(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "oneplus_face_unlock_assistive_lighting_enable", i);
      return;
    }
  }
  
  private void unbindFaceUnlockService()
  {
    Log.i("OPFaceUnlockSettings", "Start unbind oneplus face unlockservice");
    getActivity().unbindService(this.mFaceUnlockConnection);
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230791);
    initView();
    bindFaceUnlockService();
  }
  
  public void onDestroy()
  {
    unbindFaceUnlockService();
    super.onDestroy();
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = paramPreference.getKey();
    boolean bool = ((Boolean)paramObject).booleanValue();
    if ("key_face_unlock_enable".equals(paramPreference))
    {
      switchFaceUnlock(bool);
      return true;
    }
    if ("key_auto_face_unlock_enable".equals(paramPreference))
    {
      switchAutoFaceUnlock(bool);
      return true;
    }
    if ("key_face_unlock_assistive_lighting".equals(paramPreference))
    {
      switchFaceUnlockAssistiveLighting(bool);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    paramPreference = paramPreference.getKey();
    if ("key_add_face".equals(paramPreference))
    {
      gotoAddFaceData();
      return true;
    }
    if ("key_remove_face".equals(paramPreference))
    {
      showRemoveFaceDataDialog();
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    refreshUI();
  }
  
  public void showRemoveFaceDataDialog()
  {
    this.mRemoveDialog = new AlertDialog.Builder(getActivity()).setTitle(2131690545).setMessage(2131690546).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPFaceUnlockSettings.-wrap1(OPFaceUnlockSettings.this);
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    }).create();
    this.mRemoveDialog.show();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\faceunlock\OPFaceUnlockSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
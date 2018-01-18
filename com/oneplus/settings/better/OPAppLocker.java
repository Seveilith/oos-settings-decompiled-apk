package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.ui.OPTextViewButtonPreference;
import java.util.ArrayList;
import java.util.List;

public class OPAppLocker
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String KEY_APP_LOCKER_ADD_APPS = "app_locker_add_apps";
  private static final String KEY_APP_LOCKER_OPEN_APPS = "app_locker_open_apps";
  private static final String KEY_APP_LOCKER_SWITCH = "app_locker_switch";
  private static final String TAG = "OPAppLocker";
  private List<OPAppModel> mAppList = new ArrayList();
  private Preference mAppLockerAddAppsPreference;
  private SwitchPreference mAppLockerSwitch;
  private AppOpsManager mAppOpsManager;
  private Context mContext;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(final Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      OPAppLocker.-get4(OPAppLocker.this).removeAll();
      OPAppLocker.-get0(OPAppLocker.this).clear();
      OPAppLocker.-get0(OPAppLocker.this).addAll(OPAppLocker.-get3(OPAppLocker.this).getAppListByType(paramAnonymousMessage.what));
      int j = OPAppLocker.-get0(OPAppLocker.this).size();
      int i = 0;
      while (i < j)
      {
        paramAnonymousMessage = (OPAppModel)OPAppLocker.-get0(OPAppLocker.this).get(i);
        final OPTextViewButtonPreference localOPTextViewButtonPreference = new OPTextViewButtonPreference(OPAppLocker.-get2(OPAppLocker.this));
        localOPTextViewButtonPreference.setIcon(paramAnonymousMessage.getAppIcon());
        localOPTextViewButtonPreference.setTitle(paramAnonymousMessage.getLabel());
        localOPTextViewButtonPreference.setButtonEnable(true);
        localOPTextViewButtonPreference.setButtonString(OPAppLocker.-get2(OPAppLocker.this).getString(2131693620));
        localOPTextViewButtonPreference.setOnButtonClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            localOPTextViewButtonPreference.setButtonEnable(false);
            OPAppLocker.-get4(OPAppLocker.this).removePreference(localOPTextViewButtonPreference);
            OPAppLocker.-get1(OPAppLocker.this).setMode(63, paramAnonymousMessage.getUid(), paramAnonymousMessage.getPkgName(), 1);
          }
        });
        OPAppLocker.-get4(OPAppLocker.this).addPreference(localOPTextViewButtonPreference);
        i += 1;
      }
    }
  };
  private OPApplicationLoader mOPApplicationLoader;
  private PreferenceCategory mOpenAppsList;
  private PackageManager mPackageManager;
  
  private void updateListData()
  {
    if (!this.mOPApplicationLoader.isLoading())
    {
      this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(63);
      this.mOPApplicationLoader.initData(1, this.mHandler);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230784);
    this.mContext = getActivity();
    this.mAppOpsManager = ((AppOpsManager)getSystemService("appops"));
    this.mPackageManager = getPackageManager();
    this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
    this.mOPApplicationLoader.setAppType(63);
    this.mOpenAppsList = ((PreferenceCategory)findPreference("app_locker_open_apps"));
    this.mAppLockerAddAppsPreference = findPreference("app_locker_add_apps");
    if (this.mAppLockerAddAppsPreference != null) {
      this.mAppLockerAddAppsPreference.setOnPreferenceClickListener(this);
    }
    this.mAppLockerSwitch = ((SwitchPreference)findPreference("app_locker_switch"));
    if (this.mAppLockerSwitch != null) {
      this.mAppLockerSwitch.setOnPreferenceChangeListener(this);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = paramPreference.getKey();
    paramObject = (Boolean)paramObject;
    if ("app_locker_switch".equals(paramPreference))
    {
      Log.d("OPAppLocker", "KEY_APP_LOCKER_SWITCH");
      paramPreference = getContentResolver();
      if (!((Boolean)paramObject).booleanValue()) {
        break label53;
      }
    }
    label53:
    for (int i = 1;; i = 0)
    {
      Settings.System.putIntForUser(paramPreference, "app_locker_switch", i, -2);
      return true;
    }
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().equals("app_locker_add_apps"))
    {
      Log.d("OPAppLocker", "KEY_APP_LOCKER_ADD_APPS");
      paramPreference = new Intent("android.intent.action.ONEPLUS_GAME_READ_APP_LIST_ACTION");
      paramPreference.putExtra("op_load_app_tyep", 63);
      this.mContext.startActivity(paramPreference);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    updateListData();
    if (this.mAppLockerSwitch != null)
    {
      int i = Settings.System.getIntForUser(getContentResolver(), "app_locker_switch", 0, -2);
      SwitchPreference localSwitchPreference = this.mAppLockerSwitch;
      if (i != 0) {
        bool = true;
      }
      localSwitchPreference.setChecked(bool);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPAppLocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
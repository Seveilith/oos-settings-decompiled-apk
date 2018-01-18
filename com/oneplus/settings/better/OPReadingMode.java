package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
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

public class OPReadingMode
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String KEY_AUTO_TURN_ON_APPS = "auto_turn_on_apps";
  private static final String KEY_READING_MODE_ADD_APPS = "reading_mode_add_apps";
  private static final String KEY_READING_MODE_TURN_ON = "reading_mode_turn_on";
  public static final String READING_MODE_STATUS = "reading_mode_status";
  public static final String READING_MODE_STATUS_AUTO = "reading_mode_status_auto";
  public static final String READING_MODE_STATUS_MANUAL = "reading_mode_status_manual";
  private static final String TAG = "OPReadingMode";
  private List<OPAppModel> mAppList = new ArrayList();
  private AppOpsManager mAppOpsManager;
  private PreferenceCategory mAutoTurnOnAppList;
  private ContentObserver mContentObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      paramAnonymousBoolean = false;
      int i = Settings.System.getIntForUser(OPReadingMode.-wrap0(OPReadingMode.this), "reading_mode_status", 0, -2);
      if (OPReadingMode.-get5(OPReadingMode.this) != null)
      {
        paramAnonymousUri = OPReadingMode.-get5(OPReadingMode.this);
        if (i != 0) {
          paramAnonymousBoolean = true;
        }
        paramAnonymousUri.setChecked(paramAnonymousBoolean);
      }
    }
  };
  private Context mContext;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(final Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      OPReadingMode.-get2(OPReadingMode.this).removeAll();
      OPReadingMode.-get0(OPReadingMode.this).clear();
      OPReadingMode.-get0(OPReadingMode.this).addAll(OPReadingMode.-get4(OPReadingMode.this).getAppListByType(paramAnonymousMessage.what));
      int j = OPReadingMode.-get0(OPReadingMode.this).size();
      int i = 0;
      while (i < j)
      {
        paramAnonymousMessage = (OPAppModel)OPReadingMode.-get0(OPReadingMode.this).get(i);
        if (paramAnonymousMessage != null)
        {
          final OPTextViewButtonPreference localOPTextViewButtonPreference = new OPTextViewButtonPreference(OPReadingMode.-get3(OPReadingMode.this));
          localOPTextViewButtonPreference.setIcon(paramAnonymousMessage.getAppIcon());
          localOPTextViewButtonPreference.setTitle(paramAnonymousMessage.getLabel());
          localOPTextViewButtonPreference.setButtonEnable(true);
          localOPTextViewButtonPreference.setButtonString(OPReadingMode.-get3(OPReadingMode.this).getString(2131693620));
          localOPTextViewButtonPreference.setOnButtonClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymous2View)
            {
              localOPTextViewButtonPreference.setButtonEnable(false);
              OPReadingMode.-get2(OPReadingMode.this).removePreference(localOPTextViewButtonPreference);
              OPReadingMode.-get1(OPReadingMode.this).setMode(67, paramAnonymousMessage.getUid(), paramAnonymousMessage.getPkgName(), 1);
            }
          });
          OPReadingMode.-get2(OPReadingMode.this).addPreference(localOPTextViewButtonPreference);
        }
        i += 1;
      }
    }
  };
  private OPApplicationLoader mOPApplicationLoader;
  private PackageManager mPackageManager;
  private Preference mReadingModeAddAppsPreference;
  private SwitchPreference mReadingModeTurnOnPreference;
  
  private void updateListData()
  {
    if (!this.mOPApplicationLoader.isLoading())
    {
      this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(67);
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
    addPreferencesFromResource(2131230808);
    this.mContext = getActivity();
    this.mAppOpsManager = ((AppOpsManager)getSystemService("appops"));
    this.mPackageManager = getPackageManager();
    this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
    this.mReadingModeTurnOnPreference = ((SwitchPreference)findPreference("reading_mode_turn_on"));
    if (this.mReadingModeTurnOnPreference != null) {
      this.mReadingModeTurnOnPreference.setOnPreferenceChangeListener(this);
    }
    this.mAutoTurnOnAppList = ((PreferenceCategory)findPreference("auto_turn_on_apps"));
    this.mReadingModeAddAppsPreference = findPreference("reading_mode_add_apps");
    if (this.mReadingModeAddAppsPreference != null) {
      this.mReadingModeAddAppsPreference.setOnPreferenceClickListener(this);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(this.mContentObserver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("reading_mode_turn_on".equals(paramPreference.getKey()))
    {
      if (!((Boolean)paramObject).booleanValue()) {
        break label38;
      }
      Settings.System.putStringForUser(getContentResolver(), "reading_mode_status_manual", "force-on", -2);
    }
    for (;;)
    {
      return true;
      label38:
      Settings.System.putStringForUser(getContentResolver(), "reading_mode_status_manual", "force-off", -2);
    }
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().equals("reading_mode_add_apps"))
    {
      Log.d("OPReadingMode", "KEY_READING_MODE_ADD_APPS");
      paramPreference = new Intent("android.intent.action.ONEPLUS_GAME_READ_APP_LIST_ACTION");
      paramPreference.putExtra("op_load_app_tyep", 67);
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
    int i = Settings.System.getIntForUser(getContentResolver(), "reading_mode_status", 0, -2);
    if (this.mReadingModeTurnOnPreference != null)
    {
      SwitchPreference localSwitchPreference = this.mReadingModeTurnOnPreference;
      if (i != 0) {
        bool = true;
      }
      localSwitchPreference.setChecked(bool);
    }
    getContentResolver().registerContentObserver(Settings.System.getUriFor("reading_mode_status"), true, this.mContentObserver, -2);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPReadingMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.oneplus.settings.others;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPOthersSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, Indexable
{
  private static final String ACTION_OTG_AUTO_SHUTDOWN = "oneplus.intent.action.otg_auto_shutdown";
  private static final String ANTI_MISOPERATION_SCREEN_TOUCH = "anti_misoperation_of_the_screen_touch_enable";
  private static final String KEY_OP_BLUETOOTH_AUDIO_CODEC = "op_bluetooth_audio_codec";
  private static final String KEY_OP_MULTITASKING_CLEAN_WAY = "op_multitasking_clean_way";
  private static final String KEY_OTG_READ_ENABLE = "otg_read_enable";
  public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      if (OPUtils.isGuestMode()) {
        paramAnonymousContext.add("anti_misoperation_of_the_screen_touch_enable");
      }
      if (!SettingsBaseApplication.mApplication.getPackageManager().hasSystemFeature("oem.otg.switch.support")) {
        paramAnonymousContext.add("otg_read_enable");
      }
      if (OPOthersSettings.-get0()) {
        paramAnonymousContext.add("op_bluetooth_audio_codec");
      }
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230804;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final String TAG = "OPOthersSettings";
  private static final String TIMER_SHUTDOWN_STARTUP_KEY = "timer_shutdown_startup_settings";
  private static final String USER_ENJOY_PLAY_KEY = "user_enjoy_plan";
  private static boolean isSupportAptxHdSupport;
  private SwitchPreference mAntiMisOperationTouch;
  private ListPreference mBluetoothAudioCodec;
  private Context mContext;
  private Preference mOPMultitaskingCleanWayPreference;
  private ContentObserver mOTGContentObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      super.onChange(paramAnonymousBoolean, paramAnonymousUri);
      if (Settings.Global.getInt(OPOthersSettings.-wrap0(OPOthersSettings.this), "oneplus_otg_auto_disable", 0) != 0) {}
      for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
      {
        if (OPOthersSettings.-get1(OPOthersSettings.this) != null) {
          OPOthersSettings.-get1(OPOthersSettings.this).setChecked(paramAnonymousBoolean);
        }
        return;
      }
    }
  };
  private SwitchPreference mOTGReadEnableSwitchPreference;
  private Preference mTimerShutdownPreference;
  private SwitchPreference mUserPlanPreference;
  
  private void updateView()
  {
    this.mAntiMisOperationTouch = ((SwitchPreference)findPreference("anti_misoperation_of_the_screen_touch_enable"));
    this.mAntiMisOperationTouch.setOnPreferenceChangeListener(this);
    SwitchPreference localSwitchPreference = this.mAntiMisOperationTouch;
    boolean bool;
    if (Settings.System.getInt(getContentResolver(), "oem_acc_anti_misoperation_screen", 0) == 0)
    {
      bool = false;
      localSwitchPreference.setChecked(bool);
      this.mUserPlanPreference = ((SwitchPreference)findPreference("user_enjoy_plan"));
      this.mUserPlanPreference.setOnPreferenceClickListener(this);
      this.mTimerShutdownPreference = findPreference("timer_shutdown_startup_settings");
      if (!checkIfNeedPasswordToPowerOn()) {
        break label295;
      }
      this.mTimerShutdownPreference.setEnabled(false);
      this.mTimerShutdownPreference.setSummary(2131690251);
      label101:
      this.mOPMultitaskingCleanWayPreference = findPreference("op_multitasking_clean_way");
      if (OPUtils.isGuestMode())
      {
        getPreferenceScreen().removePreference(this.mTimerShutdownPreference);
        getPreferenceScreen().removePreference(this.mAntiMisOperationTouch);
        getPreferenceScreen().removePreference(this.mOPMultitaskingCleanWayPreference);
      }
      getPreferenceScreen().removePreference(this.mUserPlanPreference);
      this.mOTGReadEnableSwitchPreference = ((SwitchPreference)findPreference("otg_read_enable"));
      this.mOTGReadEnableSwitchPreference.setOnPreferenceChangeListener(this);
      if (!"true".equals(SystemProperties.get("persist.sys.oem.otg_support", "false"))) {
        break label306;
      }
      this.mOTGReadEnableSwitchPreference.setChecked(true);
    }
    for (;;)
    {
      if (!this.mContext.getPackageManager().hasSystemFeature("oem.otg.switch.support")) {
        getPreferenceScreen().removePreference(this.mOTGReadEnableSwitchPreference);
      }
      this.mBluetoothAudioCodec = ((ListPreference)findPreference("op_bluetooth_audio_codec"));
      this.mBluetoothAudioCodec.setOnPreferenceChangeListener(this);
      isSupportAptxHdSupport = SettingsBaseApplication.mApplication.getPackageManager().hasSystemFeature("oem.aptx.hd.support");
      if (isSupportAptxHdSupport) {
        getPreferenceScreen().removePreference(this.mBluetoothAudioCodec);
      }
      return;
      bool = true;
      break;
      label295:
      this.mTimerShutdownPreference.setEnabled(true);
      break label101;
      label306:
      this.mOTGReadEnableSwitchPreference.setChecked(false);
    }
  }
  
  public boolean checkIfNeedPasswordToPowerOn()
  {
    return Settings.Global.getInt(getActivity().getContentResolver(), "require_password_to_decrypt", 0) == 1;
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230804);
    this.mContext = getActivity();
    updateView();
    getContentResolver().registerContentObserver(Settings.Global.getUriFor("oneplus_otg_auto_disable"), true, this.mOTGContentObserver, -1);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    getContentResolver().unregisterContentObserver(this.mOTGContentObserver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    int i = 0;
    boolean bool;
    if (paramPreference.getKey().equals("anti_misoperation_of_the_screen_touch_enable"))
    {
      bool = ((Boolean)paramObject).booleanValue();
      paramPreference = getContentResolver();
      if (bool) {
        i = 1;
      }
      Settings.System.putInt(paramPreference, "oem_acc_anti_misoperation_screen", i);
      return true;
    }
    if (paramPreference.getKey().equals("otg_read_enable"))
    {
      bool = ((Boolean)paramObject).booleanValue();
      if (bool) {}
      for (paramPreference = "true";; paramPreference = "false")
      {
        SystemProperties.set("persist.sys.oem.otg_support", paramPreference);
        paramPreference = getContentResolver();
        i = j;
        if (bool) {
          i = 1;
        }
        Settings.Global.putInt(paramPreference, "oneplus_otg_auto_disable", i);
        return true;
      }
    }
    if (paramPreference.getKey().equals("op_bluetooth_audio_codec"))
    {
      i = Integer.parseInt((String)paramObject);
      Settings.Secure.putInt(getContentResolver(), "bluetooth_aptx_hd", i);
      paramPreference = this.mBluetoothAudioCodec.getEntries();
      this.mBluetoothAudioCodec.setSummary(paramPreference[i]);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    int i = 0;
    if (paramPreference.getKey().equals("user_enjoy_plan"))
    {
      paramPreference = getContentResolver();
      if (!this.mUserPlanPreference.isChecked()) {}
      for (;;)
      {
        Settings.System.putInt(paramPreference, "oem_join_user_plan_settings", i);
        if (!this.mUserPlanPreference.isChecked()) {
          break;
        }
        paramPreference = new Intent();
        paramPreference.setAction("INTENT_START_ODM");
        getActivity().sendOrderedBroadcast(paramPreference, null);
        return true;
        i = 1;
      }
      paramPreference = new Intent();
      paramPreference.setAction("INTENT_STOP_ODM");
      getActivity().sendOrderedBroadcast(paramPreference, null);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    boolean bool = false;
    super.onResume();
    Object localObject;
    if (this.mUserPlanPreference != null)
    {
      localObject = this.mUserPlanPreference;
      if (Settings.System.getInt(getActivity().getContentResolver(), "oem_join_user_plan_settings", 0) != 0) {
        break label89;
      }
    }
    for (;;)
    {
      ((SwitchPreference)localObject).setChecked(bool);
      if (this.mBluetoothAudioCodec != null)
      {
        int i = Settings.Secure.getInt(getContentResolver(), "bluetooth_aptx_hd", 1);
        this.mBluetoothAudioCodec.setValue(String.valueOf(i));
        localObject = this.mBluetoothAudioCodec.getEntries();
        this.mBluetoothAudioCodec.setSummary(localObject[i]);
      }
      return;
      label89:
      bool = true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\others\OPOthersSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
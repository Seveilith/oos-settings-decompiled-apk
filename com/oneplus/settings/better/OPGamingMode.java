package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.ContentResolver;
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
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.ui.OPTextViewButtonPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGamingMode
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  public static final String GAME_MODE_ANSWER_NO_INCALLUI = "game_mode_answer_no_incallui";
  public static final String GAME_MODE_BLOCK_NOTIFICATION = "game_mode_block_notification";
  public static final String GAME_MODE_LOCK_BUTTONS = "game_mode_lock_buttons";
  public static final String GAME_MODE_STATUS = "game_mode_status";
  public static final String GAME_MODE_STATUS_AUTO = "game_mode_status_auto";
  public static final String GAME_MODE_STATUS_MANUAL = "game_mode_status_manual";
  private static final String KEY_AUTO_TURN_ON_APPS = "auto_turn_on_apps";
  private static final String KEY_BLOCK_NOTIFICATIONS = "block_notifications";
  private static final String KEY_DO_NOT_DISTURB_ANSWER_CALL_BY_SPEAKER = "do_not_disturb_answer_call_by_speaker";
  private static final String KEY_DO_NOT_DISTURB_SETTINGS = "do_not_disturb_settings";
  private static final String KEY_GAMING_MODE_ADD_APPS = "gaming_mode_add_apps";
  private static final String KEY_LOCK_BUTTONS = "lock_buttons";
  private static final String TAG = "OPGamingMode";
  private static Toast mToast;
  private SwitchPreference mAnswerCallBySpeakerPreference;
  private List<OPAppModel> mAppList = new ArrayList();
  private AppOpsManager mAppOpsManager;
  private PreferenceCategory mAutoTurnOnAppList;
  private SwitchPreference mBlockNotificationsPreference;
  private Context mContext;
  private PreferenceCategory mDoNotDisturbSettings;
  private Preference mGamingModeAddAppsPreference;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(final Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      OPGamingMode.-get2(OPGamingMode.this).removeAll();
      OPGamingMode.-get0(OPGamingMode.this).clear();
      OPGamingMode.-get0(OPGamingMode.this).addAll(OPGamingMode.-get4(OPGamingMode.this).getAppListByType(paramAnonymousMessage.what));
      int j = OPGamingMode.-get0(OPGamingMode.this).size();
      int i = 0;
      while (i < j)
      {
        paramAnonymousMessage = (OPAppModel)OPGamingMode.-get0(OPGamingMode.this).get(i);
        final OPTextViewButtonPreference localOPTextViewButtonPreference = new OPTextViewButtonPreference(OPGamingMode.-get3(OPGamingMode.this));
        localOPTextViewButtonPreference.setIcon(paramAnonymousMessage.getAppIcon());
        localOPTextViewButtonPreference.setTitle(paramAnonymousMessage.getLabel());
        localOPTextViewButtonPreference.setButtonEnable(true);
        localOPTextViewButtonPreference.setButtonString(OPGamingMode.-get3(OPGamingMode.this).getString(2131693620));
        localOPTextViewButtonPreference.setOnButtonClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            localOPTextViewButtonPreference.setButtonEnable(false);
            OPGamingMode.-get2(OPGamingMode.this).removePreference(localOPTextViewButtonPreference);
            OPGamingMode.-get1(OPGamingMode.this).setMode(68, paramAnonymousMessage.getUid(), paramAnonymousMessage.getPkgName(), 1);
          }
        });
        OPGamingMode.-get2(OPGamingMode.this).addPreference(localOPTextViewButtonPreference);
        i += 1;
      }
    }
  };
  private SwitchPreference mLockButtonsPreference;
  private OPApplicationLoader mOPApplicationLoader;
  private PackageManager mPackageManager;
  
  private void showToast()
  {
    if (mToast != null) {
      mToast.cancel();
    }
    for (mToast = Toast.makeText(getPrefContext(), 2131690400, 1);; mToast = Toast.makeText(getPrefContext(), 2131690400, 1))
    {
      mToast.show();
      return;
    }
  }
  
  private void updateListData()
  {
    if (!this.mOPApplicationLoader.isLoading())
    {
      this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(68);
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
    addPreferencesFromResource(2131230794);
    this.mContext = getActivity();
    this.mAppOpsManager = ((AppOpsManager)getSystemService("appops"));
    this.mPackageManager = getPackageManager();
    this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
    this.mAnswerCallBySpeakerPreference = ((SwitchPreference)findPreference("do_not_disturb_answer_call_by_speaker"));
    this.mBlockNotificationsPreference = ((SwitchPreference)findPreference("block_notifications"));
    this.mLockButtonsPreference = ((SwitchPreference)findPreference("lock_buttons"));
    if (this.mAnswerCallBySpeakerPreference != null) {
      this.mAnswerCallBySpeakerPreference.setOnPreferenceChangeListener(this);
    }
    if (this.mBlockNotificationsPreference != null) {
      this.mBlockNotificationsPreference.setOnPreferenceChangeListener(this);
    }
    if (this.mLockButtonsPreference != null) {
      this.mLockButtonsPreference.setOnPreferenceChangeListener(this);
    }
    this.mAutoTurnOnAppList = ((PreferenceCategory)findPreference("auto_turn_on_apps"));
    this.mGamingModeAddAppsPreference = findPreference("gaming_mode_add_apps");
    if (this.mGamingModeAddAppsPreference != null) {
      this.mGamingModeAddAppsPreference.setOnPreferenceClickListener(this);
    }
    this.mDoNotDisturbSettings = ((PreferenceCategory)findPreference("do_not_disturb_settings"));
    if ((OPUtils.isSurportBackFingerprint(this.mContext)) && (this.mLockButtonsPreference != null)) {
      this.mDoNotDisturbSettings.removePreference(this.mLockButtonsPreference);
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    int k = 0;
    int i = 0;
    String str = paramPreference.getKey();
    paramPreference = (Boolean)paramObject;
    if ("block_notifications".equals(str))
    {
      Log.d("OPGamingMode", "KEY_BLOCK_NOTIFICATIONS");
      paramObject = getContentResolver();
      if (paramPreference.booleanValue()) {
        i = 1;
      }
      Settings.System.putIntForUser((ContentResolver)paramObject, "game_mode_block_notification", i, -2);
    }
    do
    {
      return true;
      if ("lock_buttons".equals(str))
      {
        Log.d("OPGamingMode", "KEY_LOCK_BUTTONS");
        paramObject = getContentResolver();
        i = j;
        if (paramPreference.booleanValue()) {
          i = 1;
        }
        Settings.System.putIntForUser((ContentResolver)paramObject, "game_mode_lock_buttons", i, -2);
        return true;
      }
    } while (!"do_not_disturb_answer_call_by_speaker".equals(str));
    Log.d("OPGamingMode", "KEY_LOCK_BUTTONS");
    paramObject = getContentResolver();
    i = k;
    if (paramPreference.booleanValue()) {
      i = 1;
    }
    Settings.System.putIntForUser((ContentResolver)paramObject, "game_mode_answer_no_incallui", i, -2);
    return true;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().equals("gaming_mode_add_apps"))
    {
      Log.d("OPGamingMode", "KEY_GAMING_MODE_ADD_APPS");
      paramPreference = new Intent("android.intent.action.ONEPLUS_GAME_READ_APP_LIST_ACTION");
      paramPreference.putExtra("op_load_app_tyep", 68);
      this.mContext.startActivity(paramPreference);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    boolean bool2 = true;
    super.onResume();
    updateListData();
    int i = Settings.System.getIntForUser(getContentResolver(), "game_mode_answer_no_incallui", 0, -2);
    SwitchPreference localSwitchPreference;
    if (this.mAnswerCallBySpeakerPreference != null)
    {
      localSwitchPreference = this.mAnswerCallBySpeakerPreference;
      if (i != 0)
      {
        bool1 = true;
        localSwitchPreference.setChecked(bool1);
      }
    }
    else
    {
      i = Settings.System.getIntForUser(getContentResolver(), "game_mode_block_notification", 0, -2);
      if (this.mBlockNotificationsPreference != null)
      {
        localSwitchPreference = this.mBlockNotificationsPreference;
        if (i == 0) {
          break label130;
        }
        bool1 = true;
        label80:
        localSwitchPreference.setChecked(bool1);
      }
      i = Settings.System.getIntForUser(getContentResolver(), "game_mode_lock_buttons", 0, -2);
      if (this.mLockButtonsPreference != null)
      {
        localSwitchPreference = this.mLockButtonsPreference;
        if (i == 0) {
          break label135;
        }
      }
    }
    label130:
    label135:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      localSwitchPreference.setChecked(bool1);
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label80;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPGamingMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
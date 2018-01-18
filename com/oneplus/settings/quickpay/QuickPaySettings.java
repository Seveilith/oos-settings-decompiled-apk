package com.oneplus.settings.quickpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.location.RadioButtonPreference;
import com.google.android.collect.Lists;
import com.oneplus.settings.OPButtonsSettings;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.utils.OPUtils;
import java.util.Iterator;
import java.util.List;

public class QuickPaySettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, QuickPayAnimPreference.OnPreferenceViewClickListener
{
  public static final int CODE_REQUEST_FINGERPRINT = 1;
  private static final int MY_USER_ID = ;
  public static final int OP_HOME_LONG_ACTION_QUICKPAY = 11;
  public static final String OP_QUICKPAY_DEFAULT_WAY = "op_quickpay_default_way";
  public static final String OP_QUICKPAY_ENABLE = "op_quickpay_enable";
  public static final String OP_QUICKPAY_SHOW = "op_quickpay_show";
  public static final String[] sPayWaysKey = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427492);
  public static final int[] sPayWaysValue = SettingsBaseApplication.mApplication.getResources().getIntArray(2131427493);
  private final String KEY_FINGERPRINT_LONGPRESS_ACTION_FOR_QUICKPAY = "op_fingerprint_longpress_action_for_quickpay";
  private final String KEY_PREFERENCE_DIVIDER_LINE2 = "preference_divider_line2";
  private final String KEY_QUICKPAY_INSTRUCTIONS = "key_quickpay_instructions";
  private final String KEY_QUICKPAY_SELECT_DEFAULT_WAY_CATEGORY = "key_quickpay_select_default_way_category";
  private final String KEY_QUICKPAY_UNINSTALL_APP_CATEGORY = "key_quickpay_uninstall_app_category";
  private final String KEY_SWITCH_LOCKSCREEN = "key_switch_lockscreen";
  private final String KEY_SWITCH_UNLOCKSCREEN = "key_switch_unlockscreen";
  private final String OP_FINGERPRINT_LONG_PRESS_ACTION = "op_fingerprint_long_press_action";
  private SettingsActivity mActivity;
  private List<RadioButtonPreference> mAllPayWaysPreference = Lists.newArrayList();
  private int mDefaultLongPressOnHomeBehavior;
  private SwitchPreference mFingerprintLongpressQuickpay;
  private FingerprintManager mFingerprintManager;
  private boolean mHasFingerprint;
  private String[] mHomeKeyActionName;
  private String[] mHomeKeyActionValue;
  private IntentFilter mIntentFilter;
  private List<String> mPayWaysKeyList = Lists.newArrayList();
  private String[] mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427491);
  private List<String> mPayWaysNameList = Lists.newArrayList();
  private List<Integer> mPayWaysValueList = Lists.newArrayList();
  private AppInstallAndUninstallReceiver mQuickPayAppsAddOrRemovedReceiver;
  private OPPreferenceDivider preference_divider_line2;
  private QuickPayAnimPreference quickpay_instructions;
  private PreferenceCategory quickpay_select_default_way_category;
  private PreferenceCategory quickpay_uninstall_app_category;
  private SwitchPreference switch_lockscreen;
  private SwitchPreference switch_unlockscreen;
  
  public static boolean canShowQuickPay(Context paramContext)
  {
    if (Settings.Secure.getIntForUser(paramContext.getContentResolver(), "op_quickpay_show", 0, 0) == 1) {
      return true;
    }
    boolean bool1 = OPUtils.isAppExist(paramContext, "com.tencent.mm");
    boolean bool2 = OPUtils.isAppExist(paramContext, "com.eg.android.AlipayGphone");
    boolean bool3 = OPUtils.isAppExist(paramContext, "net.one97.paytm");
    if ((bool2) || (bool1) || (bool3)) {
      return Settings.Secure.putInt(paramContext.getContentResolver(), "op_quickpay_show", 1);
    }
    return false;
  }
  
  private void checkFingerPrint()
  {
    if (this.mFingerprintManager.getEnrolledFingerprints(MY_USER_ID).size() > 0)
    {
      this.mHasFingerprint = true;
      return;
    }
    this.mHasFingerprint = false;
  }
  
  private int getLongPressHomeActionIndexByValue(int paramInt)
  {
    int i = 0;
    while (i < this.mHomeKeyActionValue.length)
    {
      if (paramInt == Integer.parseInt(this.mHomeKeyActionValue[i])) {
        return i;
      }
      i += 1;
    }
    return 0;
  }
  
  public static void gotoQuickPaySettingsPage(Context paramContext)
  {
    Object localObject = null;
    try
    {
      localIntent = new Intent("com.oneplus.action.QUICKPAY_SETTINGS");
      Log.d("QuickPaySettings", "No activity found for " + paramContext);
    }
    catch (ActivityNotFoundException paramContext)
    {
      try
      {
        paramContext.startActivity(localIntent);
        return;
      }
      catch (ActivityNotFoundException paramContext)
      {
        for (;;)
        {
          Intent localIntent;
          paramContext = localIntent;
        }
      }
      paramContext = paramContext;
      paramContext = (Context)localObject;
    }
  }
  
  private void initHomeActionName()
  {
    if (!OPButtonsSettings.checkGMS(this.mActivity))
    {
      this.mHomeKeyActionName = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427489);
      this.mHomeKeyActionValue = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427490);
      return;
    }
    this.mHomeKeyActionName = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427487);
    this.mHomeKeyActionValue = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427488);
  }
  
  private void initPayWayData()
  {
    this.mPayWaysNameList.clear();
    this.mPayWaysKeyList.clear();
    this.mPayWaysValueList.clear();
    if (OPUtils.isAppExist(this.mActivity, "com.tencent.mm"))
    {
      this.mPayWaysNameList.add(this.mPayWaysName[0]);
      this.mPayWaysKeyList.add(sPayWaysKey[0]);
      this.mPayWaysValueList.add(Integer.valueOf(sPayWaysValue[0]));
      this.mPayWaysNameList.add(this.mPayWaysName[1]);
      this.mPayWaysKeyList.add(sPayWaysKey[1]);
      this.mPayWaysValueList.add(Integer.valueOf(sPayWaysValue[1]));
    }
    if (OPUtils.isAppExist(this.mActivity, "com.eg.android.AlipayGphone"))
    {
      this.mPayWaysNameList.add(this.mPayWaysName[2]);
      this.mPayWaysKeyList.add(sPayWaysKey[2]);
      this.mPayWaysValueList.add(Integer.valueOf(sPayWaysValue[2]));
      this.mPayWaysNameList.add(this.mPayWaysName[3]);
      this.mPayWaysKeyList.add(sPayWaysKey[3]);
      this.mPayWaysValueList.add(Integer.valueOf(sPayWaysValue[3]));
    }
    if (OPUtils.isAppExist(this.mActivity, "net.one97.paytm"))
    {
      this.mPayWaysNameList.add(this.mPayWaysName[4]);
      this.mPayWaysKeyList.add(sPayWaysKey[4]);
      this.mPayWaysValueList.add(Integer.valueOf(sPayWaysValue[4]));
    }
  }
  
  private void initPreference()
  {
    addPreferencesFromResource(2131230807);
    this.switch_lockscreen = ((SwitchPreference)findPreference("key_switch_lockscreen"));
    this.switch_lockscreen.setOnPreferenceChangeListener(this);
    this.switch_unlockscreen = ((SwitchPreference)findPreference("key_switch_unlockscreen"));
    this.switch_unlockscreen.setOnPreferenceChangeListener(this);
    if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication))
    {
      this.switch_lockscreen.setSummary(2131690514);
      removePreference("key_switch_unlockscreen");
    }
    this.quickpay_uninstall_app_category = ((PreferenceCategory)findPreference("key_quickpay_uninstall_app_category"));
    this.quickpay_select_default_way_category = ((PreferenceCategory)findPreference("key_quickpay_select_default_way_category"));
    this.quickpay_instructions = ((QuickPayAnimPreference)findPreference("key_quickpay_instructions"));
    this.quickpay_instructions.setViewOnClick(this);
    this.preference_divider_line2 = ((OPPreferenceDivider)findPreference("preference_divider_line2"));
  }
  
  private void refreshQuickPayEnableUI(boolean paramBoolean)
  {
    removePreference("preference_divider_line2");
    if (!paramBoolean)
    {
      removePreference("key_quickpay_select_default_way_category");
      removePreference("key_quickpay_uninstall_app_category");
      return;
    }
    if (this.mPayWaysNameList.size() > 0)
    {
      getPreferenceScreen().addPreference(this.quickpay_select_default_way_category);
      getPreferenceScreen().addPreference(this.preference_divider_line2);
      return;
    }
    getPreferenceScreen().addPreference(this.quickpay_uninstall_app_category);
  }
  
  private void showConfirmChangeHomeAction(final boolean paramBoolean, int paramInt)
  {
    if (paramInt >= this.mHomeKeyActionName.length)
    {
      Log.e("QuickPaySettings", "longPressHomeActionIndex is out of max length.longPressHomeActionIndex=" + paramInt);
      return;
    }
    String str = this.mHomeKeyActionName[paramInt];
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.mActivity);
    localBuilder.setMessage(this.mActivity.getString(2131690420, new Object[] { str }));
    localBuilder.setPositiveButton(this.mActivity.getString(2131690706), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        QuickPaySettings.-wrap1(QuickPaySettings.this, paramBoolean);
        QuickPaySettings.-get0(QuickPaySettings.this).setChecked(paramBoolean);
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localBuilder.setNegativeButton(this.mActivity.getString(2131690707), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localBuilder.setCancelable(false);
    localBuilder.create().show();
  }
  
  private void updateLockHomeAction(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = 1;
      Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", i);
      if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
        break label54;
      }
      if (paramBoolean) {
        break label49;
      }
      paramBoolean = this.switch_unlockscreen.isChecked();
    }
    label49:
    label54:
    for (;;)
    {
      refreshQuickPayEnableUI(paramBoolean);
      return;
      i = 0;
      break;
      paramBoolean = true;
    }
  }
  
  private void updatePreferenceState()
  {
    initPayWayData();
    this.mDefaultLongPressOnHomeBehavior = getActivity().getResources().getInteger(17694818);
    int k = Settings.Secure.getIntForUser(getContentResolver(), "op_quickpay_default_way", -1, 0);
    int m = Settings.System.getIntForUser(getContentResolver(), "key_home_long_press_action", this.mDefaultLongPressOnHomeBehavior, 0);
    Settings.System.getIntForUser(getContentResolver(), "op_fingerprint_long_press_action", this.mDefaultLongPressOnHomeBehavior, 0);
    this.quickpay_select_default_way_category.removeAll();
    this.mAllPayWaysPreference.clear();
    int j;
    int i;
    if (this.mPayWaysNameList.size() > 0)
    {
      removePreference("key_quickpay_uninstall_app_category");
      getPreferenceScreen().addPreference(this.quickpay_select_default_way_category);
      j = 0;
      i = 0;
      while (i < this.mPayWaysNameList.size())
      {
        RadioButtonPreference localRadioButtonPreference = new RadioButtonPreference(this.mActivity);
        localRadioButtonPreference.setKey((String)this.mPayWaysKeyList.get(i));
        localRadioButtonPreference.setTitle((CharSequence)this.mPayWaysNameList.get(i));
        if (k == ((Integer)this.mPayWaysValueList.get(i)).intValue())
        {
          localRadioButtonPreference.setChecked(true);
          j = 1;
        }
        localRadioButtonPreference.setOnPreferenceClickListener(this);
        this.mAllPayWaysPreference.add(localRadioButtonPreference);
        this.quickpay_select_default_way_category.addPreference(localRadioButtonPreference);
        i += 1;
      }
      if ((this.mAllPayWaysPreference.size() <= 0) || (j != 0))
      {
        j = Settings.Secure.getInt(getContentResolver(), "op_quickpay_enable", 0);
        if (j != 1) {
          break label474;
        }
        if (this.mHasFingerprint) {
          break label463;
        }
        Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", 0);
        this.switch_lockscreen.setChecked(false);
        j = 0;
        label300:
        if (Settings.System.getInt(getContentResolver(), "buttons_show_on_screen_navkeys", 0) == 0) {
          break label485;
        }
        k = 1;
        label316:
        if (Settings.System.getInt(getContentResolver(), "buttons_force_home_enabled", 0) == 0) {
          break label490;
        }
        i = 1;
        label332:
        if (k == 0) {
          break label495;
        }
        if (k == 0) {
          break label500;
        }
        label340:
        boolean bool2 = false;
        bool1 = bool2;
        if (this.switch_lockscreen != null)
        {
          if (!OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
            break label505;
          }
          bool1 = bool2;
        }
        label367:
        if (OPUtils.isSurportBackFingerprint(SettingsBaseApplication.mApplication)) {
          if (j != 1) {
            break label574;
          }
        }
      }
    }
    label463:
    label474:
    label485:
    label490:
    label495:
    label500:
    label505:
    label568:
    label574:
    for (boolean bool1 = true;; bool1 = false)
    {
      refreshQuickPayEnableUI(bool1);
      return;
      ((RadioButtonPreference)this.mAllPayWaysPreference.get(0)).setChecked(true);
      if (k != -1) {
        break;
      }
      Settings.Secure.putInt(getContentResolver(), "op_quickpay_default_way", ((Integer)this.mPayWaysValueList.get(0)).intValue());
      break;
      removePreference("key_quickpay_select_default_way_category");
      getPreferenceScreen().addPreference(this.quickpay_uninstall_app_category);
      break;
      this.switch_lockscreen.setChecked(true);
      break label300;
      this.switch_lockscreen.setChecked(false);
      break label300;
      k = 0;
      break label316;
      i = 0;
      break label332;
      i = 1;
      break label340;
      i = 0;
      break label340;
      if ((m == 11) && (i != 0)) {
        this.switch_unlockscreen.setChecked(true);
      }
      for (;;)
      {
        if ((!this.switch_unlockscreen.isChecked()) && (j != 1)) {
          break label568;
        }
        bool1 = true;
        break;
        this.switch_unlockscreen.setChecked(false);
        if (i == 0) {
          this.switch_unlockscreen.setEnabled(false);
        }
      }
      bool1 = false;
      break label367;
    }
  }
  
  private void updateUnLockFingerprintLongpressAction(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = 11;
      Settings.System.putInt(getContentResolver(), "op_fingerprint_long_press_action", i);
      if (paramBoolean) {
        break label41;
      }
    }
    label41:
    for (paramBoolean = this.switch_lockscreen.isChecked();; paramBoolean = true)
    {
      refreshQuickPayEnableUI(paramBoolean);
      return;
      i = 0;
      break;
    }
  }
  
  private void updateUnLockHomeAction(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = 11;
      Settings.System.putInt(getContentResolver(), "key_home_long_press_action", i);
      if (paramBoolean) {
        break label42;
      }
    }
    label42:
    for (paramBoolean = this.switch_lockscreen.isChecked();; paramBoolean = true)
    {
      refreshQuickPayEnableUI(paramBoolean);
      return;
      i = 0;
      break;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void gotoFingerprintEnrollIntroduction(int paramInt)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollIntroduction.class.getName());
    startActivityForResult(localIntent, paramInt);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mActivity = ((SettingsActivity)getActivity());
    initHomeActionName();
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    }
    for (;;)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      return;
      checkFingerPrint();
      if (this.mHasFingerprint) {
        refreshQuickPayEnableUI(Settings.Secure.putInt(getContentResolver(), "op_quickpay_enable", 1));
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    this.mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(2131427491);
    initHomeActionName();
    super.onConfigurationChanged(paramConfiguration);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mFingerprintManager = ((FingerprintManager)getActivity().getSystemService("fingerprint"));
    this.mIntentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
    this.mIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    this.mIntentFilter.addDataScheme("package");
    this.mQuickPayAppsAddOrRemovedReceiver = new AppInstallAndUninstallReceiver();
    initPreference();
  }
  
  public void onPause()
  {
    this.quickpay_instructions.stopAnim();
    SettingsBaseApplication.mApplication.unregisterReceiver(this.mQuickPayAppsAddOrRemovedReceiver);
    super.onPause();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool;
    if (paramPreference == this.switch_lockscreen)
    {
      bool = ((Boolean)paramObject).booleanValue();
      if ((!bool) || (this.mHasFingerprint))
      {
        updateLockHomeAction(bool);
        return true;
      }
      gotoFingerprintEnrollIntroduction(1);
      return false;
    }
    if (paramPreference == this.switch_unlockscreen)
    {
      bool = ((Boolean)paramObject).booleanValue();
      int i = getLongPressHomeActionIndexByValue(Settings.System.getIntForUser(getContentResolver(), "key_home_long_press_action", this.mDefaultLongPressOnHomeBehavior, 0));
      if ((bool) && (i != 0))
      {
        showConfirmChangeHomeAction(bool, i);
        return false;
      }
      updateUnLockHomeAction(bool);
      return true;
    }
    if (paramPreference == this.mFingerprintLongpressQuickpay)
    {
      updateUnLockFingerprintLongpressAction(((Boolean)paramObject).booleanValue());
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    Object localObject = paramPreference.getKey();
    int i = 0;
    while (i < this.mPayWaysKeyList.size())
    {
      if (((String)localObject).equals(this.mPayWaysKeyList.get(i)))
      {
        Settings.Secure.putInt(getContentResolver(), "op_quickpay_default_way", ((Integer)this.mPayWaysValueList.get(i)).intValue());
        localObject = this.mAllPayWaysPreference.iterator();
        while (((Iterator)localObject).hasNext()) {
          ((RadioButtonPreference)((Iterator)localObject).next()).setChecked(false);
        }
        ((RadioButtonPreference)paramPreference).setChecked(true);
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public void onPreferenceViewClick(View paramView)
  {
    this.quickpay_instructions.playOrStopAnim();
  }
  
  public void onResume()
  {
    super.onResume();
    checkFingerPrint();
    updatePreferenceState();
    SettingsBaseApplication.mApplication.registerReceiver(this.mQuickPayAppsAddOrRemovedReceiver, this.mIntentFilter);
  }
  
  class AppInstallAndUninstallReceiver
    extends BroadcastReceiver
  {
    AppInstallAndUninstallReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if (("android.intent.action.PACKAGE_REMOVED".equals(paramContext)) || ("android.intent.action.PACKAGE_ADDED".equals(paramContext)))
      {
        paramContext = paramIntent.getData().getSchemeSpecificPart();
        if (TextUtils.isEmpty(paramContext)) {
          return;
        }
        if (("com.tencent.mm".equals(paramContext)) || ("com.eg.android.AlipayGphone".equals(paramContext)) || ("net.one97.paytm".equals(paramContext))) {
          QuickPaySettings.-wrap0(QuickPaySettings.this);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\quickpay\QuickPaySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
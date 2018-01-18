package com.android.settings.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.SettingsActivity;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;

public abstract class ZenModeRuleSettingsBase
  extends ZenModeSettingsBase
  implements SwitchBar.OnSwitchChangeListener
{
  protected static final boolean DEBUG = ZenModeSettingsBase.DEBUG;
  private static final String KEY_RULE_NAME = "rule_name";
  private static final String KEY_ZEN_MODE = "zen_mode";
  protected static final String TAG = "ZenModeSettings";
  protected Context mContext;
  private boolean mDeleting;
  protected boolean mDisableListeners;
  private Toast mEnabledToast;
  protected String mId;
  protected AutomaticZenRule mRule;
  private Preference mRuleName;
  private SwitchBar mSwitchBar;
  private DropDownPreference mZenMode;
  
  private AutomaticZenRule getZenRule()
  {
    return NotificationManager.from(this.mContext).getAutomaticZenRule(this.mId);
  }
  
  private boolean refreshRuleOrFinish()
  {
    this.mRule = getZenRule();
    if (DEBUG) {
      Log.d("ZenModeSettings", "mRule=" + this.mRule);
    }
    if (!setRule(this.mRule))
    {
      toastAndFinish();
      return true;
    }
    return false;
  }
  
  private void showDeleteRuleDialog()
  {
    View localView = new AlertDialog.Builder(this.mContext).setMessage(getString(2131693269, new Object[] { this.mRule.getName() })).setNegativeButton(2131690993, null).setPositiveButton(2131693270, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        MetricsLogger.action(ZenModeRuleSettingsBase.this.mContext, 175);
        ZenModeRuleSettingsBase.-set0(ZenModeRuleSettingsBase.this, true);
        ZenModeRuleSettingsBase.this.removeZenRule(ZenModeRuleSettingsBase.this.mId);
      }
    }).show().findViewById(16908299);
    if (localView != null) {
      localView.setTextDirection(5);
    }
  }
  
  private void showRuleNameDialog()
  {
    new ZenRuleNameDialog(this.mContext, this.mRule.getName())
    {
      public void onOk(String paramAnonymousString)
      {
        ZenModeRuleSettingsBase.this.mRule.setName(paramAnonymousString);
        ZenModeRuleSettingsBase.this.setZenRule(ZenModeRuleSettingsBase.this.mId, ZenModeRuleSettingsBase.this.mRule);
      }
    }.show();
  }
  
  private void toastAndFinish()
  {
    if (!this.mDeleting) {
      Toast.makeText(this.mContext, 2131693287, 0).show();
    }
    getActivity().finish();
  }
  
  private void updateControls()
  {
    this.mDisableListeners = true;
    updateRuleName();
    updateControlsInternal();
    this.mZenMode.setValue(Integer.toString(this.mRule.getInterruptionFilter()));
    if (this.mSwitchBar != null) {
      this.mSwitchBar.setChecked(this.mRule.isEnabled());
    }
    this.mDisableListeners = false;
  }
  
  private void updateRuleName()
  {
    getActivity().setTitle(this.mRule.getName());
    this.mRuleName.setSummary(this.mRule.getName());
  }
  
  protected abstract int getEnabledToastText();
  
  protected abstract String getZenModeDependency();
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.addOnSwitchChangeListener(this);
    this.mSwitchBar.show();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    paramBundle = getActivity().getIntent();
    if (DEBUG) {
      Log.d("ZenModeSettings", "onCreate getIntent()=" + paramBundle);
    }
    if (paramBundle == null)
    {
      Log.w("ZenModeSettings", "No intent");
      toastAndFinish();
      return;
    }
    this.mId = paramBundle.getStringExtra("android.service.notification.extra.RULE_ID");
    if (DEBUG) {
      Log.d("ZenModeSettings", "mId=" + this.mId);
    }
    if (refreshRuleOrFinish()) {
      return;
    }
    setHasOptionsMenu(true);
    onCreateInternal();
    paramBundle = getPreferenceScreen();
    this.mRuleName = paramBundle.findPreference("rule_name");
    this.mRuleName.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        ZenModeRuleSettingsBase.-wrap0(ZenModeRuleSettingsBase.this);
        return true;
      }
    });
    this.mZenMode = ((DropDownPreference)paramBundle.findPreference("zen_mode"));
    this.mZenMode.setEntries(new CharSequence[] { getString(2131693202), getString(2131693203), getString(2131693204) });
    this.mZenMode.setEntryValues(new CharSequence[] { Integer.toString(2), Integer.toString(4), Integer.toString(3) });
    this.mZenMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModeRuleSettingsBase.this.mDisableListeners) {
          return false;
        }
        int i = Integer.parseInt((String)paramAnonymousObject);
        if (i == ZenModeRuleSettingsBase.this.mRule.getInterruptionFilter()) {
          return false;
        }
        if (ZenModeRuleSettingsBase.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange zenMode=" + i);
        }
        ZenModeRuleSettingsBase.this.mRule.setInterruptionFilter(i);
        ZenModeRuleSettingsBase.this.setZenRule(ZenModeRuleSettingsBase.this.mId, ZenModeRuleSettingsBase.this.mRule);
        return true;
      }
    });
    this.mZenMode.setOrder(10);
    this.mZenMode.setDependency(getZenModeDependency());
  }
  
  protected abstract void onCreateInternal();
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (DEBUG) {
      Log.d("ZenModeSettings", "onCreateOptionsMenu");
    }
    paramMenuInflater.inflate(2132017161, paramMenu);
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mSwitchBar.hide();
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (DEBUG) {
      Log.d("ZenModeSettings", "onOptionsItemSelected " + paramMenuItem.getItemId());
    }
    if (paramMenuItem.getItemId() == 2131362675)
    {
      MetricsLogger.action(this.mContext, 174);
      showDeleteRuleDialog();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onResume()
  {
    super.onResume();
    if (isUiRestricted()) {
      return;
    }
    updateControls();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    if (DEBUG) {
      Log.d("ZenModeSettings", "onSwitchChanged " + paramBoolean);
    }
    if (this.mDisableListeners) {
      return;
    }
    if (paramBoolean == this.mRule.isEnabled()) {
      return;
    }
    MetricsLogger.action(this.mContext, 176, paramBoolean);
    if (DEBUG) {
      Log.d("ZenModeSettings", "onSwitchChanged enabled=" + paramBoolean);
    }
    this.mRule.setEnabled(paramBoolean);
    setZenRule(this.mId, this.mRule);
    if (paramBoolean)
    {
      i = getEnabledToastText();
      if (i != 0)
      {
        this.mEnabledToast = Toast.makeText(this.mContext, i, 0);
        this.mEnabledToast.show();
      }
    }
    while (this.mEnabledToast == null)
    {
      int i;
      return;
    }
    this.mEnabledToast.cancel();
  }
  
  protected void onZenModeChanged() {}
  
  protected void onZenModeConfigChanged()
  {
    if (!refreshRuleOrFinish()) {
      updateControls();
    }
  }
  
  protected abstract boolean setRule(AutomaticZenRule paramAutomaticZenRule);
  
  protected abstract void updateControlsInternal();
  
  protected void updateRule(Uri paramUri)
  {
    this.mRule.setConditionId(paramUri);
    setZenRule(this.mId, this.mRule);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeRuleSettingsBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
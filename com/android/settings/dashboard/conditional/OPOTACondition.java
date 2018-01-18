package com.android.settings.dashboard.conditional;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.provider.Settings.System;
import com.oneplus.settings.SettingsBaseApplication;

public class OPOTACondition
  extends Condition
{
  private static final String BOOT_BROADCAST = "android.intent.action.BOOT_COMPLETED";
  private static final String HAS_NEW_VERSION_TO_UPDATE = "has_new_version_to_update";
  private static final String OEM_BOOT_BROADCAST = "com.oem.intent.action.BOOT_COMPLETED";
  private static final String STRONG_PROMPT_OTA = "strong_prompt_ota";
  
  public OPOTACondition(ConditionManager paramConditionManager)
  {
    super(paramConditionManager);
  }
  
  private static boolean activeRefresh()
  {
    boolean bool2 = false;
    int i;
    if (Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "has_new_version_to_update", 0) == 1)
    {
      i = 1;
      if (Settings.System.getInt(SettingsBaseApplication.mApplication.getContentResolver(), "strong_prompt_ota", 0) != 100) {
        break label52;
      }
    }
    label52:
    for (boolean bool1 = true;; bool1 = false)
    {
      if (i != 0) {
        bool2 = bool1;
      }
      return bool2;
      i = 0;
      break;
    }
  }
  
  public CharSequence[] getActions()
  {
    return new CharSequence[] { this.mManager.getContext().getString(2131693601) };
  }
  
  public Icon getIcon()
  {
    return Icon.createWithResource(this.mManager.getContext(), 2130838096);
  }
  
  public int getMetricsConstant()
  {
    return 9999;
  }
  
  public CharSequence getSummary()
  {
    return this.mManager.getContext().getString(2131690470);
  }
  
  public CharSequence getTitle()
  {
    return this.mManager.getContext().getString(2131690470);
  }
  
  public void onActionClick(int paramInt) {}
  
  public void onPrimaryClick()
  {
    this.mManager.getContext().startActivity(new Intent("android.intent.action.CheckUpdate"));
  }
  
  public void refreshState()
  {
    setActive(activeRefresh());
  }
  
  public static class Receiver
    extends BroadcastReceiver
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if ((("android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction())) || ("com.oem.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction()))) && (OPOTACondition.-wrap0())) {
        ((OPOTACondition)ConditionManager.get(paramContext).getCondition(OPOTACondition.class)).setSilenced();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\conditional\OPOTACondition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
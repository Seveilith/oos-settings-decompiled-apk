package com.oneplus.settings.statusbar;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import java.util.Set;

public class StatusBarSwitch
  extends SwitchPreference
{
  private static final String TAG = "StatusBarSwitch";
  private Set<String> mBlacklist;
  private Utils mUtils;
  
  public StatusBarSwitch(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mUtils = new Utils(paramContext);
  }
  
  private void setList(Set<String> paramSet)
  {
    Settings.Secure.putStringForUser(getContext().getContentResolver(), "icon_blacklist", TextUtils.join(",", paramSet), ActivityManager.getCurrentUser());
    Log.i("StatusBarSwitch", " setList blacklist:" + paramSet);
  }
  
  private void updateList()
  {
    this.mBlacklist = Utils.getIconBlacklist(this.mUtils.getValue("icon_blacklist"));
  }
  
  private void updateUI()
  {
    updateList();
    if (this.mBlacklist.contains(getKey())) {}
    for (boolean bool = false;; bool = true)
    {
      setChecked(bool);
      Log.i("StatusBarSwitch", " updateUI blacklist:" + this.mBlacklist);
      return;
    }
  }
  
  public void onAttached()
  {
    super.onAttached();
    updateUI();
  }
  
  public void onDetached()
  {
    super.onDetached();
  }
  
  protected boolean persistBoolean(boolean paramBoolean)
  {
    updateList();
    Log.i("StatusBarSwitch", "set key:" + getKey() + " value:" + paramBoolean);
    if (!paramBoolean) {
      if (!this.mBlacklist.contains(getKey()))
      {
        this.mBlacklist.add(getKey());
        setList(this.mBlacklist);
      }
    }
    for (;;)
    {
      return true;
      if (this.mBlacklist.remove(getKey())) {
        setList(this.mBlacklist);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\statusbar\StatusBarSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
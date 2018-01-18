package com.oneplus.settings.statusbar;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;

public class ClockPreference
  extends ListPreference
{
  private static final String DEFAULT = "default";
  private static final String DISABLED = "disabled";
  private static final String SECONDS = "seconds";
  private static final String TAG = "ClockPreference";
  private ArraySet<String> mBlacklist;
  private final String mClock;
  private boolean mClockEnabled;
  private boolean mHasSeconds;
  private boolean mHasSetValue;
  private Utils mUtils;
  
  public ClockPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mUtils = new Utils(paramContext);
    this.mClock = paramContext.getString(17039410);
    setEntryValues(new CharSequence[] { "seconds", "default", "disabled" });
  }
  
  private void updateStatus()
  {
    boolean bool2 = true;
    this.mBlacklist = Utils.getIconBlacklist(this.mUtils.getValue("icon_blacklist"));
    if (this.mBlacklist.contains(this.mClock))
    {
      bool1 = false;
      this.mClockEnabled = bool1;
      if (this.mUtils.getValue("clock_seconds", 0) == 0) {
        break label110;
      }
    }
    label110:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mHasSeconds = bool1;
      Log.i("ClockPreference", "updateStatus mBlacklist:" + this.mBlacklist + " TextUtils.join:" + TextUtils.join(",", this.mBlacklist));
      return;
      bool1 = true;
      break;
    }
  }
  
  private void updateUI()
  {
    updateStatus();
    if (!this.mHasSetValue)
    {
      this.mHasSetValue = true;
      if ((this.mClockEnabled) && (this.mHasSeconds)) {
        setValue("seconds");
      }
    }
    else
    {
      return;
    }
    if (this.mClockEnabled)
    {
      setValue("default");
      return;
    }
    setValue("disabled");
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
  
  protected boolean persistString(String paramString)
  {
    if (this.mUtils == null) {
      return true;
    }
    updateStatus();
    Utils localUtils = this.mUtils;
    int i;
    if ("seconds".equals(paramString))
    {
      i = 1;
      localUtils.setValue("clock_seconds", i);
      if (!"disabled".equals(paramString)) {
        break label124;
      }
      this.mBlacklist.add(this.mClock);
    }
    for (;;)
    {
      Log.i("ClockPreference", "update value:" + paramString + " mBlacklist:" + TextUtils.join(",", this.mBlacklist));
      this.mUtils.setValue("icon_blacklist", TextUtils.join(",", this.mBlacklist));
      return true;
      i = 0;
      break;
      label124:
      this.mBlacklist.remove(this.mClock);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\statusbar\ClockPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.widget.Switch;
import com.android.settings.SeekBarPreference;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;

public class ToggleAutoclickPreferenceFragment
  extends ToggleFeaturePreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceChangeListener
{
  private static final int AUTOCLICK_DELAY_STEP = 100;
  private static final int MAX_AUTOCLICK_DELAY = 1000;
  private static final int MIN_AUTOCLICK_DELAY = 200;
  private static final int[] mAutoclickPreferenceSummaries = { 2131951629, 2131951630, 2131951631, 2131951632, 2131951633 };
  private SeekBarPreference mDelay;
  
  private int delayToSeekBarProgress(int paramInt)
  {
    return (paramInt - 200) / 100;
  }
  
  static CharSequence getAutoclickPreferenceSummary(Resources paramResources, int paramInt)
  {
    int i = getAutoclickPreferenceSummaryIndex(paramInt);
    return paramResources.getQuantityString(mAutoclickPreferenceSummaries[i], paramInt, new Object[] { Integer.valueOf(paramInt) });
  }
  
  private static int getAutoclickPreferenceSummaryIndex(int paramInt)
  {
    if (paramInt <= 200) {
      return 0;
    }
    if (paramInt >= 1000) {
      return mAutoclickPreferenceSummaries.length - 1;
    }
    return (paramInt - 200) / (800 / (mAutoclickPreferenceSummaries.length - 1));
  }
  
  private int seekBarProgressToDelay(int paramInt)
  {
    return paramInt * 100 + 200;
  }
  
  protected int getMetricsCategory()
  {
    return 335;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230721);
    int i = Settings.Secure.getInt(getContentResolver(), "accessibility_autoclick_delay", 600);
    this.mDelay = ((SeekBarPreference)findPreference("autoclick_delay"));
    this.mDelay.setMax(delayToSeekBarProgress(1000));
    this.mDelay.setProgress(delayToSeekBarProgress(i));
    this.mDelay.setOnPreferenceChangeListener(this);
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    boolean bool2 = true;
    super.onInstallSwitchBarToggleSwitch();
    int i = Settings.Secure.getInt(getContentResolver(), "accessibility_autoclick_enabled", 0);
    Object localObject = this.mSwitchBar;
    if (i == 1)
    {
      bool1 = true;
      ((SwitchBar)localObject).setCheckedInternal(bool1);
      this.mSwitchBar.addOnSwitchChangeListener(this);
      localObject = this.mDelay;
      if (i != 1) {
        break label69;
      }
    }
    label69:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      ((SeekBarPreference)localObject).setEnabled(bool1);
      return;
      bool1 = false;
      break;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ((paramPreference == this.mDelay) && ((paramObject instanceof Integer)))
    {
      Settings.Secure.putInt(getContentResolver(), "accessibility_autoclick_delay", seekBarProgressToDelay(((Integer)paramObject).intValue()));
      return true;
    }
    return false;
  }
  
  protected void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, paramString, i);
      this.mDelay.setEnabled(paramBoolean);
      return;
    }
  }
  
  protected void onRemoveSwitchBarToggleSwitch()
  {
    super.onRemoveSwitchBarToggleSwitch();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    onPreferenceToggled("accessibility_autoclick_enabled", paramBoolean);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setTitle(getString(2131692348));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleAutoclickPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
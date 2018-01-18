package com.android.settings.accessibility;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.view.View;
import android.widget.Switch;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;

public class ToggleDaltonizerPreferenceFragment
  extends ToggleFeaturePreferenceFragment
  implements Preference.OnPreferenceChangeListener, SwitchBar.OnSwitchChangeListener
{
  private static final int DEFAULT_TYPE = 12;
  private static final String ENABLED = "accessibility_display_daltonizer_enabled";
  private static final String TYPE = "accessibility_display_daltonizer";
  private ListPreference mType;
  
  private void initPreferences()
  {
    String str = Integer.toString(Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer", 12));
    this.mType.setValue(str);
    this.mType.setOnPreferenceChangeListener(this);
    if (this.mType.findIndexOfValue(str) < 0) {
      this.mType.setSummary(getString(2131689774, new Object[] { getString(2131689713) }));
    }
  }
  
  protected int getMetricsCategory()
  {
    return 5;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230722);
    this.mType = ((ListPreference)findPreference("type"));
    initPreferences();
  }
  
  protected void onInstallSwitchBarToggleSwitch()
  {
    boolean bool = true;
    super.onInstallSwitchBarToggleSwitch();
    SwitchBar localSwitchBar = this.mSwitchBar;
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer_enabled", 0) == 1) {}
    for (;;)
    {
      localSwitchBar.setCheckedInternal(bool);
      this.mSwitchBar.addOnSwitchChangeListener(this);
      return;
      bool = false;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mType)
    {
      Settings.Secure.putInt(getContentResolver(), "accessibility_display_daltonizer", Integer.parseInt((String)paramObject));
      paramPreference.setSummary("%s");
    }
    return true;
  }
  
  protected void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    paramString = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(paramString, "accessibility_display_daltonizer_enabled", i);
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
    onPreferenceToggled(this.mPreferenceKey, paramBoolean);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setTitle(getString(2131689772));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\ToggleDaltonizerPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
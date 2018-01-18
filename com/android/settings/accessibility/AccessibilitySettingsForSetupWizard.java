package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Iterator;

public class AccessibilitySettingsForSetupWizard
  extends SettingsPreferenceFragment
  implements DialogCreatable, Preference.OnPreferenceChangeListener
{
  private static final String DISPLAY_MAGNIFICATION_PREFERENCE = "screen_magnification_preference";
  private static final String FONT_SIZE_PREFERENCE = "font_size_preference";
  private static final String SCREEN_READER_PREFERENCE = "screen_reader_preference";
  private static final String TAG = AccessibilitySettingsForSetupWizard.class.getSimpleName();
  private Preference mDisplayMagnificationPreference;
  private Preference mScreenReaderPreference;
  
  private AccessibilityServiceInfo findFirstServiceWithSpokenFeedback()
  {
    Iterator localIterator = ((AccessibilityManager)getActivity().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList().iterator();
    while (localIterator.hasNext())
    {
      AccessibilityServiceInfo localAccessibilityServiceInfo = (AccessibilityServiceInfo)localIterator.next();
      if ((localAccessibilityServiceInfo.feedbackType & 0x1) != 0) {
        return localAccessibilityServiceInfo;
      }
    }
    return null;
  }
  
  private void updateScreenReaderPreference()
  {
    Object localObject1 = findFirstServiceWithSpokenFeedback();
    if (localObject1 == null) {
      this.mScreenReaderPreference.setEnabled(false);
    }
    for (;;)
    {
      if ((localObject1 != null) && (((AccessibilityServiceInfo)localObject1).getResolveInfo() != null))
      {
        Object localObject2 = ((AccessibilityServiceInfo)localObject1).getResolveInfo().serviceInfo;
        String str = ((AccessibilityServiceInfo)localObject1).getResolveInfo().loadLabel(getPackageManager()).toString();
        this.mScreenReaderPreference.setTitle(str);
        ComponentName localComponentName = new ComponentName(((ServiceInfo)localObject2).packageName, ((ServiceInfo)localObject2).name);
        this.mScreenReaderPreference.setKey(localComponentName.flattenToString());
        localObject2 = this.mScreenReaderPreference.getExtras();
        ((Bundle)localObject2).putParcelable("component_name", localComponentName);
        ((Bundle)localObject2).putString("preference_key", this.mScreenReaderPreference.getKey());
        ((Bundle)localObject2).putString("title", str);
        str = ((AccessibilityServiceInfo)localObject1).loadDescription(getPackageManager());
        localObject1 = str;
        if (TextUtils.isEmpty(str)) {
          localObject1 = getString(2131692399);
        }
        ((Bundle)localObject2).putString("summary", (String)localObject1);
      }
      return;
      this.mScreenReaderPreference.setEnabled(true);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 367;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(false);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230724);
    this.mDisplayMagnificationPreference = findPreference("screen_magnification_preference");
    this.mScreenReaderPreference = findPreference("screen_reader_preference");
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    boolean bool = true;
    Bundle localBundle;
    if (this.mDisplayMagnificationPreference == paramPreference)
    {
      localBundle = this.mDisplayMagnificationPreference.getExtras();
      localBundle.putString("title", getString(2131692330));
      localBundle.putCharSequence("summary", getText(2131692332));
      if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_magnification_enabled", 0) != 1) {
        break label69;
      }
    }
    for (;;)
    {
      localBundle.putBoolean("checked", bool);
      return super.onPreferenceTreeClick(paramPreference);
      label69:
      bool = false;
    }
  }
  
  public void onResume()
  {
    super.onResume();
    updateScreenReaderPreference();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\AccessibilitySettingsForSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
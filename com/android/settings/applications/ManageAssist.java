package com.android.settings.applications;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.voice.VoiceInputListPreference;

public class ManageAssist
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String KEY_CONTEXT = "context";
  private static final String KEY_DEFAULT_ASSIST = "default_assist";
  private static final String KEY_SCREENSHOT = "screenshot";
  private static final String KEY_VOICE_INPUT = "voice_input_settings";
  private SwitchPreference mContextPref;
  private DefaultAssistPreference mDefaultAssitPref;
  private Handler mHandler = new Handler();
  private SwitchPreference mScreenshotPref;
  private VoiceInputListPreference mVoiceInputPref;
  
  private void confirmNewAssist(final String paramString)
  {
    int i = this.mDefaultAssitPref.findIndexOfValue(paramString);
    Object localObject = this.mDefaultAssitPref.getEntries()[i];
    String str = getString(2131693432, new Object[] { localObject });
    localObject = getString(2131693433, new Object[] { localObject });
    paramString = new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ManageAssist.-wrap0(ManageAssist.this, paramString);
      }
    };
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getContext());
    localBuilder.setTitle(str).setMessage((CharSequence)localObject).setCancelable(true).setPositiveButton(2131693434, paramString).setNegativeButton(2131693435, null);
    localBuilder.create().show();
  }
  
  private boolean isCurrentAssistVoiceService()
  {
    ComponentName localComponentName1 = this.mDefaultAssitPref.getCurrentAssist();
    ComponentName localComponentName2 = this.mVoiceInputPref.getCurrentService();
    if ((localComponentName1 == null) && (localComponentName2 == null)) {
      return true;
    }
    if (localComponentName1 != null) {
      return localComponentName1.equals(localComponentName2);
    }
    return false;
  }
  
  private void postUpdateUi()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        ManageAssist.-wrap1(ManageAssist.this);
      }
    });
  }
  
  private void setDefaultAssist(String paramString)
  {
    this.mDefaultAssitPref.setValue(paramString);
    updateUi();
  }
  
  private void updateUi()
  {
    boolean bool = true;
    this.mDefaultAssitPref.refreshAssistApps();
    this.mVoiceInputPref.refreshVoiceInputs();
    Object localObject = this.mDefaultAssitPref.getCurrentAssist();
    int i;
    if (localObject != null)
    {
      i = 1;
      if (i == 0) {
        break label130;
      }
      getPreferenceScreen().addPreference(this.mContextPref);
      getPreferenceScreen().addPreference(this.mScreenshotPref);
      label58:
      if (!isCurrentAssistVoiceService()) {
        break label157;
      }
      getPreferenceScreen().removePreference(this.mVoiceInputPref);
      label77:
      this.mScreenshotPref.setEnabled(this.mContextPref.isChecked());
      localObject = this.mScreenshotPref;
      if ((!this.mContextPref.isChecked()) || (Settings.Secure.getInt(getContentResolver(), "assist_screenshot_enabled", 1) == 0)) {
        break label180;
      }
    }
    for (;;)
    {
      ((SwitchPreference)localObject).setChecked(bool);
      return;
      i = 0;
      break;
      label130:
      getPreferenceScreen().removePreference(this.mContextPref);
      getPreferenceScreen().removePreference(this.mScreenshotPref);
      break label58;
      label157:
      getPreferenceScreen().addPreference(this.mVoiceInputPref);
      this.mVoiceInputPref.setAssistRestrict((ComponentName)localObject);
      break label77;
      label180:
      bool = false;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 201;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230779);
    this.mDefaultAssitPref = ((DefaultAssistPreference)findPreference("default_assist"));
    this.mDefaultAssitPref.setOnPreferenceChangeListener(this);
    this.mContextPref = ((SwitchPreference)findPreference("context"));
    paramBundle = this.mContextPref;
    if (Settings.Secure.getInt(getContentResolver(), "assist_structure_enabled", 1) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      paramBundle.setChecked(bool);
      this.mContextPref.setOnPreferenceChangeListener(this);
      this.mScreenshotPref = ((SwitchPreference)findPreference("screenshot"));
      this.mScreenshotPref.setOnPreferenceChangeListener(this);
      this.mVoiceInputPref = ((VoiceInputListPreference)findPreference("voice_input_settings"));
      updateUi();
      return;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    int i = 0;
    if (paramPreference == this.mContextPref)
    {
      paramPreference = getContentResolver();
      if (((Boolean)paramObject).booleanValue()) {
        i = 1;
      }
      Settings.Secure.putInt(paramPreference, "assist_structure_enabled", i);
      postUpdateUi();
      return true;
    }
    if (paramPreference == this.mScreenshotPref)
    {
      paramPreference = getContentResolver();
      i = j;
      if (((Boolean)paramObject).booleanValue()) {
        i = 1;
      }
      Settings.Secure.putInt(paramPreference, "assist_screenshot_enabled", i);
      return true;
    }
    if (paramPreference == this.mDefaultAssitPref)
    {
      paramPreference = (String)paramObject;
      if ((paramPreference == null) || (paramPreference.contentEquals("")))
      {
        setDefaultAssist("");
        return false;
      }
      paramObject = this.mDefaultAssitPref.getValue();
      if ((paramObject != null) && (paramPreference.contentEquals((CharSequence)paramObject))) {
        return false;
      }
      confirmNewAssist(paramPreference);
      return false;
    }
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ManageAssist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
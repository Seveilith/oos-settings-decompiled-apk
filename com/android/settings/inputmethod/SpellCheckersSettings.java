package com.android.settings.inputmethod;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.Switch;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;

public class SpellCheckersSettings
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
  private static final boolean DBG = false;
  private static final int ITEM_ID_USE_SYSTEM_LANGUAGE = 0;
  private static final String KEY_DEFAULT_SPELL_CHECKER = "default_spellchecker";
  private static final String KEY_SPELL_CHECKER_LANGUAGE = "spellchecker_language";
  private static final String TAG = SpellCheckersSettings.class.getSimpleName();
  private SpellCheckerInfo mCurrentSci;
  private AlertDialog mDialog = null;
  private SpellCheckerInfo[] mEnabledScis;
  private Preference mSpellCheckerLanaguagePref;
  private SwitchBar mSwitchBar;
  private TextServicesManager mTsm;
  
  private void changeCurrentSpellChecker(SpellCheckerInfo paramSpellCheckerInfo)
  {
    this.mTsm.setCurrentSpellChecker(paramSpellCheckerInfo);
    updatePreferenceScreen();
  }
  
  private static int convertDialogItemIdToSubtypeIndex(int paramInt)
  {
    return paramInt - 1;
  }
  
  private static int convertSubtypeIndexToDialogItemId(int paramInt)
  {
    return paramInt + 1;
  }
  
  private CharSequence getSpellCheckerSubtypeLabel(SpellCheckerInfo paramSpellCheckerInfo, SpellCheckerSubtype paramSpellCheckerSubtype)
  {
    if (paramSpellCheckerInfo == null) {
      return getString(2131693672);
    }
    if (paramSpellCheckerSubtype == null) {
      return getString(2131692296);
    }
    return paramSpellCheckerSubtype.getDisplayName(getActivity(), paramSpellCheckerInfo.getPackageName(), paramSpellCheckerInfo.getServiceInfo().applicationInfo);
  }
  
  private void populatePreferenceScreen()
  {
    int i = 0;
    SpellCheckerPreference localSpellCheckerPreference = new SpellCheckerPreference(getPrefContext(), this.mEnabledScis);
    localSpellCheckerPreference.setTitle(2131693670);
    if (this.mEnabledScis == null)
    {
      if (i <= 0) {
        break label71;
      }
      localSpellCheckerPreference.setSummary("%s");
    }
    for (;;)
    {
      localSpellCheckerPreference.setKey("default_spellchecker");
      localSpellCheckerPreference.setOnPreferenceChangeListener(this);
      getPreferenceScreen().addPreference(localSpellCheckerPreference);
      return;
      i = this.mEnabledScis.length;
      break;
      label71:
      localSpellCheckerPreference.setSummary(2131693672);
    }
  }
  
  private void showChooseLanguageDialog()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing())) {
      this.mDialog.dismiss();
    }
    final SpellCheckerInfo localSpellCheckerInfo = this.mTsm.getCurrentSpellChecker();
    if (localSpellCheckerInfo == null) {
      return;
    }
    SpellCheckerSubtype localSpellCheckerSubtype1 = this.mTsm.getCurrentSpellCheckerSubtype(false);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(2131692226);
    int m = localSpellCheckerInfo.getSubtypeCount();
    CharSequence[] arrayOfCharSequence = new CharSequence[m + 1];
    arrayOfCharSequence[0] = getSpellCheckerSubtypeLabel(localSpellCheckerInfo, null);
    int j = 0;
    int i = 0;
    while (i < m)
    {
      SpellCheckerSubtype localSpellCheckerSubtype2 = localSpellCheckerInfo.getSubtypeAt(i);
      int k = convertSubtypeIndexToDialogItemId(i);
      arrayOfCharSequence[k] = getSpellCheckerSubtypeLabel(localSpellCheckerInfo, localSpellCheckerSubtype2);
      if (localSpellCheckerSubtype2.equals(localSpellCheckerSubtype1)) {
        j = k;
      }
      i += 1;
    }
    localBuilder.setSingleChoiceItems(arrayOfCharSequence, j, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0) {
          SpellCheckersSettings.-get0(SpellCheckersSettings.this).setSpellCheckerSubtype(null);
        }
        for (;;)
        {
          paramAnonymousDialogInterface.dismiss();
          SpellCheckersSettings.-wrap2(SpellCheckersSettings.this);
          return;
          paramAnonymousInt = SpellCheckersSettings.-wrap0(paramAnonymousInt);
          SpellCheckersSettings.-get0(SpellCheckersSettings.this).setSpellCheckerSubtype(localSpellCheckerInfo.getSubtypeAt(paramAnonymousInt));
        }
      }
    });
    this.mDialog = localBuilder.create();
    this.mDialog.show();
  }
  
  private void showSecurityWarnDialog(final SpellCheckerInfo paramSpellCheckerInfo)
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing())) {
      this.mDialog.dismiss();
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(17039380);
    localBuilder.setMessage(getString(2131692237, new Object[] { paramSpellCheckerInfo.loadLabel(getPackageManager()) }));
    localBuilder.setCancelable(true);
    localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        SpellCheckersSettings.-wrap1(SpellCheckersSettings.this, paramSpellCheckerInfo);
      }
    });
    localBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    });
    this.mDialog = localBuilder.create();
    this.mDialog.show();
  }
  
  private void updatePreferenceScreen()
  {
    boolean bool2 = false;
    this.mCurrentSci = this.mTsm.getCurrentSpellChecker();
    boolean bool3 = this.mTsm.isSpellCheckerEnabled();
    this.mSwitchBar.setChecked(bool3);
    if (this.mCurrentSci != null) {}
    for (Object localObject = this.mTsm.getCurrentSpellCheckerSubtype(false);; localObject = null)
    {
      this.mSpellCheckerLanaguagePref.setSummary(getSpellCheckerSubtypeLabel(this.mCurrentSci, (SpellCheckerSubtype)localObject));
      localObject = getPreferenceScreen();
      int j = ((PreferenceScreen)localObject).getPreferenceCount();
      int i = 0;
      while (i < j)
      {
        Preference localPreference = ((PreferenceScreen)localObject).getPreference(i);
        localPreference.setEnabled(bool3);
        if ((localPreference instanceof SpellCheckerPreference)) {
          ((SpellCheckerPreference)localPreference).setSelected(this.mCurrentSci);
        }
        i += 1;
      }
    }
    localObject = this.mSpellCheckerLanaguagePref;
    boolean bool1 = bool2;
    if (bool3)
    {
      bool1 = bool2;
      if (this.mCurrentSci != null) {
        bool1 = true;
      }
    }
    ((Preference)localObject).setEnabled(bool1);
  }
  
  protected int getMetricsCategory()
  {
    return 59;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230863);
    this.mSpellCheckerLanaguagePref = findPreference("spellchecker_language");
    this.mSpellCheckerLanaguagePref.setOnPreferenceClickListener(this);
    this.mTsm = ((TextServicesManager)getSystemService("textservices"));
    this.mCurrentSci = this.mTsm.getCurrentSpellChecker();
    this.mEnabledScis = this.mTsm.getEnabledSpellCheckers();
    populatePreferenceScreen();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = (SpellCheckerInfo)paramObject;
    if ((paramPreference.getServiceInfo().applicationInfo.flags & 0x1) != 0) {}
    for (int i = 1; i != 0; i = 0)
    {
      changeCurrentSpellChecker(paramPreference);
      return true;
    }
    showSecurityWarnDialog(paramPreference);
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mSpellCheckerLanaguagePref)
    {
      showChooseLanguageDialog();
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSwitchBar = ((SettingsActivity)getActivity()).getSwitchBar();
    this.mSwitchBar.show();
    this.mSwitchBar.addOnSwitchChangeListener(this);
    updatePreferenceScreen();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    this.mTsm.setSpellCheckerEnabled(paramBoolean);
    updatePreferenceScreen();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\SpellCheckersSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
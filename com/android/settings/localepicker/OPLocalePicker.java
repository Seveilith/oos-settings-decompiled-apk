package com.android.settings.localepicker;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleSelectionListener;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment.SettingsDialogFragment;
import com.android.settings.Utils;
import java.util.Locale;

public class OPLocalePicker
  extends LocalePicker
  implements LocalePicker.LocaleSelectionListener, DialogCreatable
{
  private static final int DLG_SHOW_GLOBAL_WARNING = 1;
  private static final String SAVE_TARGET_LOCALE = "locale";
  private static final String TAG = "LocalePicker";
  private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
  private Locale mTargetLocale;
  
  public OPLocalePicker()
  {
    setLocaleSelectionListener(this);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if ((paramBundle != null) && (paramBundle.containsKey("locale"))) {
      this.mTargetLocale = new Locale(paramBundle.getString("locale"));
    }
    setHasOptionsMenu(true);
  }
  
  public Dialog onCreateDialog(final int paramInt)
  {
    Utils.buildGlobalChangeWarningDialog(getActivity(), 2131692974, new Runnable()
    {
      public void run()
      {
        OPLocalePicker.this.removeDialog(paramInt);
        OPLocalePicker.this.getActivity().onBackPressed();
        OPLocalePicker.updateLocale(OPLocalePicker.-get0(OPLocalePicker.this));
      }
    });
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    Utils.forcePrepareCustomPreferencesList(paramViewGroup, paramLayoutInflater, (ListView)paramLayoutInflater.findViewById(16908298), false);
    return paramLayoutInflater;
  }
  
  public void onLocaleSelected(Locale paramLocale)
  {
    getActivity().onBackPressed();
    updateLocale(paramLocale);
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mTargetLocale != null) {
      paramBundle.putString("locale", this.mTargetLocale.toString());
    }
  }
  
  protected void removeDialog(int paramInt)
  {
    if ((this.mDialogFragment != null) && (this.mDialogFragment.getDialogId() == paramInt)) {
      this.mDialogFragment.dismiss();
    }
    this.mDialogFragment = null;
  }
  
  protected void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null) {
      Log.e("LocalePicker", "Old dialog fragment not null!");
    }
    this.mDialogFragment = new SettingsPreferenceFragment.SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\localepicker\OPLocalePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.android.settings.inputmethod;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.text.Collator;

class InputMethodPreference
  extends OPRestrictedSwitchPreference
  implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
  private static final String EMPTY_TEXT = "";
  private static final int NO_WIDGET = 0;
  private static final String TAG = InputMethodPreference.class.getSimpleName();
  private AlertDialog mDialog = null;
  private final boolean mHasPriorityInSorting;
  private final InputMethodInfo mImi;
  private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
  private final boolean mIsAllowedByOrganization;
  private final OnSavePreferenceListener mOnSaveListener;
  
  InputMethodPreference(Context paramContext, InputMethodInfo paramInputMethodInfo, boolean paramBoolean1, boolean paramBoolean2, OnSavePreferenceListener paramOnSavePreferenceListener)
  {
    super(paramContext);
    setPersistent(false);
    this.mImi = paramInputMethodInfo;
    this.mIsAllowedByOrganization = paramBoolean2;
    this.mOnSaveListener = paramOnSavePreferenceListener;
    if (!paramBoolean1) {
      setWidgetLayoutResource(0);
    }
    setSwitchTextOn("");
    setSwitchTextOff("");
    setKey(paramInputMethodInfo.getId());
    setTitle(paramInputMethodInfo.loadLabel(paramContext.getPackageManager()));
    paramOnSavePreferenceListener = paramInputMethodInfo.getSettingsActivity();
    if (TextUtils.isEmpty(paramOnSavePreferenceListener)) {
      setIntent(null);
    }
    for (;;)
    {
      this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(paramContext);
      paramBoolean1 = bool;
      if (InputMethodUtils.isSystemIme(paramInputMethodInfo)) {
        paramBoolean1 = this.mInputMethodSettingValues.isValidSystemNonAuxAsciiCapableIme(paramInputMethodInfo, paramContext);
      }
      this.mHasPriorityInSorting = paramBoolean1;
      setOnPreferenceClickListener(this);
      setOnPreferenceChangeListener(this);
      return;
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClassName(paramInputMethodInfo.getPackageName(), paramOnSavePreferenceListener);
      setIntent(localIntent);
    }
  }
  
  private InputMethodManager getInputMethodManager()
  {
    return (InputMethodManager)getContext().getSystemService("input_method");
  }
  
  private String getSummaryString()
  {
    return InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(getInputMethodManager().getEnabledInputMethodSubtypeList(this.mImi, true), getContext(), this.mImi);
  }
  
  private boolean isImeEnabler()
  {
    boolean bool = false;
    if (getWidgetLayoutResource() != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void setCheckedInternal(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    this.mOnSaveListener.onSaveInputMethodPreference(this);
    notifyChanged();
  }
  
  private void showDirectBootWarnDialog()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing())) {
      this.mDialog.dismiss();
    }
    Context localContext = getContext();
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(localContext);
    localBuilder.setCancelable(true);
    localBuilder.setMessage(localContext.getText(2131693372));
    localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        InputMethodPreference.-wrap0(InputMethodPreference.this, true);
      }
    });
    localBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        InputMethodPreference.-wrap0(InputMethodPreference.this, false);
      }
    });
    this.mDialog = localBuilder.create();
    this.mDialog.show();
  }
  
  private void showSecurityWarnDialog()
  {
    if ((this.mDialog != null) && (this.mDialog.isShowing())) {
      this.mDialog.dismiss();
    }
    Context localContext = getContext();
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(localContext);
    localBuilder.setCancelable(true);
    localBuilder.setTitle(17039380);
    localBuilder.setMessage(localContext.getString(2131692236, new Object[] { this.mImi.getServiceInfo().applicationInfo.loadLabel(localContext.getPackageManager()) }));
    localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        if (InputMethodPreference.-get0(InputMethodPreference.this).getServiceInfo().directBootAware)
        {
          InputMethodPreference.-wrap0(InputMethodPreference.this, true);
          return;
        }
        InputMethodPreference.-wrap1(InputMethodPreference.this);
      }
    });
    localBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        InputMethodPreference.-wrap0(InputMethodPreference.this, false);
      }
    });
    this.mDialog = localBuilder.create();
    this.mDialog.show();
  }
  
  int compareTo(InputMethodPreference paramInputMethodPreference, Collator paramCollator)
  {
    if (this == paramInputMethodPreference) {
      return 0;
    }
    if (this.mHasPriorityInSorting == paramInputMethodPreference.mHasPriorityInSorting)
    {
      CharSequence localCharSequence = getTitle();
      paramInputMethodPreference = paramInputMethodPreference.getTitle();
      if (TextUtils.isEmpty(localCharSequence)) {
        return 1;
      }
      if (TextUtils.isEmpty(paramInputMethodPreference)) {
        return -1;
      }
      return paramCollator.compare(localCharSequence.toString(), paramInputMethodPreference.toString());
    }
    if (this.mHasPriorityInSorting) {
      return -1;
    }
    return 1;
  }
  
  public InputMethodInfo getInputMethodInfo()
  {
    return this.mImi;
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (!isImeEnabler()) {
      return false;
    }
    if (isChecked())
    {
      setCheckedInternal(false);
      return false;
    }
    if (InputMethodUtils.isSystemIme(this.mImi))
    {
      if (this.mImi.getServiceInfo().directBootAware)
      {
        setCheckedInternal(true);
        return false;
      }
      showDirectBootWarnDialog();
      return false;
    }
    showSecurityWarnDialog();
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (isImeEnabler()) {
      return true;
    }
    paramPreference = getContext();
    try
    {
      Intent localIntent = getIntent();
      if (localIntent != null) {
        paramPreference.startActivity(localIntent);
      }
      return true;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.d(TAG, "IME's Settings Activity Not Found", localActivityNotFoundException);
      Toast.makeText(paramPreference, paramPreference.getString(2131692240, new Object[] { this.mImi.loadLabel(paramPreference.getPackageManager()) }), 1).show();
    }
    return true;
  }
  
  void updatePreferenceViews()
  {
    if ((this.mInputMethodSettingValues.isAlwaysCheckedIme(this.mImi, getContext())) && (isImeEnabler()))
    {
      setDisabledByAdmin(null);
      setEnabled(false);
    }
    for (;;)
    {
      setChecked(this.mInputMethodSettingValues.isEnabledImi(this.mImi));
      if (!isDisabledByAdmin()) {
        setSummary(getSummaryString());
      }
      return;
      if (!this.mIsAllowedByOrganization) {
        setDisabledByAdmin(RestrictedLockUtils.checkIfInputMethodDisallowed(getContext(), this.mImi.getPackageName(), UserHandle.myUserId()));
      } else {
        setEnabled(true);
      }
    }
  }
  
  static abstract interface OnSavePreferenceListener
  {
    public abstract void onSaveInputMethodPreference(InputMethodPreference paramInputMethodPreference);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\inputmethod\InputMethodPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
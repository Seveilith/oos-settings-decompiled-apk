package com.android.settings.fuelgauge;

import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.TextView;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.oneplus.settings.SettingsBaseApplication;

public class HighPowerDetail
  extends DialogFragment
  implements DialogInterface.OnClickListener, View.OnClickListener
{
  private static final String ARG_DEFAULT_ON = "default_on";
  private final PowerWhitelistBackend mBackend = PowerWhitelistBackend.getInstance();
  private boolean mDefaultOn;
  private boolean mIsEnabled;
  private CharSequence mLabel;
  private Checkable mOptionOff;
  private Checkable mOptionOn;
  private String mPackageName;
  
  public static CharSequence getSummary(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
  {
    return getSummary(paramContext, paramAppEntry.info.packageName);
  }
  
  public static CharSequence getSummary(Context paramContext, String paramString)
  {
    paramContext = PowerWhitelistBackend.getInstance();
    Application localApplication = SettingsBaseApplication.mApplication;
    int i;
    if (paramContext.isSysWhitelisted(paramString)) {
      i = 2131693463;
    }
    for (;;)
    {
      return localApplication.getString(i);
      if (paramContext.isWhitelisted(paramString)) {
        i = 2131693461;
      } else {
        i = 2131693462;
      }
    }
  }
  
  public static void show(Fragment paramFragment, String paramString, int paramInt, boolean paramBoolean)
  {
    HighPowerDetail localHighPowerDetail = new HighPowerDetail();
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramString);
    localBundle.putBoolean("default_on", paramBoolean);
    localHighPowerDetail.setArguments(localBundle);
    localHighPowerDetail.setTargetFragment(paramFragment, paramInt);
    localHighPowerDetail.show(paramFragment.getFragmentManager(), HighPowerDetail.class.getSimpleName());
  }
  
  private void updateViews()
  {
    this.mOptionOn.setChecked(this.mIsEnabled);
    Checkable localCheckable = this.mOptionOff;
    if (this.mIsEnabled) {}
    for (boolean bool = false;; bool = true)
    {
      localCheckable.setChecked(bool);
      return;
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      boolean bool = this.mIsEnabled;
      if (bool != this.mBackend.isWhitelisted(this.mPackageName))
      {
        if (!bool) {
          break label41;
        }
        this.mBackend.addApp(this.mPackageName);
      }
    }
    return;
    label41:
    this.mBackend.removeApp(this.mPackageName);
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mOptionOn)
    {
      this.mIsEnabled = true;
      updateViews();
    }
    while (paramView != this.mOptionOff) {
      return;
    }
    this.mIsEnabled = false;
    updateViews();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPackageName = getArguments().getString("package");
    paramBundle = getContext().getPackageManager();
    try
    {
      this.mLabel = paramBundle.getApplicationInfo(this.mPackageName, 0).loadLabel(paramBundle);
      this.mDefaultOn = getArguments().getBoolean("default_on");
      if (!this.mDefaultOn)
      {
        bool = this.mBackend.isWhitelisted(this.mPackageName);
        this.mIsEnabled = bool;
        return;
      }
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      for (;;)
      {
        this.mLabel = this.mPackageName;
        continue;
        boolean bool = true;
      }
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    paramBundle = new AlertDialog.Builder(getContext()).setTitle(this.mLabel).setNegativeButton(2131690993, null).setView(2130968730);
    if (!this.mBackend.isSysWhitelisted(this.mPackageName)) {
      paramBundle.setPositiveButton(2131690998, this);
    }
    return paramBundle.create();
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    super.onDismiss(paramDialogInterface);
    paramDialogInterface = getTargetFragment();
    if (paramDialogInterface != null) {
      paramDialogInterface.onActivityResult(getTargetRequestCode(), 0, null);
    }
  }
  
  public void onStart()
  {
    super.onStart();
    this.mOptionOn = setup(getDialog().findViewById(2131362176), true);
    this.mOptionOff = setup(getDialog().findViewById(2131362175), false);
    updateViews();
  }
  
  public Checkable setup(View paramView, boolean paramBoolean)
  {
    TextView localTextView = (TextView)paramView.findViewById(16908310);
    if (paramBoolean)
    {
      i = 2131693523;
      localTextView.setText(i);
      localTextView = (TextView)paramView.findViewById(16908304);
      if (!paramBoolean) {
        break label94;
      }
    }
    label94:
    for (int i = 2131693525;; i = 2131693526)
    {
      localTextView.setText(i);
      paramView.setClickable(true);
      paramView.setOnClickListener(this);
      if ((!paramBoolean) && (this.mBackend.isSysWhitelisted(this.mPackageName))) {
        paramView.setEnabled(false);
      }
      return (Checkable)paramView;
      i = 2131693524;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\HighPowerDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.oneplus.settings.backgroundoptimize;

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

public class BgOptimizeDetail
  extends DialogFragment
  implements DialogInterface.OnClickListener, View.OnClickListener
{
  private static final String ARG_DEFAULT_ON = "default_on";
  private boolean mCurrentOptimized;
  private CharSequence mLabel;
  private Checkable mOptionNoOptimze;
  private Checkable mOptionOptimze;
  private String mPackageName;
  
  public static CharSequence getSummary(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
  {
    return getSummary(paramContext, paramAppEntry.info.packageName);
  }
  
  public static CharSequence getSummary(Context paramContext, String paramString)
  {
    Application localApplication = SettingsBaseApplication.mApplication;
    if (BgOActivityManager.getInstance(paramContext).getAppControlMode(paramString, 0) == 0) {}
    for (int i = 2131693524;; i = 2131693523) {
      return localApplication.getString(i);
    }
  }
  
  public static void show(Fragment paramFragment, String paramString, int paramInt, boolean paramBoolean)
  {
    BgOptimizeDetail localBgOptimizeDetail = new BgOptimizeDetail();
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramString);
    localBgOptimizeDetail.setArguments(localBundle);
    localBgOptimizeDetail.setTargetFragment(paramFragment, paramInt);
    localBgOptimizeDetail.show(paramFragment.getFragmentManager(), BgOptimizeDetail.class.getSimpleName());
  }
  
  private void updateViews()
  {
    this.mOptionOptimze.setChecked(this.mCurrentOptimized);
    Checkable localCheckable = this.mOptionNoOptimze;
    if (this.mCurrentOptimized) {}
    for (boolean bool = false;; bool = true)
    {
      localCheckable.setChecked(bool);
      return;
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    String str;
    if (paramInt == -1)
    {
      paramDialogInterface = BgOActivityManager.getInstance(getContext());
      str = this.mPackageName;
      if (!this.mCurrentOptimized) {
        break label36;
      }
    }
    label36:
    for (paramInt = 0;; paramInt = 1)
    {
      paramDialogInterface.setAppControlMode(str, 0, paramInt);
      return;
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mOptionOptimze) {
      this.mCurrentOptimized = true;
    }
    for (;;)
    {
      updateViews();
      return;
      if (paramView == this.mOptionNoOptimze) {
        this.mCurrentOptimized = false;
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool = false;
    super.onCreate(paramBundle);
    this.mPackageName = getArguments().getString("package");
    paramBundle = getContext().getPackageManager();
    try
    {
      this.mLabel = paramBundle.getApplicationInfo(this.mPackageName, 0).loadLabel(paramBundle);
      if (BgOActivityManager.getInstance(getContext()).getAppControlMode(this.mPackageName, 0) == 0) {
        bool = true;
      }
      this.mCurrentOptimized = bool;
      return;
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      for (;;)
      {
        this.mLabel = this.mPackageName;
      }
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new AlertDialog.Builder(getContext()).setTitle(this.mLabel).setNegativeButton(2131690993, null).setPositiveButton(2131690998, this).setView(2130968730).create();
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
    this.mOptionNoOptimze = setup(getDialog().findViewById(2131362176), true);
    this.mOptionOptimze = setup(getDialog().findViewById(2131362175), false);
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
        break label69;
      }
    }
    label69:
    for (int i = 2131690436;; i = 2131690435)
    {
      localTextView.setText(i);
      paramView.setClickable(true);
      paramView.setOnClickListener(this);
      return (Checkable)paramView;
      i = 2131693524;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\backgroundoptimize\BgOptimizeDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
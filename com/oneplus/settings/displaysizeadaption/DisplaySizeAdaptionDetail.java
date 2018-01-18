package com.oneplus.settings.displaysizeadaption;

import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.TextView;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.oneplus.settings.SettingsBaseApplication;

public class DisplaySizeAdaptionDetail
  extends DialogFragment
  implements DialogInterface.OnClickListener, View.OnClickListener
{
  private static final String ARG_DEFAULT_ON = "default_on";
  private static DisplaySizeAdaptiongeManager mManager;
  private AppOpsManager mAppOpsManager;
  private Context mContext;
  private Checkable mFullScreen;
  private boolean mFullScreenSelected;
  private CharSequence mLabel;
  private Checkable mOriginalSize;
  private String mPackageName;
  private int mUid;
  
  public static CharSequence getSummary(Context paramContext, ApplicationsState.AppEntry paramAppEntry)
  {
    return getSummary(paramContext, paramAppEntry.info.packageName);
  }
  
  public static CharSequence getSummary(Context paramContext, String paramString)
  {
    Application localApplication = SettingsBaseApplication.mApplication;
    if (!DisplaySizeAdaptiongeManager.getInstance(paramContext).isOriginalSizeApp(paramString)) {}
    for (int i = 2131690518;; i = 2131690519) {
      return localApplication.getString(i);
    }
  }
  
  public static void show(Fragment paramFragment, String paramString, int paramInt, boolean paramBoolean)
  {
    DisplaySizeAdaptionDetail localDisplaySizeAdaptionDetail = new DisplaySizeAdaptionDetail();
    Bundle localBundle = new Bundle();
    localBundle.putString("package", paramString);
    localDisplaySizeAdaptionDetail.setArguments(localBundle);
    localDisplaySizeAdaptionDetail.setTargetFragment(paramFragment, paramInt);
    localDisplaySizeAdaptionDetail.show(paramFragment.getFragmentManager(), DisplaySizeAdaptionDetail.class.getSimpleName());
  }
  
  private void updateViews()
  {
    this.mFullScreen.setChecked(this.mFullScreenSelected);
    Checkable localCheckable = this.mOriginalSize;
    if (this.mFullScreenSelected) {}
    for (boolean bool = false;; bool = true)
    {
      localCheckable.setChecked(bool);
      return;
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1) {
      mManager.setClassApp(this.mUid, this.mPackageName, this.mFullScreenSelected);
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mFullScreen) {
      this.mFullScreenSelected = true;
    }
    for (;;)
    {
      updateViews();
      return;
      if (paramView == this.mOriginalSize) {
        this.mFullScreenSelected = false;
      }
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    boolean bool = false;
    super.onCreate(paramBundle);
    this.mPackageName = getArguments().getString("package");
    paramBundle = getContext().getPackageManager();
    this.mContext = getContext();
    try
    {
      this.mLabel = paramBundle.getApplicationInfo(this.mPackageName, 0).loadLabel(paramBundle);
      this.mUid = paramBundle.getApplicationInfo(this.mPackageName, 0).uid;
      mManager = DisplaySizeAdaptiongeManager.getInstance(this.mContext);
      if (mManager.isOriginalSizeApp(this.mPackageName))
      {
        this.mFullScreenSelected = bool;
        return;
      }
    }
    catch (Exception paramBundle)
    {
      for (;;)
      {
        this.mLabel = this.mPackageName;
        continue;
        bool = true;
      }
    }
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return new AlertDialog.Builder(getContext()).setTitle(this.mLabel).setNegativeButton(2131690993, null).setPositiveButton(2131690998, this).setView(2130968797).create();
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
    this.mOriginalSize = setup(getDialog().findViewById(2131362176), false);
    this.mFullScreen = setup(getDialog().findViewById(2131362175), true);
    updateViews();
  }
  
  public Checkable setup(View paramView, boolean paramBoolean)
  {
    TextView localTextView = (TextView)paramView.findViewById(16908310);
    if (paramBoolean) {}
    for (int i = 2131690518;; i = 2131690519)
    {
      localTextView.setText(i);
      paramView.setClickable(true);
      paramView.setOnClickListener(this);
      return (Checkable)paramView;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\displaysizeadaption\DisplaySizeAdaptionDetail.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
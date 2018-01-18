package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class AllowBindAppWidgetActivity
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private CheckBox mAlwaysUse;
  private int mAppWidgetId;
  private AppWidgetManager mAppWidgetManager;
  private String mCallingPackage;
  private boolean mClicked;
  private ComponentName mComponentName;
  private UserHandle mProfile;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      setResult(0);
      if ((this.mAppWidgetId == -1) || (this.mComponentName == null) || (this.mCallingPackage == null)) {}
    }
    try
    {
      if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(this.mAppWidgetId, this.mProfile, this.mComponentName, null))
      {
        paramDialogInterface = new Intent();
        paramDialogInterface.putExtra("appWidgetId", this.mAppWidgetId);
        setResult(-1, paramDialogInterface);
      }
      boolean bool = this.mAlwaysUse.isChecked();
      if (bool != this.mAppWidgetManager.hasBindAppWidgetPermission(this.mCallingPackage)) {
        this.mAppWidgetManager.setBindAppWidgetPermission(this.mCallingPackage, bool);
      }
      finish();
      return;
    }
    catch (Exception paramDialogInterface)
    {
      for (;;)
      {
        Log.v("BIND_APPWIDGET", "Error binding widget with id " + this.mAppWidgetId + " and component " + this.mComponentName);
      }
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = getIntent();
    paramBundle = "";
    if (localObject != null) {}
    try
    {
      this.mAppWidgetId = ((Intent)localObject).getIntExtra("appWidgetId", -1);
      this.mProfile = ((UserHandle)((Intent)localObject).getParcelableExtra("appWidgetProviderProfile"));
      if (this.mProfile == null) {
        this.mProfile = Process.myUserHandle();
      }
      this.mComponentName = ((ComponentName)((Intent)localObject).getParcelableExtra("appWidgetProvider"));
      this.mCallingPackage = getCallingPackage();
      paramBundle = getPackageManager();
      paramBundle = paramBundle.getApplicationLabel(paramBundle.getApplicationInfo(this.mCallingPackage, 0));
      localObject = this.mAlertParams;
      ((AlertController.AlertParams)localObject).mTitle = getString(2131692304);
      ((AlertController.AlertParams)localObject).mMessage = getString(2131692305, new Object[] { paramBundle });
      ((AlertController.AlertParams)localObject).mPositiveButtonText = getString(2131690773);
      ((AlertController.AlertParams)localObject).mNegativeButtonText = getString(17039360);
      ((AlertController.AlertParams)localObject).mPositiveButtonListener = this;
      ((AlertController.AlertParams)localObject).mNegativeButtonListener = this;
      ((AlertController.AlertParams)localObject).mView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(17367089, null);
      this.mAlwaysUse = ((CheckBox)((AlertController.AlertParams)localObject).mView.findViewById(16909109));
      this.mAlwaysUse.setText(getString(2131692306, new Object[] { paramBundle }));
      this.mAlwaysUse.setPadding(this.mAlwaysUse.getPaddingLeft(), this.mAlwaysUse.getPaddingTop(), this.mAlwaysUse.getPaddingRight(), (int)(this.mAlwaysUse.getPaddingBottom() + getResources().getDimension(2131755470)));
      this.mAppWidgetManager = AppWidgetManager.getInstance(this);
      this.mAlwaysUse.setChecked(this.mAppWidgetManager.hasBindAppWidgetPermission(this.mCallingPackage, this.mProfile.getIdentifier()));
      setupAlert();
      return;
    }
    catch (Exception paramBundle)
    {
      this.mAppWidgetId = -1;
      this.mComponentName = null;
      this.mCallingPackage = null;
      Log.v("BIND_APPWIDGET", "Error getting parameters");
      setResult(0);
      finish();
    }
  }
  
  protected void onPause()
  {
    if ((!isDestroyed()) || (this.mClicked)) {}
    for (;;)
    {
      super.onDestroy();
      return;
      setResult(0);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AllowBindAppWidgetActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
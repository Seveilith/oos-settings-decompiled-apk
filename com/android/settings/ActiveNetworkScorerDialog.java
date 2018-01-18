package com.android.settings;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppManager;
import android.net.NetworkScorerAppManager.NetworkScorerAppData;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public final class ActiveNetworkScorerDialog
  extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private static final String TAG = "ActiveNetScorerDlg";
  private String mNewPackageName;
  
  private boolean buildDialog()
  {
    if (UserHandle.myUserId() != 0)
    {
      Log.i("ActiveNetScorerDlg", "Can only set scorer for owner/system user.");
      return false;
    }
    Object localObject = NetworkScorerAppManager.getScorer(this, this.mNewPackageName);
    if (localObject == null)
    {
      Log.e("ActiveNetScorerDlg", "New package " + this.mNewPackageName + " is not a valid scorer.");
      return false;
    }
    NetworkScorerAppManager.NetworkScorerAppData localNetworkScorerAppData = NetworkScorerAppManager.getActiveScorer(this);
    if ((localNetworkScorerAppData != null) && (TextUtils.equals(localNetworkScorerAppData.mPackageName, this.mNewPackageName)))
    {
      Log.i("ActiveNetScorerDlg", "New package " + this.mNewPackageName + " is already the active scorer.");
      setResult(-1);
      return false;
    }
    localObject = ((NetworkScorerAppManager.NetworkScorerAppData)localObject).mScorerName;
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131691936);
    if (localNetworkScorerAppData != null) {}
    for (localAlertParams.mMessage = getString(2131691937, new Object[] { localObject, localNetworkScorerAppData.mScorerName });; localAlertParams.mMessage = getString(2131691938, new Object[] { localObject }))
    {
      localAlertParams.mPositiveButtonText = getString(2131690771);
      localAlertParams.mNegativeButtonText = getString(2131690772);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonListener = this;
      setupAlert();
      return true;
    }
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    }
    do
    {
      return;
    } while (!((NetworkScoreManager)getSystemService("network_score")).setActiveScorer(this.mNewPackageName));
    setResult(-1);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mNewPackageName = getIntent().getStringExtra("packageName");
    if (!buildDialog()) {
      finish();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ActiveNetworkScorerDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
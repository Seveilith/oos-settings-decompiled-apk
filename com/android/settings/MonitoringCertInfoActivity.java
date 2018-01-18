package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.settingslib.RestrictedLockUtils;

public class MonitoringCertInfoActivity
  extends Activity
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
{
  private int mUserId;
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    paramDialogInterface = new Intent("com.android.settings.TRUSTED_CREDENTIALS_USER");
    paramDialogInterface.setFlags(335544320);
    paramDialogInterface.putExtra("ARG_SHOW_NEW_FOR_USER", this.mUserId);
    startActivity(paramDialogInterface);
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
    paramBundle = (DevicePolicyManager)getSystemService(DevicePolicyManager.class);
    int j = getIntent().getIntExtra("android.settings.extra.number_of_certificates", 1);
    int i;
    AlertDialog.Builder localBuilder;
    if (RestrictedLockUtils.getProfileOrDeviceOwner(this, this.mUserId) != null)
    {
      i = 2131951638;
      CharSequence localCharSequence = getResources().getQuantityText(i, j);
      setTitle(localCharSequence);
      localBuilder = new AlertDialog.Builder(this);
      localBuilder.setTitle(localCharSequence);
      localBuilder.setCancelable(true);
      localBuilder.setPositiveButton(getResources().getQuantityText(2131951638, j), this);
      localBuilder.setNeutralButton(2131690993, null);
      localBuilder.setOnDismissListener(this);
      if (paramBundle.getProfileOwnerAsUser(this.mUserId) == null) {
        break label186;
      }
      localBuilder.setMessage(getResources().getQuantityString(2131951637, j, new Object[] { paramBundle.getProfileOwnerNameAsUser(this.mUserId) }));
    }
    for (;;)
    {
      localBuilder.show();
      return;
      i = 2131951635;
      break;
      label186:
      if (paramBundle.getDeviceOwnerComponentOnCallingUser() != null)
      {
        localBuilder.setMessage(getResources().getQuantityString(2131951636, j, new Object[] { paramBundle.getDeviceOwnerNameOnAnyUser() }));
      }
      else
      {
        localBuilder.setIcon(17301624);
        localBuilder.setMessage(2131692909);
      }
    }
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    finish();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\MonitoringCertInfoActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
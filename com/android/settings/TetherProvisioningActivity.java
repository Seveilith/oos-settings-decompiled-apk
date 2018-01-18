package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.util.Log;
import java.util.List;

public class TetherProvisioningActivity
  extends Activity
{
  private static final boolean DEBUG = Log.isLoggable("TetherProvisioningAct", 3);
  private static final String EXTRA_TETHER_TYPE = "TETHER_TYPE";
  private static final int PROVISION_REQUEST = 0;
  private static final String TAG = "TetherProvisioningAct";
  private ResultReceiver mResultReceiver;
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 0)
    {
      if (DEBUG) {
        Log.d("TetherProvisioningAct", "Got result from app: " + paramInt2);
      }
      if (paramInt2 != -1) {
        break label63;
      }
    }
    label63:
    for (paramInt1 = 0;; paramInt1 = 11)
    {
      this.mResultReceiver.send(paramInt1, null);
      finish();
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mResultReceiver = ((ResultReceiver)getIntent().getParcelableExtra("extraProvisionCallback"));
    int i = getIntent().getIntExtra("extraAddTetherType", -1);
    paramBundle = getResources().getStringArray(17235994);
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClassName(paramBundle[0], paramBundle[1]);
    localIntent.putExtra("TETHER_TYPE", i);
    if (DEBUG) {
      Log.d("TetherProvisioningAct", "Starting provisioning app: " + paramBundle[0] + "." + paramBundle[1]);
    }
    if (getPackageManager().queryIntentActivities(localIntent, 65536).isEmpty())
    {
      Log.e("TetherProvisioningAct", "Provisioning app is configured, but not available.");
      this.mResultReceiver.send(11, null);
      finish();
      return;
    }
    startActivityForResultAsUser(localIntent, 0, UserHandle.CURRENT);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TetherProvisioningActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
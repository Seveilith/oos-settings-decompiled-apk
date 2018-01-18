package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;

public class RemoteBugreportActivity
  extends Activity
{
  private static final String TAG = "RemoteBugreportActivity";
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    int i = getIntent().getIntExtra("android.app.extra.bugreport_notification_type", -1);
    if (i == 2)
    {
      new AlertDialog.Builder(this).setMessage(2131693476).setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface paramAnonymousDialogInterface)
        {
          RemoteBugreportActivity.this.finish();
        }
      }).setNegativeButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          RemoteBugreportActivity.this.finish();
        }
      }).create().show();
      return;
    }
    if ((i == 1) || (i == 3))
    {
      paramBundle = new AlertDialog.Builder(this).setTitle(2131693473);
      if (i == 1) {}
      for (i = 2131693475;; i = 2131693474)
      {
        paramBundle.setMessage(i).setOnDismissListener(new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramAnonymousDialogInterface)
          {
            RemoteBugreportActivity.this.finish();
          }
        }).setNegativeButton(2131693478, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramAnonymousDialogInterface = new Intent("com.android.server.action.BUGREPORT_SHARING_DECLINED");
            RemoteBugreportActivity.this.sendBroadcastAsUser(paramAnonymousDialogInterface, UserHandle.SYSTEM, "android.permission.DUMP");
            RemoteBugreportActivity.this.finish();
          }
        }).setPositiveButton(2131693477, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            paramAnonymousDialogInterface = new Intent("com.android.server.action.BUGREPORT_SHARING_ACCEPTED");
            RemoteBugreportActivity.this.sendBroadcastAsUser(paramAnonymousDialogInterface, UserHandle.SYSTEM, "android.permission.DUMP");
            RemoteBugreportActivity.this.finish();
          }
        }).create().show();
        return;
      }
    }
    Log.e("RemoteBugreportActivity", "Incorrect dialog type, no dialog shown. Received: " + i);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RemoteBugreportActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
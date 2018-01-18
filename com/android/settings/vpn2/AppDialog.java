package com.android.settings.vpn2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.os.Bundle;

class AppDialog
  extends AlertDialog
  implements DialogInterface.OnClickListener
{
  private final String mLabel;
  private final Listener mListener;
  private final PackageInfo mPackageInfo;
  
  AppDialog(Context paramContext, Listener paramListener, PackageInfo paramPackageInfo, String paramString)
  {
    super(paramContext);
    this.mListener = paramListener;
    this.mPackageInfo = paramPackageInfo;
    this.mLabel = paramString;
  }
  
  protected void createButtons()
  {
    Context localContext = getContext();
    setButton(-2, localContext.getString(2131692860), this);
    setButton(-1, localContext.getString(2131692855), this);
  }
  
  public final PackageInfo getPackageInfo()
  {
    return this.mPackageInfo;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -2) {
      this.mListener.onForget(paramDialogInterface);
    }
    dismiss();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    setTitle(this.mLabel);
    setMessage(getContext().getString(2131692864, new Object[] { this.mPackageInfo.versionName }));
    createButtons();
    super.onCreate(paramBundle);
  }
  
  public static abstract interface Listener
  {
    public abstract void onForget(DialogInterface paramDialogInterface);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\AppDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
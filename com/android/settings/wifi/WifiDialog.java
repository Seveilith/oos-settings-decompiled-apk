package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.wifi.AccessPoint;

class WifiDialog
  extends AlertDialog
  implements WifiConfigUiBase, DialogInterface.OnClickListener
{
  private static final int BUTTON_FORGET = -3;
  private static final int BUTTON_SUBMIT = -1;
  private final AccessPoint mAccessPoint;
  private WifiConfigController mController;
  private boolean mHideSubmitButton;
  private final WifiDialogListener mListener;
  private final int mMode;
  private View mView;
  
  public WifiDialog(Context paramContext, WifiDialogListener paramWifiDialogListener, AccessPoint paramAccessPoint, int paramInt)
  {
    super(paramContext);
    this.mMode = paramInt;
    this.mListener = paramWifiDialogListener;
    this.mAccessPoint = paramAccessPoint;
    this.mHideSubmitButton = false;
  }
  
  public WifiDialog(Context paramContext, WifiDialogListener paramWifiDialogListener, AccessPoint paramAccessPoint, int paramInt, boolean paramBoolean)
  {
    this(paramContext, paramWifiDialogListener, paramAccessPoint, paramInt);
    this.mHideSubmitButton = paramBoolean;
  }
  
  public void dispatchSubmit()
  {
    if (this.mListener != null) {
      this.mListener.onSubmit(this);
    }
    dismiss();
  }
  
  public Button getCancelButton()
  {
    return getButton(-2);
  }
  
  public WifiConfigController getController()
  {
    return this.mController;
  }
  
  public Button getForgetButton()
  {
    return getButton(-3);
  }
  
  public int getMode()
  {
    return this.mMode;
  }
  
  public Button getSubmitButton()
  {
    return getButton(-1);
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (this.mListener != null) {}
    switch (paramInt)
    {
    case -2: 
    default: 
      return;
    case -1: 
      this.mListener.onSubmit(this);
      return;
    }
    if (WifiSettings.isEditabilityLockedDown(getContext(), this.mAccessPoint.getConfig()))
    {
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), RestrictedLockUtils.getDeviceOwner(getContext()));
      return;
    }
    this.mListener.onForget(this);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130969114, null);
    setView(this.mView);
    setInverseBackgroundForced(true);
    this.mController = new WifiConfigController(this, this.mView, this.mAccessPoint, this.mMode);
    super.onCreate(paramBundle);
    if (this.mHideSubmitButton) {
      this.mController.hideSubmitButton();
    }
    for (;;)
    {
      if (this.mAccessPoint == null) {
        this.mController.hideForgetButton();
      }
      return;
      this.mController.enableSubmitIfAppropriate();
    }
  }
  
  public void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    this.mController.updatePassword();
  }
  
  public void setCancelButton(CharSequence paramCharSequence)
  {
    setButton(-2, paramCharSequence, this);
  }
  
  public void setForgetButton(CharSequence paramCharSequence)
  {
    setButton(-3, paramCharSequence, this);
  }
  
  public void setSubmitButton(CharSequence paramCharSequence)
  {
    setButton(-1, paramCharSequence, this);
  }
  
  public static abstract interface WifiDialogListener
  {
    public abstract void onForget(WifiDialog paramWifiDialog);
    
    public abstract void onSubmit(WifiDialog paramWifiDialog);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
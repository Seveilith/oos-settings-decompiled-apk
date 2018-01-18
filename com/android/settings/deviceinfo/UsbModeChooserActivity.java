package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;

public class UsbModeChooserActivity
  extends Activity
{
  public static final int[] DEFAULT_MODES = { 0, 2, 4, 6, 8 };
  private UsbBackend mBackend;
  private AlertDialog mDialog;
  private BroadcastReceiver mDisconnectedReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.hardware.usb.action.USB_STATE".equals(paramAnonymousIntent.getAction()))
      {
        boolean bool1 = paramAnonymousIntent.getBooleanExtra("connected", false);
        boolean bool2 = paramAnonymousIntent.getBooleanExtra("host_connected", false);
        if ((!bool1) && (!bool2)) {}
      }
      else
      {
        return;
      }
      UsbModeChooserActivity.-get1(UsbModeChooserActivity.this).dismiss();
    }
  };
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
  private LayoutInflater mLayoutInflater;
  
  private static int getSummary(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 3: 
    case 5: 
    case 7: 
    default: 
      return 0;
    case 0: 
      return 2131693480;
    case 2: 
      return 2131693484;
    case 4: 
      return 2131693486;
    case 6: 
      return 2131693488;
    }
    return 2131693491;
  }
  
  private static int getTitle(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
    case 3: 
    case 5: 
    case 7: 
    default: 
      return 0;
    case 0: 
      return 2131693479;
    case 2: 
      return 2131693483;
    case 4: 
      return 2131693485;
    case 6: 
      return 2131693487;
    }
    return 2131693490;
  }
  
  private void inflateOption(final int paramInt, boolean paramBoolean1, LinearLayout paramLinearLayout, final boolean paramBoolean2)
  {
    boolean bool1 = SystemProperties.getBoolean("persist.sys.sim.activate", false);
    boolean bool2 = SystemProperties.getBoolean("persist.sys.usb.security", false);
    View localView = this.mLayoutInflater.inflate(2130968956, paramLinearLayout, false);
    TextView localTextView1 = (TextView)localView.findViewById(16908310);
    localTextView1.setText(getTitle(paramInt));
    TextView localTextView2 = (TextView)localView.findViewById(16908304);
    localTextView2.setText(getSummary(paramInt));
    if (paramBoolean2)
    {
      if (this.mEnforcedAdmin != null) {
        setDisabledByAdmin(localView, localTextView1, localTextView2);
      }
    }
    else
    {
      localView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((paramBoolean2) && (UsbModeChooserActivity.-get2(UsbModeChooserActivity.this) != null))
          {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(UsbModeChooserActivity.this, UsbModeChooserActivity.-get2(UsbModeChooserActivity.this));
            return;
          }
          if ((!SystemProperties.getBoolean("sys.debug.watchdog", false)) && (!ActivityManager.isUserAMonkey())) {
            UsbModeChooserActivity.-get0(UsbModeChooserActivity.this).setMode(paramInt);
          }
          UsbModeChooserActivity.-get1(UsbModeChooserActivity.this).dismiss();
          UsbModeChooserActivity.this.finish();
        }
      });
      ((Checkable)localView).setChecked(paramBoolean1);
      if ((!bool1) && (bool2)) {
        localView.setEnabled(paramBoolean1);
      }
      paramLinearLayout.addView(localView);
      return;
    }
  }
  
  private void setDisabledByAdmin(View paramView, TextView paramTextView1, TextView paramTextView2)
  {
    if (this.mEnforcedAdmin != null)
    {
      paramTextView1.setEnabled(false);
      paramTextView2.setEnabled(false);
      paramView.findViewById(2131362509).setVisibility(0);
      paramTextView1.getCompoundDrawablesRelative()[0].mutate().setColorFilter(getColor(2131492864), PorterDuff.Mode.MULTIPLY);
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mLayoutInflater = LayoutInflater.from(this);
    this.mDialog = new AlertDialog.Builder(this).setTitle(2131693489).setView(2130969093).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        UsbModeChooserActivity.this.finish();
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
    }).create();
    this.mDialog.show();
    paramBundle = (LinearLayout)this.mDialog.findViewById(2131362663);
    this.mEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(this, "no_usb_file_transfer", UserHandle.myUserId());
    this.mBackend = new UsbBackend(this);
    int j = this.mBackend.getCurrentMode();
    int i = 0;
    if (i < DEFAULT_MODES.length)
    {
      if ((!this.mBackend.isModeSupported(DEFAULT_MODES[i])) || (this.mBackend.isModeDisallowedBySystem(DEFAULT_MODES[i]))) {}
      while ((!getResources().getBoolean(2131558462)) && (8 == DEFAULT_MODES[i]))
      {
        i += 1;
        break;
      }
      int k = DEFAULT_MODES[i];
      if (j == DEFAULT_MODES[i]) {}
      for (boolean bool = true;; bool = false)
      {
        inflateOption(k, bool, paramBundle, this.mBackend.isModeDisallowed(DEFAULT_MODES[i]));
        break;
      }
    }
  }
  
  public void onStart()
  {
    super.onStart();
    IntentFilter localIntentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
    registerReceiver(this.mDisconnectedReceiver, localIntentFilter);
  }
  
  protected void onStop()
  {
    unregisterReceiver(this.mDisconnectedReceiver);
    super.onStop();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\UsbModeChooserActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
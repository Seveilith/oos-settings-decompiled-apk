package com.android.settings;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.internal.widget.LockPatternUtils;
import java.util.Locale;

public class CryptKeeperConfirm
  extends InstrumentedFragment
{
  private static final String TAG = "CryptKeeperConfirm";
  private View mContentView;
  private Button mFinalButton;
  private View.OnClickListener mFinalClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      boolean bool = true;
      if (Utils.isMonkeyRunning()) {
        return;
      }
      paramAnonymousView = new LockPatternUtils(CryptKeeperConfirm.this.getActivity());
      paramAnonymousView.setVisiblePatternEnabled(paramAnonymousView.isVisiblePatternEnabled(0), 0);
      if (paramAnonymousView.isOwnerInfoEnabled(0)) {
        paramAnonymousView.setOwnerInfo(paramAnonymousView.getOwnerInfo(0), 0);
      }
      if (Settings.System.getInt(CryptKeeperConfirm.this.getContext().getContentResolver(), "show_password", 1) != 0) {}
      for (;;)
      {
        paramAnonymousView.setVisiblePasswordEnabled(bool, 0);
        paramAnonymousView = new Intent(CryptKeeperConfirm.this.getActivity(), CryptKeeperConfirm.Blank.class);
        paramAnonymousView.putExtras(CryptKeeperConfirm.this.getArguments());
        CryptKeeperConfirm.this.startActivity(paramAnonymousView);
        try
        {
          IMountService.Stub.asInterface(ServiceManager.getService("mount")).setField("SystemLocale", Locale.getDefault().toLanguageTag());
          return;
        }
        catch (Exception paramAnonymousView)
        {
          Log.e("CryptKeeperConfirm", "Error storing locale for decryption UI", paramAnonymousView);
        }
        bool = false;
      }
    }
  };
  
  private void establishFinalConfirmationState()
  {
    this.mFinalButton = ((Button)this.mContentView.findViewById(2131362052));
    this.mFinalButton.setOnClickListener(this.mFinalClickListener);
  }
  
  protected int getMetricsCategory()
  {
    return 33;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mContentView = paramLayoutInflater.inflate(2130968654, null);
    establishFinalConfirmationState();
    return this.mContentView;
  }
  
  public static class Blank
    extends Activity
  {
    private Handler mHandler = new Handler();
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      setContentView(2130968653);
      if (Utils.isMonkeyRunning()) {
        finish();
      }
      ((StatusBarManager)getSystemService("statusbar")).disable(58130432);
      this.mHandler.postDelayed(new Runnable()
      {
        public void run()
        {
          Object localObject = ServiceManager.getService("mount");
          if (localObject == null)
          {
            Log.e("CryptKeeper", "Failed to find the mount service");
            CryptKeeperConfirm.Blank.this.finish();
            return;
          }
          localObject = IMountService.Stub.asInterface((IBinder)localObject);
          try
          {
            Bundle localBundle = CryptKeeperConfirm.Blank.this.getIntent().getExtras();
            ((IMountService)localObject).encryptStorage(localBundle.getInt("type", -1), localBundle.getString("password"));
            return;
          }
          catch (Exception localException)
          {
            Log.e("CryptKeeper", "Error while encrypting...", localException);
          }
        }
      }, 700L);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\CryptKeeperConfirm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
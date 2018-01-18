package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.lib.widget.button.OPButton;
import com.oneplus.settings.utils.OPUtils;

public class SetFullBackupPassword
  extends Activity
{
  static final String TAG = "SetFullBackupPassword";
  IBackupManager mBackupManager;
  View.OnClickListener mButtonListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (paramAnonymousView == SetFullBackupPassword.this.mSet)
      {
        paramAnonymousView = SetFullBackupPassword.this.mCurrentPw.getText().toString();
        String str = SetFullBackupPassword.this.mNewPw.getText().toString();
        if (!str.equals(SetFullBackupPassword.this.mConfirmNewPw.getText().toString()))
        {
          Log.i("SetFullBackupPassword", "password mismatch");
          Toast.makeText(SetFullBackupPassword.this, 2131689747, 1).show();
          return;
        }
        if (SetFullBackupPassword.-wrap0(SetFullBackupPassword.this, paramAnonymousView, str))
        {
          Log.i("SetFullBackupPassword", "password set successfully");
          Toast.makeText(SetFullBackupPassword.this, 2131689746, 1).show();
          SetFullBackupPassword.this.finish();
          return;
        }
        Log.i("SetFullBackupPassword", "failure; password mismatch?");
        Toast.makeText(SetFullBackupPassword.this, 2131689748, 1).show();
        return;
      }
      if (paramAnonymousView == SetFullBackupPassword.this.mCancel)
      {
        SetFullBackupPassword.this.finish();
        return;
      }
      Log.w("SetFullBackupPassword", "Click on unknown view");
    }
  };
  OPButton mCancel;
  TextView mConfirmNewPw;
  TextView mCurrentPw;
  TextView mNewPw;
  OPButton mSet;
  
  private boolean setBackupPassword(String paramString1, String paramString2)
  {
    try
    {
      boolean bool = this.mBackupManager.setBackupPassword(paramString1, paramString2);
      return bool;
    }
    catch (RemoteException paramString1)
    {
      Log.e("SetFullBackupPassword", "Unable to communicate with backup manager");
    }
    return false;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    setContentView(2130968982);
    paramBundle = getActionBar();
    paramBundle.setDisplayHomeAsUpEnabled(true);
    paramBundle.setTitle(2131689743);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    this.mCurrentPw = ((TextView)findViewById(2131362537));
    this.mNewPw = ((TextView)findViewById(2131362539));
    this.mConfirmNewPw = ((TextView)findViewById(2131362541));
    this.mCancel = ((OPButton)findViewById(2131362542));
    this.mSet = ((OPButton)findViewById(2131362543));
    this.mCancel.setOnClickListener(this.mButtonListener);
    this.mSet.setOnClickListener(this.mButtonListener);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 16908332)
    {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetFullBackupPassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
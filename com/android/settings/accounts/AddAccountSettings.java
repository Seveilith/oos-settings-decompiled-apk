package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.Settings.ChooseAccountActivity;
import com.android.settings.Utils;
import java.io.IOException;

public class AddAccountSettings
  extends Activity
{
  private static final int ADD_ACCOUNT_REQUEST = 2;
  private static final int CHOOSE_ACCOUNT_REQUEST = 1;
  static final String EXTRA_HAS_MULTIPLE_USERS = "hasMultipleUsers";
  static final String EXTRA_SELECTED_ACCOUNT = "selected_account";
  private static final String KEY_ADD_CALLED = "AddAccountCalled";
  private static final String KEY_CALLER_IDENTITY = "pendingIntent";
  private static final String SHOULD_NOT_RESOLVE = "SHOULDN'T RESOLVE!";
  private static final String TAG = "AccountSettings";
  private static final int UNLOCK_WORK_PROFILE_REQUEST = 3;
  private boolean mAddAccountCalled = false;
  private final AccountManagerCallback<Bundle> mCallback = new AccountManagerCallback()
  {
    public void run(AccountManagerFuture<Bundle> paramAnonymousAccountManagerFuture)
    {
      int i2 = 1;
      int i3 = 1;
      int i4 = 1;
      int i5 = 1;
      int i1 = 1;
      k = i2;
      m = i3;
      n = i4;
      i = i5;
      for (;;)
      {
        try
        {
          paramAnonymousAccountManagerFuture = (Bundle)paramAnonymousAccountManagerFuture.getResult();
          k = i2;
          m = i3;
          n = i4;
          i = i5;
          Intent localIntent = (Intent)paramAnonymousAccountManagerFuture.get("intent");
          if (localIntent != null)
          {
            i1 = 0;
            i2 = 0;
            i3 = 0;
            i4 = 0;
            j = 0;
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            Bundle localBundle = new Bundle();
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            localBundle.putParcelable("pendingIntent", AddAccountSettings.-get0(AddAccountSettings.this));
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            localBundle.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(AddAccountSettings.this));
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            localBundle.putParcelable("android.intent.extra.USER", AddAccountSettings.-get1(AddAccountSettings.this));
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            localIntent.putExtras(localBundle);
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            localIntent.addFlags(268435456);
            k = i1;
            m = i2;
            n = i3;
            i = i4;
            AddAccountSettings.this.startActivityForResultAsUser(localIntent, 2, AddAccountSettings.-get1(AddAccountSettings.this));
            k = j;
            m = j;
            n = j;
            i = j;
            if (Log.isLoggable("AccountSettings", 2))
            {
              k = j;
              m = j;
              n = j;
              i = j;
              Log.v("AccountSettings", "account added: " + paramAnonymousAccountManagerFuture);
            }
            return;
          }
        }
        catch (OperationCanceledException paramAnonymousAccountManagerFuture)
        {
          int j;
          i = k;
          if (!Log.isLoggable("AccountSettings", 2)) {
            continue;
          }
          i = k;
          Log.v("AccountSettings", "addAccount was canceled");
          return;
        }
        catch (AuthenticatorException paramAnonymousAccountManagerFuture)
        {
          i = m;
          if (!Log.isLoggable("AccountSettings", 2)) {
            continue;
          }
          i = m;
          Log.v("AccountSettings", "addAccount failed: " + paramAnonymousAccountManagerFuture);
          return;
        }
        catch (IOException paramAnonymousAccountManagerFuture)
        {
          i = n;
          if (!Log.isLoggable("AccountSettings", 2)) {
            continue;
          }
          i = n;
          Log.v("AccountSettings", "addAccount failed: " + paramAnonymousAccountManagerFuture);
          return;
        }
        finally
        {
          if (i == 0) {
            continue;
          }
          AddAccountSettings.this.finish();
        }
        k = i2;
        m = i3;
        n = i4;
        i = i5;
        AddAccountSettings.this.setResult(-1);
        j = i1;
        k = i2;
        m = i3;
        n = i4;
        i = i5;
        if (AddAccountSettings.-get0(AddAccountSettings.this) != null)
        {
          k = i2;
          m = i3;
          n = i4;
          i = i5;
          AddAccountSettings.-get0(AddAccountSettings.this).cancel();
          k = i2;
          m = i3;
          n = i4;
          i = i5;
          AddAccountSettings.-set0(AddAccountSettings.this, null);
          j = i1;
        }
      }
    }
  };
  private PendingIntent mPendingIntent;
  private UserHandle mUserHandle;
  
  private void addAccount(String paramString)
  {
    Bundle localBundle = new Bundle();
    Intent localIntent = new Intent();
    localIntent.setComponent(new ComponentName("SHOULDN'T RESOLVE!", "SHOULDN'T RESOLVE!"));
    localIntent.setAction("SHOULDN'T RESOLVE!");
    localIntent.addCategory("SHOULDN'T RESOLVE!");
    this.mPendingIntent = PendingIntent.getBroadcast(this, 0, localIntent, 0);
    localBundle.putParcelable("pendingIntent", this.mPendingIntent);
    localBundle.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(this));
    AccountManager.get(this).addAccountAsUser(paramString, null, null, localBundle, null, this.mCallback, null, this.mUserHandle);
    this.mAddAccountCalled = true;
  }
  
  private void requestChooseAccount()
  {
    String[] arrayOfString1 = getIntent().getStringArrayExtra("authorities");
    String[] arrayOfString2 = getIntent().getStringArrayExtra("account_types");
    Intent localIntent = new Intent(this, Settings.ChooseAccountActivity.class);
    if (arrayOfString1 != null) {
      localIntent.putExtra("authorities", arrayOfString1);
    }
    if (arrayOfString2 != null) {
      localIntent.putExtra("account_types", arrayOfString2);
    }
    localIntent.putExtra("android.intent.extra.USER", this.mUserHandle);
    startActivityForResult(localIntent, 1);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    default: 
      return;
    case 3: 
      if (paramInt2 == -1)
      {
        requestChooseAccount();
        return;
      }
      finish();
      return;
    case 1: 
      if (paramInt2 == 0)
      {
        if (paramIntent != null) {
          startActivityAsUser(paramIntent, this.mUserHandle);
        }
        setResult(paramInt2);
        finish();
        return;
      }
      addAccount(paramIntent.getStringExtra("selected_account"));
      return;
    }
    setResult(paramInt2);
    if (this.mPendingIntent != null)
    {
      this.mPendingIntent.cancel();
      this.mPendingIntent = null;
    }
    finish();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      this.mAddAccountCalled = paramBundle.getBoolean("AddAccountCalled");
      if (Log.isLoggable("AccountSettings", 2)) {
        Log.v("AccountSettings", "restored");
      }
    }
    paramBundle = (UserManager)getSystemService("user");
    this.mUserHandle = Utils.getSecureTargetUser(getActivityToken(), paramBundle, null, getIntent().getExtras());
    if (paramBundle.hasUserRestriction("no_modify_accounts", this.mUserHandle))
    {
      Toast.makeText(this, 2131692938, 1).show();
      finish();
      return;
    }
    if (this.mAddAccountCalled)
    {
      finish();
      return;
    }
    if (Utils.startQuietModeDialogIfNecessary(this, paramBundle, this.mUserHandle.getIdentifier()))
    {
      finish();
      return;
    }
    if (paramBundle.isUserUnlocked(this.mUserHandle)) {
      requestChooseAccount();
    }
    for (;;)
    {
      getActionBar().setHomeButtonEnabled(true);
      return;
      if (!new ChooseLockSettingsHelper(this).launchConfirmationActivity(3, getString(2131691150), false, this.mUserHandle.getIdentifier())) {
        requestChooseAccount();
      }
    }
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
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("AddAccountCalled", this.mAddAccountCalled);
    if (Log.isLoggable("AccountSettings", 2)) {
      Log.v("AccountSettings", "saved");
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\AddAccountSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
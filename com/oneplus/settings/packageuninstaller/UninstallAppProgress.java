package com.oneplus.settings.packageuninstaller;

import android.app.Activity;
import android.app.admin.IDevicePolicyManager;
import android.app.admin.IDevicePolicyManager.Stub;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver.Stub;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageDeleteObserver2.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Iterator;
import java.util.List;

public class UninstallAppProgress
  extends Activity
  implements View.OnClickListener
{
  private static final int QUICK_INSTALL_DELAY_MILLIS = 500;
  private static final int UNINSTALL_COMPLETE = 1;
  private static final int UNINSTALL_IS_SLOW = 2;
  private final String TAG = "UninstallAppProgress";
  private boolean mAllUsers;
  private ApplicationInfo mAppInfo;
  private IBinder mCallback;
  private Button mDeviceManagerButton;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if ((UninstallAppProgress.this.isFinishing()) || (UninstallAppProgress.this.isDestroyed())) {
        return;
      }
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 2: 
        UninstallAppProgress.this.initView();
        return;
      }
      UninstallAppProgress.-get4(UninstallAppProgress.this).removeMessages(2);
      if (paramAnonymousMessage.arg1 != 1) {
        UninstallAppProgress.this.initView();
      }
      UninstallAppProgress.-set0(UninstallAppProgress.this, paramAnonymousMessage.arg1);
      String str = (String)paramAnonymousMessage.obj;
      if (UninstallAppProgress.-get2(UninstallAppProgress.this) != null) {
        paramAnonymousMessage = IPackageDeleteObserver2.Stub.asInterface(UninstallAppProgress.-get2(UninstallAppProgress.this));
      }
      try
      {
        paramAnonymousMessage.onPackageDeleted(UninstallAppProgress.-get1(UninstallAppProgress.this).packageName, UninstallAppProgress.-get5(UninstallAppProgress.this), str);
        UninstallAppProgress.this.finish();
        return;
        Object localObject1;
        int i;
        if (UninstallAppProgress.this.getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false))
        {
          paramAnonymousMessage = new Intent();
          paramAnonymousMessage.putExtra("android.intent.extra.INSTALL_RESULT", UninstallAppProgress.-get5(UninstallAppProgress.this));
          localObject1 = UninstallAppProgress.this;
          if (UninstallAppProgress.-get5(UninstallAppProgress.this) == 1) {}
          for (i = -1;; i = 1)
          {
            ((UninstallAppProgress)localObject1).setResult(i, paramAnonymousMessage);
            UninstallAppProgress.this.finish();
            return;
          }
        }
        switch (paramAnonymousMessage.arg1)
        {
        case -3: 
        case -1: 
        case 0: 
        default: 
          Log.d("UninstallAppProgress", "Uninstall failed for " + str + " with code " + paramAnonymousMessage.arg1);
          paramAnonymousMessage = UninstallAppProgress.this.getString(2131690485);
        }
        for (;;)
        {
          UninstallAppProgress.this.findViewById(2131362258).setVisibility(8);
          UninstallAppProgress.this.findViewById(2131362259).setVisibility(0);
          ((TextView)UninstallAppProgress.this.findViewById(2131362260)).setText(paramAnonymousMessage);
          UninstallAppProgress.this.findViewById(2131362261).setVisibility(0);
          return;
          paramAnonymousMessage = UninstallAppProgress.this.getString(2131690484);
          Toast.makeText(UninstallAppProgress.this.getBaseContext(), paramAnonymousMessage, 1).show();
          UninstallAppProgress.this.setResultAndFinish(UninstallAppProgress.-get5(UninstallAppProgress.this));
          return;
          Object localObject2 = (UserManager)UninstallAppProgress.this.getSystemService("user");
          Object localObject3 = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"));
          i = UserHandle.myUserId();
          localObject1 = null;
          Object localObject4 = ((UserManager)localObject2).getUsers().iterator();
          for (;;)
          {
            paramAnonymousMessage = (Message)localObject1;
            if (((Iterator)localObject4).hasNext())
            {
              paramAnonymousMessage = (UserInfo)((Iterator)localObject4).next();
              if (UninstallAppProgress.-wrap0(UninstallAppProgress.this, (UserManager)localObject2, i, paramAnonymousMessage.id)) {}
            }
            else
            {
              try
              {
                boolean bool = ((IDevicePolicyManager)localObject3).packageHasActiveAdmins(str, paramAnonymousMessage.id);
                if (bool)
                {
                  if (paramAnonymousMessage != null) {
                    break label597;
                  }
                  Log.d("UninstallAppProgress", "Uninstall failed because " + str + " is a device admin");
                  UninstallAppProgress.-get3(UninstallAppProgress.this).setVisibility(0);
                  paramAnonymousMessage = UninstallAppProgress.this.getString(2131690486);
                }
              }
              catch (RemoteException paramAnonymousMessage)
              {
                Log.e("UninstallAppProgress", "Failed to talk to package manager", paramAnonymousMessage);
              }
            }
          }
          label597:
          Log.d("UninstallAppProgress", "Uninstall failed because " + str + " is a device admin of user " + paramAnonymousMessage);
          UninstallAppProgress.-get3(UninstallAppProgress.this).setVisibility(8);
          paramAnonymousMessage = String.format(UninstallAppProgress.this.getString(2131690487), new Object[] { paramAnonymousMessage.name });
          continue;
          localObject1 = (UserManager)UninstallAppProgress.this.getSystemService("user");
          localObject2 = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
          localObject3 = ((UserManager)localObject1).getUsers();
          int k = 55536;
          i = 0;
          label712:
          int j = k;
          if (i < ((List)localObject3).size()) {
            localObject4 = (UserInfo)((List)localObject3).get(i);
          }
          for (;;)
          {
            try
            {
              if (((IPackageManager)localObject2).getBlockUninstallForUser(str, ((UserInfo)localObject4).id))
              {
                j = ((UserInfo)localObject4).id;
                i = UserHandle.myUserId();
                if (!UninstallAppProgress.-wrap0(UninstallAppProgress.this, (UserManager)localObject1, i, j)) {
                  break label828;
                }
                UninstallAppProgress.-get3(UninstallAppProgress.this).setVisibility(0);
                if (j != 0) {
                  break label854;
                }
                paramAnonymousMessage = UninstallAppProgress.this.getString(2131690490);
              }
            }
            catch (RemoteException localRemoteException)
            {
              Log.e("UninstallAppProgress", "Failed to talk to package manager", localRemoteException);
              i += 1;
            }
            break label712;
            label828:
            UninstallAppProgress.-get3(UninstallAppProgress.this).setVisibility(8);
            UninstallAppProgress.-get6(UninstallAppProgress.this).setVisibility(0);
          }
          label854:
          if (j == 55536)
          {
            Log.d("UninstallAppProgress", "Uninstall failed for " + str + " with code " + paramAnonymousMessage.arg1 + " no blocking user");
            paramAnonymousMessage = UninstallAppProgress.this.getString(2131690485);
          }
          else if (UninstallAppProgress.-get0(UninstallAppProgress.this))
          {
            paramAnonymousMessage = UninstallAppProgress.this.getString(2131690488);
          }
          else
          {
            paramAnonymousMessage = UninstallAppProgress.this.getString(2131690489);
          }
        }
      }
      catch (RemoteException paramAnonymousMessage)
      {
        for (;;) {}
      }
    }
  };
  private boolean mIsViewInitialized;
  private Button mOkButton;
  private volatile int mResultCode = -1;
  private UserHandle mUser;
  private Button mUsersButton;
  
  private boolean isProfileOfOrSame(UserManager paramUserManager, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return true;
    }
    paramUserManager = paramUserManager.getProfileParent(paramInt2);
    return (paramUserManager != null) && (paramUserManager.id == paramInt1);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getKeyCode() == 4)
    {
      if (this.mResultCode == -1) {
        return true;
      }
      setResult(this.mResultCode);
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public void initView()
  {
    int i = 1;
    if (this.mIsViewInitialized) {
      return;
    }
    this.mIsViewInitialized = true;
    Object localObject = new TypedValue();
    getTheme().resolveAttribute(16842836, (TypedValue)localObject, true);
    if ((((TypedValue)localObject).type >= 28) && (((TypedValue)localObject).type <= 31))
    {
      getWindow().setBackgroundDrawable(new ColorDrawable(((TypedValue)localObject).data));
      getTheme().resolveAttribute(16843858, (TypedValue)localObject, true);
      getWindow().setNavigationBarColor(((TypedValue)localObject).data);
      getTheme().resolveAttribute(16843857, (TypedValue)localObject, true);
      getWindow().setStatusBarColor(((TypedValue)localObject).data);
      if ((this.mAppInfo.flags & 0x80) == 0) {
        break label287;
      }
      label131:
      if (i == 0) {
        break label292;
      }
    }
    label287:
    label292:
    for (i = 2131690492;; i = 2131690491)
    {
      setTitle(i);
      setContentView(2130968783);
      localObject = findViewById(2131361955);
      PackageUtil.initSnippetForInstalledApp(this, this.mAppInfo, (View)localObject);
      this.mDeviceManagerButton = ((Button)findViewById(2131362262));
      this.mUsersButton = ((Button)findViewById(2131362263));
      this.mDeviceManagerButton.setVisibility(8);
      this.mDeviceManagerButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Intent();
          paramAnonymousView.setClassName("com.android.settings", "com.android.settings.Settings$DeviceAdminSettingsActivity");
          paramAnonymousView.setFlags(1342177280);
          UninstallAppProgress.this.startActivity(paramAnonymousView);
          UninstallAppProgress.this.finish();
        }
      });
      this.mUsersButton.setVisibility(8);
      this.mUsersButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new Intent("android.settings.USER_SETTINGS");
          paramAnonymousView.setFlags(1342177280);
          UninstallAppProgress.this.startActivity(paramAnonymousView);
          UninstallAppProgress.this.finish();
        }
      });
      this.mOkButton = ((Button)findViewById(2131362264));
      this.mOkButton.setOnClickListener(this);
      return;
      getWindow().setBackgroundDrawable(getResources().getDrawable(((TypedValue)localObject).resourceId, getTheme()));
      break;
      i = 0;
      break label131;
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mOkButton)
    {
      Log.i("UninstallAppProgress", "Finished uninstalling pkg: " + this.mAppInfo.packageName);
      setResultAndFinish(this.mResultCode);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject = getIntent();
    this.mAppInfo = ((ApplicationInfo)((Intent)localObject).getParcelableExtra("com.android.packageinstaller.applicationInfo"));
    this.mCallback = ((Intent)localObject).getIBinderExtra("android.content.pm.extra.CALLBACK");
    if (paramBundle != null)
    {
      this.mResultCode = -1;
      if (this.mCallback != null) {
        paramBundle = IPackageDeleteObserver2.Stub.asInterface(this.mCallback);
      }
    }
    try
    {
      paramBundle.onPackageDeleted(this.mAppInfo.packageName, this.mResultCode, null);
      finish();
      return;
      setResultAndFinish(this.mResultCode);
      return;
      this.mAllUsers = ((Intent)localObject).getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
      String str;
      if ((!this.mAllUsers) || (UserManager.get(this).isAdminUser()))
      {
        this.mUser = ((UserHandle)((Intent)localObject).getParcelableExtra("android.intent.extra.USER"));
        if (this.mUser != null) {
          break label256;
        }
        this.mUser = Process.myUserHandle();
        paramBundle = new PackageDeleteObserver();
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().setStatusBarColor(0);
        getWindow().setNavigationBarColor(0);
        localObject = getPackageManager();
        str = this.mAppInfo.packageName;
        if (!this.mAllUsers) {
          break label330;
        }
      }
      label256:
      label330:
      for (int i = 2;; i = 0)
      {
        ((PackageManager)localObject).deletePackageAsUser(str, paramBundle, i, this.mUser.getIdentifier());
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 500L);
        return;
        throw new SecurityException("Only admin user can request uninstall for all users");
        if (((UserManager)getSystemService("user")).getUserProfiles().contains(this.mUser)) {
          break;
        }
        throw new SecurityException("User " + Process.myUserHandle() + " can't " + "request uninstall for user " + this.mUser);
      }
    }
    catch (RemoteException paramBundle)
    {
      for (;;) {}
    }
  }
  
  void setResultAndFinish(int paramInt)
  {
    setResult(paramInt);
    finish();
  }
  
  class PackageDeleteObserver
    extends IPackageDeleteObserver.Stub
  {
    PackageDeleteObserver() {}
    
    public void packageDeleted(String paramString, int paramInt)
    {
      Message localMessage = UninstallAppProgress.-get4(UninstallAppProgress.this).obtainMessage(1);
      localMessage.arg1 = paramInt;
      localMessage.obj = paramString;
      UninstallAppProgress.-get4(UninstallAppProgress.this).sendMessage(localMessage);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\packageuninstaller\UninstallAppProgress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
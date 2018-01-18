package com.oneplus.settings.multiapp;

import android.accounts.AccountManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.oneplus.lib.util.loading.DialogLoadingHelper;
import com.oneplus.lib.util.loading.LoadingHelper;
import com.oneplus.lib.util.loading.LoadingHelper.FinishShowCallback;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OPMultiAppListSettings
  extends BaseActivity
  implements AdapterView.OnItemClickListener
{
  public static final int FLAG_FROM = 67108864;
  public static final int INSTALL_MULTI_APP = 88;
  public static final String PROFILE_NAME = "Multi-App";
  public static final String TAG = "OPMultiAppListSettings";
  private static final ComponentName TEST_COMPONENT_NAME = ComponentName.unflattenFromString("com.oneplus.settings.multiapp/com.oneplus.settings.multiapp.OPBasicDeviceAdminReceiver");
  private static final String TEST_PACKAGE_NAME = "com.android.settings";
  private AccountManager mAccountManager;
  private List<OPAppModel> mAppList = new ArrayList();
  private ListView mAppListView;
  private AppOpsManager mAppOpsManager;
  private Context mContext;
  private AsyncTask<String, Void, Void> mCreateManagedProfileTask;
  private boolean mFirstLoad = true;
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      if ((OPMultiAppListSettings.-get8(OPMultiAppListSettings.this) != null) && (OPMultiAppListSettings.-get7(OPMultiAppListSettings.this) != null))
      {
        OPMultiAppListSettings.-get0(OPMultiAppListSettings.this).clear();
        OPMultiAppListSettings.-get0(OPMultiAppListSettings.this).addAll(OPMultiAppListSettings.-get7(OPMultiAppListSettings.this).getAppListByType(paramAnonymousMessage.what));
        OPMultiAppListSettings.-get8(OPMultiAppListSettings.this).setData(OPMultiAppListSettings.-get0(OPMultiAppListSettings.this));
        paramAnonymousMessage = OPMultiAppListSettings.this.findViewById(2131362268);
        if (OPMultiAppListSettings.-get0(OPMultiAppListSettings.this).isEmpty())
        {
          paramAnonymousMessage.setVisibility(0);
          OPMultiAppListSettings.-get1(OPMultiAppListSettings.this).setEmptyView(paramAnonymousMessage);
        }
      }
    }
  };
  private HandlerThread mHandlerThread;
  private boolean mHasTargetUser = false;
  private int mInitPosition;
  private Handler mInstallMultiApphandler;
  private IPackageManager mIpm;
  private boolean mIsInCreating = false;
  private boolean mIsWarnDialogShowing = false;
  private View mLoadingContainer;
  private LoadingHelper mLoadingHelper;
  private TextView mLoadingMessageView;
  private UserInfo mManagedProfileOrUserInfo;
  private boolean mNeedReloadData = false;
  private OPApplicationLoader mOPApplicationLoader;
  private OPMultiAppAdapter mOPMultiAppAdapter;
  private final BroadcastReceiver mPackageBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext == null) {
        return;
      }
      if ((TextUtils.equals(paramAnonymousContext, "android.intent.action.PACKAGE_REMOVED")) || (TextUtils.equals(paramAnonymousContext, "android.intent.action.PACKAGE_ADDED")))
      {
        paramAnonymousContext = paramAnonymousIntent.getData().getSchemeSpecificPart();
        Log.d("OPMultiAppListSettings", paramAnonymousContext + "has changed");
        OPMultiAppListSettings.-set3(OPMultiAppListSettings.this, true);
      }
    }
  };
  private PackageManager mPackageManager;
  private ProgressDialog mProgressDialog;
  private Handler mRefreshUIHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      int i;
      if (paramAnonymousMessage.what == 88)
      {
        i = paramAnonymousMessage.arg1;
        if (!OPMultiAppListSettings.-get8(OPMultiAppListSettings.this).getSelected(i)) {
          break label101;
        }
      }
      label101:
      for (boolean bool = false;; bool = true)
      {
        paramAnonymousMessage = (OPAppModel)OPMultiAppListSettings.-get1(OPMultiAppListSettings.this).getItemAtPosition(i);
        OPMultiAppListSettings.-get8(OPMultiAppListSettings.this).setSelected(i, bool);
        OPMultiAppListSettings.-get2(OPMultiAppListSettings.this).setMode(69, paramAnonymousMessage.getUid(), paramAnonymousMessage.getPkgName(), 0);
        Toast.makeText(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this), OPMultiAppListSettings.this.getEnabledString(paramAnonymousMessage), 0).show();
        return;
      }
    }
  };
  private UserManager mUserManager;
  private AlertDialog mWarnDialog;
  
  private void enableProfile()
  {
    int i = this.mManagedProfileOrUserInfo.id;
    this.mUserManager.setUserName(this.mManagedProfileOrUserInfo.id, "Multi-App");
    this.mUserManager.setUserEnabled(i);
    UserInfo localUserInfo = this.mUserManager.getProfileParent(i);
    Intent localIntent = new Intent("android.intent.action.MANAGED_PROFILE_ADDED");
    localIntent.putExtra("android.intent.extra.USER", new UserHandle(i));
    localIntent.addFlags(1342177280);
    this.mContext.sendBroadcastAsUser(localIntent, new UserHandle(localUserInfo.id));
  }
  
  private UserInfo getCorpUserInfo(Context paramContext)
  {
    int i = this.mUserManager.getUserHandle();
    paramContext = this.mUserManager.getUsers().iterator();
    while (paramContext.hasNext())
    {
      UserInfo localUserInfo1 = (UserInfo)paramContext.next();
      if (localUserInfo1.id == 999)
      {
        UserInfo localUserInfo2 = this.mUserManager.getProfileParent(localUserInfo1.id);
        if ((localUserInfo2 != null) && (localUserInfo2.id == i)) {
          return localUserInfo1;
        }
      }
    }
    return null;
  }
  
  private OPAppModel getModelWithPosition(int paramInt)
  {
    return (OPAppModel)this.mAppListView.getItemAtPosition(paramInt);
  }
  
  private void initFailed()
  {
    if (this.mLoadingHelper != null) {
      this.mLoadingHelper.finishShowProgress(new LoadingHelper.FinishShowCallback()
      {
        public void finish(boolean paramAnonymousBoolean)
        {
          List localList = OPMultiAppListSettings.-get11(OPMultiAppListSettings.this).getUsers();
          if ((localList != null) && (localList.size() >= 4))
          {
            Toast.makeText(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this), 2131690474, 0).show();
            return;
          }
          Toast.makeText(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this), 2131690550, 0).show();
        }
      });
    }
  }
  
  private void initView()
  {
    this.mProgressDialog = new ProgressDialog(this);
    this.mAppListView = ((ListView)findViewById(2131362267));
    OPUtils.setListDivider(SettingsBaseApplication.mApplication, this.mAppListView, 2130838238, 2130838236, 2131755334);
    this.mOPMultiAppAdapter = new OPMultiAppAdapter(this, this.mAppList);
    this.mAppListView.setAdapter(this.mOPMultiAppAdapter);
    this.mAppListView.setOnItemClickListener(this);
    this.mLoadingContainer = findViewById(2131362189);
    this.mLoadingMessageView = ((TextView)this.mLoadingContainer.findViewById(2131362190));
    this.mLoadingHelper = new LoadingHelper()
    {
      protected void hideProgree(Object paramAnonymousObject)
      {
        try
        {
          if ((OPMultiAppListSettings.-get9(OPMultiAppListSettings.this) != null) && (OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).isShowing())) {
            OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).dismiss();
          }
          return;
        }
        catch (Throwable paramAnonymousObject) {}
      }
      
      protected Object showProgree()
      {
        OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).show();
        OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).setCancelable(false);
        OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).setCanceledOnTouchOutside(false);
        OPMultiAppListSettings.-get9(OPMultiAppListSettings.this).setMessage(OPMultiAppListSettings.this.getString(2131690472));
        return OPMultiAppListSettings.-get9(OPMultiAppListSettings.this);
      }
    };
    this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
    startLoadData();
  }
  
  private void installMdmOnManagedProfile()
  {
    Log.e("OPMultiAppListSettings", "Installing mobile device management app on managed profile");
    for (;;)
    {
      try
      {
        int i = this.mIpm.installExistingPackageAsUser("com.android.settings", this.mManagedProfileOrUserInfo.id);
        switch (i)
        {
        case -111: 
          Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile. Unknown status: " + i);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the user is restricted");
      Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the package could not be found");
    }
  }
  
  private void installMultiApp(String paramString, OPAppModel paramOPAppModel)
  {
    Log.e("OPMultiAppListSettings", "installMultiApp" + paramString);
    if (this.mManagedProfileOrUserInfo == null) {
      return;
    }
    for (;;)
    {
      try
      {
        int i = this.mIpm.installExistingPackageAsUser(paramString, this.mManagedProfileOrUserInfo.id);
        switch (i)
        {
        case 1: 
          Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile. Unknown status: " + i);
          return;
        }
      }
      catch (RemoteException paramString)
      {
        Log.e("OPMultiAppListSettings", "This should not happen.", paramString);
        return;
      }
      Log.e("OPMultiAppListSettings", "installMultiApp" + paramString + "success");
      this.mAppOpsManager.setMode(69, paramOPAppModel.getUid(), paramOPAppModel.getPkgName(), 0);
      return;
      Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the user is restricted");
      Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the package could not be found");
    }
  }
  
  private void refreshList(int paramInt)
  {
    refreshList(paramInt, getModelWithPosition(paramInt));
  }
  
  private void refreshList(int paramInt, OPAppModel paramOPAppModel)
  {
    if (this.mOPMultiAppAdapter.getSelected(paramInt)) {}
    for (int i = 0; i != 0; i = 1)
    {
      paramOPAppModel = new ProgressDialog(this);
      Message localMessage = new Message();
      localMessage.what = (paramInt + 88);
      localMessage.arg1 = paramInt;
      localMessage.obj = paramOPAppModel;
      this.mInstallMultiApphandler.sendMessage(localMessage);
      return;
    }
    showWarnigDialog(paramInt);
  }
  
  private void refreshListByMovePackage(String paramString)
  {
    if (this.mOPMultiAppAdapter == null) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.mOPApplicationLoader.getAppListByType(69));
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      OPAppModel localOPAppModel = (OPAppModel)localIterator.next();
      if ((paramString != null) && (paramString.equals(localOPAppModel.getPkgName()))) {
        localArrayList.remove(localOPAppModel);
      }
    }
    this.mOPMultiAppAdapter.setData(localArrayList);
  }
  
  private void registerPackageReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    registerReceiver(this.mPackageBroadcastReceiver, localIntentFilter);
  }
  
  private void removeMultiApp(String paramString)
  {
    Log.e("OPMultiAppListSettings", "removeMultiApp ," + paramString);
    if (this.mManagedProfileOrUserInfo == null) {
      return;
    }
    IPackageManager localIPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    try
    {
      localIPackageManager.deletePackageAsUser(paramString, new PackageDeleteObserver(null), this.mManagedProfileOrUserInfo.id, 0);
      return;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  private void removeMultiAppByPosition(int paramInt)
  {
    OPAppModel localOPAppModel = getModelWithPosition(paramInt);
    this.mInstallMultiApphandler.removeMessages(paramInt + 88);
    removeMultiApp(localOPAppModel.getPkgName());
    this.mOPMultiAppAdapter.setSelected(paramInt, false);
    this.mAppOpsManager.setMode(69, localOPAppModel.getUid(), localOPAppModel.getPkgName(), 1);
  }
  
  private void removeProfile()
  {
    ((DevicePolicyManager)getSystemService("device_policy")).wipeData(0);
  }
  
  private void setMdmAsActiveAdmin()
  {
    ((DevicePolicyManager)getSystemService("device_policy")).setActiveAdmin(TEST_COMPONENT_NAME, true, this.mManagedProfileOrUserInfo.id);
  }
  
  private void setMdmAsManagedProfileOwner()
  {
    if (!((DevicePolicyManager)getSystemService("device_policy")).setProfileOwner(TEST_COMPONENT_NAME, "com.android.settings", this.mManagedProfileOrUserInfo.id)) {
      Log.e("OPMultiAppListSettings", "Could not set profile owner.");
    }
  }
  
  private void setUpUserOrProfile()
  {
    enableProfile();
    this.mHasTargetUser = true;
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    try
    {
      localIActivityManager.startUserInBackground(this.mManagedProfileOrUserInfo.id);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void setUserProvisioningState(int paramInt1, int paramInt2)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getSystemService("device_policy");
    Log.e("OPMultiAppListSettings", "Setting userProvisioningState for user " + paramInt2 + " to: " + paramInt1);
    localDevicePolicyManager.setUserProvisioningState(paramInt1, paramInt2);
  }
  
  private void showWarnigDialog(final int paramInt)
  {
    if ((isFinishing()) || (isDestroyed())) {
      return;
    }
    this.mWarnDialog = new AlertDialog.Builder(this).setMessage(2131690477).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPMultiAppListSettings.-set1(OPMultiAppListSettings.this, false);
        OPMultiAppListSettings.-wrap4(OPMultiAppListSettings.this, paramInt);
      }
    }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        OPMultiAppListSettings.-set1(OPMultiAppListSettings.this, false);
      }
    }).create();
    this.mWarnDialog.setCanceledOnTouchOutside(false);
    this.mWarnDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
    {
      public void onCancel(DialogInterface paramAnonymousDialogInterface)
      {
        OPMultiAppListSettings.-set1(OPMultiAppListSettings.this, false);
      }
    });
    if (!this.mIsWarnDialogShowing)
    {
      this.mWarnDialog.show();
      this.mIsWarnDialogShowing = true;
    }
  }
  
  private void startLoadData()
  {
    this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(69);
    this.mOPApplicationLoader.initData(3, this.mHandler);
  }
  
  public String getEnabledString(OPAppModel paramOPAppModel)
  {
    return String.format(getString(2131690473), new Object[] { paramOPAppModel.getLabel() });
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968789);
    this.mContext = this;
    this.mIpm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    this.mAccountManager = ((AccountManager)getSystemService("account"));
    this.mUserManager = ((UserManager)getSystemService("user"));
    this.mAppOpsManager = ((AppOpsManager)getSystemService("appops"));
    this.mPackageManager = getPackageManager();
    this.mOPApplicationLoader = new OPApplicationLoader(this, this.mAppOpsManager, this.mPackageManager);
    this.mHandlerThread = new HandlerThread("install-multiapp-handler-thread");
    this.mHandlerThread.start();
    this.mInstallMultiApphandler = new Handler(this.mHandlerThread.getLooper())
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        super.handleMessage(paramAnonymousMessage);
        if ((OPMultiAppListSettings.-get1(OPMultiAppListSettings.this) == null) || (OPMultiAppListSettings.-get8(OPMultiAppListSettings.this) == null)) {}
        while (OPMultiAppListSettings.-get2(OPMultiAppListSettings.this) == null) {
          return;
        }
        Object localObject = (ProgressDialog)paramAnonymousMessage.obj;
        ((ProgressDialog)localObject).setCancelable(false);
        ((ProgressDialog)localObject).setCanceledOnTouchOutside(false);
        ((ProgressDialog)localObject).setMessage(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this).getString(2131690472));
        localObject = new DialogLoadingHelper((Dialog)localObject);
        ((DialogLoadingHelper)localObject).beginShowProgress();
        final int i = paramAnonymousMessage.arg1;
        paramAnonymousMessage = (OPAppModel)OPMultiAppListSettings.-get1(OPMultiAppListSettings.this).getItemAtPosition(i);
        OPMultiAppListSettings.-wrap2(OPMultiAppListSettings.this, paramAnonymousMessage.getPkgName(), paramAnonymousMessage);
        ((DialogLoadingHelper)localObject).finishShowProgress(new LoadingHelper.FinishShowCallback()
        {
          public void finish(boolean paramAnonymous2Boolean)
          {
            Message localMessage = new Message();
            localMessage.what = 88;
            localMessage.arg1 = i;
            OPMultiAppListSettings.-get10(OPMultiAppListSettings.this).sendMessage(localMessage);
          }
        });
      }
    };
    initView();
    this.mManagedProfileOrUserInfo = getCorpUserInfo(this.mContext);
    registerPackageReceiver();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mPackageBroadcastReceiver);
    if (this.mHandlerThread != null) {
      this.mHandlerThread.quit();
    }
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (this.mIsInCreating) {
      return;
    }
    if (this.mManagedProfileOrUserInfo == null)
    {
      this.mIsInCreating = true;
      this.mInitPosition = paramInt;
      this.mCreateManagedProfileTask = new CreateManagedProfileTask(null);
      this.mLoadingHelper.beginShowProgress();
      this.mCreateManagedProfileTask.execute(new String[] { "Multi-App" });
      return;
    }
    refreshList(paramInt, getModelWithPosition(paramInt));
  }
  
  public void onPause()
  {
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    if ((!this.mNeedReloadData) || (this.mFirstLoad)) {}
    for (;;)
    {
      this.mFirstLoad = false;
      return;
      startLoadData();
      this.mNeedReloadData = false;
    }
  }
  
  private class CreateManagedProfileTask
    extends AsyncTask<String, Void, Void>
  {
    private CreateManagedProfileTask() {}
    
    protected Void doInBackground(String... paramVarArgs)
    {
      try
      {
        if (OPMultiAppListSettings.-get11(OPMultiAppListSettings.this).hasUserRestriction("no_add_user", UserHandle.OWNER)) {
          OPMultiAppListSettings.-get11(OPMultiAppListSettings.this).setUserRestriction("no_add_user", false, UserHandle.OWNER);
        }
        OPMultiAppListSettings.-set2(OPMultiAppListSettings.this, OPMultiAppListSettings.-get11(OPMultiAppListSettings.this).createProfileForUser(paramVarArgs[0], 67108960, Process.myUserHandle().getIdentifier()));
        Log.d("OPMultiAppListSettings", "Oneplus ManagedProfileOrUserInfo:" + OPMultiAppListSettings.-get6(OPMultiAppListSettings.this));
        if (OPMultiAppListSettings.-get6(OPMultiAppListSettings.this) != null)
        {
          new OPDeleteNonRequiredAppsTask(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this), "com.android.settings", 1, true, OPMultiAppListSettings.-get6(OPMultiAppListSettings.this).id, false, new OPDeleteNonRequiredAppsTask.Callback()
          {
            public void onError()
            {
              Log.e("OPMultiAppListSettings", "Delete non required apps task failed.", new Exception());
              Log.e("OPMultiAppListSettings", "onCreate----createProfileForUser--onError");
              OPMultiAppListSettings.-wrap1(OPMultiAppListSettings.this);
            }
            
            public void onSuccess()
            {
              try
              {
                OPMultiAppListSettings.-wrap5(OPMultiAppListSettings.this);
                if (OPMultiAppListSettings.-get5(OPMultiAppListSettings.this) != null) {
                  OPMultiAppListSettings.-get5(OPMultiAppListSettings.this).finishShowProgress(new LoadingHelper.FinishShowCallback()
                  {
                    public void finish(boolean paramAnonymous2Boolean)
                    {
                      OPMultiAppListSettings.-wrap3(OPMultiAppListSettings.this, OPMultiAppListSettings.-get4(OPMultiAppListSettings.this));
                      Toast.makeText(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this), OPMultiAppListSettings.this.getEnabledString(OPMultiAppListSettings.-wrap0(OPMultiAppListSettings.this, OPMultiAppListSettings.-get4(OPMultiAppListSettings.this))), 0).show();
                    }
                  });
                }
                Settings.Secure.putIntForUser(OPMultiAppListSettings.-get3(OPMultiAppListSettings.this).getContentResolver(), "user_setup_complete", 1, OPMultiAppListSettings.-get6(OPMultiAppListSettings.this).id);
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  Log.e("OPMultiAppListSettings", "Provisioning failed", localException);
                }
              }
            }
          }).run();
          Log.e("OPMultiAppListSettings", "onCreate----doInBackground-finish");
          OPMultiAppListSettings.-set0(OPMultiAppListSettings.this, false);
          return null;
        }
        OPMultiAppListSettings.-wrap1(OPMultiAppListSettings.this);
        return null;
      }
      catch (SecurityException paramVarArgs)
      {
        Log.e("OPMultiAppListSettings", "Exception" + paramVarArgs);
      }
      return null;
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      super.onPostExecute(paramVoid);
    }
  }
  
  private class PackageDeleteObserver
    extends IPackageDeleteObserver.Stub
  {
    private PackageDeleteObserver() {}
    
    public void packageDeleted(String paramString, int paramInt)
    {
      Log.e("OPMultiAppListSettings", "PackageDeleteObserver ," + paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\multiapp\OPMultiAppListSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
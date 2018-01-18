package com.android.settings.search;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.print.PrintManager;
import android.print.PrintServicesLoader;
import android.printservice.PrintServiceInfo;
import android.provider.UserDictionary.Words;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.content.PackageMonitor;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.print.PrintSettingsFragment;
import java.util.ArrayList;
import java.util.List;

public final class DynamicIndexableContentMonitor
  extends PackageMonitor
  implements InputManager.InputDeviceListener, LoaderManager.LoaderCallbacks<List<PrintServiceInfo>>
{
  private static final long DELAY_PROCESS_PACKAGE_CHANGE = 2000L;
  private static final int MSG_PACKAGE_AVAILABLE = 1;
  private static final int MSG_PACKAGE_UNAVAILABLE = 2;
  private static final String TAG = "DynamicIndexableContentMonitor";
  private final List<String> mAccessibilityServices = new ArrayList();
  private Context mContext;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        paramAnonymousMessage = (String)paramAnonymousMessage.obj;
        DynamicIndexableContentMonitor.-wrap0(DynamicIndexableContentMonitor.this, paramAnonymousMessage);
        return;
      }
      paramAnonymousMessage = (String)paramAnonymousMessage.obj;
      DynamicIndexableContentMonitor.-wrap1(DynamicIndexableContentMonitor.this, paramAnonymousMessage);
    }
  };
  private boolean mHasFeatureIme;
  private final List<String> mImeServices = new ArrayList();
  private boolean mRegistered;
  private final ContentObserver mUserDictionaryContentObserver = new UserDictionaryContentObserver(this.mHandler);
  
  private static Intent getAccessibilityServiceIntent(String paramString)
  {
    Intent localIntent = new Intent("android.accessibilityservice.AccessibilityService");
    localIntent.setPackage(paramString);
    return localIntent;
  }
  
  private static Intent getIMEServiceIntent(String paramString)
  {
    Intent localIntent = new Intent("android.view.InputMethod");
    localIntent.setPackage(paramString);
    return localIntent;
  }
  
  private void handlePackageAvailable(String paramString)
  {
    Object localObject;
    if (!this.mAccessibilityServices.contains(paramString))
    {
      localObject = getAccessibilityServiceIntent(paramString);
      localObject = this.mContext.getPackageManager().queryIntentServices((Intent)localObject, 0);
      if ((localObject != null) && (!((List)localObject).isEmpty())) {
        break label96;
      }
    }
    for (;;)
    {
      if ((this.mHasFeatureIme) && (!this.mImeServices.contains(paramString)))
      {
        localObject = getIMEServiceIntent(paramString);
        localObject = this.mContext.getPackageManager().queryIntentServices((Intent)localObject, 0);
        if ((localObject != null) && (!((List)localObject).isEmpty())) {
          break;
        }
      }
      return;
      label96:
      this.mAccessibilityServices.add(paramString);
      Index.getInstance(this.mContext).updateFromClassNameResource(AccessibilitySettings.class.getName(), false, true);
    }
    this.mImeServices.add(paramString);
    Index.getInstance(this.mContext).updateFromClassNameResource(InputMethodAndLanguageSettings.class.getName(), false, true);
  }
  
  private void handlePackageUnavailable(String paramString)
  {
    int i = this.mAccessibilityServices.indexOf(paramString);
    if (i >= 0)
    {
      this.mAccessibilityServices.remove(i);
      Index.getInstance(this.mContext).updateFromClassNameResource(AccessibilitySettings.class.getName(), true, true);
    }
    if (this.mHasFeatureIme)
    {
      i = this.mImeServices.indexOf(paramString);
      if (i >= 0)
      {
        this.mImeServices.remove(i);
        Index.getInstance(this.mContext).updateFromClassNameResource(InputMethodAndLanguageSettings.class.getName(), true, true);
      }
    }
  }
  
  private void postMessage(int paramInt, String paramString)
  {
    paramString = this.mHandler.obtainMessage(paramInt, paramString);
    this.mHandler.sendMessageDelayed(paramString, 2000L);
  }
  
  public Loader<List<PrintServiceInfo>> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    return new PrintServicesLoader((PrintManager)this.mContext.getSystemService("print"), this.mContext, 3);
  }
  
  public void onInputDeviceAdded(int paramInt)
  {
    Index.getInstance(this.mContext).updateFromClassNameResource(InputMethodAndLanguageSettings.class.getName(), false, true);
  }
  
  public void onInputDeviceChanged(int paramInt)
  {
    Index.getInstance(this.mContext).updateFromClassNameResource(InputMethodAndLanguageSettings.class.getName(), true, true);
  }
  
  public void onInputDeviceRemoved(int paramInt)
  {
    onInputDeviceChanged(paramInt);
  }
  
  public void onLoadFinished(Loader<List<PrintServiceInfo>> paramLoader, List<PrintServiceInfo> paramList)
  {
    Index.getInstance(this.mContext).updateFromClassNameResource(PrintSettingsFragment.class.getName(), false, true);
  }
  
  public void onLoaderReset(Loader<List<PrintServiceInfo>> paramLoader) {}
  
  public void onPackageAppeared(String paramString, int paramInt)
  {
    postMessage(1, paramString);
  }
  
  public void onPackageDisappeared(String paramString, int paramInt)
  {
    postMessage(2, paramString);
  }
  
  public void onPackageModified(String paramString)
  {
    super.onPackageModified(paramString);
    try
    {
      int i = this.mContext.getPackageManager().getApplicationEnabledSetting(paramString);
      if ((i == 0) || (i == 1))
      {
        postMessage(1, paramString);
        return;
      }
      postMessage(2, paramString);
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      Log.e("DynamicIndexableContentMonitor", "Package does not exist: " + paramString, localIllegalArgumentException);
    }
  }
  
  public void register(Activity paramActivity, int paramInt)
  {
    this.mContext = paramActivity;
    if (!((UserManager)this.mContext.getSystemService(UserManager.class)).isUserUnlocked())
    {
      Log.w("DynamicIndexableContentMonitor", "Skipping content monitoring because user is locked");
      this.mRegistered = false;
      return;
    }
    this.mRegistered = true;
    boolean bool = this.mContext.getPackageManager().hasSystemFeature("android.software.print");
    this.mHasFeatureIme = this.mContext.getPackageManager().hasSystemFeature("android.software.input_methods");
    List localList = ((AccessibilityManager)this.mContext.getSystemService("accessibility")).getInstalledAccessibilityServiceList();
    int j = localList.size();
    int i = 0;
    Object localObject;
    if (i < j)
    {
      localObject = ((AccessibilityServiceInfo)localList.get(i)).getResolveInfo();
      if ((localObject == null) || (((ResolveInfo)localObject).serviceInfo == null)) {}
      for (;;)
      {
        i += 1;
        break;
        this.mAccessibilityServices.add(((ResolveInfo)localObject).serviceInfo.packageName);
      }
    }
    if (bool) {
      paramActivity.getLoaderManager().initLoader(paramInt, null, this);
    }
    if (this.mHasFeatureIme)
    {
      localList = ((InputMethodManager)this.mContext.getSystemService("input_method")).getInputMethodList();
      i = localList.size();
      paramInt = 0;
      if (paramInt < i)
      {
        localObject = ((InputMethodInfo)localList.get(paramInt)).getServiceInfo();
        if (localObject == null) {}
        for (;;)
        {
          paramInt += 1;
          break;
          this.mImeServices.add(((ServiceInfo)localObject).packageName);
        }
      }
      this.mContext.getContentResolver().registerContentObserver(UserDictionary.Words.CONTENT_URI, true, this.mUserDictionaryContentObserver);
    }
    ((InputManager)paramActivity.getSystemService("input")).registerInputDeviceListener(this, this.mHandler);
    register(paramActivity, Looper.getMainLooper(), UserHandle.CURRENT, false);
  }
  
  public void unregister()
  {
    if (!this.mRegistered) {
      return;
    }
    super.unregister();
    ((InputManager)this.mContext.getSystemService("input")).unregisterInputDeviceListener(this);
    if (this.mHasFeatureIme) {
      this.mContext.getContentResolver().unregisterContentObserver(this.mUserDictionaryContentObserver);
    }
    this.mAccessibilityServices.clear();
    this.mImeServices.clear();
  }
  
  private final class UserDictionaryContentObserver
    extends ContentObserver
  {
    public UserDictionaryContentObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      if (UserDictionary.Words.CONTENT_URI.equals(paramUri)) {
        Index.getInstance(DynamicIndexableContentMonitor.-get0(DynamicIndexableContentMonitor.this)).updateFromClassNameResource(InputMethodAndLanguageSettings.class.getName(), true, true);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\search\DynamicIndexableContentMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
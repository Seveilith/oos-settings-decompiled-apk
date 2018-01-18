package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.ApduServiceInfo;
import android.nfc.cardemulation.CardEmulation;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import com.android.internal.content.PackageMonitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentBackend
{
  public static final String TAG = "Settings.PaymentBackend";
  private NfcAdapter mAdapter;
  private ArrayList<PaymentAppInfo> mAppInfos;
  private ArrayList<Callback> mCallbacks = new ArrayList();
  private CardEmulation mCardEmuManager;
  private final Context mContext;
  private PaymentAppInfo mDefaultAppInfo;
  private final Handler mHandler = new Handler()
  {
    public void dispatchMessage(Message paramAnonymousMessage)
    {
      PaymentBackend.this.refresh();
    }
  };
  private final PackageMonitor mSettingsPackageMonitor = new SettingsPackageMonitor(null);
  
  public PaymentBackend(Context paramContext)
  {
    this.mContext = paramContext;
    try
    {
      this.mAdapter = NfcAdapter.getDefaultAdapter(paramContext);
      this.mCardEmuManager = CardEmulation.getInstance(this.mAdapter);
      refresh();
      return;
    }
    catch (Exception paramContext) {}
  }
  
  public PaymentAppInfo getDefaultApp()
  {
    return this.mDefaultAppInfo;
  }
  
  ComponentName getDefaultPaymentApp()
  {
    String str = Settings.Secure.getString(this.mContext.getContentResolver(), "nfc_payment_default_component");
    if (str != null) {
      return ComponentName.unflattenFromString(str);
    }
    return null;
  }
  
  public List<PaymentAppInfo> getPaymentAppInfos()
  {
    return this.mAppInfos;
  }
  
  boolean isForegroundMode()
  {
    boolean bool = false;
    try
    {
      int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "nfc_payment_foreground");
      if (i != 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return false;
  }
  
  Drawable loadDrawableForPackage(String paramString, int paramInt)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    try
    {
      paramString = localPackageManager.getResourcesForApplication(paramString).getDrawable(paramInt);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      return null;
    }
    catch (Resources.NotFoundException paramString) {}
    return null;
  }
  
  void makeCallbacks()
  {
    Iterator localIterator = this.mCallbacks.iterator();
    while (localIterator.hasNext()) {
      ((Callback)localIterator.next()).onPaymentAppsChanged();
    }
  }
  
  public void onPause()
  {
    this.mSettingsPackageMonitor.unregister();
  }
  
  public void onResume()
  {
    this.mSettingsPackageMonitor.register(this.mContext, this.mContext.getMainLooper(), false);
  }
  
  public void refresh()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject2 = this.mCardEmuManager.getServices("payment");
    ArrayList localArrayList = new ArrayList();
    if (localObject2 == null)
    {
      makeCallbacks();
      return;
    }
    ComponentName localComponentName = getDefaultPaymentApp();
    Object localObject1 = null;
    Iterator localIterator = ((Iterable)localObject2).iterator();
    if (localIterator.hasNext())
    {
      ApduServiceInfo localApduServiceInfo = (ApduServiceInfo)localIterator.next();
      localObject2 = new PaymentAppInfo();
      ((PaymentAppInfo)localObject2).label = localApduServiceInfo.loadLabel(localPackageManager);
      if (((PaymentAppInfo)localObject2).label == null) {
        ((PaymentAppInfo)localObject2).label = localApduServiceInfo.loadAppLabel(localPackageManager);
      }
      ((PaymentAppInfo)localObject2).isDefault = localApduServiceInfo.getComponent().equals(localComponentName);
      if (((PaymentAppInfo)localObject2).isDefault) {
        localObject1 = localObject2;
      }
      ((PaymentAppInfo)localObject2).componentName = localApduServiceInfo.getComponent();
      String str = localApduServiceInfo.getSettingsActivityName();
      if (str != null) {}
      for (((PaymentAppInfo)localObject2).settingsComponent = new ComponentName(((PaymentAppInfo)localObject2).componentName.getPackageName(), str);; ((PaymentAppInfo)localObject2).settingsComponent = null)
      {
        ((PaymentAppInfo)localObject2).description = localApduServiceInfo.getDescription();
        ((PaymentAppInfo)localObject2).banner = localApduServiceInfo.loadBanner(localPackageManager);
        localArrayList.add(localObject2);
        break;
      }
    }
    this.mAppInfos = localArrayList;
    this.mDefaultAppInfo = ((PaymentAppInfo)localObject1);
    makeCallbacks();
  }
  
  public void registerCallback(Callback paramCallback)
  {
    this.mCallbacks.add(paramCallback);
  }
  
  public void setDefaultPaymentApp(ComponentName paramComponentName)
  {
    String str = null;
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramComponentName != null) {
      str = paramComponentName.flattenToString();
    }
    Settings.Secure.putString(localContentResolver, "nfc_payment_default_component", str);
    refresh();
  }
  
  void setForegroundMode(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "nfc_payment_foreground", i);
      return;
    }
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    this.mCallbacks.remove(paramCallback);
  }
  
  public static abstract interface Callback
  {
    public abstract void onPaymentAppsChanged();
  }
  
  public static class PaymentAppInfo
  {
    Drawable banner;
    public ComponentName componentName;
    CharSequence description;
    boolean isDefault;
    CharSequence label;
    public ComponentName settingsComponent;
  }
  
  private class SettingsPackageMonitor
    extends PackageMonitor
  {
    private SettingsPackageMonitor() {}
    
    public void onPackageAdded(String paramString, int paramInt)
    {
      PaymentBackend.-get0(PaymentBackend.this).obtainMessage().sendToTarget();
    }
    
    public void onPackageAppeared(String paramString, int paramInt)
    {
      PaymentBackend.-get0(PaymentBackend.this).obtainMessage().sendToTarget();
    }
    
    public void onPackageDisappeared(String paramString, int paramInt)
    {
      PaymentBackend.-get0(PaymentBackend.this).obtainMessage().sendToTarget();
    }
    
    public void onPackageRemoved(String paramString, int paramInt)
    {
      PaymentBackend.-get0(PaymentBackend.this).obtainMessage().sendToTarget();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\nfc\PaymentBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
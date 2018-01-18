package com.android.settings.location;

import android.R.styleable;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

class SettingsInjector
{
  private static final long INJECTED_STATUS_UPDATE_TIMEOUT_MILLIS = 1000L;
  static final String TAG = "SettingsInjector";
  private static final int WHAT_RECEIVED_STATUS = 2;
  private static final int WHAT_RELOAD = 1;
  private static final int WHAT_TIMEOUT = 3;
  private final Context mContext;
  private final Handler mHandler;
  private final Set<Setting> mSettings;
  
  public SettingsInjector(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSettings = new HashSet();
    this.mHandler = new StatusLoadingHandler(null);
  }
  
  private Preference addServiceSetting(List<Preference> paramList, InjectedSetting paramInjectedSetting)
  {
    Object localObject = this.mContext.getPackageManager();
    Drawable localDrawable = ((PackageManager)localObject).getUserBadgedIcon(((PackageManager)localObject).getDrawable(paramInjectedSetting.packageName, paramInjectedSetting.iconId, null), paramInjectedSetting.mUserHandle);
    CharSequence localCharSequence = ((PackageManager)localObject).getUserBadgedLabel(paramInjectedSetting.title, paramInjectedSetting.mUserHandle);
    localObject = localCharSequence;
    if (paramInjectedSetting.title.contentEquals(localCharSequence)) {
      localObject = null;
    }
    localObject = DimmableIZatIconPreference.newInstance(this.mContext, (CharSequence)localObject, paramInjectedSetting);
    ((Preference)localObject).setTitle(paramInjectedSetting.title);
    ((Preference)localObject).setSummary(null);
    ((Preference)localObject).setIcon(localDrawable);
    ((Preference)localObject).setOnPreferenceClickListener(new ServiceSettingClickedListener(paramInjectedSetting));
    paramList.add(localObject);
    return (Preference)localObject;
  }
  
  private List<InjectedSetting> getSettings(UserHandle paramUserHandle)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject1 = new Intent("android.location.SettingInjectorService");
    int i = paramUserHandle.getIdentifier();
    Object localObject2 = localPackageManager.queryIntentServicesAsUser((Intent)localObject1, 128, i);
    if (Log.isLoggable("SettingsInjector", 3)) {
      Log.d("SettingsInjector", "Found services for profile id " + i + ": " + localObject2);
    }
    localObject1 = new ArrayList(((List)localObject2).size());
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject2).next();
      try
      {
        InjectedSetting localInjectedSetting = parseServiceInfo(localResolveInfo, paramUserHandle, localPackageManager);
        if (localInjectedSetting == null) {
          Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo);
        }
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo, localXmlPullParserException);
        continue;
        ((List)localObject1).add(localXmlPullParserException);
      }
      catch (IOException localIOException)
      {
        Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo, localIOException);
      }
    }
    if (Log.isLoggable("SettingsInjector", 3)) {
      Log.d("SettingsInjector", "Loaded settings for profile id " + i + ": " + localObject1);
    }
    return (List<InjectedSetting>)localObject1;
  }
  
  private static InjectedSetting parseAttributes(String paramString1, String paramString2, UserHandle paramUserHandle, Resources paramResources, AttributeSet paramAttributeSet)
  {
    paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.SettingInjectorService);
    try
    {
      paramAttributeSet = paramResources.getString(1);
      int i = paramResources.getResourceId(0, 0);
      String str = paramResources.getString(2);
      if (Log.isLoggable("SettingsInjector", 3)) {
        Log.d("SettingsInjector", "parsed title: " + paramAttributeSet + ", iconId: " + i + ", settingsActivity: " + str);
      }
      paramString1 = InjectedSetting.newInstance(paramString1, paramString2, paramAttributeSet, i, paramUserHandle, str);
      return paramString1;
    }
    finally
    {
      paramResources.recycle();
    }
  }
  
  private InjectedSetting parseServiceInfo(ResolveInfo paramResolveInfo, UserHandle paramUserHandle, PackageManager paramPackageManager)
    throws XmlPullParserException, IOException
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    if ((localServiceInfo.applicationInfo.flags & 0x1) == 0)
    {
      if (Log.isLoggable("SettingsInjector", 5))
      {
        Log.w("SettingsInjector", "Ignoring attempt to inject setting from app not in system image: " + paramResolveInfo);
        return null;
      }
    }
    else if (!DimmableIZatIconPreference.showIzat(this.mContext, localServiceInfo.packageName)) {
      return null;
    }
    Object localObject2 = null;
    Object localObject1 = null;
    XmlResourceParser localXmlResourceParser;
    try
    {
      localXmlResourceParser = localServiceInfo.loadXmlMetaData(paramPackageManager, "android.location.SettingInjectorService");
      if (localXmlResourceParser == null)
      {
        localObject1 = localXmlResourceParser;
        localObject2 = localXmlResourceParser;
        throw new XmlPullParserException("No android.location.SettingInjectorService meta-data for " + paramResolveInfo + ": " + localServiceInfo);
      }
    }
    catch (PackageManager.NameNotFoundException paramResolveInfo)
    {
      localObject2 = localObject1;
      throw new XmlPullParserException("Unable to load resources for package " + localServiceInfo.packageName);
    }
    finally
    {
      if (localObject2 != null) {
        ((XmlResourceParser)localObject2).close();
      }
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramResolveInfo = Xml.asAttributeSet(localXmlResourceParser);
    int i;
    do
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      i = localXmlResourceParser.next();
    } while ((i != 1) && (i != 2));
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    if (!"injected-location-setting".equals(localXmlResourceParser.getName()))
    {
      localObject1 = localXmlResourceParser;
      localObject2 = localXmlResourceParser;
      throw new XmlPullParserException("Meta-data does not start with injected-location-setting tag");
    }
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramPackageManager = paramPackageManager.getResourcesForApplicationAsUser(localServiceInfo.packageName, paramUserHandle.getIdentifier());
    localObject1 = localXmlResourceParser;
    localObject2 = localXmlResourceParser;
    paramResolveInfo = parseAttributes(localServiceInfo.packageName, localServiceInfo.name, paramUserHandle, paramPackageManager, paramResolveInfo);
    if (localXmlResourceParser != null) {
      localXmlResourceParser.close();
    }
    return paramResolveInfo;
  }
  
  public List<Preference> getInjectedSettings(int paramInt)
  {
    List localList = ((UserManager)this.mContext.getSystemService("user")).getUserProfiles();
    ArrayList localArrayList = new ArrayList();
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      Object localObject = (UserHandle)localList.get(i);
      if (((UserHandle)localObject).getIdentifier() == 999) {}
      for (;;)
      {
        i += 1;
        break;
        if ((paramInt == -2) || (paramInt == ((UserHandle)localObject).getIdentifier()))
        {
          localObject = getSettings((UserHandle)localObject).iterator();
          while (((Iterator)localObject).hasNext())
          {
            InjectedSetting localInjectedSetting = (InjectedSetting)((Iterator)localObject).next();
            Preference localPreference = addServiceSetting(localArrayList, localInjectedSetting);
            this.mSettings.add(new Setting(localInjectedSetting, localPreference, null));
          }
        }
      }
    }
    reloadStatusMessages();
    return localArrayList;
  }
  
  public void reloadStatusMessages()
  {
    if (Log.isLoggable("SettingsInjector", 3)) {
      Log.d("SettingsInjector", "reloadingStatusMessages: " + this.mSettings);
    }
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
  }
  
  private class ServiceSettingClickedListener
    implements Preference.OnPreferenceClickListener
  {
    private InjectedSetting mInfo;
    
    public ServiceSettingClickedListener(InjectedSetting paramInjectedSetting)
    {
      this.mInfo = paramInjectedSetting;
    }
    
    public boolean onPreferenceClick(Preference paramPreference)
    {
      paramPreference = new Intent();
      paramPreference.setClassName(this.mInfo.packageName, this.mInfo.settingsActivity);
      paramPreference.setFlags(268468224);
      SettingsInjector.-get0(SettingsInjector.this).startActivityAsUser(paramPreference, this.mInfo.mUserHandle);
      return true;
    }
  }
  
  private final class Setting
  {
    public final Preference preference;
    public final InjectedSetting setting;
    public long startMillis;
    
    private Setting(InjectedSetting paramInjectedSetting, Preference paramPreference)
    {
      this.setting = paramInjectedSetting;
      this.preference = paramPreference;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this != paramObject)
      {
        if ((paramObject instanceof Setting)) {
          return this.setting.equals(((Setting)paramObject).setting);
        }
      }
      else {
        return true;
      }
      return false;
    }
    
    public long getElapsedTime()
    {
      return SystemClock.elapsedRealtime() - this.startMillis;
    }
    
    public int hashCode()
    {
      return this.setting.hashCode();
    }
    
    public void maybeLogElapsedTime()
    {
      if ((Log.isLoggable("SettingsInjector", 3)) && (this.startMillis != 0L))
      {
        long l = getElapsedTime();
        Log.d("SettingsInjector", this + " update took " + l + " millis");
      }
    }
    
    public void startService()
    {
      if (!((ActivityManager)SettingsInjector.-get0(SettingsInjector.this).getSystemService("activity")).isUserRunning(this.setting.mUserHandle.getIdentifier()))
      {
        if (Log.isLoggable("SettingsInjector", 2)) {
          Log.v("SettingsInjector", "Cannot start service as user " + this.setting.mUserHandle.getIdentifier() + " is not running");
        }
        return;
      }
      Handler local1 = new Handler()
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          Bundle localBundle = paramAnonymousMessage.getData();
          boolean bool = localBundle.getBoolean("enabled", true);
          if (Log.isLoggable("SettingsInjector", 3)) {
            Log.d("SettingsInjector", SettingsInjector.Setting.this.setting + ": received " + paramAnonymousMessage + ", bundle: " + localBundle);
          }
          SettingsInjector.Setting.this.preference.setSummary(null);
          SettingsInjector.Setting.this.preference.setEnabled(bool);
          SettingsInjector.-get1(SettingsInjector.this).sendMessage(SettingsInjector.-get1(SettingsInjector.this).obtainMessage(2, SettingsInjector.Setting.this));
        }
      };
      Messenger localMessenger = new Messenger(local1);
      Intent localIntent = this.setting.getServiceIntent();
      localIntent.putExtra("messenger", localMessenger);
      if (Log.isLoggable("SettingsInjector", 3)) {
        Log.d("SettingsInjector", this.setting + ": sending update intent: " + localIntent + ", handler: " + local1);
      }
      for (this.startMillis = SystemClock.elapsedRealtime();; this.startMillis = 0L)
      {
        SettingsInjector.-get0(SettingsInjector.this).startServiceAsUser(localIntent, this.setting.mUserHandle);
        return;
      }
    }
    
    public String toString()
    {
      return "Setting{setting=" + this.setting + ", preference=" + this.preference + '}';
    }
  }
  
  private final class StatusLoadingHandler
    extends Handler
  {
    private boolean mReloadRequested;
    private Set<SettingsInjector.Setting> mSettingsBeingLoaded = new HashSet();
    private Set<SettingsInjector.Setting> mSettingsToLoad = new HashSet();
    private Set<SettingsInjector.Setting> mTimedOutSettings = new HashSet();
    
    private StatusLoadingHandler() {}
    
    public void handleMessage(Message paramMessage)
    {
      if (Log.isLoggable("SettingsInjector", 3)) {
        Log.d("SettingsInjector", "handleMessage start: " + paramMessage + ", " + this);
      }
      switch (paramMessage.what)
      {
      default: 
        Log.wtf("SettingsInjector", "Unexpected what: " + paramMessage);
      }
      while ((this.mSettingsBeingLoaded.size() > 0) || (this.mTimedOutSettings.size() > 1))
      {
        if (Log.isLoggable("SettingsInjector", 2)) {
          Log.v("SettingsInjector", "too many services already live for " + paramMessage + ", " + this);
        }
        return;
        this.mReloadRequested = true;
        continue;
        localObject = (SettingsInjector.Setting)paramMessage.obj;
        ((SettingsInjector.Setting)localObject).maybeLogElapsedTime();
        this.mSettingsBeingLoaded.remove(localObject);
        this.mTimedOutSettings.remove(localObject);
        removeMessages(3, localObject);
        continue;
        localObject = (SettingsInjector.Setting)paramMessage.obj;
        this.mSettingsBeingLoaded.remove(localObject);
        this.mTimedOutSettings.add(localObject);
        if (Log.isLoggable("SettingsInjector", 5)) {
          Log.w("SettingsInjector", "Timed out after " + ((SettingsInjector.Setting)localObject).getElapsedTime() + " millis trying to get status for: " + localObject);
        }
      }
      if ((this.mReloadRequested) && (this.mSettingsToLoad.isEmpty()) && (this.mSettingsBeingLoaded.isEmpty()) && (this.mTimedOutSettings.isEmpty()))
      {
        if (Log.isLoggable("SettingsInjector", 2)) {
          Log.v("SettingsInjector", "reloading because idle and reload requesteed " + paramMessage + ", " + this);
        }
        this.mSettingsToLoad.addAll(SettingsInjector.-get2(SettingsInjector.this));
        this.mReloadRequested = false;
      }
      Object localObject = this.mSettingsToLoad.iterator();
      if (!((Iterator)localObject).hasNext())
      {
        if (Log.isLoggable("SettingsInjector", 2)) {
          Log.v("SettingsInjector", "nothing left to do for " + paramMessage + ", " + this);
        }
        return;
      }
      SettingsInjector.Setting localSetting = (SettingsInjector.Setting)((Iterator)localObject).next();
      ((Iterator)localObject).remove();
      localSetting.startService();
      this.mSettingsBeingLoaded.add(localSetting);
      sendMessageDelayed(obtainMessage(3, localSetting), 1000L);
      if (Log.isLoggable("SettingsInjector", 3)) {
        Log.d("SettingsInjector", "handleMessage end " + paramMessage + ", " + this + ", started loading " + localSetting);
      }
    }
    
    public String toString()
    {
      return "StatusLoadingHandler{mSettingsToLoad=" + this.mSettingsToLoad + ", mSettingsBeingLoaded=" + this.mSettingsBeingLoaded + ", mTimedOutSettings=" + this.mTimedOutSettings + ", mReloadRequested=" + this.mReloadRequested + '}';
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\SettingsInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
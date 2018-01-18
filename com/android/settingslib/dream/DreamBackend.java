package com.android.settingslib.dream;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.Secure;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamManager.Stub;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DreamBackend
{
  private static final boolean DEBUG = false;
  private static final String TAG = "DreamBackend";
  private final DreamInfoComparator mComparator;
  private final Context mContext;
  private final IDreamManager mDreamManager;
  private final boolean mDreamsActivatedOnDockByDefault;
  private final boolean mDreamsActivatedOnSleepByDefault;
  private final boolean mDreamsEnabledByDefault;
  
  public DreamBackend(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
    this.mComparator = new DreamInfoComparator(getDefaultDream());
    this.mDreamsEnabledByDefault = paramContext.getResources().getBoolean(17956975);
    this.mDreamsActivatedOnSleepByDefault = paramContext.getResources().getBoolean(17956977);
    this.mDreamsActivatedOnDockByDefault = paramContext.getResources().getBoolean(17956976);
  }
  
  private boolean getBoolean(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = 1; Settings.Secure.getInt(localContentResolver, paramString, i) == 1; i = 0) {
      return true;
    }
    return false;
  }
  
  private static ComponentName getDreamComponentName(ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {
      return null;
    }
    return new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name);
  }
  
  private static ComponentName getSettingsComponentName(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null)) {}
    while (paramResolveInfo.serviceInfo.metaData == null) {
      return null;
    }
    String str = null;
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject4 = null;
    Object localObject1 = str;
    try
    {
      XmlResourceParser localXmlResourceParser = paramResolveInfo.serviceInfo.loadXmlMetaData(paramPackageManager, "android.service.dream");
      if (localXmlResourceParser == null)
      {
        localObject1 = str;
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        Log.w("DreamBackend", "No android.service.dream meta-data");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramResolveInfo.serviceInfo.applicationInfo);
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        localObject1 = str;
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        i = localXmlResourceParser.next();
      } while ((i != 1) && (i != 2));
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      if (!"dream".equals(localXmlResourceParser.getName()))
      {
        localObject1 = str;
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        Log.w("DreamBackend", "Meta-data does not start with dream tag");
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return null;
      }
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.obtainAttributes(localAttributeSet, R.styleable.Dream);
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      str = paramPackageManager.getString(0);
      localObject1 = str;
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      paramPackageManager.recycle();
      localObject3 = localObject4;
      paramPackageManager = str;
      if (localXmlResourceParser != null)
      {
        localXmlResourceParser.close();
        paramPackageManager = str;
        localObject3 = localObject4;
      }
    }
    catch (PackageManager.NameNotFoundException|IOException|XmlPullParserException localNameNotFoundException)
    {
      for (;;)
      {
        localObject3 = localNameNotFoundException;
        paramPackageManager = (PackageManager)localObject1;
        if (localObject2 != null)
        {
          ((XmlResourceParser)localObject2).close();
          localObject3 = localNameNotFoundException;
          paramPackageManager = (PackageManager)localObject1;
        }
      }
    }
    finally
    {
      if (localObject3 == null) {
        break label379;
      }
      ((XmlResourceParser)localObject3).close();
    }
    if (localObject3 != null)
    {
      Log.w("DreamBackend", "Error parsing : " + paramResolveInfo.serviceInfo.packageName, (Throwable)localObject3);
      return null;
    }
    label379:
    localObject1 = paramPackageManager;
    if (paramPackageManager != null)
    {
      localObject1 = paramPackageManager;
      if (paramPackageManager.indexOf('/') < 0) {
        localObject1 = paramResolveInfo.serviceInfo.packageName + "/" + paramPackageManager;
      }
    }
    if (localObject1 == null) {
      return null;
    }
    return ComponentName.unflattenFromString((String)localObject1);
  }
  
  private static void logd(String paramString, Object... paramVarArgs) {}
  
  private void setBoolean(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, paramString, i);
      return;
    }
  }
  
  public ComponentName getActiveDream()
  {
    Object localObject2 = null;
    if (this.mDreamManager == null) {
      return null;
    }
    try
    {
      ComponentName[] arrayOfComponentName = this.mDreamManager.getDreamComponents();
      Object localObject1 = localObject2;
      if (arrayOfComponentName != null)
      {
        localObject1 = localObject2;
        if (arrayOfComponentName.length > 0) {
          localObject1 = arrayOfComponentName[0];
        }
      }
      return (ComponentName)localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DreamBackend", "Failed to get active dream", localRemoteException);
    }
    return null;
  }
  
  public CharSequence getActiveDreamName()
  {
    Object localObject2 = getActiveDream();
    if (localObject2 != null)
    {
      Object localObject1 = this.mContext.getPackageManager();
      try
      {
        localObject2 = ((PackageManager)localObject1).getServiceInfo((ComponentName)localObject2, 0);
        if (localObject2 != null)
        {
          localObject1 = ((ServiceInfo)localObject2).loadLabel((PackageManager)localObject1);
          return (CharSequence)localObject1;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        return null;
      }
    }
    return null;
  }
  
  public ComponentName getDefaultDream()
  {
    if (this.mDreamManager == null) {
      return null;
    }
    try
    {
      ComponentName localComponentName = this.mDreamManager.getDefaultDreamComponent();
      return localComponentName;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DreamBackend", "Failed to get default dream", localRemoteException);
    }
    return null;
  }
  
  public List<DreamInfo> getDreamInfos()
  {
    logd("getDreamInfos()", new Object[0]);
    ComponentName localComponentName = getActiveDream();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    Object localObject = localPackageManager.queryIntentServices(new Intent("android.service.dreams.DreamService"), 128);
    ArrayList localArrayList = new ArrayList(((List)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
      if (localResolveInfo.serviceInfo != null)
      {
        DreamInfo localDreamInfo = new DreamInfo();
        localDreamInfo.caption = localResolveInfo.loadLabel(localPackageManager);
        localDreamInfo.icon = localResolveInfo.loadIcon(localPackageManager);
        localDreamInfo.componentName = getDreamComponentName(localResolveInfo);
        localDreamInfo.isActive = localDreamInfo.componentName.equals(localComponentName);
        localDreamInfo.settingsComponentName = getSettingsComponentName(localPackageManager, localResolveInfo);
        localArrayList.add(localDreamInfo);
      }
    }
    Collections.sort(localArrayList, this.mComparator);
    return localArrayList;
  }
  
  public boolean isActivatedOnDock()
  {
    return getBoolean("screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault);
  }
  
  public boolean isActivatedOnSleep()
  {
    return getBoolean("screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefault);
  }
  
  public boolean isEnabled()
  {
    return getBoolean("screensaver_enabled", this.mDreamsEnabledByDefault);
  }
  
  public void launchSettings(DreamInfo paramDreamInfo)
  {
    logd("launchSettings(%s)", new Object[] { paramDreamInfo });
    if ((paramDreamInfo == null) || (paramDreamInfo.settingsComponentName == null)) {
      return;
    }
    this.mContext.startActivity(new Intent().setComponent(paramDreamInfo.settingsComponentName));
  }
  
  public void preview(DreamInfo paramDreamInfo)
  {
    logd("preview(%s)", new Object[] { paramDreamInfo });
    if ((this.mDreamManager == null) || (paramDreamInfo == null)) {}
    while (paramDreamInfo.componentName == null) {
      return;
    }
    try
    {
      this.mDreamManager.testDream(paramDreamInfo.componentName);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DreamBackend", "Failed to preview " + paramDreamInfo, localRemoteException);
    }
  }
  
  public void setActivatedOnDock(boolean paramBoolean)
  {
    logd("setActivatedOnDock(%s)", new Object[] { Boolean.valueOf(paramBoolean) });
    setBoolean("screensaver_activate_on_dock", paramBoolean);
  }
  
  public void setActivatedOnSleep(boolean paramBoolean)
  {
    logd("setActivatedOnSleep(%s)", new Object[] { Boolean.valueOf(paramBoolean) });
    setBoolean("screensaver_activate_on_sleep", paramBoolean);
  }
  
  public void setActiveDream(ComponentName paramComponentName)
  {
    logd("setActiveDream(%s)", new Object[] { paramComponentName });
    if (this.mDreamManager == null) {
      return;
    }
    try
    {
      ComponentName[] arrayOfComponentName = new ComponentName[1];
      arrayOfComponentName[0] = paramComponentName;
      IDreamManager localIDreamManager = this.mDreamManager;
      if (paramComponentName == null) {
        arrayOfComponentName = null;
      }
      localIDreamManager.setDreamComponents(arrayOfComponentName);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DreamBackend", "Failed to set active dream to " + paramComponentName, localRemoteException);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    logd("setEnabled(%s)", new Object[] { Boolean.valueOf(paramBoolean) });
    setBoolean("screensaver_enabled", paramBoolean);
  }
  
  public void startDreaming()
  {
    logd("startDreaming()", new Object[0]);
    if (this.mDreamManager == null) {
      return;
    }
    try
    {
      this.mDreamManager.dream();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DreamBackend", "Failed to dream", localRemoteException);
    }
  }
  
  public static class DreamInfo
  {
    public CharSequence caption;
    public ComponentName componentName;
    public Drawable icon;
    public boolean isActive;
    public ComponentName settingsComponentName;
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(DreamInfo.class.getSimpleName());
      localStringBuilder.append('[').append(this.caption);
      if (this.isActive) {
        localStringBuilder.append(",active");
      }
      localStringBuilder.append(',').append(this.componentName);
      if (this.settingsComponentName != null) {
        localStringBuilder.append("settings=").append(this.settingsComponentName);
      }
      return ']';
    }
  }
  
  private static class DreamInfoComparator
    implements Comparator<DreamBackend.DreamInfo>
  {
    private final ComponentName mDefaultDream;
    
    public DreamInfoComparator(ComponentName paramComponentName)
    {
      this.mDefaultDream = paramComponentName;
    }
    
    private String sortKey(DreamBackend.DreamInfo paramDreamInfo)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramDreamInfo.componentName.equals(this.mDefaultDream)) {}
      for (char c = '0';; c = '1')
      {
        localStringBuilder.append(c);
        localStringBuilder.append(paramDreamInfo.caption);
        return localStringBuilder.toString();
      }
    }
    
    public int compare(DreamBackend.DreamInfo paramDreamInfo1, DreamBackend.DreamInfo paramDreamInfo2)
    {
      return sortKey(paramDreamInfo1).compareTo(sortKey(paramDreamInfo2));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\dream\DreamBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.oneplus.settings.multiapp;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Xml;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodManager.Stub;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class OPDeleteNonRequiredAppsTask
{
  private static final String ATTR_VALUE = "value";
  public static final int DEVICE_OWNER = 0;
  public static final int MANAGED_USER = 2;
  public static final int PROFILE_OWNER = 1;
  public static final String TAG = "DeleteNonRequiredAppsTask";
  private static final String TAG_PACKAGE_LIST_ITEM = "item";
  private static final String TAG_SYSTEM_APPS = "system-apps";
  private final Callback mCallback;
  private final Context mContext;
  private final List<String> mDisallowedAppsList;
  private final IInputMethodManager mIInputMethodManager;
  private final IPackageManager mIPackageManager;
  private final boolean mLeaveAllSystemAppsEnabled;
  private final String mMdmPackageName;
  private final boolean mNewProfile;
  private final PackageManager mPm;
  private final int mProvisioningType;
  private final List<String> mRequiredAppsList;
  private final int mUserId;
  private final List<String> mVendorDisallowedAppsList;
  private final List<String> mVendorRequiredAppsList;
  
  OPDeleteNonRequiredAppsTask(Context paramContext, IPackageManager paramIPackageManager, IInputMethodManager paramIInputMethodManager, String paramString, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mContext = paramContext;
    this.mMdmPackageName = paramString;
    this.mProvisioningType = paramInt1;
    this.mUserId = paramInt2;
    this.mNewProfile = paramBoolean1;
    this.mLeaveAllSystemAppsEnabled = paramBoolean2;
    this.mPm = paramContext.getPackageManager();
    this.mIPackageManager = paramIPackageManager;
    this.mIInputMethodManager = paramIInputMethodManager;
    paramContext = this.mContext.getResources();
    this.mRequiredAppsList = Arrays.asList(paramContext.getStringArray(2131427500));
    this.mDisallowedAppsList = Arrays.asList(paramContext.getStringArray(2131427501));
    this.mVendorRequiredAppsList = null;
    this.mVendorDisallowedAppsList = null;
  }
  
  public OPDeleteNonRequiredAppsTask(Context paramContext, String paramString, int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, Callback paramCallback)
  {
    this(paramContext, AppGlobals.getPackageManager(), getIInputMethodManager(), paramString, paramInt1, paramBoolean1, paramInt2, paramBoolean2, paramCallback);
  }
  
  private Set<String> getCurrentAppsWithLauncher()
  {
    Object localObject1 = new Intent("android.intent.action.MAIN");
    ((Intent)localObject1).addCategory("android.intent.category.LAUNCHER");
    Object localObject2 = this.mPm.queryIntentActivitiesAsUser((Intent)localObject1, 1843712, this.mUserId);
    Log.d("DeleteNonRequiredAppsTask", "Oneplus-MATCH_SYSTEM_ONLY");
    localObject1 = new HashSet();
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext()) {
      ((Set)localObject1).add(((ResolveInfo)((Iterator)localObject2).next()).activityInfo.packageName);
    }
    return (Set<String>)localObject1;
  }
  
  private Set<String> getDisallowedApps()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(this.mDisallowedAppsList);
    return localHashSet;
  }
  
  private static IInputMethodManager getIInputMethodManager()
  {
    return IInputMethodManager.Stub.asInterface(ServiceManager.getService("input_method"));
  }
  
  private Set<String> getPackagesToDelete()
  {
    Set localSet = getCurrentAppsWithLauncher();
    localSet.removeAll(getRequiredApps());
    if ((this.mProvisioningType == 0) || (this.mProvisioningType == 2)) {
      localSet.removeAll(getSystemInputMethods());
    }
    localSet.addAll(getDisallowedApps());
    return localSet;
  }
  
  static File getSystemAppsFile(Context paramContext, int paramInt)
  {
    return new File(paramContext.getFilesDir() + File.separator + "system_apps" + File.separator + "user" + paramInt + ".xml");
  }
  
  private Set<String> getSystemInputMethods()
  {
    try
    {
      Object localObject = this.mIInputMethodManager.getInputMethodList();
      HashSet localHashSet = new HashSet();
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)((Iterator)localObject).next();
        if ((localInputMethodInfo.getServiceInfo().applicationInfo.flags & 0x1) != 0) {
          localHashSet.add(localInputMethodInfo.getPackageName());
        }
      }
      return localRemoteException;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("DeleteNonRequiredAppsTask", "Could not communicate with IInputMethodManager", localRemoteException);
      return Collections.emptySet();
    }
  }
  
  private Set<String> readSystemApps(File paramFile)
  {
    HashSet localHashSet = new HashSet();
    if (!paramFile.exists()) {
      return localHashSet;
    }
    try
    {
      paramFile = new FileInputStream(paramFile);
      XmlPullParser localXmlPullParser = Xml.newPullParser();
      localXmlPullParser.setInput(paramFile, null);
      localXmlPullParser.next();
      int i = localXmlPullParser.getDepth();
      for (;;)
      {
        int j = localXmlPullParser.next();
        if ((j == 1) || ((j == 3) && (localXmlPullParser.getDepth() <= i))) {
          break label195;
        }
        if ((j != 3) && (j != 4))
        {
          str = localXmlPullParser.getName();
          if (!str.equals("item")) {
            break;
          }
          localHashSet.add(localXmlPullParser.getAttributeValue(null, "value"));
        }
      }
    }
    catch (IOException paramFile)
    {
      for (;;)
      {
        String str;
        Log.e("DeleteNonRequiredAppsTask", "IOException trying to read the system apps", paramFile);
        return localHashSet;
        Log.e("DeleteNonRequiredAppsTask", "Unknown tag: " + str);
      }
    }
    catch (XmlPullParserException paramFile)
    {
      Log.e("DeleteNonRequiredAppsTask", "XmlPullParserException trying to read the system apps", paramFile);
      return localHashSet;
    }
    label195:
    paramFile.close();
    return localHashSet;
  }
  
  private void removeNonInstalledPackages(Set<String> paramSet)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      try
      {
        if (this.mPm.getPackageInfoAsUser(str, 0, this.mUserId) == null) {
          localHashSet.add(str);
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        localHashSet.add(str);
      }
    }
    paramSet.removeAll(localHashSet);
  }
  
  public static boolean shouldDeleteNonRequiredApps(Context paramContext, int paramInt)
  {
    return getSystemAppsFile(paramContext, paramInt).exists();
  }
  
  private void writeSystemApps(Set<String> paramSet, File paramFile)
  {
    FastXmlSerializer localFastXmlSerializer;
    try
    {
      paramFile = new FileOutputStream(paramFile, false);
      localFastXmlSerializer = new FastXmlSerializer();
      localFastXmlSerializer.setOutput(paramFile, "utf-8");
      localFastXmlSerializer.startDocument(null, Boolean.valueOf(true));
      localFastXmlSerializer.startTag(null, "system-apps");
      paramSet = paramSet.iterator();
      while (paramSet.hasNext())
      {
        String str = (String)paramSet.next();
        localFastXmlSerializer.startTag(null, "item");
        localFastXmlSerializer.attribute(null, "value", str);
        localFastXmlSerializer.endTag(null, "item");
      }
      localFastXmlSerializer.endTag(null, "system-apps");
    }
    catch (IOException paramSet)
    {
      Log.e("DeleteNonRequiredAppsTask", "IOException trying to write the system apps", paramSet);
      return;
    }
    localFastXmlSerializer.endDocument();
    paramFile.close();
  }
  
  protected Set<String> getRequiredApps()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(this.mRequiredAppsList);
    return localHashSet;
  }
  
  public void run()
  {
    if (this.mLeaveAllSystemAppsEnabled)
    {
      Log.e("DeleteNonRequiredAppsTask", "Not deleting non-required apps.");
      this.mCallback.onSuccess();
      return;
    }
    Log.e("DeleteNonRequiredAppsTask", "Deleting non required apps.");
    Object localObject = getPackagesToDelete();
    removeNonInstalledPackages((Set)localObject);
    if (((Set)localObject).isEmpty())
    {
      this.mCallback.onSuccess();
      return;
    }
    PackageDeleteObserver localPackageDeleteObserver = new PackageDeleteObserver(((Set)localObject).size());
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      Log.e("DeleteNonRequiredAppsTask", "Deleting package [" + str + "] as user " + this.mUserId);
      this.mPm.deletePackageAsUser(str, localPackageDeleteObserver, 4, this.mUserId);
    }
  }
  
  public static abstract class Callback
  {
    public abstract void onError();
    
    public abstract void onSuccess();
  }
  
  class PackageDeleteObserver
    extends IPackageDeleteObserver.Stub
  {
    private final AtomicInteger mPackageCount = new AtomicInteger(0);
    
    public PackageDeleteObserver(int paramInt)
    {
      this.mPackageCount.set(paramInt);
    }
    
    public void packageDeleted(String paramString, int paramInt)
    {
      if (paramInt != 1)
      {
        Log.e("DeleteNonRequiredAppsTask", "Could not finish the provisioning: package deletion failed");
        OPDeleteNonRequiredAppsTask.-get0(OPDeleteNonRequiredAppsTask.this).onError();
        return;
      }
      if (this.mPackageCount.decrementAndGet() == 0)
      {
        Log.e("DeleteNonRequiredAppsTask", "All non-required system apps with launcher icon, and all disallowed apps have been uninstalled.");
        OPDeleteNonRequiredAppsTask.-get0(OPDeleteNonRequiredAppsTask.this).onSuccess();
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\multiapp\OPDeleteNonRequiredAppsTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
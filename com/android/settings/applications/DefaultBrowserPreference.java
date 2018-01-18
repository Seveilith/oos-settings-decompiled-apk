package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.content.PackageMonitor;
import com.android.settings.AppListPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class DefaultBrowserPreference
  extends AppListPreference
{
  private static final Intent BROWSE_PROBE = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:"));
  private static final boolean DEBUG = Log.isLoggable("DefaultBrowserPref", 3);
  public static final String DEFAULT_BROWSER_HYDROGEN = "com.android.browser";
  public static final String DEFAULT_BROWSER_OXYGEN = "com.android.chrome";
  private static final long DELAY_UPDATE_BROWSER_MILLIS = 500L;
  private static final String TAG = "DefaultBrowserPref";
  private final Handler mHandler = new Handler();
  private final PackageMonitor mPackageMonitor = new PackageMonitor()
  {
    private void sendUpdate()
    {
      DefaultBrowserPreference.-get0(DefaultBrowserPreference.this).postDelayed(DefaultBrowserPreference.-get1(DefaultBrowserPreference.this), 500L);
    }
    
    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageAppeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
    
    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
  };
  private final PackageManager mPm;
  private final Runnable mUpdateRunnable = new Runnable()
  {
    public void run()
    {
      DefaultBrowserPreference.-wrap0(DefaultBrowserPreference.this);
    }
  };
  
  public DefaultBrowserPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mPm = paramContext.getPackageManager();
    refreshBrowserApps();
  }
  
  public static boolean hasBrowserPreference(String paramString, Context paramContext)
  {
    boolean bool2 = false;
    Intent localIntent = new Intent();
    localIntent.setAction("android.intent.action.VIEW");
    localIntent.addCategory("android.intent.category.BROWSABLE");
    localIntent.setData(Uri.parse("http:"));
    localIntent.setPackage(paramString);
    paramString = paramContext.getPackageManager().queryIntentActivities(localIntent, 0);
    boolean bool1 = bool2;
    if (paramString != null)
    {
      bool1 = bool2;
      if (paramString.size() != 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static boolean isBrowserDefault(String paramString, Context paramContext)
  {
    paramContext = paramContext.getPackageManager().getDefaultBrowserPackageNameAsUser(UserHandle.myUserId());
    if (paramContext != null) {
      return paramContext.equals(paramString);
    }
    return false;
  }
  
  private List<String> resolveBrowserApps()
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = new Intent();
    ((Intent)localObject).setAction("android.intent.action.VIEW");
    ((Intent)localObject).addCategory("android.intent.category.BROWSABLE");
    ((Intent)localObject).setData(Uri.parse("http:"));
    localObject = this.mPm.queryIntentActivitiesAsUser(BROWSE_PROBE, 131072, this.mUserId);
    int j = ((List)localObject).size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)((List)localObject).get(i);
      if ((localResolveInfo.activityInfo != null) && (!localArrayList.contains(localResolveInfo.activityInfo.packageName)) && (localResolveInfo.handleAllWebDataURI)) {
        localArrayList.add(localResolveInfo.activityInfo.packageName);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private void updateDefaultBrowserPreference()
  {
    refreshBrowserApps();
    String str = getContext().getPackageManager().getDefaultBrowserPackageNameAsUser(this.mUserId);
    if (!TextUtils.isEmpty(str))
    {
      Intent localIntent = new Intent(BROWSE_PROBE).setPackage(str);
      if (this.mPm.resolveActivityAsUser(localIntent, 0, this.mUserId) != null)
      {
        setValue(str);
        setSummary("%s");
        return;
      }
      setSummary(2131693438);
      return;
    }
    if (DEBUG) {
      Log.d("DefaultBrowserPref", "No default browser app.");
    }
    setSoleAppLabelAsSummary();
  }
  
  protected CharSequence getSoleAppLabel()
  {
    List localList = this.mPm.queryIntentActivitiesAsUser(BROWSE_PROBE, 131072, this.mUserId);
    if (localList.size() == 1) {
      return ((ResolveInfo)localList.get(0)).loadLabel(this.mPm);
    }
    return null;
  }
  
  public void onAttached()
  {
    super.onAttached();
    updateDefaultBrowserPreference();
    this.mPackageMonitor.register(getContext(), getContext().getMainLooper(), false);
  }
  
  public void onDetached()
  {
    this.mPackageMonitor.unregister();
    super.onDetached();
  }
  
  protected boolean persistString(String paramString)
  {
    boolean bool1 = false;
    if (paramString == null) {
      return false;
    }
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    boolean bool2 = this.mPm.setDefaultBrowserPackageNameAsUser(paramString.toString(), this.mUserId);
    if (bool2) {
      setSummary("%s");
    }
    if (bool2) {
      bool1 = super.persistString(paramString);
    }
    return bool1;
  }
  
  public void refreshBrowserApps()
  {
    List localList = resolveBrowserApps();
    String str2 = getValue();
    if (OPUtils.isO2()) {}
    for (String str1 = "com.android.chrome";; str1 = "com.android.browser")
    {
      setPackageNames((CharSequence[])localList.toArray(new String[localList.size()]), str2, str1);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\DefaultBrowserPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
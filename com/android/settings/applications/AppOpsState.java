package com.android.settings.applications;

import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AppOpsState
{
  public static final OpsTemplate[] ALL_TEMPLATES;
  static final boolean DEBUG = false;
  public static final OpsTemplate DEVICE_TEMPLATE;
  public static final Comparator<AppOpEntry> LABEL_COMPARATOR = new Comparator()
  {
    private final Collator sCollator = Collator.getInstance();
    
    public int compare(AppOpsState.AppOpEntry paramAnonymousAppOpEntry1, AppOpsState.AppOpEntry paramAnonymousAppOpEntry2)
    {
      return this.sCollator.compare(paramAnonymousAppOpEntry1.getAppEntry().getLabel(), paramAnonymousAppOpEntry2.getAppEntry().getLabel());
    }
  };
  public static final OpsTemplate LOCATION_TEMPLATE = new OpsTemplate(new int[] { 0, 1, 2, 10, 12, 41, 42 }, new boolean[] { 1, 1, 0, 0, 0, 0, 0 });
  public static final OpsTemplate MEDIA_TEMPLATE;
  public static final OpsTemplate MESSAGING_TEMPLATE;
  public static final OpsTemplate PERSONAL_TEMPLATE = new OpsTemplate(new int[] { 4, 5, 6, 7, 8, 9, 29, 30 }, new boolean[] { 1, 1, 1, 1, 1, 1, 0, 0 });
  public static final Comparator<AppOpEntry> RECENCY_COMPARATOR;
  public static final OpsTemplate RUN_IN_BACKGROUND_TEMPLATE;
  static final String TAG = "AppOpsState";
  final AppOpsManager mAppOps;
  List<AppOpEntry> mApps;
  final Context mContext;
  final CharSequence[] mOpLabels;
  final CharSequence[] mOpSummaries;
  final PackageManager mPm;
  
  static
  {
    MESSAGING_TEMPLATE = new OpsTemplate(new int[] { 14, 16, 17, 18, 19, 15, 20, 21, 22 }, new boolean[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 });
    MEDIA_TEMPLATE = new OpsTemplate(new int[] { 3, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39, 44 }, new boolean[] { 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    DEVICE_TEMPLATE = new OpsTemplate(new int[] { 11, 25, 13, 23, 24, 40, 46, 47, 49, 50 }, new boolean[] { 0, 1, 1, 1, 1, 1, 0, 0, 0, 0 });
    RUN_IN_BACKGROUND_TEMPLATE = new OpsTemplate(new int[] { 64 }, new boolean[] { false });
    ALL_TEMPLATES = new OpsTemplate[] { LOCATION_TEMPLATE, PERSONAL_TEMPLATE, MESSAGING_TEMPLATE, MEDIA_TEMPLATE, DEVICE_TEMPLATE, RUN_IN_BACKGROUND_TEMPLATE };
    RECENCY_COMPARATOR = new Comparator()
    {
      private final Collator sCollator = Collator.getInstance();
      
      public int compare(AppOpsState.AppOpEntry paramAnonymousAppOpEntry1, AppOpsState.AppOpEntry paramAnonymousAppOpEntry2)
      {
        if (paramAnonymousAppOpEntry1.getSwitchOrder() != paramAnonymousAppOpEntry2.getSwitchOrder())
        {
          if (paramAnonymousAppOpEntry1.getSwitchOrder() < paramAnonymousAppOpEntry2.getSwitchOrder()) {
            return -1;
          }
          return 1;
        }
        if (paramAnonymousAppOpEntry1.isRunning() != paramAnonymousAppOpEntry2.isRunning())
        {
          if (paramAnonymousAppOpEntry1.isRunning()) {
            return -1;
          }
          return 1;
        }
        if (paramAnonymousAppOpEntry1.getTime() != paramAnonymousAppOpEntry2.getTime())
        {
          if (paramAnonymousAppOpEntry1.getTime() > paramAnonymousAppOpEntry2.getTime()) {
            return -1;
          }
          return 1;
        }
        return this.sCollator.compare(paramAnonymousAppOpEntry1.getAppEntry().getLabel(), paramAnonymousAppOpEntry2.getAppEntry().getLabel());
      }
    };
  }
  
  public AppOpsState(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mPm = paramContext.getPackageManager();
    this.mOpSummaries = paramContext.getResources().getTextArray(2131427416);
    this.mOpLabels = paramContext.getResources().getTextArray(2131427417);
  }
  
  private void addOp(List<AppOpEntry> paramList, AppOpsManager.PackageOps paramPackageOps, AppEntry paramAppEntry, AppOpsManager.OpEntry paramOpEntry, boolean paramBoolean, int paramInt)
  {
    int j = 0;
    if ((paramBoolean) && (paramList.size() > 0))
    {
      localAppOpEntry = (AppOpEntry)paramList.get(paramList.size() - 1);
      if (localAppOpEntry.getAppEntry() == paramAppEntry)
      {
        if (localAppOpEntry.getTime() != 0L) {}
        for (int i = 1;; i = 0)
        {
          if (paramOpEntry.getTime() != 0L) {
            j = 1;
          }
          if (i != j) {
            break;
          }
          localAppOpEntry.addOp(paramOpEntry);
          return;
        }
      }
    }
    AppOpEntry localAppOpEntry = paramAppEntry.getOpSwitch(paramOpEntry.getOp());
    if (localAppOpEntry != null)
    {
      localAppOpEntry.addOp(paramOpEntry);
      return;
    }
    paramList.add(new AppOpEntry(paramPackageOps, paramOpEntry, paramAppEntry, paramInt));
  }
  
  private AppEntry getAppEntry(Context paramContext, HashMap<String, AppEntry> paramHashMap, String paramString, ApplicationInfo paramApplicationInfo)
  {
    AppEntry localAppEntry = (AppEntry)paramHashMap.get(paramString);
    Object localObject = localAppEntry;
    if (localAppEntry == null)
    {
      localObject = paramApplicationInfo;
      if (paramApplicationInfo != null) {}
    }
    try
    {
      localObject = this.mPm.getApplicationInfo(paramString, 8704);
      localObject = new AppEntry(this, (ApplicationInfo)localObject);
      ((AppEntry)localObject).loadLabel(paramContext);
      paramHashMap.put(paramString, localObject);
      return (AppEntry)localObject;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("AppOpsState", "Unable to find info for package " + paramString);
    }
    return null;
  }
  
  public List<AppOpEntry> buildState(OpsTemplate paramOpsTemplate)
  {
    return buildState(paramOpsTemplate, 0, null, RECENCY_COMPARATOR);
  }
  
  public List<AppOpEntry> buildState(OpsTemplate paramOpsTemplate, int paramInt, String paramString)
  {
    return buildState(paramOpsTemplate, paramInt, paramString, RECENCY_COMPARATOR);
  }
  
  public List<AppOpEntry> buildState(OpsTemplate paramOpsTemplate, int paramInt, String paramString, Comparator<AppOpEntry> paramComparator)
  {
    Context localContext = this.mContext;
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    int[] arrayOfInt = new int[71];
    int i = 0;
    Object localObject1;
    if (i < paramOpsTemplate.ops.length)
    {
      if (paramOpsTemplate.showPerms[i] != 0)
      {
        localObject1 = AppOpsManager.opToPermission(paramOpsTemplate.ops[i]);
        if ((localObject1 != null) && (!localArrayList2.contains(localObject1))) {
          break label107;
        }
      }
      for (;;)
      {
        i += 1;
        break;
        label107:
        localArrayList2.add(localObject1);
        localArrayList3.add(Integer.valueOf(paramOpsTemplate.ops[i]));
        arrayOfInt[paramOpsTemplate.ops[i]] = i;
      }
    }
    if (paramString != null) {
      paramOpsTemplate = this.mAppOps.getOpsForPackage(paramInt, paramString, paramOpsTemplate.ops);
    }
    Object localObject2;
    label238:
    Object localObject3;
    boolean bool;
    label277:
    int j;
    while (paramOpsTemplate != null)
    {
      paramInt = 0;
      for (;;)
      {
        if (paramInt < paramOpsTemplate.size())
        {
          localObject1 = (AppOpsManager.PackageOps)paramOpsTemplate.get(paramInt);
          localObject2 = getAppEntry(localContext, localHashMap, ((AppOpsManager.PackageOps)localObject1).getPackageName(), null);
          if (localObject2 == null)
          {
            paramInt += 1;
            continue;
            paramOpsTemplate = this.mAppOps.getPackagesForOps(paramOpsTemplate.ops);
            break;
          }
          i = 0;
          if (i < ((AppOpsManager.PackageOps)localObject1).getOps().size())
          {
            localObject3 = (AppOpsManager.OpEntry)((AppOpsManager.PackageOps)localObject1).getOps().get(i);
            if (paramString != null) {
              break label309;
            }
            bool = true;
            if (paramString != null) {
              break label315;
            }
          }
          label309:
          label315:
          for (j = 0;; j = arrayOfInt[localObject3.getOp()])
          {
            addOp(localArrayList1, (AppOpsManager.PackageOps)localObject1, (AppEntry)localObject2, (AppOpsManager.OpEntry)localObject3, bool, j);
            i += 1;
            break label238;
            break;
            bool = false;
            break label277;
          }
        }
      }
    }
    if (paramString != null) {
      localObject3 = new ArrayList();
    }
    try
    {
      ((List)localObject3).add(this.mPm.getPackageInfo(paramString, 4096));
      paramInt = 0;
      label362:
      if (paramInt < ((List)localObject3).size())
      {
        PackageInfo localPackageInfo = (PackageInfo)((List)localObject3).get(paramInt);
        AppEntry localAppEntry = getAppEntry(localContext, localHashMap, localPackageInfo.packageName, localPackageInfo.applicationInfo);
        if (localAppEntry == null) {}
        do
        {
          paramInt += 1;
          break label362;
          paramOpsTemplate = new String[localArrayList2.size()];
          localArrayList2.toArray(paramOpsTemplate);
          localObject3 = this.mPm.getPackagesHoldingPermissions(paramOpsTemplate, 0);
          break;
          paramOpsTemplate = null;
          localObject1 = null;
        } while (localPackageInfo.requestedPermissions == null);
        i = 0;
        label464:
        Object localObject4;
        if (i < localPackageInfo.requestedPermissions.length)
        {
          if ((localPackageInfo.requestedPermissionsFlags == null) || ((localPackageInfo.requestedPermissionsFlags[i] & 0x2) != 0)) {
            break label519;
          }
          localObject2 = paramOpsTemplate;
          localObject4 = localObject1;
        }
        label519:
        do
        {
          i += 1;
          localObject1 = localObject4;
          paramOpsTemplate = (OpsTemplate)localObject2;
          break label464;
          break;
          j = 0;
          localObject4 = localObject1;
          localObject2 = paramOpsTemplate;
        } while (j >= localArrayList2.size());
        if (!((String)localArrayList2.get(j)).equals(localPackageInfo.requestedPermissions[i]))
        {
          localObject4 = paramOpsTemplate;
          localObject2 = localObject1;
        }
        do
        {
          j += 1;
          localObject1 = localObject2;
          paramOpsTemplate = (OpsTemplate)localObject4;
          break;
          localObject2 = localObject1;
          localObject4 = paramOpsTemplate;
        } while (localAppEntry.hasOp(((Integer)localArrayList3.get(j)).intValue()));
        localObject2 = localObject1;
        localObject1 = paramOpsTemplate;
        if (paramOpsTemplate == null)
        {
          localObject1 = new ArrayList();
          localObject2 = new AppOpsManager.PackageOps(localPackageInfo.packageName, localPackageInfo.applicationInfo.uid, (List)localObject1);
        }
        paramOpsTemplate = new AppOpsManager.OpEntry(((Integer)localArrayList3.get(j)).intValue(), 0, 0L, 0L, 0, -1, null);
        ((List)localObject1).add(paramOpsTemplate);
        if (paramString == null)
        {
          bool = true;
          label701:
          if (paramString != null) {
            break label736;
          }
        }
        label736:
        for (int k = 0;; k = arrayOfInt[paramOpsTemplate.getOp()])
        {
          addOp(localArrayList1, (AppOpsManager.PackageOps)localObject2, localAppEntry, paramOpsTemplate, bool, k);
          localObject4 = localObject1;
          break;
          bool = false;
          break label701;
        }
      }
      Collections.sort(localArrayList1, paramComparator);
      return localArrayList1;
    }
    catch (PackageManager.NameNotFoundException paramOpsTemplate)
    {
      for (;;) {}
    }
  }
  
  public AppOpsManager getAppOpsManager()
  {
    return this.mAppOps;
  }
  
  public static class AppEntry
  {
    private final File mApkFile;
    private Drawable mIcon;
    private final ApplicationInfo mInfo;
    private String mLabel;
    private boolean mMounted;
    private final SparseArray<AppOpsState.AppOpEntry> mOpSwitches = new SparseArray();
    private final SparseArray<AppOpsManager.OpEntry> mOps = new SparseArray();
    private final AppOpsState mState;
    
    public AppEntry(AppOpsState paramAppOpsState, ApplicationInfo paramApplicationInfo)
    {
      this.mState = paramAppOpsState;
      this.mInfo = paramApplicationInfo;
      this.mApkFile = new File(paramApplicationInfo.sourceDir);
    }
    
    public void addOp(AppOpsState.AppOpEntry paramAppOpEntry, AppOpsManager.OpEntry paramOpEntry)
    {
      this.mOps.put(paramOpEntry.getOp(), paramOpEntry);
      this.mOpSwitches.put(AppOpsManager.opToSwitch(paramOpEntry.getOp()), paramAppOpEntry);
    }
    
    public ApplicationInfo getApplicationInfo()
    {
      return this.mInfo;
    }
    
    public Drawable getIcon()
    {
      if (this.mIcon == null)
      {
        if (this.mApkFile.exists())
        {
          this.mIcon = this.mInfo.loadIcon(this.mState.mPm);
          return this.mIcon;
        }
        this.mMounted = false;
      }
      do
      {
        return this.mState.mContext.getDrawable(17301651);
        if (this.mMounted) {
          break;
        }
      } while (!this.mApkFile.exists());
      this.mMounted = true;
      this.mIcon = this.mInfo.loadIcon(this.mState.mPm);
      return this.mIcon;
      return this.mIcon;
    }
    
    public String getLabel()
    {
      return this.mLabel;
    }
    
    public AppOpsState.AppOpEntry getOpSwitch(int paramInt)
    {
      return (AppOpsState.AppOpEntry)this.mOpSwitches.get(AppOpsManager.opToSwitch(paramInt));
    }
    
    public boolean hasOp(int paramInt)
    {
      boolean bool = false;
      if (this.mOps.indexOfKey(paramInt) >= 0) {
        bool = true;
      }
      return bool;
    }
    
    void loadLabel(Context paramContext)
    {
      if ((this.mLabel != null) && (this.mMounted)) {
        return;
      }
      if (!this.mApkFile.exists())
      {
        this.mMounted = false;
        this.mLabel = this.mInfo.packageName;
        return;
      }
      this.mMounted = true;
      paramContext = this.mInfo.loadLabel(paramContext.getPackageManager());
      if (paramContext != null) {}
      for (paramContext = paramContext.toString();; paramContext = this.mInfo.packageName)
      {
        this.mLabel = paramContext;
        return;
      }
    }
    
    public String toString()
    {
      return this.mLabel;
    }
  }
  
  public static class AppOpEntry
  {
    private final AppOpsState.AppEntry mApp;
    private final ArrayList<AppOpsManager.OpEntry> mOps = new ArrayList();
    private int mOverriddenPrimaryMode = -1;
    private final AppOpsManager.PackageOps mPkgOps;
    private final ArrayList<AppOpsManager.OpEntry> mSwitchOps = new ArrayList();
    private final int mSwitchOrder;
    
    public AppOpEntry(AppOpsManager.PackageOps paramPackageOps, AppOpsManager.OpEntry paramOpEntry, AppOpsState.AppEntry paramAppEntry, int paramInt)
    {
      this.mPkgOps = paramPackageOps;
      this.mApp = paramAppEntry;
      this.mSwitchOrder = paramInt;
      this.mApp.addOp(this, paramOpEntry);
      this.mOps.add(paramOpEntry);
      this.mSwitchOps.add(paramOpEntry);
    }
    
    private static void addOp(ArrayList<AppOpsManager.OpEntry> paramArrayList, AppOpsManager.OpEntry paramOpEntry)
    {
      int i = 0;
      while (i < paramArrayList.size())
      {
        AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)paramArrayList.get(i);
        if (localOpEntry.isRunning() != paramOpEntry.isRunning())
        {
          if (paramOpEntry.isRunning()) {
            paramArrayList.add(i, paramOpEntry);
          }
        }
        else if (localOpEntry.getTime() < paramOpEntry.getTime())
        {
          paramArrayList.add(i, paramOpEntry);
          return;
        }
        i += 1;
      }
      paramArrayList.add(paramOpEntry);
    }
    
    private CharSequence getCombinedText(ArrayList<AppOpsManager.OpEntry> paramArrayList, CharSequence[] paramArrayOfCharSequence)
    {
      if (paramArrayList.size() == 1) {
        return paramArrayOfCharSequence[((AppOpsManager.OpEntry)paramArrayList.get(0)).getOp()];
      }
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      while (i < paramArrayList.size())
      {
        if (i > 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(paramArrayOfCharSequence[((AppOpsManager.OpEntry)paramArrayList.get(i)).getOp()]);
        i += 1;
      }
      return localStringBuilder.toString();
    }
    
    public void addOp(AppOpsManager.OpEntry paramOpEntry)
    {
      this.mApp.addOp(this, paramOpEntry);
      addOp(this.mOps, paramOpEntry);
      if (this.mApp.getOpSwitch(AppOpsManager.opToSwitch(paramOpEntry.getOp())) == null) {
        addOp(this.mSwitchOps, paramOpEntry);
      }
    }
    
    public AppOpsState.AppEntry getAppEntry()
    {
      return this.mApp;
    }
    
    public int getNumOpEntry()
    {
      return this.mOps.size();
    }
    
    public AppOpsManager.OpEntry getOpEntry(int paramInt)
    {
      return (AppOpsManager.OpEntry)this.mOps.get(paramInt);
    }
    
    public AppOpsManager.PackageOps getPackageOps()
    {
      return this.mPkgOps;
    }
    
    public int getPrimaryOpMode()
    {
      if (this.mOverriddenPrimaryMode >= 0) {
        return this.mOverriddenPrimaryMode;
      }
      return ((AppOpsManager.OpEntry)this.mOps.get(0)).getMode();
    }
    
    public CharSequence getSummaryText(AppOpsState paramAppOpsState)
    {
      return getCombinedText(this.mOps, paramAppOpsState.mOpSummaries);
    }
    
    public int getSwitchOrder()
    {
      return this.mSwitchOrder;
    }
    
    public CharSequence getSwitchText(AppOpsState paramAppOpsState)
    {
      if (this.mSwitchOps.size() > 0) {
        return getCombinedText(this.mSwitchOps, paramAppOpsState.mOpLabels);
      }
      return getCombinedText(this.mOps, paramAppOpsState.mOpLabels);
    }
    
    public long getTime()
    {
      return ((AppOpsManager.OpEntry)this.mOps.get(0)).getTime();
    }
    
    public CharSequence getTimeText(Resources paramResources, boolean paramBoolean)
    {
      if (isRunning()) {
        return paramResources.getText(2131692181);
      }
      if (getTime() > 0L) {
        return DateUtils.getRelativeTimeSpanString(getTime(), System.currentTimeMillis(), 60000L, 262144);
      }
      if (paramBoolean) {
        return paramResources.getText(2131692182);
      }
      return "";
    }
    
    public boolean isRunning()
    {
      return ((AppOpsManager.OpEntry)this.mOps.get(0)).isRunning();
    }
    
    public void overridePrimaryOpMode(int paramInt)
    {
      this.mOverriddenPrimaryMode = paramInt;
    }
    
    public String toString()
    {
      return this.mApp.getLabel();
    }
  }
  
  public static class OpsTemplate
    implements Parcelable
  {
    public static final Parcelable.Creator<OpsTemplate> CREATOR = new Parcelable.Creator()
    {
      public AppOpsState.OpsTemplate createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AppOpsState.OpsTemplate(paramAnonymousParcel);
      }
      
      public AppOpsState.OpsTemplate[] newArray(int paramAnonymousInt)
      {
        return new AppOpsState.OpsTemplate[paramAnonymousInt];
      }
    };
    public final int[] ops;
    public final boolean[] showPerms;
    
    OpsTemplate(Parcel paramParcel)
    {
      this.ops = paramParcel.createIntArray();
      this.showPerms = paramParcel.createBooleanArray();
    }
    
    public OpsTemplate(int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
    {
      this.ops = paramArrayOfInt;
      this.showPerms = paramArrayOfBoolean;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeIntArray(this.ops);
      paramParcel.writeBooleanArray(this.showPerms);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppOpsState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.Formatter;
import com.android.settings.AppProgressPreference;

public class ProcessStatsPreference
  extends AppProgressPreference
{
  private ProcStatsPackageEntry mEntry;
  
  public ProcessStatsPreference(Context paramContext)
  {
    super(paramContext, null);
  }
  
  public ProcStatsPackageEntry getEntry()
  {
    return this.mEntry;
  }
  
  public void init(ProcStatsPackageEntry paramProcStatsPackageEntry, PackageManager paramPackageManager, double paramDouble1, double paramDouble2, double paramDouble3, boolean paramBoolean)
  {
    this.mEntry = paramProcStatsPackageEntry;
    String str;
    label46:
    int i;
    if (TextUtils.isEmpty(paramProcStatsPackageEntry.mUiLabel))
    {
      str = paramProcStatsPackageEntry.mPackage;
      setTitle(str);
      if (paramProcStatsPackageEntry.mUiTargetApp == null) {
        break label121;
      }
      setIcon(paramProcStatsPackageEntry.mUiTargetApp.loadIcon(paramPackageManager));
      if (paramProcStatsPackageEntry.mRunWeight <= paramProcStatsPackageEntry.mBgWeight) {
        break label136;
      }
      i = 1;
      label61:
      if (!paramBoolean) {
        break label151;
      }
      if (i == 0) {
        break label142;
      }
    }
    label121:
    label136:
    label142:
    for (paramDouble3 = paramProcStatsPackageEntry.mRunWeight;; paramDouble3 = paramProcStatsPackageEntry.mBgWeight)
    {
      paramDouble2 = paramDouble3 * paramDouble2;
      setSummary(Formatter.formatShortFileSize(getContext(), paramDouble2));
      setProgress((int)(100.0D * paramDouble2 / paramDouble1));
      return;
      str = paramProcStatsPackageEntry.mUiLabel;
      break;
      setIcon(new ColorDrawable(0));
      break label46;
      i = 0;
      break label61;
    }
    label151:
    if (i != 0) {}
    for (long l = paramProcStatsPackageEntry.mMaxRunMem;; l = paramProcStatsPackageEntry.mMaxBgMem)
    {
      paramDouble2 = l * paramDouble3 * 1024.0D;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ProcessStatsPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
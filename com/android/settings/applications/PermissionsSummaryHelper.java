package com.android.settings.applications;

import android.content.Context;
import android.content.pm.permission.RuntimePermissionPresentationInfo;
import android.content.pm.permission.RuntimePermissionPresenter;
import android.content.pm.permission.RuntimePermissionPresenter.OnResultCallback;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionsSummaryHelper
{
  public static void getPermissionSummary(Context paramContext, String paramString, PermissionsResultCallback paramPermissionsResultCallback)
  {
    RuntimePermissionPresenter.getInstance(paramContext).getAppPermissions(paramString, new RuntimePermissionPresenter.OnResultCallback()
    {
      public void onGetAppPermissions(List<RuntimePermissionPresentationInfo> paramAnonymousList)
      {
        int i2 = paramAnonymousList.size();
        int j = 0;
        int k = 0;
        int m = 0;
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        if (i < i2)
        {
          RuntimePermissionPresentationInfo localRuntimePermissionPresentationInfo = (RuntimePermissionPresentationInfo)paramAnonymousList.get(i);
          int i1 = m + 1;
          m = k;
          int n = j;
          if (localRuntimePermissionPresentationInfo.isGranted())
          {
            if (!localRuntimePermissionPresentationInfo.isStandard()) {
              break label114;
            }
            localArrayList.add(localRuntimePermissionPresentationInfo.getLabel());
            n = j + 1;
            m = k;
          }
          for (;;)
          {
            i += 1;
            k = m;
            j = n;
            m = i1;
            break;
            label114:
            m = k + 1;
            n = j;
          }
        }
        paramAnonymousList = Collator.getInstance();
        paramAnonymousList.setStrength(0);
        Collections.sort(localArrayList, paramAnonymousList);
        this.val$callback.onPermissionSummaryResult(j, m, k, localArrayList);
      }
    }, null);
  }
  
  public static abstract class PermissionsResultCallback
  {
    public void onAppWithPermissionsCountsResult(int paramInt1, int paramInt2) {}
    
    public void onPermissionSummaryResult(int paramInt1, int paramInt2, int paramInt3, List<CharSequence> paramList) {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\PermissionsSummaryHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
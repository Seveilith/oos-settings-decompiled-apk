package com.android.settings.users;

import android.content.Context;
import android.content.RestrictionEntry;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.ArrayList;
import java.util.Iterator;

public class RestrictionUtils
{
  public static final int[] sRestrictionDescriptions = { 2131693069 };
  public static final String[] sRestrictionKeys = { "no_share_location" };
  public static final int[] sRestrictionTitles = { 2131693068 };
  
  public static ArrayList<RestrictionEntry> getRestrictions(Context paramContext, UserHandle paramUserHandle)
  {
    Resources localResources = paramContext.getResources();
    ArrayList localArrayList = new ArrayList();
    paramContext = UserManager.get(paramContext).getUserRestrictions(paramUserHandle);
    int i = 0;
    if (i < sRestrictionKeys.length)
    {
      paramUserHandle = sRestrictionKeys[i];
      if (paramContext.getBoolean(sRestrictionKeys[i], false)) {}
      for (boolean bool = false;; bool = true)
      {
        paramUserHandle = new RestrictionEntry(paramUserHandle, bool);
        paramUserHandle.setTitle(localResources.getString(sRestrictionTitles[i]));
        paramUserHandle.setDescription(localResources.getString(sRestrictionDescriptions[i]));
        paramUserHandle.setType(1);
        localArrayList.add(paramUserHandle);
        i += 1;
        break;
      }
    }
    return localArrayList;
  }
  
  public static void setRestrictions(Context paramContext, ArrayList<RestrictionEntry> paramArrayList, UserHandle paramUserHandle)
  {
    paramContext = UserManager.get(paramContext);
    paramArrayList = paramArrayList.iterator();
    if (paramArrayList.hasNext())
    {
      RestrictionEntry localRestrictionEntry = (RestrictionEntry)paramArrayList.next();
      String str = localRestrictionEntry.getKey();
      if (localRestrictionEntry.getSelectedState()) {}
      for (boolean bool = false;; bool = true)
      {
        paramContext.setUserRestriction(str, bool, paramUserHandle);
        break;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\RestrictionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
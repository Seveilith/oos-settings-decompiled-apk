package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppWidgetLoader<Item extends LabelledItem>
{
  private static final boolean LOGD = false;
  private static final String TAG = "AppWidgetAdapter";
  private AppWidgetManager mAppWidgetManager;
  private Context mContext;
  ItemConstructor<Item> mItemConstructor;
  
  public AppWidgetLoader(Context paramContext, AppWidgetManager paramAppWidgetManager, ItemConstructor<Item> paramItemConstructor)
  {
    this.mContext = paramContext;
    this.mAppWidgetManager = paramAppWidgetManager;
    this.mItemConstructor = paramItemConstructor;
  }
  
  protected List<Item> getItems(Intent paramIntent)
  {
    boolean bool = paramIntent.getBooleanExtra("customSort", true);
    ArrayList localArrayList1 = new ArrayList();
    putInstalledAppWidgets(localArrayList1, paramIntent.getIntExtra("categoryFilter", 1));
    if (bool) {
      putCustomAppWidgets(localArrayList1, paramIntent);
    }
    Collections.sort(localArrayList1, new Comparator()
    {
      Collator mCollator = Collator.getInstance();
      
      public int compare(Item paramAnonymousItem1, Item paramAnonymousItem2)
      {
        return this.mCollator.compare(paramAnonymousItem1.getLabel(), paramAnonymousItem2.getLabel());
      }
    });
    if (!bool)
    {
      ArrayList localArrayList2 = new ArrayList();
      putCustomAppWidgets(localArrayList2, paramIntent);
      localArrayList1.addAll(localArrayList2);
    }
    return localArrayList1;
  }
  
  void putAppWidgetItems(List<AppWidgetProviderInfo> paramList, List<Bundle> paramList1, List<Item> paramList2, int paramInt, boolean paramBoolean)
  {
    if (paramList == null) {
      return;
    }
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      AppWidgetProviderInfo localAppWidgetProviderInfo = (AppWidgetProviderInfo)paramList.get(i);
      if ((!paramBoolean) && ((localAppWidgetProviderInfo.widgetCategory & paramInt) == 0))
      {
        i += 1;
      }
      else
      {
        ItemConstructor localItemConstructor = this.mItemConstructor;
        Context localContext = this.mContext;
        if (paramList1 != null) {}
        for (Bundle localBundle = (Bundle)paramList1.get(i);; localBundle = null)
        {
          paramList2.add((LabelledItem)localItemConstructor.createItem(localContext, localAppWidgetProviderInfo, localBundle));
          break;
        }
      }
    }
  }
  
  void putCustomAppWidgets(List<Item> paramList, Intent paramIntent)
  {
    Object localObject1 = null;
    ArrayList localArrayList = paramIntent.getParcelableArrayListExtra("customInfo");
    if ((localArrayList == null) || (localArrayList.size() == 0))
    {
      Log.i("AppWidgetAdapter", "EXTRA_CUSTOM_INFO not present.");
      paramIntent = localArrayList;
    }
    for (;;)
    {
      putAppWidgetItems(paramIntent, (List)localObject1, paramList, 0, true);
      return;
      int j = localArrayList.size();
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          localObject2 = (Parcelable)localArrayList.get(i);
          if ((localObject2 != null) && ((localObject2 instanceof AppWidgetProviderInfo)))
          {
            i += 1;
          }
          else
          {
            paramIntent = null;
            Log.e("AppWidgetAdapter", "error using EXTRA_CUSTOM_INFO index=" + i);
            break;
          }
        }
      }
      Object localObject2 = paramIntent.getParcelableArrayListExtra("customExtras");
      if (localObject2 == null)
      {
        paramIntent = null;
        Log.e("AppWidgetAdapter", "EXTRA_CUSTOM_INFO without EXTRA_CUSTOM_EXTRAS");
        localObject1 = localObject2;
      }
      else
      {
        int k = ((ArrayList)localObject2).size();
        if (j != k)
        {
          paramIntent = null;
          localObject1 = null;
          Log.e("AppWidgetAdapter", "list size mismatch: EXTRA_CUSTOM_INFO: " + j + " EXTRA_CUSTOM_EXTRAS: " + k);
        }
        else
        {
          i = 0;
          for (;;)
          {
            paramIntent = localArrayList;
            localObject1 = localObject2;
            if (i >= k) {
              break;
            }
            paramIntent = (Parcelable)((ArrayList)localObject2).get(i);
            if ((paramIntent == null) || (!(paramIntent instanceof Bundle))) {
              break label253;
            }
            i += 1;
          }
          label253:
          paramIntent = null;
          localObject1 = null;
          Log.e("AppWidgetAdapter", "error using EXTRA_CUSTOM_EXTRAS index=" + i);
        }
      }
    }
  }
  
  void putInstalledAppWidgets(List<Item> paramList, int paramInt)
  {
    putAppWidgetItems(this.mAppWidgetManager.getInstalledProviders(paramInt), null, paramList, paramInt, false);
  }
  
  public static abstract interface ItemConstructor<Item>
  {
    public abstract Item createItem(Context paramContext, AppWidgetProviderInfo paramAppWidgetProviderInfo, Bundle paramBundle);
  }
  
  static abstract interface LabelledItem
  {
    public abstract CharSequence getLabel();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppWidgetLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
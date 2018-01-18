package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.List;

public class AppWidgetPickActivity
  extends ActivityPicker
  implements AppWidgetLoader.ItemConstructor<ActivityPicker.PickAdapter.Item>
{
  static final boolean LOGD = false;
  private static final String TAG = "AppWidgetPickActivity";
  private int mAppWidgetId;
  private AppWidgetLoader<ActivityPicker.PickAdapter.Item> mAppWidgetLoader;
  private AppWidgetManager mAppWidgetManager;
  List<ActivityPicker.PickAdapter.Item> mItems;
  private PackageManager mPackageManager;
  
  public ActivityPicker.PickAdapter.Item createItem(Context paramContext, AppWidgetProviderInfo paramAppWidgetProviderInfo, Bundle paramBundle)
  {
    String str = paramAppWidgetProviderInfo.label;
    Object localObject2 = null;
    Object localObject1 = null;
    if (paramAppWidgetProviderInfo.icon != 0) {}
    for (;;)
    {
      try
      {
        i = paramContext.getResources().getDisplayMetrics().densityDpi;
        switch (i)
        {
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        int i;
        Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(paramAppWidgetProviderInfo.icon) + " for provider: " + paramAppWidgetProviderInfo.provider);
        continue;
        continue;
      }
      i = (int)(i * 0.75F + 0.5F);
      localObject2 = this.mPackageManager.getResourcesForApplication(paramAppWidgetProviderInfo.provider.getPackageName()).getDrawableForDensity(paramAppWidgetProviderInfo.icon, i);
      localObject1 = localObject2;
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        Log.w("AppWidgetPickActivity", "Can't load icon drawable 0x" + Integer.toHexString(paramAppWidgetProviderInfo.icon) + " for provider: " + paramAppWidgetProviderInfo.provider);
        localObject2 = localObject1;
      }
      paramContext = new ActivityPicker.PickAdapter.Item(paramContext, str, (Drawable)localObject2);
      paramContext.packageName = paramAppWidgetProviderInfo.provider.getPackageName();
      paramContext.className = paramAppWidgetProviderInfo.provider.getClassName();
      paramContext.extras = paramBundle;
      return paramContext;
    }
  }
  
  protected List<ActivityPicker.PickAdapter.Item> getItems()
  {
    this.mItems = this.mAppWidgetLoader.getItems(getIntent());
    return this.mItems;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    Intent localIntent = getIntentForPosition(paramInt);
    if (((ActivityPicker.PickAdapter.Item)this.mItems.get(paramInt)).extras != null) {
      setResultData(-1, localIntent);
    }
    for (;;)
    {
      finish();
      return;
      paramDialogInterface = null;
      try
      {
        if (localIntent.getExtras() != null) {
          paramDialogInterface = localIntent.getExtras().getBundle("appWidgetOptions");
        }
        this.mAppWidgetManager.bindAppWidgetId(this.mAppWidgetId, localIntent.getComponent(), paramDialogInterface);
        paramInt = -1;
      }
      catch (IllegalArgumentException paramDialogInterface)
      {
        for (;;)
        {
          paramInt = 0;
        }
      }
      setResultData(paramInt, null);
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    this.mPackageManager = getPackageManager();
    this.mAppWidgetManager = AppWidgetManager.getInstance(this);
    this.mAppWidgetLoader = new AppWidgetLoader(this, this.mAppWidgetManager, this);
    super.onCreate(paramBundle);
    setResultData(0, null);
    paramBundle = getIntent();
    if (paramBundle.hasExtra("appWidgetId"))
    {
      this.mAppWidgetId = paramBundle.getIntExtra("appWidgetId", 0);
      return;
    }
    finish();
  }
  
  void setResultData(int paramInt, Intent paramIntent)
  {
    if (paramIntent != null) {}
    for (;;)
    {
      paramIntent.putExtra("appWidgetId", this.mAppWidgetId);
      setResult(paramInt, paramIntent);
      return;
      paramIntent = new Intent();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppWidgetPickActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
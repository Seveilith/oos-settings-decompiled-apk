package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WallpaperTypeSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1 = new Intent("android.intent.action.SET_WALLPAPER");
      PackageManager localPackageManager = paramAnonymousContext.getPackageManager();
      Iterator localIterator = localPackageManager.queryIntentActivities((Intent)localObject1, 65536).iterator();
      while (localIterator.hasNext())
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
        Object localObject2 = localResolveInfo.loadLabel(localPackageManager);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = localResolveInfo.activityInfo.packageName;
        }
        localObject2 = new SearchIndexableRaw(paramAnonymousContext);
        ((SearchIndexableRaw)localObject2).title = ((CharSequence)localObject1).toString();
        ((SearchIndexableRaw)localObject2).screenTitle = paramAnonymousContext.getResources().getString(2131691629);
        ((SearchIndexableRaw)localObject2).intentAction = "android.intent.action.SET_WALLPAPER";
        ((SearchIndexableRaw)localObject2).intentTargetPackage = localResolveInfo.activityInfo.packageName;
        ((SearchIndexableRaw)localObject2).intentTargetClass = localResolveInfo.activityInfo.name;
        localArrayList.add(localObject2);
      }
      return localArrayList;
    }
  };
  
  private void populateWallpaperTypes()
  {
    Intent localIntent = new Intent("android.intent.action.SET_WALLPAPER");
    PackageManager localPackageManager = getPackageManager();
    Object localObject = localPackageManager.queryIntentActivities(localIntent, 65536);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.setOrderingAsAdded(false);
    Iterator localIterator = ((Iterable)localObject).iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      Preference localPreference = new Preference(getPrefContext());
      localPreference.setLayoutResource(2130968926);
      localObject = new Intent(localIntent);
      ((Intent)localObject).setComponent(new ComponentName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name));
      localPreference.setIntent((Intent)localObject);
      CharSequence localCharSequence = localResolveInfo.loadLabel(localPackageManager);
      localObject = localCharSequence;
      if (localCharSequence == null) {
        localObject = localResolveInfo.activityInfo.packageName;
      }
      localPreference.setTitle((CharSequence)localObject);
      localPreference.setIcon(localResolveInfo.loadIcon(localPackageManager));
      localPreferenceScreen.addPreference(localPreference);
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693002;
  }
  
  protected int getMetricsCategory()
  {
    return 101;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230881);
    populateWallpaperTypes();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\WallpaperTypeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
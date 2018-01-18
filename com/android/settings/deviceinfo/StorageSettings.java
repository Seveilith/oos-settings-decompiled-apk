package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.RestrictedLockUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class StorageSettings
  extends SettingsPreferenceFragment
  implements Indexable
{
  static final int[] COLOR_PRIVATE;
  static final int COLOR_PUBLIC = Color.parseColor("#ff9e9e9e");
  static final int COLOR_WARNING = Color.parseColor("#fff4511e");
  private static final int REQUEST_SHOULD_FINISH = 99;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691689);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131692127);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      StorageManager localStorageManager = (StorageManager)paramAnonymousContext.getSystemService(StorageManager.class);
      Iterator localIterator = localStorageManager.getVolumes().iterator();
      while (localIterator.hasNext())
      {
        VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
        if (StorageSettings.-wrap0(localVolumeInfo))
        {
          localSearchIndexableRaw.title = localStorageManager.getBestVolumeDescription(localVolumeInfo);
          localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
          localArrayList.add(localSearchIndexableRaw);
        }
      }
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691719);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691717);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691721);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691724);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691725);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691723);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691727);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = paramAnonymousContext.getString(2131691726);
      localSearchIndexableRaw.screenTitle = paramAnonymousContext.getString(2131691689);
      localArrayList.add(localSearchIndexableRaw);
      return localArrayList;
    }
  };
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY;
  static final String TAG = "StorageSettings";
  private static final String TAG_DISK_INIT = "disk_init";
  private static final String TAG_VOLUME_UNMOUNTED = "volume_unmounted";
  private PreferenceCategory mExternalCategory;
  private PreferenceCategory mInternalCategory;
  private StorageSummaryPreference mInternalSummary;
  private boolean mIsFinished = false;
  private final StorageEventListener mStorageListener = new StorageEventListener()
  {
    public void onDiskDestroyed(DiskInfo paramAnonymousDiskInfo)
    {
      StorageSettings.-wrap1(StorageSettings.this);
    }
    
    public void onVolumeStateChanged(VolumeInfo paramAnonymousVolumeInfo, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (StorageSettings.-wrap0(paramAnonymousVolumeInfo)) {
        StorageSettings.-wrap1(StorageSettings.this);
      }
    }
  };
  private StorageManager mStorageManager;
  private boolean shouldFinish = false;
  
  static
  {
    COLOR_PRIVATE = new int[] { Color.parseColor("#ff26a69a"), Color.parseColor("#ffab47bc"), Color.parseColor("#fff2a600"), Color.parseColor("#ffec407a"), Color.parseColor("#ffc0ca33") };
    SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
    {
      public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
      {
        return new StorageSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader, null);
      }
    };
  }
  
  private static boolean isInteresting(VolumeInfo paramVolumeInfo)
  {
    switch (paramVolumeInfo.getType())
    {
    default: 
      return false;
    }
    return true;
  }
  
  private void refresh()
  {
    if (this.mIsFinished) {
      return;
    }
    Object localObject1 = getPrefContext();
    getPreferenceScreen().removeAll();
    this.mInternalCategory.removeAll();
    this.mExternalCategory.removeAll();
    this.mInternalCategory.addPreference(this.mInternalSummary);
    int i = 0;
    long l2 = 0L;
    long l1 = 0L;
    Object localObject2 = this.mStorageManager.getVolumes();
    Collections.sort((List)localObject2, VolumeInfo.getDescriptionComparator());
    localObject2 = ((Iterable)localObject2).iterator();
    for (;;)
    {
      Object localObject3;
      Object localObject4;
      int j;
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (VolumeInfo)((Iterator)localObject2).next();
        if (((VolumeInfo)localObject3).getType() == 1)
        {
          localObject4 = COLOR_PRIVATE;
          j = i + 1;
          i = localObject4[(i % COLOR_PRIVATE.length)];
          this.mInternalCategory.addPreference(new StorageVolumePreference((Context)localObject1, (VolumeInfo)localObject3, i));
          if (((VolumeInfo)localObject3).isMountedReadable())
          {
            localObject3 = ((VolumeInfo)localObject3).getPath();
            l2 += ((File)localObject3).getTotalSpace() - ((File)localObject3).getFreeSpace();
            l1 += ((File)localObject3).getTotalSpace();
            i = j;
          }
        }
        else
        {
          if (((VolumeInfo)localObject3).getType() != 0) {
            continue;
          }
          this.mExternalCategory.addPreference(new StorageVolumePreference((Context)localObject1, (VolumeInfo)localObject3, COLOR_PUBLIC));
        }
      }
      else
      {
        localObject2 = this.mStorageManager.getVolumeRecords().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (VolumeRecord)((Iterator)localObject2).next();
          if ((((VolumeRecord)localObject3).getType() == 1) && (this.mStorageManager.findVolumeByUuid(((VolumeRecord)localObject3).getFsUuid()) == null))
          {
            localObject4 = ((Context)localObject1).getDrawable(2130838081);
            ((Drawable)localObject4).mutate();
            ((Drawable)localObject4).setTint(COLOR_PUBLIC);
            Preference localPreference = new Preference((Context)localObject1);
            localPreference.setKey(((VolumeRecord)localObject3).getFsUuid());
            localPreference.setTitle(((VolumeRecord)localObject3).getNickname());
            localPreference.setSummary(17040484);
            localPreference.setIcon((Drawable)localObject4);
            this.mInternalCategory.addPreference(localPreference);
          }
        }
        localObject2 = this.mStorageManager.getDisks().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (DiskInfo)((Iterator)localObject2).next();
          if ((((DiskInfo)localObject3).volumeCount == 0) && (((DiskInfo)localObject3).size > 0L))
          {
            localObject4 = new Preference((Context)localObject1);
            ((Preference)localObject4).setKey(((DiskInfo)localObject3).getId());
            ((Preference)localObject4).setTitle(((DiskInfo)localObject3).getDescription());
            ((Preference)localObject4).setSummary(17040481);
            ((Preference)localObject4).setIcon(2130838081);
            this.mExternalCategory.addPreference((Preference)localObject4);
          }
        }
        localObject2 = Formatter.formatBytes(getResources(), l2, 0);
        this.mInternalSummary.setTitle(TextUtils.expandTemplate(getText(2131691771), new CharSequence[] { ((Formatter.BytesResult)localObject2).value, ((Formatter.BytesResult)localObject2).units }));
        this.mInternalSummary.setSummary(getString(2131691773, new Object[] { Formatter.formatFileSize((Context)localObject1, l1) }));
        if (this.mInternalCategory.getPreferenceCount() > 0) {
          getPreferenceScreen().addPreference(this.mInternalCategory);
        }
        if (this.mExternalCategory.getPreferenceCount() > 0) {
          getPreferenceScreen().addPreference(this.mExternalCategory);
        }
        if ((this.mInternalCategory.getPreferenceCount() == 2) && (this.mExternalCategory.getPreferenceCount() == 0))
        {
          if (this.shouldFinish)
          {
            finish();
            this.shouldFinish = false;
            return;
          }
          this.mIsFinished = true;
          localObject1 = new Bundle();
          ((Bundle)localObject1).putString("android.os.storage.extra.VOLUME_ID", "private");
          localObject1 = Utils.onBuildStartFragmentIntent(getActivity(), PrivateVolumeSettings.class.getName(), (Bundle)localObject1, null, 2131693443, null, false);
          ((Intent)localObject1).putExtra("show_drawer_menu", false);
          getActivity().startActivity((Intent)localObject1);
          finish();
        }
        if (!this.shouldFinish) {
          this.shouldFinish = false;
        }
        return;
      }
      i = j;
    }
  }
  
  protected int getHelpResource()
  {
    return 2131693007;
  }
  
  protected int getMetricsCategory()
  {
    return 42;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 99) && (paramInt2 == -1)) {
      this.shouldFinish = true;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mStorageManager = ((StorageManager)getActivity().getSystemService(StorageManager.class));
    this.mStorageManager.registerListener(this.mStorageListener);
    addPreferencesFromResource(2131230761);
    this.mInternalCategory = ((PreferenceCategory)findPreference("storage_internal"));
    this.mExternalCategory = ((PreferenceCategory)findPreference("storage_external"));
    this.mInternalSummary = new StorageSummaryPreference(getPrefContext());
    setHasOptionsMenu(true);
  }
  
  public void onPause()
  {
    super.onPause();
    this.mStorageManager.unregisterListener(this.mStorageListener);
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    Object localObject = paramPreference.getKey();
    if ((paramPreference instanceof StorageVolumePreference))
    {
      paramPreference = this.mStorageManager.findVolumeById((String)localObject);
      if (paramPreference == null) {
        return false;
      }
      if (paramPreference.getState() == 0)
      {
        VolumeUnmountedFragment.show(this, paramPreference.getId());
        return true;
      }
      if (paramPreference.getState() == 6)
      {
        DiskInitFragment.show(this, 2131691782, paramPreference.getDiskId());
        return true;
      }
      if (paramPreference.getType() == 1)
      {
        localObject = new Bundle();
        ((Bundle)localObject).putString("android.os.storage.extra.VOLUME_ID", paramPreference.getId());
        startFragment(this, PrivateVolumeSettings.class.getCanonicalName(), -1, 99, (Bundle)localObject);
        return true;
      }
      if (paramPreference.getType() == 0)
      {
        if (paramPreference.isMountedReadable())
        {
          startActivity(paramPreference.buildBrowseIntent());
          return true;
        }
        localObject = new Bundle();
        ((Bundle)localObject).putString("android.os.storage.extra.VOLUME_ID", paramPreference.getId());
        startFragment(this, PublicVolumeSettings.class.getCanonicalName(), -1, 0, (Bundle)localObject);
        return true;
      }
    }
    else
    {
      if (((String)localObject).startsWith("disk:"))
      {
        DiskInitFragment.show(this, 2131691783, (String)localObject);
        return true;
      }
      paramPreference = new Bundle();
      paramPreference.putString("android.os.storage.extra.FS_UUID", (String)localObject);
      startFragment(this, PrivateVolumeForget.class.getCanonicalName(), 2131691754, 0, paramPreference);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    this.mStorageManager.registerListener(this.mStorageListener);
    refresh();
  }
  
  public static class DiskInitFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment, int paramInt, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.intent.extra.TEXT", paramInt);
      localBundle.putString("android.os.storage.extra.DISK_ID", paramString);
      paramString = new DiskInitFragment();
      paramString.setArguments(localBundle);
      paramString.setTargetFragment(paramFragment, 0);
      paramString.show(paramFragment.getFragmentManager(), "disk_init");
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      paramBundle = getActivity();
      Object localObject = (StorageManager)paramBundle.getSystemService(StorageManager.class);
      int i = getArguments().getInt("android.intent.extra.TEXT");
      final String str = getArguments().getString("android.os.storage.extra.DISK_ID");
      localObject = ((StorageManager)localObject).findDiskById(str);
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBundle);
      localBuilder.setMessage(TextUtils.expandTemplate(getText(i), new CharSequence[] { ((DiskInfo)localObject).getDescription() }));
      localBuilder.setPositiveButton(2131691755, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = new Intent(paramBundle, StorageWizardInit.class);
          paramAnonymousDialogInterface.putExtra("android.os.storage.extra.DISK_ID", str);
          StorageSettings.DiskInitFragment.this.startActivity(paramAnonymousDialogInterface);
        }
      });
      localBuilder.setNegativeButton(2131690993, null);
      return localBuilder.create();
    }
  }
  
  public static class MountTask
    extends AsyncTask<Void, Void, Exception>
  {
    private final Context mContext;
    private final String mDescription;
    private final StorageManager mStorageManager;
    private final String mVolumeId;
    
    public MountTask(Context paramContext, VolumeInfo paramVolumeInfo)
    {
      this.mContext = paramContext.getApplicationContext();
      this.mStorageManager = ((StorageManager)this.mContext.getSystemService(StorageManager.class));
      this.mVolumeId = paramVolumeInfo.getId();
      this.mDescription = this.mStorageManager.getBestVolumeDescription(paramVolumeInfo);
    }
    
    protected Exception doInBackground(Void... paramVarArgs)
    {
      try
      {
        this.mStorageManager.mount(this.mVolumeId);
        return null;
      }
      catch (Exception paramVarArgs) {}
      return paramVarArgs;
    }
    
    protected void onPostExecute(Exception paramException)
    {
      if (paramException == null)
      {
        Toast.makeText(this.mContext, this.mContext.getString(2131691774, new Object[] { this.mDescription }), 0).show();
        return;
      }
      Log.e("StorageSettings", "Failed to mount " + this.mVolumeId, paramException);
      Toast.makeText(this.mContext, this.mContext.getString(2131691775, new Object[] { this.mDescription }), 0).show();
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mLoader;
    
    private SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mLoader = paramSummaryLoader;
    }
    
    private void updateSummary()
    {
      Object localObject1 = ((StorageManager)this.mContext.getSystemService(StorageManager.class)).getVolumes();
      long l2 = 0L;
      long l1 = 0L;
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (VolumeInfo)((Iterator)localObject1).next();
        if ((((VolumeInfo)localObject2).getType() == 0) || (((VolumeInfo)localObject2).getType() == 1))
        {
          localObject2 = ((VolumeInfo)localObject2).getPath();
          if (localObject2 != null)
          {
            l2 += ((File)localObject2).getTotalSpace() - ((File)localObject2).getUsableSpace();
            l1 += ((File)localObject2).getTotalSpace();
          }
        }
      }
      this.mLoader.setSummary(this, this.mContext.getString(2131693585, new Object[] { Formatter.formatFileSize(this.mContext, l2), Formatter.formatFileSize(this.mContext, l1) }));
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean) {
        updateSummary();
      }
    }
  }
  
  public static class UnmountTask
    extends AsyncTask<Void, Void, Exception>
  {
    private final Context mContext;
    private final String mDescription;
    private final StorageManager mStorageManager;
    private final String mVolumeId;
    
    public UnmountTask(Context paramContext, VolumeInfo paramVolumeInfo)
    {
      this.mContext = paramContext.getApplicationContext();
      this.mStorageManager = ((StorageManager)this.mContext.getSystemService(StorageManager.class));
      this.mVolumeId = paramVolumeInfo.getId();
      this.mDescription = this.mStorageManager.getBestVolumeDescription(paramVolumeInfo);
    }
    
    protected Exception doInBackground(Void... paramVarArgs)
    {
      try
      {
        this.mStorageManager.unmount(this.mVolumeId);
        return null;
      }
      catch (Exception paramVarArgs) {}
      return paramVarArgs;
    }
    
    protected void onPostExecute(Exception paramException)
    {
      if (paramException == null)
      {
        Toast.makeText(this.mContext, this.mContext.getString(2131691776, new Object[] { this.mDescription }), 0).show();
        return;
      }
      Log.e("StorageSettings", "Failed to unmount " + this.mVolumeId, paramException);
      Toast.makeText(this.mContext, this.mContext.getString(2131691777, new Object[] { this.mDescription }), 0).show();
    }
  }
  
  public static class VolumeUnmountedFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("android.os.storage.extra.VOLUME_ID", paramString);
      paramString = new VolumeUnmountedFragment();
      paramString.setArguments(localBundle);
      paramString.setTargetFragment(paramFragment, 0);
      paramString.show(paramFragment.getFragmentManager(), "volume_unmounted");
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      paramBundle = getActivity();
      final VolumeInfo localVolumeInfo = ((StorageManager)paramBundle.getSystemService(StorageManager.class)).findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBundle);
      localBuilder.setMessage(TextUtils.expandTemplate(getText(2131691781), new CharSequence[] { localVolumeInfo.getDisk().getDescription() }));
      localBuilder.setPositiveButton(2131691748, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = RestrictedLockUtils.checkIfRestrictionEnforced(StorageSettings.VolumeUnmountedFragment.this.getActivity(), "no_physical_media", UserHandle.myUserId());
          boolean bool = RestrictedLockUtils.hasBaseUserRestriction(StorageSettings.VolumeUnmountedFragment.this.getActivity(), "no_physical_media", UserHandle.myUserId());
          if ((paramAnonymousDialogInterface == null) || (bool))
          {
            new StorageSettings.MountTask(paramBundle, localVolumeInfo).execute(new Void[0]);
            return;
          }
          RestrictedLockUtils.sendShowAdminSupportDetailsIntent(StorageSettings.VolumeUnmountedFragment.this.getActivity(), paramAnonymousDialogInterface);
        }
      });
      localBuilder.setNegativeButton(2131690993, null);
      return localBuilder.create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\StorageSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
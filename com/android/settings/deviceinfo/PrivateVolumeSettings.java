package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.util.Log;
import android.util.OpFeatures;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.deviceinfo.StorageMeasurement;
import com.android.settingslib.deviceinfo.StorageMeasurement.MeasurementDetails;
import com.android.settingslib.deviceinfo.StorageMeasurement.MeasurementReceiver;
import com.google.android.collect.Lists;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.storage.OPMediaProvider;
import com.oneplus.settings.storage.OPMediaProvider.Callback;
import com.oneplus.settings.storage.OPMediaProvider.FileType;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import com.oneplus.settings.utils.OPUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PrivateVolumeSettings
  extends SettingsPreferenceFragment
{
  private static final String AUTHORITY_MEDIA = "com.android.providers.media.documents";
  public static final String BROWSER_TYPE = "browser_type";
  private static final String DIRECTORY_VIDEO = "Video";
  public static final int FILETYPE_AUDIO = 1;
  public static final int FILETYPE_IMAGE = 0;
  public static final int FILETYPE_VIDEO = 2;
  private static final int[] ITEMS_NO_SHOW_SHARED = { 2131691789 };
  private static final int[] ITEMS_SHOW_SHARED = { 2131691789, 2131691790, 2131691791, 2131691792, 2131691794 };
  public static final String NEW_FILEMANAGE_ACTION = "android.intent.action.ONEPLUS_BROWSER_CLASSIFICATION";
  public static final String ONEPLUS_FILEMANAGE_DIR_INTENT = "android.intent.action.ONEPLUS_BROWSER_DIR";
  private static final String STORAGE_CLEANUP_PACKAGE = "com.qti.storagecleaner";
  private static final String STORAGE_CLENUP_CLASS = "com.qti.storagecleaner.CleanerActivity";
  private static final String TAG_CONFIRM_CLEAR_CACHE = "confirmClearCache";
  private static final String TAG_OTHER_INFO = "otherInfo";
  private static final String TAG_RENAME = "rename";
  private static final String TAG_USER_INFO = "userInfo";
  private int mCalculateUsedTimes = 0;
  private Preference mCleanPref;
  Context mContext;
  private UserInfo mCurrentUser;
  private Preference mExplore;
  private int mHeaderPoolIndex;
  private List<PreferenceCategory> mHeaderPreferencePool = Lists.newArrayList();
  private int mItemPoolIndex;
  private List<StorageItemPreference> mItemPreferencePool = Lists.newArrayList();
  private final OPMediaProvider.Callback mLoadMediaCallback = new OPMediaProvider.Callback()
  {
    public void loaded(OPMediaProvider.FileType paramAnonymousFileType, long paramAnonymousLong)
    {
      PrivateVolumeSettings.-wrap1(PrivateVolumeSettings.this, paramAnonymousFileType, paramAnonymousLong);
    }
  };
  private StorageMeasurement mMeasure;
  private Preference mMemoryPreference;
  private boolean mNeedsUpdate;
  private final StorageMeasurement.MeasurementReceiver mReceiver = new StorageMeasurement.MeasurementReceiver()
  {
    public void onDetailsChanged(StorageMeasurement.MeasurementDetails paramAnonymousMeasurementDetails)
    {
      PrivateVolumeSettings.-wrap0(PrivateVolumeSettings.this, paramAnonymousMeasurementDetails);
    }
  };
  private VolumeInfo mSharedVolume;
  private long mStorageExceptMediaUsed;
  private final StorageEventListener mStorageListener = new StorageEventListener()
  {
    public void onVolumeRecordChanged(VolumeRecord paramAnonymousVolumeRecord)
    {
      if (Objects.equals(PrivateVolumeSettings.-get1(PrivateVolumeSettings.this).getFsUuid(), paramAnonymousVolumeRecord.getFsUuid()))
      {
        PrivateVolumeSettings.-set0(PrivateVolumeSettings.this, PrivateVolumeSettings.-get0(PrivateVolumeSettings.this).findVolumeById(PrivateVolumeSettings.-get2(PrivateVolumeSettings.this)));
        PrivateVolumeSettings.-wrap2(PrivateVolumeSettings.this);
      }
    }
    
    public void onVolumeStateChanged(VolumeInfo paramAnonymousVolumeInfo, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (Objects.equals(PrivateVolumeSettings.-get1(PrivateVolumeSettings.this).getId(), paramAnonymousVolumeInfo.getId()))
      {
        PrivateVolumeSettings.-set0(PrivateVolumeSettings.this, paramAnonymousVolumeInfo);
        PrivateVolumeSettings.-wrap2(PrivateVolumeSettings.this);
      }
    }
  };
  private StorageManager mStorageManager;
  private long mStorageMediaAudio;
  private long mStorageMediaImage;
  private long mStorageMediaVideo;
  private long mStorageUsed;
  private StorageSummaryPreference mSummary;
  private UserManager mUserManager;
  private VolumeInfo mVolume;
  private String mVolumeId;
  
  public PrivateVolumeSettings()
  {
    setRetainInstance(true);
  }
  
  private PreferenceCategory addCategory(PreferenceGroup paramPreferenceGroup, CharSequence paramCharSequence)
  {
    PreferenceCategory localPreferenceCategory;
    if (this.mHeaderPoolIndex < this.mHeaderPreferencePool.size()) {
      localPreferenceCategory = (PreferenceCategory)this.mHeaderPreferencePool.get(this.mHeaderPoolIndex);
    }
    for (;;)
    {
      localPreferenceCategory.setTitle(paramCharSequence);
      localPreferenceCategory.removeAll();
      addPreference(paramPreferenceGroup, localPreferenceCategory);
      this.mHeaderPoolIndex += 1;
      return localPreferenceCategory;
      localPreferenceCategory = new PreferenceCategory(getPrefContext(), null, 16842892);
      this.mHeaderPreferencePool.add(localPreferenceCategory);
    }
  }
  
  private void addCleanPreference(PreferenceScreen paramPreferenceScreen, int paramInt)
  {
    this.mCleanPref = new Preference(this.mContext);
    this.mCleanPref.setTitle(paramInt);
    this.mCleanPref.setKey(Integer.toString(paramInt));
    paramPreferenceScreen.addPreference(this.mCleanPref);
  }
  
  private void addDetailItems(PreferenceGroup paramPreferenceGroup, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {}
    for (int[] arrayOfInt = ITEMS_SHOW_SHARED;; arrayOfInt = ITEMS_NO_SHOW_SHARED)
    {
      int i = 0;
      while (i < arrayOfInt.length)
      {
        addItem(paramPreferenceGroup, arrayOfInt[i], null, paramInt);
        i += 1;
      }
    }
  }
  
  private void addItem(PreferenceGroup paramPreferenceGroup, int paramInt1, CharSequence paramCharSequence, int paramInt2)
  {
    StorageItemPreference localStorageItemPreference;
    if (this.mItemPoolIndex < this.mItemPreferencePool.size())
    {
      localStorageItemPreference = (StorageItemPreference)this.mItemPreferencePool.get(this.mItemPoolIndex);
      if (paramCharSequence == null) {
        break label109;
      }
      localStorageItemPreference.setTitle(paramCharSequence);
      localStorageItemPreference.setKey(paramCharSequence.toString());
    }
    for (;;)
    {
      localStorageItemPreference.setSummary(2131691720);
      localStorageItemPreference.userHandle = paramInt2;
      addPreference(paramPreferenceGroup, localStorageItemPreference);
      this.mItemPoolIndex += 1;
      return;
      localStorageItemPreference = buildItem();
      this.mItemPreferencePool.add(localStorageItemPreference);
      break;
      label109:
      localStorageItemPreference.setTitle(paramInt1);
      localStorageItemPreference.setKey(Integer.toString(paramInt1));
    }
  }
  
  private void addMemoryPreference(PreferenceScreen paramPreferenceScreen, int paramInt)
  {
    this.mMemoryPreference = new Preference(this.mContext);
    this.mMemoryPreference.setTitle(paramInt);
    this.mMemoryPreference.setKey(Integer.toString(paramInt));
    paramPreferenceScreen.addPreference(this.mMemoryPreference);
  }
  
  private void addPreference(PreferenceGroup paramPreferenceGroup, Preference paramPreference)
  {
    paramPreference.setOrder(Integer.MAX_VALUE);
    paramPreferenceGroup.addPreference(paramPreference);
  }
  
  private Preference buildAction(int paramInt)
  {
    Preference localPreference = new Preference(getPrefContext());
    localPreference.setTitle(paramInt);
    localPreference.setKey(Integer.toString(paramInt));
    return localPreference;
  }
  
  private StorageItemPreference buildItem()
  {
    return new StorageItemPreference(getPrefContext());
  }
  
  private boolean isProfileOf(UserInfo paramUserInfo1, UserInfo paramUserInfo2)
  {
    if (paramUserInfo1.id != paramUserInfo2.id)
    {
      if (paramUserInfo1.profileGroupId == 55536) {}
    }
    else {
      return paramUserInfo1.profileGroupId == paramUserInfo2.profileGroupId;
    }
    return false;
  }
  
  private boolean isVolumeValid()
  {
    if ((this.mVolume != null) && (this.mVolume.getType() == 1)) {
      return this.mVolume.isMountedReadable();
    }
    return false;
  }
  
  private void setTitle()
  {
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.setTitle(2131690147);
    }
  }
  
  private void startStorageCleanupActivity()
  {
    try
    {
      Intent localIntent = new Intent();
      localIntent.setClassName("com.qti.storagecleaner", "com.qti.storagecleaner.CleanerActivity");
      startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("StorageSettings", "Can't start storage cleanup activity");
    }
  }
  
  private static long totalValues(StorageMeasurement.MeasurementDetails paramMeasurementDetails, int paramInt, String... paramVarArgs)
  {
    long l1 = 0L;
    paramMeasurementDetails = (HashMap)paramMeasurementDetails.mediaSize.get(paramInt);
    if (paramMeasurementDetails != null)
    {
      int i = paramVarArgs.length;
      paramInt = 0;
      for (;;)
      {
        l2 = l1;
        if (paramInt >= i) {
          break;
        }
        String str = paramVarArgs[paramInt];
        l2 = l1;
        if (paramMeasurementDetails.containsKey(str)) {
          l2 = l1 + ((Long)paramMeasurementDetails.get(str)).longValue();
        }
        paramInt += 1;
        l1 = l2;
      }
    }
    Log.w("StorageSettings", "MeasurementDetails mediaSize array does not have key for user " + paramInt);
    long l2 = l1;
    return l2;
  }
  
  private void update()
  {
    if (!isVolumeValid())
    {
      getActivity().finish();
      return;
    }
    setTitle();
    getFragmentManager().invalidateOptionsMenu();
    Activity localActivity = getActivity();
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    localPreferenceScreen.addPreference(new OPPreferenceHeaderMargin(this.mContext));
    if ((!OpFeatures.isSupport(new int[] { 1 })) && (OPUtils.isAppExist(getActivity(), "com.oneplus.security"))) {
      addCleanPreference(localPreferenceScreen, 2131690454);
    }
    addMemoryPreference(localPreferenceScreen, 2131693448);
    addPreference(localPreferenceScreen, this.mSummary);
    List localList = this.mUserManager.getUsers();
    int i2 = localList.size();
    int i;
    boolean bool;
    label156:
    int k;
    int m;
    int j;
    label173:
    UserInfo localUserInfo;
    int n;
    int i1;
    if (i2 > 1)
    {
      i = 1;
      if (this.mSharedVolume == null) {
        break label309;
      }
      bool = this.mSharedVolume.isMountedReadable();
      this.mItemPoolIndex = 0;
      this.mHeaderPoolIndex = 0;
      k = 0;
      m = 0;
      j = 0;
      if (j >= i2) {
        break label367;
      }
      localUserInfo = (UserInfo)localList.get(j);
      n = k;
      i1 = m;
      if (isProfileOf(this.mCurrentUser, localUserInfo))
      {
        m += 1;
        if (!hasMultiAppProfiles()) {
          break label318;
        }
        if (localUserInfo.id != 999)
        {
          if (i == 0) {
            break label315;
          }
          addCategory(localPreferenceScreen, localUserInfo.name);
        }
      }
    }
    label309:
    label315:
    for (;;)
    {
      n = k;
      i1 = m;
      if (localUserInfo.id == 999)
      {
        addDetailItems(localPreferenceScreen, bool, localUserInfo.id);
        i1 = m;
        n = k;
      }
      j += 1;
      k = n;
      m = i1;
      break label173;
      i = 0;
      break;
      bool = false;
      break label156;
    }
    label318:
    if (i != 0) {}
    for (Object localObject = addCategory(localPreferenceScreen, localUserInfo.name);; localObject = localPreferenceScreen)
    {
      addDetailItems((PreferenceGroup)localObject, bool, localUserInfo.id);
      n = k + 1;
      i1 = m;
      break;
    }
    label367:
    if (i2 - k > 0)
    {
      localObject = addCategory(localPreferenceScreen, getText(2131691767));
      i = 0;
      while (i < i2)
      {
        localUserInfo = (UserInfo)localList.get(i);
        if (!isProfileOf(this.mCurrentUser, localUserInfo)) {
          addItem((PreferenceGroup)localObject, 0, localUserInfo.name, localUserInfo.id);
        }
        i += 1;
      }
    }
    addItem(localPreferenceScreen, 2131691793, null, 55536);
    if (bool) {
      addPreference(localPreferenceScreen, this.mExplore);
    }
    localObject = this.mVolume.getPath();
    long l1 = ((File)localObject).getTotalSpace();
    long l2 = l1 - ((File)localObject).getUsableSpace();
    this.mStorageUsed = l2;
    localObject = Formatter.formatBytes(getResources(), l2, 0);
    this.mSummary.setTitle(TextUtils.expandTemplate(getText(2131691771), new CharSequence[] { ((Formatter.BytesResult)localObject).value, ((Formatter.BytesResult)localObject).units }));
    this.mSummary.setSummary(getString(2131691772, new Object[] { Formatter.formatFileSize(localActivity, l1) }));
    this.mSummary.setPercent((int)(100L * l2 / l1));
    this.mMeasure.forceMeasure();
    this.mNeedsUpdate = false;
  }
  
  private void updateDetails(StorageMeasurement.MeasurementDetails paramMeasurementDetails)
  {
    long l3 = 0L;
    long l2 = 0L;
    long l1 = 0L;
    Object localObject = null;
    int i = 0;
    for (;;)
    {
      if (i < this.mItemPoolIndex)
      {
        StorageItemPreference localStorageItemPreference = (StorageItemPreference)this.mItemPreferencePool.get(i);
        int k = localStorageItemPreference.userHandle;
        try
        {
          j = Integer.parseInt(localStorageItemPreference.getKey());
          switch (j)
          {
          default: 
            i += 1;
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          for (;;)
          {
            int j = 0;
            continue;
            l3 += paramMeasurementDetails.appsSize.get(k);
            updatePreference(localStorageItemPreference, l3);
            continue;
            localObject = localStorageItemPreference;
            updatePreference(localStorageItemPreference, paramMeasurementDetails.miscSize.get(k));
            continue;
            l2 += paramMeasurementDetails.cacheSize;
            updatePreference(localStorageItemPreference, paramMeasurementDetails.cacheSize);
            continue;
            long l4 = paramMeasurementDetails.usersSize.get(k);
            l1 += l4;
            updatePreference(localStorageItemPreference, l4);
          }
        }
      }
    }
    this.mCalculateUsedTimes += 1;
    if ((localObject != null) && (this.mCalculateUsedTimes > OPMediaProvider.sTypeArray.length))
    {
      this.mStorageExceptMediaUsed = (l3 + l2 + l1);
      l1 = this.mStorageExceptMediaUsed + this.mStorageMediaImage + this.mStorageMediaVideo + this.mStorageMediaAudio;
      if (this.mStorageUsed > l1) {
        updatePreference((StorageItemPreference)localObject, this.mStorageUsed - l1);
      }
    }
  }
  
  private void updateMediaDetails(OPMediaProvider.FileType paramFileType, long paramLong)
  {
    Object localObject1 = null;
    int i = 0;
    for (;;)
    {
      if (i < this.mItemPoolIndex)
      {
        StorageItemPreference localStorageItemPreference = (StorageItemPreference)this.mItemPreferencePool.get(i);
        int j = localStorageItemPreference.userHandle;
        try
        {
          j = Integer.parseInt(localStorageItemPreference.getKey());
          Object localObject2 = localObject1;
          switch (j)
          {
          default: 
            localObject2 = localObject1;
          case 2131691793: 
            i += 1;
            localObject1 = localObject2;
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          for (;;)
          {
            j = 0;
            continue;
            Object localObject3 = localObject1;
            if (paramFileType == OPMediaProvider.FileType.Image)
            {
              this.mStorageMediaImage = paramLong;
              updatePreference(localStorageItemPreference, this.mStorageMediaImage);
              localObject3 = localObject1;
              continue;
              localObject3 = localObject1;
              if (paramFileType == OPMediaProvider.FileType.Video)
              {
                this.mStorageMediaVideo = paramLong;
                updatePreference(localStorageItemPreference, this.mStorageMediaVideo);
                localObject3 = localObject1;
                continue;
                localObject3 = localObject1;
                if (paramFileType == OPMediaProvider.FileType.Audio)
                {
                  this.mStorageMediaAudio = paramLong;
                  updatePreference(localStorageItemPreference, this.mStorageMediaAudio);
                  localObject3 = localObject1;
                  continue;
                  localObject3 = localStorageItemPreference;
                }
              }
            }
          }
        }
      }
    }
    this.mCalculateUsedTimes += 1;
    if ((localObject1 != null) && (this.mCalculateUsedTimes > OPMediaProvider.sTypeArray.length))
    {
      paramLong = this.mStorageExceptMediaUsed + this.mStorageMediaImage + this.mStorageMediaVideo + this.mStorageMediaAudio;
      if (this.mStorageUsed > paramLong) {
        updatePreference((StorageItemPreference)localObject1, this.mStorageUsed - paramLong);
      }
    }
  }
  
  private void updatePreference(StorageItemPreference paramStorageItemPreference, long paramLong)
  {
    paramStorageItemPreference.setStorageSize(paramLong, this.mVolume.getPath().getTotalSpace());
  }
  
  protected int getMetricsCategory()
  {
    return 42;
  }
  
  public boolean hasMultiAppProfiles()
  {
    boolean bool2 = false;
    Iterator localIterator = this.mUserManager.getProfiles(UserHandle.myUserId()).iterator();
    do
    {
      bool1 = bool2;
      if (!localIterator.hasNext()) {
        break;
      }
    } while (((UserInfo)localIterator.next()).id != 999);
    boolean bool1 = true;
    return bool1;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getActivity();
    this.mContext = getActivity();
    this.mUserManager = ((UserManager)paramBundle.getSystemService(UserManager.class));
    this.mStorageManager = ((StorageManager)paramBundle.getSystemService(StorageManager.class));
    this.mVolumeId = getArguments().getString("android.os.storage.extra.VOLUME_ID");
    this.mVolume = this.mStorageManager.findVolumeById(this.mVolumeId);
    this.mSharedVolume = this.mStorageManager.findEmulatedForPrivate(this.mVolume);
    this.mMeasure = new StorageMeasurement(paramBundle, this.mVolume, this.mSharedVolume);
    this.mMeasure.setReceiver(this.mReceiver);
    if (!isVolumeValid())
    {
      getActivity().finish();
      return;
    }
    addPreferencesFromResource(2131230762);
    getPreferenceScreen().setOrderingAsAdded(true);
    this.mSummary = new StorageSummaryPreference(getPrefContext());
    this.mCurrentUser = this.mUserManager.getUserInfo(UserHandle.myUserId());
    this.mExplore = buildAction(2131691756);
    this.mNeedsUpdate = true;
    setHasOptionsMenu(true);
    setResult(-1);
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2132017158, paramMenu);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mMeasure != null) {
      this.mMeasure.onDestroy();
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    Activity localActivity = getActivity();
    Bundle localBundle = new Bundle();
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    case 2131362856: 
      RenameFragment.show(this, this.mVolume);
      return true;
    case 2131362857: 
      new StorageSettings.MountTask(localActivity, this.mVolume).execute(new Void[0]);
      return true;
    case 2131362858: 
      localBundle.putString("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
      startFragment(this, PrivateVolumeUnmount.class.getCanonicalName(), 2131691749, 0, localBundle);
      return true;
    case 2131362859: 
      localBundle.putString("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
      startFragment(this, PrivateVolumeFormat.class.getCanonicalName(), 2131691750, 0, localBundle);
      return true;
    case 2131362860: 
      paramMenuItem = new Intent(localActivity, StorageWizardMigrateConfirm.class);
      paramMenuItem.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
      startActivity(paramMenuItem);
      return true;
    }
    startStorageCleanupActivity();
    return true;
  }
  
  public void onPause()
  {
    super.onPause();
    this.mStorageManager.unregisterListener(this.mStorageListener);
  }
  
  /* Error */
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 269
    //   4: ifeq +138 -> 142
    //   7: aload_1
    //   8: checkcast 269	com/android/settings/deviceinfo/StorageItemPreference
    //   11: getfield 283	com/android/settings/deviceinfo/StorageItemPreference:userHandle	I
    //   14: istore_2
    //   15: aload_1
    //   16: invokevirtual 766	android/support/v7/preference/Preference:getKey	()Ljava/lang/String;
    //   19: invokestatic 550	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   22: istore_3
    //   23: aconst_null
    //   24: astore 4
    //   26: aconst_null
    //   27: astore 5
    //   29: iload_3
    //   30: lookupswitch	default:+90->120, 0:+594->624, 2131690454:+124->154, 2131691756:+553->583, 2131691789:+203->233, 2131691790:+282->312, 2131691791:+354->384, 2131691792:+426->456, 2131691793:+547->577, 2131691794:+498->528, 2131693448:+176->206
    //   120: aload 5
    //   122: astore 4
    //   124: aload 4
    //   126: ifnull +564 -> 690
    //   129: iload_2
    //   130: iconst_m1
    //   131: if_icmpne +507 -> 638
    //   134: aload_0
    //   135: aload 4
    //   137: invokevirtual 343	com/android/settings/deviceinfo/PrivateVolumeSettings:startActivity	(Landroid/content/Intent;)V
    //   140: iconst_1
    //   141: ireturn
    //   142: iconst_m1
    //   143: istore_2
    //   144: goto -129 -> 15
    //   147: astore 4
    //   149: iconst_0
    //   150: istore_3
    //   151: goto -128 -> 23
    //   154: new 334	android/content/Intent
    //   157: dup
    //   158: ldc_w 768
    //   161: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   164: astore_1
    //   165: aload_0
    //   166: aload_1
    //   167: invokevirtual 343	com/android/settings/deviceinfo/PrivateVolumeSettings:startActivity	(Landroid/content/Intent;)V
    //   170: iconst_1
    //   171: ireturn
    //   172: astore_1
    //   173: aload 4
    //   175: astore_1
    //   176: ldc_w 345
    //   179: new 380	java/lang/StringBuilder
    //   182: dup
    //   183: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   186: ldc_w 772
    //   189: invokevirtual 387	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: aload_1
    //   193: invokevirtual 775	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   196: invokevirtual 391	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   199: invokestatic 394	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   202: pop
    //   203: goto -33 -> 170
    //   206: new 646	android/os/Bundle
    //   209: dup
    //   210: invokespecial 703	android/os/Bundle:<init>	()V
    //   213: astore_1
    //   214: aload_0
    //   215: aload_0
    //   216: ldc_w 777
    //   219: invokevirtual 739	java/lang/Class:getCanonicalName	()Ljava/lang/String;
    //   222: ldc_w 436
    //   225: iconst_0
    //   226: aload_1
    //   227: invokevirtual 744	com/android/settings/deviceinfo/PrivateVolumeSettings:startFragment	(Landroid/app/Fragment;Ljava/lang/String;IILandroid/os/Bundle;)Z
    //   230: pop
    //   231: iconst_1
    //   232: ireturn
    //   233: new 646	android/os/Bundle
    //   236: dup
    //   237: invokespecial 703	android/os/Bundle:<init>	()V
    //   240: astore 4
    //   242: aload 4
    //   244: ldc_w 779
    //   247: ldc_w 781
    //   250: invokevirtual 784	java/lang/Class:getName	()Ljava/lang/String;
    //   253: invokevirtual 732	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   256: aload 4
    //   258: ldc_w 786
    //   261: aload_0
    //   262: getfield 128	com/android/settings/deviceinfo/PrivateVolumeSettings:mVolume	Landroid/os/storage/VolumeInfo;
    //   265: invokevirtual 789	android/os/storage/VolumeInfo:getFsUuid	()Ljava/lang/String;
    //   268: invokevirtual 732	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   271: aload 4
    //   273: ldc_w 791
    //   276: aload_0
    //   277: getfield 128	com/android/settings/deviceinfo/PrivateVolumeSettings:mVolume	Landroid/os/storage/VolumeInfo;
    //   280: invokevirtual 794	android/os/storage/VolumeInfo:getDescription	()Ljava/lang/String;
    //   283: invokevirtual 732	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
    //   286: aload_0
    //   287: invokevirtual 325	com/android/settings/deviceinfo/PrivateVolumeSettings:getActivity	()Landroid/app/Activity;
    //   290: ldc_w 796
    //   293: invokevirtual 784	java/lang/Class:getName	()Ljava/lang/String;
    //   296: aload 4
    //   298: aconst_null
    //   299: ldc_w 797
    //   302: aconst_null
    //   303: iconst_0
    //   304: invokestatic 803	com/android/settings/Utils:onBuildStartFragmentIntent	(Landroid/content/Context;Ljava/lang/String;Landroid/os/Bundle;Ljava/lang/String;ILjava/lang/CharSequence;Z)Landroid/content/Intent;
    //   307: astore 4
    //   309: goto -185 -> 124
    //   312: new 334	android/content/Intent
    //   315: dup
    //   316: ldc_w 805
    //   319: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   322: astore 4
    //   324: aload_0
    //   325: getfield 236	com/android/settings/deviceinfo/PrivateVolumeSettings:mContext	Landroid/content/Context;
    //   328: aload 4
    //   330: ldc 56
    //   332: invokestatic 809	com/oneplus/settings/utils/OPUtils:isActionExist	(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/String;)Z
    //   335: ifeq +32 -> 367
    //   338: aload 4
    //   340: ldc 56
    //   342: invokevirtual 813	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   345: pop
    //   346: aload 4
    //   348: ldc 40
    //   350: iconst_0
    //   351: invokevirtual 816	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   354: pop
    //   355: aload 4
    //   357: ldc_w 818
    //   360: invokevirtual 820	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   363: pop
    //   364: goto -240 -> 124
    //   367: aload 4
    //   369: ldc 37
    //   371: ldc_w 822
    //   374: invokestatic 828	android/provider/DocumentsContract:buildRootUri	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
    //   377: invokevirtual 832	android/content/Intent:setData	(Landroid/net/Uri;)Landroid/content/Intent;
    //   380: pop
    //   381: goto -26 -> 355
    //   384: new 334	android/content/Intent
    //   387: dup
    //   388: ldc_w 805
    //   391: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   394: astore 4
    //   396: aload_0
    //   397: getfield 236	com/android/settings/deviceinfo/PrivateVolumeSettings:mContext	Landroid/content/Context;
    //   400: aload 4
    //   402: ldc 56
    //   404: invokestatic 809	com/oneplus/settings/utils/OPUtils:isActionExist	(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/String;)Z
    //   407: ifeq +32 -> 439
    //   410: aload 4
    //   412: ldc 56
    //   414: invokevirtual 813	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   417: pop
    //   418: aload 4
    //   420: ldc 40
    //   422: iconst_2
    //   423: invokevirtual 816	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   426: pop
    //   427: aload 4
    //   429: ldc_w 818
    //   432: invokevirtual 820	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   435: pop
    //   436: goto -312 -> 124
    //   439: aload 4
    //   441: ldc 37
    //   443: ldc_w 834
    //   446: invokestatic 828	android/provider/DocumentsContract:buildRootUri	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
    //   449: invokevirtual 832	android/content/Intent:setData	(Landroid/net/Uri;)Landroid/content/Intent;
    //   452: pop
    //   453: goto -26 -> 427
    //   456: new 334	android/content/Intent
    //   459: dup
    //   460: ldc_w 805
    //   463: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   466: astore 4
    //   468: aload_0
    //   469: getfield 236	com/android/settings/deviceinfo/PrivateVolumeSettings:mContext	Landroid/content/Context;
    //   472: aload 4
    //   474: ldc 56
    //   476: invokestatic 809	com/oneplus/settings/utils/OPUtils:isActionExist	(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/String;)Z
    //   479: ifeq +32 -> 511
    //   482: aload 4
    //   484: ldc 56
    //   486: invokevirtual 813	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
    //   489: pop
    //   490: aload 4
    //   492: ldc 40
    //   494: iconst_1
    //   495: invokevirtual 816	android/content/Intent:putExtra	(Ljava/lang/String;I)Landroid/content/Intent;
    //   498: pop
    //   499: aload 4
    //   501: ldc_w 818
    //   504: invokevirtual 820	android/content/Intent:addCategory	(Ljava/lang/String;)Landroid/content/Intent;
    //   507: pop
    //   508: goto -384 -> 124
    //   511: aload 4
    //   513: ldc 37
    //   515: ldc_w 836
    //   518: invokestatic 828	android/provider/DocumentsContract:buildRootUri	(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
    //   521: invokevirtual 832	android/content/Intent:setData	(Landroid/net/Uri;)Landroid/content/Intent;
    //   524: pop
    //   525: goto -26 -> 499
    //   528: aload_0
    //   529: getfield 236	com/android/settings/deviceinfo/PrivateVolumeSettings:mContext	Landroid/content/Context;
    //   532: aconst_null
    //   533: ldc 56
    //   535: invokestatic 809	com/oneplus/settings/utils/OPUtils:isActionExist	(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/String;)Z
    //   538: ifeq +18 -> 556
    //   541: aload_0
    //   542: new 334	android/content/Intent
    //   545: dup
    //   546: ldc 59
    //   548: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   551: invokevirtual 343	com/android/settings/deviceinfo/PrivateVolumeSettings:startActivity	(Landroid/content/Intent;)V
    //   554: iconst_1
    //   555: ireturn
    //   556: aload_0
    //   557: aload_0
    //   558: getfield 123	com/android/settings/deviceinfo/PrivateVolumeSettings:mStorageManager	Landroid/os/storage/StorageManager;
    //   561: aload_0
    //   562: getfield 128	com/android/settings/deviceinfo/PrivateVolumeSettings:mVolume	Landroid/os/storage/VolumeInfo;
    //   565: invokevirtual 840	android/os/storage/StorageManager:getBestVolumeDescription	(Landroid/os/storage/VolumeInfo;)Ljava/lang/String;
    //   568: aload_0
    //   569: getfield 450	com/android/settings/deviceinfo/PrivateVolumeSettings:mSharedVolume	Landroid/os/storage/VolumeInfo;
    //   572: invokestatic 843	com/android/settings/deviceinfo/PrivateVolumeSettings$OtherInfoFragment:show	(Landroid/app/Fragment;Ljava/lang/String;Landroid/os/storage/VolumeInfo;)V
    //   575: iconst_1
    //   576: ireturn
    //   577: aload_0
    //   578: invokestatic 846	com/android/settings/deviceinfo/PrivateVolumeSettings$ConfirmClearCacheFragment:show	(Landroid/app/Fragment;)V
    //   581: iconst_1
    //   582: ireturn
    //   583: new 334	android/content/Intent
    //   586: dup
    //   587: ldc 59
    //   589: invokespecial 770	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   592: astore 5
    //   594: aload 5
    //   596: astore 4
    //   598: aload_0
    //   599: getfield 236	com/android/settings/deviceinfo/PrivateVolumeSettings:mContext	Landroid/content/Context;
    //   602: aload 5
    //   604: ldc 59
    //   606: invokestatic 809	com/oneplus/settings/utils/OPUtils:isActionExist	(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/String;)Z
    //   609: ifne -485 -> 124
    //   612: aload_0
    //   613: getfield 450	com/android/settings/deviceinfo/PrivateVolumeSettings:mSharedVolume	Landroid/os/storage/VolumeInfo;
    //   616: invokevirtual 850	android/os/storage/VolumeInfo:buildBrowseIntent	()Landroid/content/Intent;
    //   619: astore 4
    //   621: goto -497 -> 124
    //   624: aload_0
    //   625: aload_1
    //   626: invokevirtual 854	android/support/v7/preference/Preference:getTitle	()Ljava/lang/CharSequence;
    //   629: aload_1
    //   630: invokevirtual 857	android/support/v7/preference/Preference:getSummary	()Ljava/lang/CharSequence;
    //   633: invokestatic 860	com/android/settings/deviceinfo/PrivateVolumeSettings$UserInfoFragment:show	(Landroid/app/Fragment;Ljava/lang/CharSequence;Ljava/lang/CharSequence;)V
    //   636: iconst_1
    //   637: ireturn
    //   638: aload_0
    //   639: invokevirtual 325	com/android/settings/deviceinfo/PrivateVolumeSettings:getActivity	()Landroid/app/Activity;
    //   642: aload 4
    //   644: new 604	android/os/UserHandle
    //   647: dup
    //   648: iload_2
    //   649: invokespecial 862	android/os/UserHandle:<init>	(I)V
    //   652: invokevirtual 866	android/app/Activity:startActivityAsUser	(Landroid/content/Intent;Landroid/os/UserHandle;)V
    //   655: goto -515 -> 140
    //   658: astore_1
    //   659: ldc_w 345
    //   662: new 380	java/lang/StringBuilder
    //   665: dup
    //   666: invokespecial 381	java/lang/StringBuilder:<init>	()V
    //   669: ldc_w 772
    //   672: invokevirtual 387	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   675: aload 4
    //   677: invokevirtual 775	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   680: invokevirtual 391	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   683: invokestatic 394	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   686: pop
    //   687: goto -547 -> 140
    //   690: aload_0
    //   691: aload_1
    //   692: invokespecial 868	com/android/settings/SettingsPreferenceFragment:onPreferenceTreeClick	(Landroid/support/v7/preference/Preference;)Z
    //   695: ireturn
    //   696: astore 4
    //   698: goto -522 -> 176
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	701	0	this	PrivateVolumeSettings
    //   0	701	1	paramPreference	Preference
    //   14	635	2	i	int
    //   22	129	3	j	int
    //   24	112	4	localObject1	Object
    //   147	27	4	localNumberFormatException	NumberFormatException
    //   240	436	4	localObject2	Object
    //   696	1	4	localActivityNotFoundException	ActivityNotFoundException
    //   27	576	5	localIntent	Intent
    // Exception table:
    //   from	to	target	type
    //   15	23	147	java/lang/NumberFormatException
    //   154	165	172	android/content/ActivityNotFoundException
    //   134	140	658	android/content/ActivityNotFoundException
    //   638	655	658	android/content/ActivityNotFoundException
    //   165	170	696	android/content/ActivityNotFoundException
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool2 = false;
    if (!isVolumeValid()) {
      return;
    }
    MenuItem localMenuItem3 = paramMenu.findItem(2131362856);
    MenuItem localMenuItem4 = paramMenu.findItem(2131362857);
    MenuItem localMenuItem5 = paramMenu.findItem(2131362858);
    MenuItem localMenuItem6 = paramMenu.findItem(2131362859);
    MenuItem localMenuItem1 = paramMenu.findItem(2131362860);
    MenuItem localMenuItem2 = paramMenu.findItem(2131362861);
    paramMenu.findItem(2131362862).setVisible(false);
    if ("private".equals(this.mVolume.getId()))
    {
      localMenuItem3.setVisible(false);
      localMenuItem4.setVisible(false);
      localMenuItem5.setVisible(false);
      localMenuItem6.setVisible(false);
      localMenuItem6.setTitle(2131691751);
      paramMenu = getActivity().getPackageManager().getPrimaryStorageCurrentVolume();
      bool1 = bool2;
      if (paramMenu != null)
      {
        bool1 = bool2;
        if (paramMenu.getType() == 1) {
          if (!Objects.equals(this.mVolume, paramMenu)) {
            break label303;
          }
        }
      }
    }
    label236:
    label298:
    label303:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localMenuItem1.setVisible(bool1);
      localMenuItem2.setVisible(getResources().getBoolean(2131558437));
      return;
      if (this.mVolume.getType() == 1)
      {
        bool1 = true;
        localMenuItem3.setVisible(bool1);
        if (this.mVolume.getState() != 0) {
          break label298;
        }
      }
      for (bool1 = true;; bool1 = false)
      {
        localMenuItem4.setVisible(bool1);
        localMenuItem5.setVisible(this.mVolume.isMountedReadable());
        localMenuItem6.setVisible(true);
        break;
        bool1 = false;
        break label236;
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mVolume = this.mStorageManager.findVolumeById(this.mVolumeId);
    if (!isVolumeValid())
    {
      getActivity().finish();
      return;
    }
    this.mStorageManager.registerListener(this.mStorageListener);
    if (this.mNeedsUpdate) {
      update();
    }
    for (;;)
    {
      OPMediaProvider.startLoadInfo(SettingsBaseApplication.mApplication, OPMediaProvider.FileType.All, this.mLoadMediaCallback);
      return;
      setTitle();
    }
  }
  
  private static class ClearCacheObserver
    extends IPackageDataObserver.Stub
  {
    private int mRemaining;
    private final PrivateVolumeSettings mTarget;
    
    public ClearCacheObserver(PrivateVolumeSettings paramPrivateVolumeSettings, int paramInt)
    {
      this.mTarget = paramPrivateVolumeSettings;
      this.mRemaining = paramInt;
    }
    
    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      try
      {
        int i = this.mRemaining - 1;
        this.mRemaining = i;
        if (i == 0) {
          this.mTarget.getActivity().runOnUiThread(new Runnable()
          {
            public void run()
            {
              PrivateVolumeSettings.-wrap2(PrivateVolumeSettings.this);
            }
          });
        }
        return;
      }
      finally {}
    }
  }
  
  public static class ConfirmClearCacheFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment)
    {
      if (!paramFragment.isAdded()) {
        return;
      }
      ConfirmClearCacheFragment localConfirmClearCacheFragment = new ConfirmClearCacheFragment();
      localConfirmClearCacheFragment.setTargetFragment(paramFragment, 0);
      localConfirmClearCacheFragment.show(paramFragment.getFragmentManager(), "confirmClearCache");
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      paramBundle = getActivity();
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBundle);
      localBuilder.setTitle(2131691735);
      localBuilder.setMessage(getString(2131691736));
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = (PrivateVolumeSettings)PrivateVolumeSettings.ConfirmClearCacheFragment.this.getTargetFragment();
          PackageManager localPackageManager = paramBundle.getPackageManager();
          int[] arrayOfInt = ((UserManager)paramBundle.getSystemService(UserManager.class)).getProfileIdsWithDisabled(paramBundle.getUserId());
          int i = arrayOfInt.length;
          paramAnonymousInt = 0;
          while (paramAnonymousInt < i)
          {
            int j = arrayOfInt[paramAnonymousInt];
            Object localObject = localPackageManager.getInstalledPackagesAsUser(0, j);
            PrivateVolumeSettings.ClearCacheObserver localClearCacheObserver = new PrivateVolumeSettings.ClearCacheObserver(paramAnonymousDialogInterface, ((List)localObject).size());
            localObject = ((Iterable)localObject).iterator();
            while (((Iterator)localObject).hasNext()) {
              localPackageManager.deleteApplicationCacheFilesAsUser(((PackageInfo)((Iterator)localObject).next()).packageName, j, localClearCacheObserver);
            }
            paramAnonymousInt += 1;
          }
        }
      });
      localBuilder.setNegativeButton(17039360, null);
      return localBuilder.create();
    }
  }
  
  public static class OtherInfoFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment, String paramString, VolumeInfo paramVolumeInfo)
    {
      if (!paramFragment.isAdded()) {
        return;
      }
      OtherInfoFragment localOtherInfoFragment = new OtherInfoFragment();
      localOtherInfoFragment.setTargetFragment(paramFragment, 0);
      Bundle localBundle = new Bundle();
      localBundle.putString("android.intent.extra.TITLE", paramString);
      localBundle.putParcelable("android.intent.extra.INTENT", paramVolumeInfo.buildBrowseIntent());
      localOtherInfoFragment.setArguments(localBundle);
      localOtherInfoFragment.show(paramFragment.getFragmentManager(), "otherInfo");
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Object localObject = getActivity();
      paramBundle = getArguments().getString("android.intent.extra.TITLE");
      final Intent localIntent = (Intent)getArguments().getParcelable("android.intent.extra.INTENT");
      localObject = new AlertDialog.Builder((Context)localObject);
      ((AlertDialog.Builder)localObject).setMessage(TextUtils.expandTemplate(getText(2131691797), new CharSequence[] { paramBundle }));
      ((AlertDialog.Builder)localObject).setPositiveButton(2131691756, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          PrivateVolumeSettings.OtherInfoFragment.this.startActivity(localIntent);
        }
      });
      ((AlertDialog.Builder)localObject).setNegativeButton(17039360, null);
      return ((AlertDialog.Builder)localObject).create();
    }
  }
  
  public static class RenameFragment
    extends DialogFragment
  {
    public static void show(PrivateVolumeSettings paramPrivateVolumeSettings, VolumeInfo paramVolumeInfo)
    {
      if (!paramPrivateVolumeSettings.isAdded()) {
        return;
      }
      RenameFragment localRenameFragment = new RenameFragment();
      localRenameFragment.setTargetFragment(paramPrivateVolumeSettings, 0);
      Bundle localBundle = new Bundle();
      localBundle.putString("android.os.storage.extra.FS_UUID", paramVolumeInfo.getFsUuid());
      localRenameFragment.setArguments(localBundle);
      localRenameFragment.show(paramPrivateVolumeSettings.getFragmentManager(), "rename");
    }
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      Object localObject1 = getActivity();
      paramBundle = (StorageManager)((Context)localObject1).getSystemService(StorageManager.class);
      final String str = getArguments().getString("android.os.storage.extra.FS_UUID");
      paramBundle.findVolumeByUuid(str);
      VolumeRecord localVolumeRecord = paramBundle.findRecordByUuid(str);
      getArguments().getString("android.intent.extra.TITLE");
      Object localObject2 = (Intent)getArguments().getParcelable("android.intent.extra.INTENT");
      if (OPUtils.isActionExist((Context)localObject1, (Intent)localObject2, "android.intent.action.ONEPLUS_BROWSER_CLASSIFICATION")) {
        ((Intent)localObject2).setAction("android.intent.action.ONEPLUS_BROWSER_CLASSIFICATION");
      }
      localObject1 = new AlertDialog.Builder((Context)localObject1);
      localObject2 = LayoutInflater.from(((AlertDialog.Builder)localObject1).getContext()).inflate(2130968687, null, false);
      final EditText localEditText = (EditText)((View)localObject2).findViewById(2131362124);
      localEditText.setText(localVolumeRecord.getNickname());
      ((AlertDialog.Builder)localObject1).setTitle(2131691780);
      ((AlertDialog.Builder)localObject1).setView((View)localObject2);
      ((AlertDialog.Builder)localObject1).setPositiveButton(2131690997, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramBundle.setVolumeNickname(str, localEditText.getText().toString());
        }
      });
      ((AlertDialog.Builder)localObject1).setNegativeButton(2131690993, null);
      return ((AlertDialog.Builder)localObject1).create();
    }
  }
  
  public static class UserInfoFragment
    extends DialogFragment
  {
    public static void show(Fragment paramFragment, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
    {
      if (!paramFragment.isAdded()) {
        return;
      }
      UserInfoFragment localUserInfoFragment = new UserInfoFragment();
      localUserInfoFragment.setTargetFragment(paramFragment, 0);
      Bundle localBundle = new Bundle();
      localBundle.putCharSequence("android.intent.extra.TITLE", paramCharSequence1);
      localBundle.putCharSequence("android.intent.extra.SUBJECT", paramCharSequence2);
      localUserInfoFragment.setArguments(localBundle);
      localUserInfoFragment.show(paramFragment.getFragmentManager(), "userInfo");
    }
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Object localObject = getActivity();
      paramBundle = getArguments().getCharSequence("android.intent.extra.TITLE");
      CharSequence localCharSequence = getArguments().getCharSequence("android.intent.extra.SUBJECT");
      localObject = new AlertDialog.Builder((Context)localObject);
      ((AlertDialog.Builder)localObject).setMessage(TextUtils.expandTemplate(getText(2131691799), new CharSequence[] { paramBundle, localCharSequence }));
      ((AlertDialog.Builder)localObject).setPositiveButton(17039370, null);
      return ((AlertDialog.Builder)localObject).create();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\deviceinfo\PrivateVolumeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
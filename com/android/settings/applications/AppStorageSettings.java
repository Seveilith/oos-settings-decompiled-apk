package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.MutableInt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.android.settings.Utils;
import com.android.settings.deviceinfo.StorageWizardMoveConfirm;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import com.android.settingslib.applications.ApplicationsState.Callbacks;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

public class AppStorageSettings
  extends AppInfoWithHeader
  implements View.OnClickListener, ApplicationsState.Callbacks, DialogInterface.OnClickListener
{
  private static final int DLG_CANNOT_CLEAR_DATA = 2;
  private static final int DLG_CLEAR_DATA = 1;
  private static final String KEY_APP_SIZE = "app_size";
  private static final String KEY_CACHE_SIZE = "cache_size";
  private static final String KEY_CHANGE_STORAGE = "change_storage_button";
  private static final String KEY_CLEAR_CACHE = "clear_cache_button";
  private static final String KEY_CLEAR_DATA = "clear_data_button";
  private static final String KEY_CLEAR_URI = "clear_uri_button";
  private static final String KEY_DATA_SIZE = "data_size";
  private static final String KEY_EXTERNAL_CODE_SIZE = "external_code_size";
  private static final String KEY_EXTERNAL_DATA_SIZE = "external_data_size";
  private static final String KEY_STORAGE_CATEGORY = "storage_category";
  private static final String KEY_STORAGE_SPACE = "storage_space";
  private static final String KEY_STORAGE_USED = "storage_used";
  private static final String KEY_TOTAL_SIZE = "total_size";
  private static final String KEY_URI_CATEGORY = "uri_category";
  private static final int MSG_CLEAR_CACHE = 3;
  private static final int MSG_CLEAR_USER_DATA = 1;
  private static final int OP_FAILED = 2;
  private static final int OP_SUCCESSFUL = 1;
  public static final int REQUEST_MANAGE_SPACE = 2;
  private static final int SIZE_INVALID = -1;
  private static final String TAG = AppStorageSettings.class.getSimpleName();
  private Preference mAppSize;
  private Preference mCacheSize;
  private boolean mCanClearData = true;
  private VolumeInfo[] mCandidates;
  private Button mChangeStorageButton;
  private Button mClearCacheButton;
  private ClearCacheObserver mClearCacheObserver;
  private Button mClearDataButton;
  private ClearUserDataObserver mClearDataObserver;
  private LayoutPreference mClearUri;
  private Button mClearUriButton;
  private CharSequence mComputingStr;
  private Preference mDataSize;
  private AlertDialog.Builder mDialogBuilder;
  private Preference mExternalCodeSize;
  private Preference mExternalDataSize;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (AppStorageSettings.this.getView() == null) {
        return;
      }
      switch (paramAnonymousMessage.what)
      {
      case 2: 
      default: 
        return;
      case 1: 
        AppStorageSettings.-wrap1(AppStorageSettings.this, paramAnonymousMessage);
        return;
      }
      AppStorageSettings.this.mState.requestSize(AppStorageSettings.this.mPackageName, AppStorageSettings.this.mUserId);
    }
  };
  private boolean mHaveSizes = false;
  private CharSequence mInvalidSizeStr;
  private long mLastCacheSize = -1L;
  private long mLastCodeSize = -1L;
  private long mLastDataSize = -1L;
  private long mLastExternalCodeSize = -1L;
  private long mLastExternalDataSize = -1L;
  private long mLastTotalSize = -1L;
  private Preference mStorageUsed;
  private Preference mTotalSize;
  private PreferenceCategory mUri;
  
  private void clearUriPermissions()
  {
    ((ActivityManager)getActivity().getSystemService("activity")).clearGrantedUriPermissions(this.mAppEntry.info.packageName);
    refreshGrantedUriPermissions();
  }
  
  private static CharSequence getSize(ApplicationsState.AppEntry paramAppEntry, Context paramContext)
  {
    long l = paramAppEntry.size;
    if (l == -1L) {
      return paramContext.getText(2131692150);
    }
    return Formatter.formatFileSize(paramContext, l);
  }
  
  private String getSizeStr(long paramLong)
  {
    if (paramLong == -1L) {
      return this.mInvalidSizeStr.toString();
    }
    return Formatter.formatFileSize(getActivity(), paramLong);
  }
  
  public static CharSequence getSummary(ApplicationsState.AppEntry paramAppEntry, Context paramContext)
  {
    if ((paramAppEntry.size == -2L) || (paramAppEntry.size == -1L)) {
      return paramContext.getText(2131692149);
    }
    if ((paramAppEntry.info.flags & 0x40000) != 0) {}
    for (int i = 2131693383;; i = 2131693382)
    {
      String str = paramContext.getString(i);
      return paramContext.getString(2131693376, new Object[] { getSize(paramAppEntry, paramContext), str });
    }
  }
  
  private void initDataButtons()
  {
    int j = 1;
    int i;
    boolean bool;
    if (this.mAppEntry.info.manageSpaceActivityName != null)
    {
      i = 1;
      bool = this.mDpm.packageHasActiveAdmins(this.mPackageName);
      if ((this.mAppEntry.info.flags & 0x41) != 1) {
        break label162;
      }
      label46:
      if (j != 0) {
        break label167;
      }
      label50:
      Intent localIntent = new Intent("android.intent.action.VIEW");
      if (i != 0) {
        localIntent.setClassName(this.mAppEntry.info.packageName, this.mAppEntry.info.manageSpaceActivityName);
      }
      if (getPackageManager().resolveActivity(localIntent, 0) == null) {
        break label172;
      }
      j = 1;
      label107:
      if (((i == 0) && (bool)) || (j == 0)) {
        break label177;
      }
      if (i == 0) {
        break label203;
      }
      this.mClearDataButton.setText(2131692116);
    }
    for (;;)
    {
      this.mClearDataButton.setOnClickListener(this);
      for (;;)
      {
        if (this.mAppsControlDisallowedBySystem) {
          this.mClearDataButton.setEnabled(false);
        }
        return;
        i = 0;
        break;
        label162:
        j = 0;
        break label46;
        label167:
        bool = true;
        break label50;
        label172:
        j = 0;
        break label107;
        label177:
        this.mClearDataButton.setText(2131692098);
        this.mClearDataButton.setEnabled(false);
        this.mCanClearData = false;
      }
      label203:
      this.mClearDataButton.setText(2131692098);
    }
  }
  
  private void initMoveDialog()
  {
    Object localObject = getActivity();
    StorageManager localStorageManager = (StorageManager)((Context)localObject).getSystemService(StorageManager.class);
    localObject = ((Context)localObject).getPackageManager().getPackageCandidateVolumes(this.mAppEntry.info);
    if (((List)localObject).size() > 1)
    {
      Collections.sort((List)localObject, VolumeInfo.getDescriptionComparator());
      CharSequence[] arrayOfCharSequence = new CharSequence[((List)localObject).size()];
      int j = -1;
      int i = 0;
      while (i < ((List)localObject).size())
      {
        String str = localStorageManager.getBestVolumeDescription((VolumeInfo)((List)localObject).get(i));
        if (Objects.equals(str, this.mStorageUsed.getSummary())) {
          j = i;
        }
        arrayOfCharSequence[i] = str;
        i += 1;
      }
      this.mCandidates = ((VolumeInfo[])((List)localObject).toArray(new VolumeInfo[((List)localObject).size()]));
      this.mDialogBuilder = new AlertDialog.Builder(getContext()).setTitle(2131693388).setSingleChoiceItems(arrayOfCharSequence, j, this).setNegativeButton(2131690993, null);
      return;
    }
    removePreference("storage_used");
    removePreference("change_storage_button");
    removePreference("storage_space");
  }
  
  private void initiateClearUserData()
  {
    this.mClearDataButton.setEnabled(false);
    String str = this.mAppEntry.info.packageName;
    Log.i(TAG, "Clearing user data for package : " + str);
    if (this.mClearDataObserver == null) {
      this.mClearDataObserver = new ClearUserDataObserver();
    }
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    try
    {
      if (!localActivityManager.clearApplicationUserData(str, this.mClearDataObserver))
      {
        Log.i(TAG, "Couldnt clear application user data for package:" + str);
        showDialogInner(2, 0);
        return;
      }
      this.mClearDataButton.setText(2131692130);
      return;
    }
    catch (Exception localException) {}
  }
  
  private boolean isCacheClearableApp()
  {
    String str = this.mPackageInfo.packageName;
    String[] arrayOfString = getResources().getStringArray(2131427372);
    if (TextUtils.isEmpty(str)) {
      return false;
    }
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (str.equals(arrayOfString[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private boolean isMoveInProgress()
  {
    try
    {
      AppGlobals.getPackageManager().checkPackageStartable(this.mPackageName, UserHandle.myUserId());
      return false;
    }
    catch (RemoteException|SecurityException localRemoteException) {}
    return true;
  }
  
  private void processClearMsg(Message paramMessage)
  {
    int i = paramMessage.arg1;
    paramMessage = this.mAppEntry.info.packageName;
    this.mClearDataButton.setText(2131692098);
    if (i == 1)
    {
      Log.i(TAG, "Cleared user data for package : " + paramMessage);
      this.mState.requestSize(this.mPackageName, this.mUserId);
      return;
    }
    this.mClearDataButton.setEnabled(true);
  }
  
  private void refreshButtons()
  {
    initMoveDialog();
    initDataButtons();
  }
  
  private void refreshGrantedUriPermissions()
  {
    removeUriPermissionsFromUi();
    Object localObject3 = ((ActivityManager)getActivity().getSystemService("activity")).getGrantedUriPermissions(this.mAppEntry.info.packageName).getList();
    if (((List)localObject3).isEmpty())
    {
      this.mClearUriButton.setVisibility(8);
      return;
    }
    Object localObject1 = getActivity().getPackageManager();
    Object localObject2 = new TreeMap();
    localObject3 = ((Iterable)localObject3).iterator();
    while (((Iterator)localObject3).hasNext())
    {
      CharSequence localCharSequence = ((PackageManager)localObject1).resolveContentProvider(((UriPermission)((Iterator)localObject3).next()).getUri().getAuthority(), 0).applicationInfo.loadLabel((PackageManager)localObject1);
      MutableInt localMutableInt = (MutableInt)((Map)localObject2).get(localCharSequence);
      if (localMutableInt == null) {
        ((Map)localObject2).put(localCharSequence, new MutableInt(1));
      } else {
        localMutableInt.value += 1;
      }
    }
    localObject1 = ((Map)localObject2).entrySet().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      int i = ((MutableInt)((Map.Entry)localObject2).getValue()).value;
      localObject3 = new Preference(getPrefContext());
      ((Preference)localObject3).setTitle((CharSequence)((Map.Entry)localObject2).getKey());
      ((Preference)localObject3).setSummary(getPrefContext().getResources().getQuantityString(2131951628, i, new Object[] { Integer.valueOf(i) }));
      ((Preference)localObject3).setSelectable(false);
      ((Preference)localObject3).setLayoutResource(2130968717);
      ((Preference)localObject3).setOrder(0);
      Log.v(TAG, "Adding preference '" + localObject3 + "' at order " + 0);
      this.mUri.addPreference((Preference)localObject3);
    }
    if (this.mAppsControlDisallowedBySystem) {
      this.mClearUriButton.setEnabled(false);
    }
    this.mClearUri.setOrder(0);
    this.mClearUriButton.setVisibility(0);
  }
  
  private void refreshSizeInfo()
  {
    if ((this.mAppEntry.size == -2L) || (this.mAppEntry.size == -1L))
    {
      this.mLastTotalSize = -1L;
      this.mLastCacheSize = -1L;
      this.mLastDataSize = -1L;
      this.mLastCodeSize = -1L;
      if (!this.mHaveSizes)
      {
        this.mAppSize.setSummary(this.mComputingStr);
        this.mDataSize.setSummary(this.mComputingStr);
        this.mCacheSize.setSummary(this.mComputingStr);
        this.mTotalSize.setSummary(this.mComputingStr);
      }
      this.mClearDataButton.setEnabled(false);
      this.mClearCacheButton.setEnabled(false);
    }
    for (;;)
    {
      if (this.mAppsControlDisallowedBySystem)
      {
        this.mClearCacheButton.setEnabled(false);
        this.mClearDataButton.setEnabled(false);
      }
      return;
      this.mHaveSizes = true;
      long l4 = this.mAppEntry.codeSize;
      long l3 = this.mAppEntry.dataSize;
      long l2;
      long l1;
      if (Environment.isExternalStorageEmulated())
      {
        l2 = l4 + this.mAppEntry.externalCodeSize;
        l1 = l3 + this.mAppEntry.externalDataSize;
        label198:
        if (this.mLastCodeSize != l2)
        {
          this.mLastCodeSize = l2;
          this.mAppSize.setSummary(getSizeStr(l2));
        }
        if (this.mLastDataSize != l1)
        {
          this.mLastDataSize = l1;
          this.mDataSize.setSummary(getSizeStr(l1));
        }
        l1 = this.mAppEntry.cacheSize + this.mAppEntry.externalCacheSize;
        if (this.mLastCacheSize != l1)
        {
          this.mLastCacheSize = l1;
          this.mCacheSize.setSummary(getSizeStr(l1));
        }
        if (this.mLastTotalSize != this.mAppEntry.size)
        {
          this.mLastTotalSize = this.mAppEntry.size;
          this.mTotalSize.setSummary(getSizeStr(this.mAppEntry.size));
        }
        if ((this.mAppEntry.dataSize + this.mAppEntry.externalDataSize <= 0L) || (!this.mCanClearData)) {
          break label514;
        }
        this.mClearDataButton.setEnabled(true);
        this.mClearDataButton.setOnClickListener(this);
      }
      for (;;)
      {
        if ((l1 <= 0L) || (!isCacheClearableApp())) {
          break label525;
        }
        this.mClearCacheButton.setEnabled(true);
        this.mClearCacheButton.setOnClickListener(this);
        break;
        if (this.mLastExternalCodeSize != this.mAppEntry.externalCodeSize)
        {
          this.mLastExternalCodeSize = this.mAppEntry.externalCodeSize;
          this.mExternalCodeSize.setSummary(getSizeStr(this.mAppEntry.externalCodeSize));
        }
        l2 = l4;
        l1 = l3;
        if (this.mLastExternalDataSize == this.mAppEntry.externalDataSize) {
          break label198;
        }
        this.mLastExternalDataSize = this.mAppEntry.externalDataSize;
        this.mExternalDataSize.setSummary(getSizeStr(this.mAppEntry.externalDataSize));
        l2 = l4;
        l1 = l3;
        break label198;
        label514:
        this.mClearDataButton.setEnabled(false);
      }
      label525:
      this.mClearCacheButton.setEnabled(false);
    }
  }
  
  private void removeUriPermissionsFromUi()
  {
    int i = this.mUri.getPreferenceCount() - 1;
    while (i >= 0)
    {
      Preference localPreference = this.mUri.getPreference(i);
      if (localPreference != this.mClearUri) {
        this.mUri.removePreference(localPreference);
      }
      i -= 1;
    }
  }
  
  private void setupViews()
  {
    this.mComputingStr = getActivity().getText(2131692149);
    this.mInvalidSizeStr = getActivity().getText(2131692150);
    this.mTotalSize = findPreference("total_size");
    this.mAppSize = findPreference("app_size");
    this.mDataSize = findPreference("data_size");
    this.mExternalCodeSize = findPreference("external_code_size");
    this.mExternalDataSize = findPreference("external_data_size");
    if (Environment.isExternalStorageEmulated())
    {
      PreferenceCategory localPreferenceCategory = (PreferenceCategory)findPreference("storage_category");
      localPreferenceCategory.removePreference(this.mExternalCodeSize);
      localPreferenceCategory.removePreference(this.mExternalDataSize);
    }
    this.mClearDataButton = ((Button)((LayoutPreference)findPreference("clear_data_button")).findViewById(2131362227));
    this.mStorageUsed = findPreference("storage_used");
    this.mChangeStorageButton = ((Button)((LayoutPreference)findPreference("change_storage_button")).findViewById(2131362227));
    this.mChangeStorageButton.setText(2131693387);
    this.mChangeStorageButton.setOnClickListener(this);
    this.mCacheSize = findPreference("cache_size");
    this.mClearCacheButton = ((Button)((LayoutPreference)findPreference("clear_cache_button")).findViewById(2131362227));
    this.mClearCacheButton.setText(2131692083);
    this.mUri = ((PreferenceCategory)findPreference("uri_category"));
    this.mClearUri = ((LayoutPreference)this.mUri.findPreference("clear_uri_button"));
    this.mClearUriButton = ((Button)this.mClearUri.findViewById(2131362227));
    this.mClearUriButton.setText(2131692085);
    this.mClearUriButton.setOnClickListener(this);
  }
  
  protected AlertDialog createDialog(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      return null;
    case 1: 
      new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131692131)).setMessage(getActivity().getText(2131692132)).setPositiveButton(2131692133, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          AppStorageSettings.-wrap0(AppStorageSettings.this);
        }
      }).setNegativeButton(2131692134, null).create();
    }
    new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131692138)).setMessage(getActivity().getText(2131692139)).setNeutralButton(2131692133, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        AppStorageSettings.-get0(AppStorageSettings.this).setEnabled(false);
        AppStorageSettings.this.setIntentAndFinish(false, false);
      }
    }).create();
  }
  
  protected int getMetricsCategory()
  {
    return 19;
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    Object localObject = getActivity();
    VolumeInfo localVolumeInfo = this.mCandidates[paramInt];
    if (!Objects.equals(localVolumeInfo, ((Context)localObject).getPackageManager().getPackageCurrentVolume(this.mAppEntry.info)))
    {
      localObject = new Intent((Context)localObject, StorageWizardMoveConfirm.class);
      ((Intent)localObject).putExtra("android.os.storage.extra.VOLUME_ID", localVolumeInfo.getId());
      ((Intent)localObject).putExtra("android.intent.extra.PACKAGE_NAME", this.mAppEntry.info.packageName);
      startActivity((Intent)localObject);
    }
    paramDialogInterface.dismiss();
  }
  
  public void onClick(View paramView)
  {
    if (paramView == this.mClearCacheButton) {
      if ((this.mAppsControlDisallowedAdmin == null) || (this.mAppsControlDisallowedBySystem))
      {
        if (this.mClearCacheObserver == null) {
          this.mClearCacheObserver = new ClearCacheObserver();
        }
        this.mPm.deleteApplicationCacheFiles(this.mPackageName, this.mClearCacheObserver);
      }
    }
    label171:
    label178:
    do
    {
      do
      {
        return;
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
        return;
        if (paramView != this.mClearDataButton) {
          break label178;
        }
        if ((this.mAppsControlDisallowedAdmin != null) && (!this.mAppsControlDisallowedBySystem)) {
          break;
        }
        if (this.mAppEntry.info.manageSpaceActivityName == null) {
          break label171;
        }
      } while (Utils.isMonkeyRunning());
      try
      {
        paramView = new Intent("android.intent.action.VIEW");
        paramView.setClassName(this.mAppEntry.info.packageName, this.mAppEntry.info.manageSpaceActivityName);
        startActivityForResult(paramView, 2);
        return;
      }
      catch (Exception paramView)
      {
        paramView.printStackTrace();
        return;
      }
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
      return;
      showDialogInner(1, 0);
      return;
      if ((paramView == this.mChangeStorageButton) && (this.mDialogBuilder != null) && (!isMoveInProgress())) {
        break;
      }
    } while (paramView != this.mClearUriButton);
    if ((this.mAppsControlDisallowedAdmin == null) || (this.mAppsControlDisallowedBySystem))
    {
      clearUriPermissions();
      return;
      this.mDialogBuilder.show();
      return;
    }
    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mAppsControlDisallowedAdmin);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230736);
    setupViews();
    initMoveDialog();
  }
  
  public void onPackageSizeChanged(String paramString)
  {
    if ((this.mAppEntry == null) || (this.mAppEntry.info == null)) {
      return;
    }
    if (paramString.equals(this.mAppEntry.info.packageName)) {
      refreshSizeInfo();
    }
  }
  
  public void onResume()
  {
    super.onResume();
    this.mState.requestSize(this.mPackageName, this.mUserId);
  }
  
  protected boolean refreshUi()
  {
    retrieveAppEntry();
    if (this.mAppEntry == null) {
      return false;
    }
    refreshSizeInfo();
    refreshGrantedUriPermissions();
    VolumeInfo localVolumeInfo = getActivity().getPackageManager().getPackageCurrentVolume(this.mAppEntry.info);
    StorageManager localStorageManager = (StorageManager)getContext().getSystemService(StorageManager.class);
    this.mStorageUsed.setSummary(localStorageManager.getBestVolumeDescription(localVolumeInfo));
    refreshButtons();
    return true;
  }
  
  class ClearCacheObserver
    extends IPackageDataObserver.Stub
  {
    ClearCacheObserver() {}
    
    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      paramString = AppStorageSettings.-get1(AppStorageSettings.this).obtainMessage(3);
      if (paramBoolean) {}
      for (int i = 1;; i = 2)
      {
        paramString.arg1 = i;
        AppStorageSettings.-get1(AppStorageSettings.this).sendMessage(paramString);
        return;
      }
    }
  }
  
  class ClearUserDataObserver
    extends IPackageDataObserver.Stub
  {
    ClearUserDataObserver() {}
    
    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      int i = 1;
      paramString = AppStorageSettings.-get1(AppStorageSettings.this).obtainMessage(1);
      if (paramBoolean) {}
      for (;;)
      {
        paramString.arg1 = i;
        AppStorageSettings.-get1(AppStorageSettings.this).sendMessage(paramString);
        return;
        i = 2;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppStorageSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
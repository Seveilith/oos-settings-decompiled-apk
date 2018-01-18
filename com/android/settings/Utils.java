package com.android.settings;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.AppGlobals;
import android.app.Dialog;
import android.app.Fragment;
import android.app.IActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Profile;
import android.provider.Settings.Global;
import android.service.persistentdata.PersistentDataBlockManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TtsSpan.TextBuilder;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import com.android.internal.R.styleable;
import com.android.internal.app.UnlaunchableAppActivity;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.wifi.SavedAccessPointsWifiSettings;
import com.android.settingslib.drawable.UserIcons;
import com.oneplus.settings.SettingsBaseApplication;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class Utils
  extends com.android.settingslib.Utils
{
  public static final int[] BADNESS_COLORS = { 0, -3917784, -1750760, -754944, -344276, -9986505, -16089278 };
  public static final float DISABLED_ALPHA = 0.4F;
  public static final String OS_PKG = "os";
  private static final int SECONDS_PER_DAY = 86400;
  private static final int SECONDS_PER_HOUR = 3600;
  private static final int SECONDS_PER_MINUTE = 60;
  private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";
  private static final String TAG = "Settings";
  public static final int UPDATE_PREFERENCE_FLAG_SET_TITLE_TO_MATCHING_ACTIVITY = 1;
  private static final StringBuilder sBuilder = new StringBuilder(50);
  private static SparseArray<Bitmap> sDarkDefaultUserBitmapCache = new SparseArray();
  private static final Formatter sFormatter = new Formatter(sBuilder, Locale.getDefault());
  
  private static void addAll(PreferenceGroup paramPreferenceGroup, List<String> paramList)
  {
    if (paramPreferenceGroup == null) {
      return;
    }
    int i = 0;
    while (i < paramPreferenceGroup.getPreferenceCount())
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      paramList.add(localPreference.getKey());
      if ((localPreference instanceof PreferenceGroup)) {
        addAll((PreferenceGroup)localPreference, paramList);
      }
      i += 1;
    }
  }
  
  public static void assignDefaultPhoto(Context paramContext, int paramInt)
  {
    Object localObject = paramContext;
    if (paramContext == null) {
      localObject = SettingsBaseApplication.mApplication;
    }
    ((UserManager)((Context)localObject).getSystemService("user")).setUserIcon(paramInt, getDefaultUserIconAsBitmap(paramInt));
  }
  
  public static Dialog buildGlobalChangeWarningDialog(Context paramContext, int paramInt, Runnable paramRunnable)
  {
    paramContext = new AlertDialog.Builder(paramContext);
    paramContext.setTitle(paramInt);
    paramContext.setMessage(2131692973);
    paramContext.setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        this.val$positiveAction.run();
      }
    });
    paramContext.setNegativeButton(17039360, null);
    return paramContext.create();
  }
  
  private static void checkPrefs(PreferenceGroup paramPreferenceGroup, List<String> paramList)
  {
    if (paramPreferenceGroup == null) {
      return;
    }
    int i = 0;
    if (i < paramPreferenceGroup.getPreferenceCount())
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      if ((!(localPreference instanceof SelfAvailablePreference)) || (((SelfAvailablePreference)localPreference).isAvailable(paramPreferenceGroup.getContext()))) {
        if ((localPreference instanceof PreferenceGroup)) {
          checkPrefs((PreferenceGroup)localPreference, paramList);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        paramList.add(localPreference.getKey());
        if ((localPreference instanceof PreferenceGroup)) {
          addAll((PreferenceGroup)localPreference, paramList);
        }
      }
    }
  }
  
  public static void ciActionOnSysUpdate(Context paramContext, PersistableBundle paramPersistableBundle)
  {
    String str1 = paramPersistableBundle.getString("ci_action_on_sys_update_intent_string");
    if (!TextUtils.isEmpty(str1))
    {
      String str2 = paramPersistableBundle.getString("ci_action_on_sys_update_extra_string");
      paramPersistableBundle = paramPersistableBundle.getString("ci_action_on_sys_update_extra_val_string");
      Intent localIntent = new Intent(str1);
      if (!TextUtils.isEmpty(str2)) {
        localIntent.putExtra(str2, paramPersistableBundle);
      }
      Log.d("Settings", "ciActionOnSysUpdate: broadcasting intent " + str1 + " with extra " + str2 + ", " + paramPersistableBundle);
      paramContext.getApplicationContext().sendBroadcast(localIntent);
    }
  }
  
  public static void copyMeProfilePhoto(Context paramContext, UserInfo paramUserInfo)
  {
    Uri localUri = ContactsContract.Profile.CONTENT_URI;
    if (paramUserInfo != null) {}
    for (int i = paramUserInfo.id;; i = UserHandle.myUserId())
    {
      paramUserInfo = ContactsContract.Contacts.openContactPhotoInputStream(paramContext.getContentResolver(), localUri, true);
      if (paramUserInfo != null) {
        break;
      }
      assignDefaultPhoto(paramContext, i);
      return;
    }
    ((UserManager)paramContext.getSystemService("user")).setUserIcon(i, BitmapFactory.decodeStream(paramUserInfo));
    try
    {
      paramUserInfo.close();
      return;
    }
    catch (IOException paramContext) {}
  }
  
  public static SpannableString createAccessibleSequence(CharSequence paramCharSequence, String paramString)
  {
    SpannableString localSpannableString = new SpannableString(paramCharSequence);
    localSpannableString.setSpan(new TtsSpan.TextBuilder(paramString).build(), 0, paramCharSequence.length(), 18);
    return localSpannableString;
  }
  
  public static Locale createLocaleFromString(String paramString)
  {
    if (paramString == null) {
      return Locale.getDefault();
    }
    paramString = paramString.split("_", 3);
    if (1 == paramString.length) {
      return new Locale(paramString[0]);
    }
    if (2 == paramString.length) {
      return new Locale(paramString[0], paramString[1]);
    }
    return new Locale(paramString[0], paramString[1], paramString[2]);
  }
  
  public static int enforceSameOwner(Context paramContext, int paramInt)
  {
    if (ArrayUtils.contains(getUserManager(paramContext).getProfileIdsWithDisabled(UserHandle.myUserId()), paramInt)) {
      return paramInt;
    }
    throw new SecurityException("Given user id " + paramInt + " does not belong to user " + UserHandle.myUserId());
  }
  
  public static void forceCustomPadding(View paramView, boolean paramBoolean)
  {
    Resources localResources = paramView.getResources();
    int k = localResources.getDimensionPixelSize(2131755495);
    int i;
    if (paramBoolean)
    {
      i = paramView.getPaddingStart();
      if (!paramBoolean) {
        break label61;
      }
    }
    label61:
    for (int j = paramView.getPaddingEnd();; j = 0)
    {
      paramView.setPaddingRelative(k + i, 0, k + j, localResources.getDimensionPixelSize(17104946));
      return;
      i = 0;
      break;
    }
  }
  
  public static void forcePrepareCustomPreferencesList(ViewGroup paramViewGroup, View paramView, ListView paramListView, boolean paramBoolean)
  {
    paramListView.setScrollBarStyle(33554432);
    paramListView.setClipToPadding(false);
    prepareCustomPreferencesList(paramViewGroup, paramView, paramListView, paramBoolean);
  }
  
  public static String formatDateRange(Context paramContext, long paramLong1, long paramLong2)
  {
    synchronized (sBuilder)
    {
      sBuilder.setLength(0);
      paramContext = DateUtils.formatDateRange(paramContext, sFormatter, paramLong1, paramLong2, 65552, null).toString();
      return paramContext;
    }
  }
  
  public static String formatElapsedTime(Context paramContext, double paramDouble, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = (int)Math.floor(paramDouble / 1000.0D);
    int j = i;
    if (!paramBoolean) {
      j = i + 30;
    }
    int k = 0;
    int m = 0;
    int n = 0;
    i = j;
    if (j >= 86400)
    {
      k = j / 86400;
      i = j - 86400 * k;
    }
    j = i;
    if (i >= 3600)
    {
      m = i / 3600;
      j = i - m * 3600;
    }
    i = j;
    if (j >= 60)
    {
      n = j / 60;
      i = j - n * 60;
    }
    if (paramBoolean) {
      if (k > 0) {
        localStringBuilder.append(paramContext.getString(2131692307, new Object[] { Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i) }));
      }
    }
    for (;;)
    {
      return localStringBuilder.toString();
      if (m > 0)
      {
        localStringBuilder.append(paramContext.getString(2131692308, new Object[] { Integer.valueOf(m), Integer.valueOf(n), Integer.valueOf(i) }));
      }
      else if (n > 0)
      {
        localStringBuilder.append(paramContext.getString(2131692309, new Object[] { Integer.valueOf(n), Integer.valueOf(i) }));
      }
      else
      {
        localStringBuilder.append(paramContext.getString(2131692310, new Object[] { Integer.valueOf(i) }));
        continue;
        if (k > 0) {
          localStringBuilder.append(paramContext.getString(2131692311, new Object[] { Integer.valueOf(k), Integer.valueOf(m), Integer.valueOf(n) }));
        } else if (m > 0) {
          localStringBuilder.append(paramContext.getString(2131692312, new Object[] { Integer.valueOf(m), Integer.valueOf(n) }));
        } else {
          localStringBuilder.append(paramContext.getString(2131692313, new Object[] { Integer.valueOf(n) }));
        }
      }
    }
  }
  
  private static String formatIpAddresses(LinkProperties paramLinkProperties)
  {
    if (paramLinkProperties == null) {
      return null;
    }
    Iterator localIterator = paramLinkProperties.getAllAddresses().iterator();
    if (!localIterator.hasNext()) {
      return null;
    }
    paramLinkProperties = "";
    while (localIterator.hasNext())
    {
      String str = paramLinkProperties + ((InetAddress)localIterator.next()).getHostAddress();
      paramLinkProperties = str;
      if (localIterator.hasNext()) {
        paramLinkProperties = str + "\n";
      }
    }
    return paramLinkProperties;
  }
  
  public static ApplicationInfo getAdminApplicationInfo(Context paramContext, int paramInt)
  {
    paramContext = ((DevicePolicyManager)paramContext.getSystemService("device_policy")).getProfileOwnerAsUser(paramInt);
    if (paramContext == null) {
      return null;
    }
    paramContext = paramContext.getPackageName();
    try
    {
      ApplicationInfo localApplicationInfo = AppGlobals.getPackageManager().getApplicationInfo(paramContext, 0, paramInt);
      return localApplicationInfo;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("Settings", "Error while retrieving application info for package " + paramContext + ", userId " + paramInt, localRemoteException);
    }
    return null;
  }
  
  public static CharSequence getApplicationLabel(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramString, 8704).loadLabel(paramContext.getPackageManager());
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("Settings", "Unable to find info for package: " + paramString);
    }
    return null;
  }
  
  public static String getBatteryPercentage(Intent paramIntent)
  {
    return formatPercentage(getBatteryLevel(paramIntent));
  }
  
  public static int getCredentialOwnerUserId(Context paramContext)
  {
    return getCredentialOwnerUserId(paramContext, UserHandle.myUserId());
  }
  
  public static int getCredentialOwnerUserId(Context paramContext, int paramInt)
  {
    return getUserManager(paramContext).getCredentialOwnerProfile(paramInt);
  }
  
  public static String getDefaultIpAddresses(ConnectivityManager paramConnectivityManager)
  {
    return formatIpAddresses(paramConnectivityManager.getActiveLinkProperties());
  }
  
  public static Bitmap getDefaultUserIconAsBitmap(int paramInt)
  {
    Bitmap localBitmap2 = (Bitmap)sDarkDefaultUserBitmapCache.get(paramInt);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null)
    {
      localBitmap1 = UserIcons.convertToBitmap(UserIcons.getDefaultUserIcon(paramInt, false));
      sDarkDefaultUserBitmapCache.put(paramInt, localBitmap1);
    }
    return localBitmap1;
  }
  
  public static int getEffectiveUserId(Context paramContext)
  {
    paramContext = UserManager.get(paramContext);
    if (paramContext != null) {
      return paramContext.getCredentialOwnerProfile(UserHandle.myUserId());
    }
    Log.e("Settings", "Unable to acquire UserManager");
    return UserHandle.myUserId();
  }
  
  public static UserInfo getExistingUser(UserManager paramUserManager, UserHandle paramUserHandle)
  {
    paramUserManager = paramUserManager.getUsers(true);
    int i = paramUserHandle.getIdentifier();
    paramUserManager = paramUserManager.iterator();
    while (paramUserManager.hasNext())
    {
      paramUserHandle = (UserInfo)paramUserManager.next();
      if (paramUserHandle.id == i) {
        return paramUserHandle;
      }
    }
    return null;
  }
  
  public static ArraySet<String> getHandledDomains(PackageManager paramPackageManager, String paramString)
  {
    Object localObject = paramPackageManager.getIntentFilterVerifications(paramString);
    paramString = paramPackageManager.getAllIntentFilters(paramString);
    paramPackageManager = new ArraySet();
    if (((List)localObject).size() > 0)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Iterator localIterator = ((IntentFilterVerificationInfo)((Iterator)localObject).next()).getDomains().iterator();
        while (localIterator.hasNext()) {
          paramPackageManager.add((String)localIterator.next());
        }
      }
    }
    if ((paramString != null) && (paramString.size() > 0))
    {
      paramString = paramString.iterator();
      while (paramString.hasNext())
      {
        localObject = (IntentFilter)paramString.next();
        if ((((IntentFilter)localObject).hasCategory("android.intent.category.BROWSABLE")) && ((((IntentFilter)localObject).hasDataScheme("http")) || (((IntentFilter)localObject).hasDataScheme("https")))) {
          paramPackageManager.addAll(((IntentFilter)localObject).getHostsList());
        }
      }
    }
    return paramPackageManager;
  }
  
  public static UserHandle getInsecureTargetUser(IBinder paramIBinder, Bundle paramBundle1, Bundle paramBundle2)
  {
    UserHandle localUserHandle = new UserHandle(UserHandle.myUserId());
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    try
    {
      paramIBinder = new UserHandle(UserHandle.getUserId(localIActivityManager.getLaunchedFromUid(paramIBinder)));
      if ((paramIBinder == null) || (paramIBinder.equals(localUserHandle)))
      {
        if (paramBundle2 == null) {
          break label112;
        }
        paramIBinder = (UserHandle)paramBundle2.getParcelable("android.intent.extra.USER");
        if ((paramIBinder != null) && (!paramIBinder.equals(localUserHandle))) {
          break label117;
        }
        if (paramBundle1 == null) {
          break label119;
        }
      }
      label112:
      label117:
      label119:
      for (paramIBinder = (UserHandle)paramBundle1.getParcelable("android.intent.extra.USER");; paramIBinder = null)
      {
        if (paramIBinder != null)
        {
          boolean bool = paramIBinder.equals(localUserHandle);
          if (!bool) {
            break label124;
          }
        }
        return localUserHandle;
        return paramIBinder;
        paramIBinder = null;
        break;
        return paramIBinder;
      }
      label124:
      return paramIBinder;
    }
    catch (RemoteException paramIBinder)
    {
      Log.v("Settings", "Could not talk to activity manager.", paramIBinder);
    }
    return null;
  }
  
  private static String getLocalProfileGivenName(Context paramContext)
  {
    paramContext = paramContext.getContentResolver();
    Object localObject1 = paramContext.query(ContactsContract.Profile.CONTENT_RAW_CONTACTS_URI, new String[] { "_id" }, "account_type IS NULL AND account_name IS NULL", null, null);
    if (localObject1 == null) {
      return null;
    }
    boolean bool;
    Object localObject2;
    try
    {
      bool = ((Cursor)localObject1).moveToFirst();
      if (!bool) {
        return null;
      }
      long l = ((Cursor)localObject1).getLong(0);
      ((Cursor)localObject1).close();
      localObject1 = ContactsContract.Profile.CONTENT_URI.buildUpon().appendPath("data").build();
      localObject2 = "raw_contact_id=" + l;
      localObject2 = paramContext.query((Uri)localObject1, new String[] { "data2", "data3" }, (String)localObject2, null, null);
      if (localObject2 == null) {
        return null;
      }
    }
    finally
    {
      ((Cursor)localObject1).close();
    }
    try
    {
      bool = ((Cursor)localObject2).moveToFirst();
      if (!bool) {
        return null;
      }
      localObject1 = ((Cursor)localObject2).getString(0);
      paramContext = (Context)localObject1;
      if (TextUtils.isEmpty((CharSequence)localObject1)) {
        paramContext = ((Cursor)localObject2).getString(1);
      }
      return paramContext;
    }
    finally
    {
      ((Cursor)localObject2).close();
    }
  }
  
  public static UserHandle getManagedProfile(UserManager paramUserManager)
  {
    List localList = paramUserManager.getUserProfiles();
    int j = localList.size();
    int i = 0;
    if (i < j)
    {
      UserHandle localUserHandle = (UserHandle)localList.get(i);
      if (localUserHandle.getIdentifier() == paramUserManager.getUserHandle()) {}
      UserInfo localUserInfo;
      do
      {
        i += 1;
        break;
        localUserInfo = paramUserManager.getUserInfo(localUserHandle.getIdentifier());
      } while ((!localUserInfo.isManagedProfile()) || (localUserInfo.id == 999));
      return localUserHandle;
    }
    return null;
  }
  
  public static int getManagedProfileId(UserManager paramUserManager, int paramInt)
  {
    paramUserManager = paramUserManager.getProfileIdsWithDisabled(paramInt);
    int i = 0;
    int j = paramUserManager.length;
    while (i < j)
    {
      int k = paramUserManager[i];
      if (k != paramInt) {
        return k;
      }
      i += 1;
    }
    return 55536;
  }
  
  public static String getMeProfileName(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean) {
      return getProfileDisplayName(paramContext);
    }
    return getShorterNameIfPossible(paramContext);
  }
  
  public static List<String> getNonIndexable(int paramInt, Context paramContext)
  {
    if (Looper.myLooper() == null) {
      Looper.prepare();
    }
    ArrayList localArrayList = new ArrayList();
    checkPrefs(new PreferenceManager(paramContext).inflateFromResource(paramContext, paramInt, null), localArrayList);
    return localArrayList;
  }
  
  private static final String getProfileDisplayName(Context paramContext)
  {
    paramContext = paramContext.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[] { "display_name" }, null, null, null);
    if (paramContext == null) {
      return null;
    }
    try
    {
      boolean bool = paramContext.moveToFirst();
      if (!bool) {
        return null;
      }
      String str = paramContext.getString(0);
      return str;
    }
    finally
    {
      paramContext.close();
    }
  }
  
  public static UserHandle getSecureTargetUser(IBinder paramIBinder, UserManager paramUserManager, Bundle paramBundle1, Bundle paramBundle2)
  {
    Object localObject = null;
    UserHandle localUserHandle = new UserHandle(UserHandle.myUserId());
    IActivityManager localIActivityManager = ActivityManagerNative.getDefault();
    for (;;)
    {
      try
      {
        boolean bool = "com.android.settings".equals(localIActivityManager.getLaunchedFromPackage(paramIBinder));
        paramIBinder = new UserHandle(UserHandle.getUserId(localIActivityManager.getLaunchedFromUid(paramIBinder)));
        if ((paramIBinder == null) || (paramIBinder.equals(localUserHandle)))
        {
          if (paramBundle2 == null) {
            break label187;
          }
          paramIBinder = (UserHandle)paramBundle2.getParcelable("android.intent.extra.USER");
          if ((paramIBinder == null) || (paramIBinder.equals(localUserHandle)))
          {
            paramIBinder = (IBinder)localObject;
            if (paramBundle1 != null) {
              paramIBinder = (UserHandle)paramBundle1.getParcelable("android.intent.extra.USER");
            }
            if (paramIBinder == null) {
              break label184;
            }
            if (!paramIBinder.equals(localUserHandle)) {
              continue;
            }
            return localUserHandle;
          }
        }
        else
        {
          if (!isProfileOf(paramUserManager, paramIBinder)) {
            continue;
          }
          return paramIBinder;
        }
        if ((!bool) || (!isProfileOf(paramUserManager, paramIBinder))) {
          continue;
        }
        return paramIBinder;
        if (bool)
        {
          bool = isProfileOf(paramUserManager, paramIBinder);
          if (bool) {
            return paramIBinder;
          }
        }
      }
      catch (RemoteException paramIBinder)
      {
        Log.v("Settings", "Could not talk to activity manager.", paramIBinder);
      }
      label184:
      return localUserHandle;
      label187:
      paramIBinder = null;
    }
  }
  
  private static String getShorterNameIfPossible(Context paramContext)
  {
    String str = getLocalProfileGivenName(paramContext);
    if (!TextUtils.isEmpty(str)) {
      return str;
    }
    return getProfileDisplayName(paramContext);
  }
  
  public static int getUserIdFromBundle(Context paramContext, Bundle paramBundle)
  {
    if (paramBundle == null) {
      return getCredentialOwnerUserId(paramContext);
    }
    return enforceSameOwner(paramContext, paramBundle.getInt("android.intent.extra.USER_ID", UserHandle.myUserId()));
  }
  
  public static UserManager getUserManager(Context paramContext)
  {
    paramContext = UserManager.get(paramContext);
    if (paramContext == null) {
      throw new IllegalStateException("Unable to load UserManager");
    }
    return paramContext;
  }
  
  public static String getWifiIpAddresses(Context paramContext)
  {
    return formatIpAddresses(((ConnectivityManager)paramContext.getSystemService("connectivity")).getLinkProperties(1));
  }
  
  public static void handleLoadingContainer(View paramView1, View paramView2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {}
    for (boolean bool = false;; bool = true)
    {
      setViewShown(paramView1, bool, paramBoolean2);
      setViewShown(paramView2, paramBoolean1, paramBoolean2);
      return;
    }
  }
  
  public static boolean hasMultipleUsers(Context paramContext)
  {
    return ((UserManager)paramContext.getSystemService("user")).getUsers().size() > 1;
  }
  
  public static boolean hasPreferredActivities(PackageManager paramPackageManager, String paramString)
  {
    boolean bool = false;
    ArrayList localArrayList = new ArrayList();
    paramPackageManager.getPreferredActivities(new ArrayList(), localArrayList, paramString);
    Log.d("Settings", "Have " + localArrayList.size() + " number of activities in preferred list");
    if (localArrayList.size() > 0) {
      bool = true;
    }
    return bool;
  }
  
  public static View inflateCategoryHeader(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup)
  {
    TypedArray localTypedArray = paramLayoutInflater.getContext().obtainStyledAttributes(null, R.styleable.Preference, 16842892, 0);
    int i = localTypedArray.getResourceId(3, 0);
    localTypedArray.recycle();
    return paramLayoutInflater.inflate(i, paramViewGroup, false);
  }
  
  public static boolean isBandwidthControlEnabled()
  {
    INetworkManagementService localINetworkManagementService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    try
    {
      boolean bool = localINetworkManagementService.isBandwidthControlEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException) {}
    return false;
  }
  
  public static boolean isBatteryPresent(Intent paramIntent)
  {
    return paramIntent.getBooleanExtra("present", true);
  }
  
  public static boolean isDeviceProvisioned(Context paramContext)
  {
    boolean bool = false;
    if (Settings.Global.getInt(paramContext.getContentResolver(), "device_provisioned", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public static boolean isLowStorage(Context paramContext)
  {
    return StorageManager.from(paramContext).getStorageBytesUntilLow(paramContext.getFilesDir()) < 0L;
  }
  
  public static boolean isManagedProfile(UserManager paramUserManager)
  {
    return isManagedProfile(paramUserManager, UserHandle.myUserId());
  }
  
  public static boolean isManagedProfile(UserManager paramUserManager, int paramInt)
  {
    boolean bool = false;
    if (paramUserManager == null) {
      throw new IllegalArgumentException("userManager must not be null");
    }
    paramUserManager = paramUserManager.getUserInfo(paramInt);
    if (paramInt == 999) {
      return false;
    }
    if (paramUserManager != null) {
      bool = paramUserManager.isManagedProfile();
    }
    return bool;
  }
  
  public static boolean isMonkeyRunning()
  {
    return ActivityManager.isUserAMonkey();
  }
  
  public static boolean isNetworkSettingsApkAvailable(Context paramContext)
  {
    Intent localIntent = new Intent("org.codeaurora.settings.NETWORK_OPERATOR_SETTINGS_ASYNC");
    paramContext = paramContext.getPackageManager().queryIntentActivities(localIntent, 0).iterator();
    while (paramContext.hasNext()) {
      if ((((ResolveInfo)paramContext.next()).activityInfo.applicationInfo.flags & 0x1) != 0) {
        return true;
      }
    }
    return false;
  }
  
  static boolean isOemUnlockEnabled(Context paramContext)
  {
    return ((PersistentDataBlockManager)paramContext.getSystemService("persistent_data_block")).getOemUnlockEnabled();
  }
  
  public static boolean isPackageDirectBootAware(Context paramContext, String paramString)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramString, 0);
      if (!paramContext.isDirectBootAware())
      {
        boolean bool = paramContext.isPartiallyDirectBootAware();
        return bool;
      }
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public static boolean isPackageEnabled(Context paramContext, String paramString)
  {
    try
    {
      boolean bool = paramContext.getPackageManager().getApplicationInfo(paramString, 0).enabled;
      return bool;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  private static boolean isProfileOf(UserManager paramUserManager, UserHandle paramUserHandle)
  {
    if ((paramUserManager == null) || (paramUserHandle == null)) {
      return false;
    }
    if (UserHandle.myUserId() != paramUserHandle.getIdentifier()) {
      return paramUserManager.getUserProfiles().contains(paramUserHandle);
    }
    return true;
  }
  
  public static boolean isVoiceCapable(Context paramContext)
  {
    paramContext = (TelephonyManager)paramContext.getSystemService("phone");
    if (paramContext != null) {
      return paramContext.isVoiceCapable();
    }
    return false;
  }
  
  public static boolean isWifiOnly(Context paramContext)
  {
    boolean bool = false;
    if (!((ConnectivityManager)paramContext.getSystemService("connectivity")).isNetworkSupported(0)) {
      bool = true;
    }
    return bool;
  }
  
  public static Intent onBuildStartFragmentIntent(Context paramContext, String paramString1, Bundle paramBundle, String paramString2, int paramInt, CharSequence paramCharSequence, boolean paramBoolean)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    if (BluetoothSettings.class.getName().equals(paramString1))
    {
      localIntent.setClass(paramContext, SubSettings.BluetoothSubSettings.class);
      localIntent.putExtra(":settings:show_fragment_as_subsetting", true);
    }
    for (;;)
    {
      localIntent.putExtra(":settings:show_fragment", paramString1);
      localIntent.putExtra(":settings:show_fragment_args", paramBundle);
      localIntent.putExtra(":settings:show_fragment_title_res_package_name", paramString2);
      localIntent.putExtra(":settings:show_fragment_title_resid", paramInt);
      localIntent.putExtra(":settings:show_fragment_title", paramCharSequence);
      localIntent.putExtra(":settings:show_fragment_as_shortcut", paramBoolean);
      return localIntent;
      if (SavedAccessPointsWifiSettings.class.getName().equals(paramString1))
      {
        localIntent.setClass(paramContext, SubSettings.SavedAccessPointsSubSettings.class);
        localIntent.putExtra(":settings:show_fragment_as_subsetting", true);
      }
      else
      {
        localIntent.setClass(paramContext, SubSettings.class);
      }
    }
  }
  
  public static void prepareCustomPreferencesList(ViewGroup paramViewGroup, View paramView1, View paramView2, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramView2.getScrollBarStyle() == 33554432)
    {
      i = 1;
      if (i != 0)
      {
        Resources localResources = paramView2.getResources();
        i = localResources.getDimensionPixelSize(2131755495);
        j = localResources.getDimensionPixelSize(17104946);
        if (!(paramViewGroup instanceof PreferenceFrameLayout)) {
          break label90;
        }
        ((PreferenceFrameLayout.LayoutParams)paramView1.getLayoutParams()).removeBorders = true;
        if (!paramBoolean) {
          break label87;
        }
        i = 0;
      }
    }
    label87:
    for (;;)
    {
      paramView2.setPaddingRelative(i, 0, i, j);
      return;
      i = 0;
      break;
    }
    label90:
    paramView2.setPaddingRelative(i, 0, i, j);
  }
  
  public static int resolveResource(Context paramContext, int paramInt)
  {
    TypedValue localTypedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(paramInt, localTypedValue, true);
    return localTypedValue.resourceId;
  }
  
  static void setOemUnlockEnabled(Context paramContext, boolean paramBoolean)
  {
    ((PersistentDataBlockManager)paramContext.getSystemService("persistent_data_block")).setOemUnlockEnabled(paramBoolean);
  }
  
  private static void setViewShown(View paramView, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      Object localObject = paramView.getContext();
      if (paramBoolean1)
      {
        i = 17432576;
        localObject = AnimationUtils.loadAnimation((Context)localObject, i);
        if (!paramBoolean1) {
          break label49;
        }
        paramView.setVisibility(0);
      }
      for (;;)
      {
        paramView.startAnimation((Animation)localObject);
        return;
        i = 17432577;
        break;
        label49:
        ((Animation)localObject).setAnimationListener(new Animation.AnimationListener()
        {
          public void onAnimationEnd(Animation paramAnonymousAnimation)
          {
            this.val$view.setVisibility(4);
          }
          
          public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
          
          public void onAnimationStart(Animation paramAnonymousAnimation) {}
        });
      }
    }
    paramView.clearAnimation();
    if (paramBoolean1) {}
    for (int i = 0;; i = 4)
    {
      paramView.setVisibility(i);
      return;
    }
  }
  
  public static boolean showAccount(Context paramContext, String paramString)
  {
    paramContext = paramContext.getResources().getStringArray(2131427451);
    if ((paramContext == null) || (paramContext.length == 0)) {
      return true;
    }
    int j = paramContext.length;
    int i = 0;
    while (i < j)
    {
      if (paramContext[i].equals(paramString)) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static boolean showSimCardTile(Context paramContext)
  {
    return ((TelephonyManager)paramContext.getSystemService("phone")).getSimCount() > 1;
  }
  
  public static boolean startQuietModeDialogIfNecessary(Context paramContext, UserManager paramUserManager, int paramInt)
  {
    if (paramUserManager.isQuietModeEnabled(UserHandle.of(paramInt)))
    {
      paramContext.startActivity(UnlaunchableAppActivity.createInQuietModeDialogIntent(paramInt));
      return true;
    }
    return false;
  }
  
  public static void startWithFragment(Context paramContext, String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    startWithFragment(paramContext, paramString, paramBundle, paramFragment, paramInt1, null, paramInt2, paramCharSequence, false);
  }
  
  public static void startWithFragment(Context paramContext, String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt1, int paramInt2, CharSequence paramCharSequence, boolean paramBoolean)
  {
    paramString = onBuildStartFragmentIntent(paramContext, paramString, paramBundle, null, paramInt2, paramCharSequence, paramBoolean);
    if (paramFragment == null)
    {
      paramContext.startActivity(paramString);
      return;
    }
    paramFragment.startActivityForResult(paramString, paramInt1);
  }
  
  public static void startWithFragment(Context paramContext, String paramString1, Bundle paramBundle, Fragment paramFragment, int paramInt1, String paramString2, int paramInt2, CharSequence paramCharSequence)
  {
    startWithFragment(paramContext, paramString1, paramBundle, paramFragment, paramInt1, paramString2, paramInt2, paramCharSequence, false);
  }
  
  public static void startWithFragment(Context paramContext, String paramString1, Bundle paramBundle, Fragment paramFragment, int paramInt1, String paramString2, int paramInt2, CharSequence paramCharSequence, boolean paramBoolean)
  {
    paramString1 = onBuildStartFragmentIntent(paramContext, paramString1, paramBundle, paramString2, paramInt2, paramCharSequence, paramBoolean);
    if (paramFragment == null)
    {
      paramContext.startActivity(paramString1);
      return;
    }
    paramFragment.startActivityForResult(paramString1, paramInt1);
  }
  
  public static void startWithFragmentAsUser(Context paramContext, String paramString, Bundle paramBundle, int paramInt, CharSequence paramCharSequence, boolean paramBoolean, UserHandle paramUserHandle)
  {
    if (paramUserHandle.getIdentifier() == UserHandle.myUserId())
    {
      startWithFragment(paramContext, paramString, paramBundle, null, 0, paramInt, paramCharSequence, paramBoolean);
      return;
    }
    paramString = onBuildStartFragmentIntent(paramContext, paramString, paramBundle, null, paramInt, paramCharSequence, paramBoolean);
    paramString.addFlags(268435456);
    paramString.addFlags(32768);
    paramContext.startActivityAsUser(paramString, paramUserHandle);
  }
  
  public static void startWithFragmentAsUser(Context paramContext, String paramString1, Bundle paramBundle, String paramString2, int paramInt, CharSequence paramCharSequence, boolean paramBoolean, UserHandle paramUserHandle)
  {
    if (paramUserHandle.getIdentifier() == UserHandle.myUserId())
    {
      startWithFragment(paramContext, paramString1, paramBundle, null, 0, paramString2, paramInt, paramCharSequence, paramBoolean);
      return;
    }
    paramString1 = onBuildStartFragmentIntent(paramContext, paramString1, paramBundle, paramString2, paramInt, paramCharSequence, paramBoolean);
    paramString1.addFlags(268435456);
    paramString1.addFlags(32768);
    paramContext.startActivityAsUser(paramString1, paramUserHandle);
  }
  
  public static boolean unlockWorkProfileIfNecessary(Context paramContext, int paramInt)
  {
    try
    {
      boolean bool = ActivityManagerNative.getDefault().isUserRunning(paramInt, 2);
      if (!bool) {
        return false;
      }
    }
    catch (RemoteException paramContext)
    {
      return false;
    }
    if (!new LockPatternUtils(paramContext).isSecure(paramInt)) {
      return false;
    }
    Intent localIntent = ((KeyguardManager)paramContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(null, null, paramInt);
    if (localIntent != null)
    {
      paramContext.startActivity(localIntent);
      return true;
    }
    return false;
  }
  
  public static boolean updatePreferenceToSpecificActivityOrRemove(Context paramContext, PreferenceGroup paramPreferenceGroup, String paramString, int paramInt)
  {
    paramString = paramPreferenceGroup.findPreference(paramString);
    if (paramString == null) {
      return false;
    }
    Object localObject = paramString.getIntent();
    if (localObject != null)
    {
      paramContext = paramContext.getPackageManager();
      localObject = paramContext.queryIntentActivities((Intent)localObject, 0);
      int j = ((List)localObject).size();
      int i = 0;
      while (i < j)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)((List)localObject).get(i);
        if ((localResolveInfo.activityInfo.applicationInfo.flags & 0x1) != 0)
        {
          paramString.setIntent(new Intent().setClassName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name));
          if ((paramInt & 0x1) != 0) {
            paramString.setTitle(localResolveInfo.loadLabel(paramContext));
          }
          return true;
        }
        i += 1;
      }
    }
    paramPreferenceGroup.removePreference(paramString);
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
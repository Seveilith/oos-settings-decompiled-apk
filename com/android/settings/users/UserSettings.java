package com.android.settings.users;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.DimmableIconPreference;
import com.android.settings.OwnerInfoSettings;
import com.android.settings.SettingsActivity;
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
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.drawable.CircleFramedDrawable;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPPreferenceDivider;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UserSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceClickListener, View.OnClickListener, DialogInterface.OnDismissListener, Preference.OnPreferenceChangeListener, EditUserInfoController.OnContentChangedCallback, Indexable
{
  private static final String ACTION_EDIT_EMERGENCY_INFO = "android.settings.EDIT_EMERGENGY_INFO";
  private static final int DIALOG_ADD_USER = 2;
  private static final int DIALOG_CHOOSE_USER_TYPE = 6;
  private static final int DIALOG_CONFIRM_EXIT_GUEST = 8;
  private static final int DIALOG_CONFIRM_REMOVE = 1;
  private static final int DIALOG_NEED_LOCKSCREEN = 7;
  private static final int DIALOG_SETUP_PROFILE = 4;
  private static final int DIALOG_SETUP_USER = 3;
  private static final int DIALOG_USER_CANNOT_MANAGE = 5;
  private static final int DIALOG_USER_PROFILE_EDITOR = 9;
  private static final String KEY_ADD_USER = "user_add";
  private static final String KEY_ADD_USER_LONG_MESSAGE_DISPLAYED = "key_add_user_long_message_displayed";
  private static final String KEY_EMERGENCY_INFO = "emergency_info";
  private static final String KEY_SUMMARY = "summary";
  private static final String KEY_TITLE = "title";
  private static final String KEY_USER_LIST = "user_list";
  private static final String KEY_USER_ME = "user_me";
  private static final int MENU_REMOVE_USER = 1;
  private static final int MESSAGE_CONFIG_USER = 3;
  private static final int MESSAGE_SETUP_USER = 2;
  private static final int MESSAGE_UPDATE_LIST = 1;
  private static final int REQUEST_CHOOSE_LOCK = 10;
  private static final String SAVE_ADDING_USER = "adding_user";
  private static final String SAVE_REMOVING_USER = "removing_user";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      UserSettings.UserCapabilities localUserCapabilities = UserSettings.UserCapabilities.create(paramAnonymousContext);
      if (!localUserCapabilities.mEnabled) {
        return localArrayList;
      }
      Resources localResources = paramAnonymousContext.getResources();
      SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = localResources.getString(2131692910);
      localSearchIndexableRaw.screenTitle = localResources.getString(2131692910);
      localArrayList.add(localSearchIndexableRaw);
      if ((localUserCapabilities.mCanAddUser) || (localUserCapabilities.mDisallowAddUserSetByAdmin))
      {
        localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
        if (!localUserCapabilities.mCanAddRestrictedProfile) {
          break label196;
        }
      }
      label196:
      for (int i = 2131692912;; i = 2131692913)
      {
        localSearchIndexableRaw.title = localResources.getString(i);
        localSearchIndexableRaw.screenTitle = localResources.getString(2131692910);
        localArrayList.add(localSearchIndexableRaw);
        if (UserSettings.-wrap2(paramAnonymousContext))
        {
          paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
          paramAnonymousContext.title = localResources.getString(2131692967);
          paramAnonymousContext.screenTitle = localResources.getString(2131692967);
          localArrayList.add(paramAnonymousContext);
        }
        return localArrayList;
      }
    }
  };
  private static final long STORAGE_SIZE_LIMIT = 209715200L;
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new UserSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private static final String TAG = "UserSettings";
  private static final int USER_TYPE_RESTRICTED_PROFILE = 2;
  private static final int USER_TYPE_USER = 1;
  private DimmableIconPreference mAddUser;
  private OPRestrictedSwitchPreference mAddUserWhenLocked;
  private int mAddedUserId = 0;
  private boolean mAddingUser;
  private String mAddingUserName;
  private Drawable mDefaultIconDrawable;
  private EditUserInfoController mEditUserInfoController = new EditUserInfoController();
  private Preference mEmergencyInfoPreference;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        UserSettings.-wrap12(UserSettings.this);
        return;
      case 2: 
        UserSettings.-wrap9(UserSettings.this, paramAnonymousMessage.arg1);
        return;
      }
      UserSettings.-wrap8(UserSettings.this, paramAnonymousMessage.arg1, true);
    }
  };
  private PreferenceGroup mLockScreenSettings;
  private UserPreference mMePreference;
  PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
    {
      super.onCallStateChanged(paramAnonymousInt, paramAnonymousString);
      boolean bool = UserSettings.-get6(UserSettings.this).canSwitchUsers();
      UserSettings.-get0(UserSettings.this).setEnabled(bool);
    }
  };
  private int mRemovingUserId = -1;
  private boolean mShouldUpdateUserList = true;
  private UserCapabilities mUserCaps;
  private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.USER_REMOVED")) {
        UserSettings.-set1(UserSettings.this, -1);
      }
      for (;;)
      {
        UserSettings.-get2(UserSettings.this).sendEmptyMessage(1);
        return;
        if (paramAnonymousIntent.getAction().equals("android.intent.action.USER_INFO_CHANGED"))
        {
          int i = paramAnonymousIntent.getIntExtra("android.intent.extra.user_handle", -1);
          if (i != -1) {
            UserSettings.-get4(UserSettings.this).remove(i);
          }
        }
      }
    }
  };
  private SparseArray<Bitmap> mUserIcons = new SparseArray();
  private PreferenceGroup mUserListCategory;
  private final Object mUserLock = new Object();
  private UserManager mUserManager;
  private TelephonyManager tm;
  
  private void addUserNow(final int paramInt)
  {
    synchronized (this.mUserLock)
    {
      this.mAddingUser = true;
      if (paramInt == 1)
      {
        str = getString(2131692943);
        this.mAddingUserName = str;
        new Thread()
        {
          public void run()
          {
            if (paramInt == 1) {}
            for (UserInfo localUserInfo = UserSettings.-wrap1(UserSettings.this); localUserInfo == null; localUserInfo = UserSettings.-wrap0(UserSettings.this))
            {
              UserSettings.-set0(UserSettings.this, false);
              return;
            }
            synchronized (UserSettings.-get5(UserSettings.this))
            {
              if (paramInt == 1)
              {
                UserSettings.-get2(UserSettings.this).sendEmptyMessage(1);
                UserSettings.-get2(UserSettings.this).sendMessage(UserSettings.-get2(UserSettings.this).obtainMessage(2, localUserInfo.id, localUserInfo.serialNumber));
                return;
              }
              UserSettings.-get2(UserSettings.this).sendMessage(UserSettings.-get2(UserSettings.this).obtainMessage(3, localUserInfo.id, localUserInfo.serialNumber));
            }
          }
        }.start();
        return;
      }
      String str = getString(2131692944);
    }
  }
  
  private UserInfo createRestrictedProfile()
  {
    UserInfo localUserInfo = this.mUserManager.createRestrictedProfile(this.mAddingUserName);
    Utils.assignDefaultPhoto(getActivity(), localUserInfo.id);
    return localUserInfo;
  }
  
  private UserInfo createTrustedUser()
  {
    UserInfo localUserInfo = this.mUserManager.createUser(this.mAddingUserName, 0);
    if (localUserInfo != null) {
      Utils.assignDefaultPhoto(getActivity(), localUserInfo.id);
    }
    return localUserInfo;
  }
  
  private static boolean emergencyInfoActivityPresent(Context paramContext)
  {
    Intent localIntent = new Intent("android.settings.EDIT_EMERGENGY_INFO").setPackage("com.android.emergency");
    paramContext = paramContext.getPackageManager().queryIntentActivities(localIntent, 0);
    return (paramContext != null) && (!paramContext.isEmpty());
  }
  
  private Drawable encircle(Bitmap paramBitmap)
  {
    return CircleFramedDrawable.getInstance(getActivity(), paramBitmap);
  }
  
  private void exitGuest()
  {
    if (!this.mUserCaps.mIsGuest) {
      return;
    }
    removeThisUser();
  }
  
  private void finishLoadProfile(String paramString)
  {
    if (getActivity() == null) {
      return;
    }
    this.mMePreference.setTitle(getString(2131692921, new Object[] { paramString }));
    int i = UserHandle.myUserId();
    paramString = this.mUserManager.getUserIcon(i);
    if (paramString != null)
    {
      this.mMePreference.setIcon(encircle(paramString));
      this.mUserIcons.put(i, paramString);
    }
  }
  
  public static long getAvailableInternalMemorySize()
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    long l = localStatFs.getBlockSize();
    return localStatFs.getAvailableBlocks() * l;
  }
  
  private Drawable getEncircledDefaultIcon()
  {
    if (this.mDefaultIconDrawable == null) {
      this.mDefaultIconDrawable = encircle(Utils.getDefaultUserIconAsBitmap(55536));
    }
    return this.mDefaultIconDrawable;
  }
  
  private int getMaxRealUsers()
  {
    int j = UserManager.getMaxSupportedUsers();
    Object localObject = this.mUserManager.getUsers();
    int i = 0;
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      if (((UserInfo)((Iterator)localObject).next()).isManagedProfile()) {
        i += 1;
      }
    }
    return j + 1 - i;
  }
  
  private boolean hasLockscreenSecurity()
  {
    return new LockPatternUtils(getActivity()).isSecure(UserHandle.myUserId());
  }
  
  private boolean isInitialized(UserInfo paramUserInfo)
  {
    boolean bool = false;
    if ((paramUserInfo.flags & 0x10) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void launchChooseLockscreen()
  {
    Intent localIntent = new Intent("android.app.action.SET_NEW_PASSWORD");
    localIntent.putExtra("minimum_quality", 65536);
    startActivityForResult(localIntent, 10);
  }
  
  private void loadIconsAsync(List<Integer> paramList)
  {
    new AsyncTask()
    {
      protected Void doInBackground(List<Integer>... paramAnonymousVarArgs)
      {
        Iterator localIterator = paramAnonymousVarArgs[0].iterator();
        while (localIterator.hasNext())
        {
          int i = ((Integer)localIterator.next()).intValue();
          Bitmap localBitmap = UserSettings.-get6(UserSettings.this).getUserIcon(i);
          paramAnonymousVarArgs = localBitmap;
          if (localBitmap == null) {
            paramAnonymousVarArgs = Utils.getDefaultUserIconAsBitmap(i);
          }
          UserSettings.-get4(UserSettings.this).append(i, paramAnonymousVarArgs);
        }
        return null;
      }
      
      protected void onPostExecute(Void paramAnonymousVoid)
      {
        UserSettings.-wrap12(UserSettings.this);
      }
    }.execute(new List[] { paramList });
  }
  
  private void loadProfile()
  {
    if (this.mUserCaps.mIsGuest)
    {
      this.mMePreference.setIcon(getEncircledDefaultIcon());
      this.mMePreference.setTitle(2131692956);
      return;
    }
    new AsyncTask()
    {
      protected String doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = UserSettings.-get6(UserSettings.this).getUserInfo(UserHandle.myUserId());
        if ((paramAnonymousVarArgs.iconPath == null) || (paramAnonymousVarArgs.iconPath.equals(""))) {
          Utils.copyMeProfilePhoto(SettingsBaseApplication.mApplication.getApplicationContext(), paramAnonymousVarArgs);
        }
        return paramAnonymousVarArgs.name;
      }
      
      protected void onPostExecute(String paramAnonymousString)
      {
        UserSettings.-wrap5(UserSettings.this, paramAnonymousString);
      }
    }.execute(new Void[0]);
  }
  
  private void onAddUserClicked(int paramInt)
  {
    for (;;)
    {
      synchronized (this.mUserLock)
      {
        if (this.mRemovingUserId == -1)
        {
          boolean bool = this.mAddingUser;
          if (!bool) {}
        }
        else
        {
          return;
        }
        switch (paramInt)
        {
        default: 
          break;
        case 1: 
          showDialog(2);
        }
      }
      if (hasLockscreenSecurity()) {
        addUserNow(2);
      } else {
        showDialog(7);
      }
    }
  }
  
  private void onManageUserClicked(int paramInt, boolean paramBoolean)
  {
    this.mAddingUser = false;
    if (paramInt == -11)
    {
      localObject = new Bundle();
      ((Bundle)localObject).putBoolean("guest_user", true);
      ((SettingsActivity)getActivity()).startPreferencePanel(UserDetailsSettings.class.getName(), (Bundle)localObject, 2131689590, null, null, 0);
      return;
    }
    Object localObject = this.mUserManager.getUserInfo(paramInt);
    if ((((UserInfo)localObject).isRestricted()) && (this.mUserCaps.mIsAdmin))
    {
      localObject = new Bundle();
      ((Bundle)localObject).putInt("user_id", paramInt);
      ((Bundle)localObject).putBoolean("new_user", paramBoolean);
      ((SettingsActivity)getActivity()).startPreferencePanel(RestrictedProfileSettings.class.getName(), (Bundle)localObject, 2131693055, null, null, 0);
    }
    do
    {
      return;
      if (((UserInfo)localObject).id == UserHandle.myUserId())
      {
        OwnerInfoSettings.show(this);
        return;
      }
    } while (!this.mUserCaps.mIsAdmin);
    Bundle localBundle = new Bundle();
    localBundle.putInt("user_id", paramInt);
    ((SettingsActivity)getActivity()).startPreferencePanel(UserDetailsSettings.class.getName(), localBundle, -1, ((UserInfo)localObject).name, null, 0);
  }
  
  private void onRemoveUserClicked(int paramInt)
  {
    synchronized (this.mUserLock)
    {
      if (this.mRemovingUserId == -1)
      {
        boolean bool = this.mAddingUser;
        if (!bool) {}
      }
      else
      {
        return;
      }
      this.mRemovingUserId = paramInt;
      showDialog(1);
    }
  }
  
  private void onUserCreated(int paramInt)
  {
    this.mAddedUserId = paramInt;
    this.mAddingUser = false;
    if (this.mUserManager.getUserInfo(paramInt).isRestricted())
    {
      showDialog(4);
      return;
    }
    showDialog(3);
  }
  
  private void removeThisUser()
  {
    if (!this.mUserManager.canSwitchUsers())
    {
      Log.w("UserSettings", "Cannot remove current user when switching is disabled");
      return;
    }
    try
    {
      ActivityManagerNative.getDefault().switchUser(0);
      ((UserManager)getContext().getSystemService(UserManager.class)).removeUser(UserHandle.myUserId());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("UserSettings", "Unable to remove self user");
    }
  }
  
  private void removeUserNow()
  {
    if (this.mRemovingUserId == UserHandle.myUserId())
    {
      removeThisUser();
      return;
    }
    new Thread()
    {
      public void run()
      {
        synchronized (UserSettings.-get5(UserSettings.this))
        {
          UserSettings.-get6(UserSettings.this).removeUser(UserSettings.-get3(UserSettings.this));
          UserSettings.-get2(UserSettings.this).sendEmptyMessage(1);
          return;
        }
      }
    }.start();
  }
  
  private void setPhotoId(Preference paramPreference, UserInfo paramUserInfo)
  {
    paramUserInfo = (Bitmap)this.mUserIcons.get(paramUserInfo.id);
    if (paramUserInfo != null) {
      paramPreference.setIcon(encircle(paramUserInfo));
    }
  }
  
  private void switchUserNow(int paramInt)
  {
    try
    {
      ActivityManagerNative.getDefault().switchUser(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void updateUserList()
  {
    if (getActivity() == null) {
      return;
    }
    Object localObject1 = this.mUserManager.getUsers(true);
    boolean bool2 = Utils.isVoiceCapable(getActivity());
    Object localObject4 = new ArrayList();
    Object localObject3 = new ArrayList();
    ((ArrayList)localObject3).add(this.mMePreference);
    Iterator localIterator = ((Iterable)localObject1).iterator();
    label111:
    label141:
    boolean bool1;
    label231:
    label254:
    label256:
    label275:
    Object localObject2;
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if (localUserInfo.supportsSwitchToByUser())
      {
        if (localUserInfo.id == UserHandle.myUserId())
        {
          localObject1 = this.mMePreference;
          if (localObject1 == null) {
            break label369;
          }
          if (isInitialized(localUserInfo)) {
            break label422;
          }
          if (!localUserInfo.isRestricted()) {
            break label411;
          }
          ((UserPreference)localObject1).setSummary(2131692918);
          ((UserPreference)localObject1).setOnPreferenceClickListener(this);
          ((UserPreference)localObject1).setSelectable(true);
        }
        for (;;)
        {
          if (localUserInfo.iconPath != null)
          {
            if (this.mUserIcons.get(localUserInfo.id) == null)
            {
              ((ArrayList)localObject4).add(Integer.valueOf(localUserInfo.id));
              ((UserPreference)localObject1).setIcon(getEncircledDefaultIcon());
              break;
              if (localUserInfo.isGuest()) {
                break;
              }
              int i;
              Context localContext;
              int j;
              if (this.mUserCaps.mIsAdmin) {
                if (!bool2)
                {
                  bool1 = localUserInfo.isRestricted();
                  if (!this.mUserCaps.mIsAdmin) {
                    break label394;
                  }
                  if ((!bool2) && (!localUserInfo.isRestricted())) {
                    break label381;
                  }
                  i = 0;
                  localContext = getPrefContext();
                  j = localUserInfo.id;
                  if (!bool1) {
                    break label399;
                  }
                  localObject1 = this;
                  if (i == 0) {
                    break label405;
                  }
                }
              }
              label369:
              label381:
              label394:
              label399:
              label405:
              for (localObject2 = this;; localObject2 = null)
              {
                localObject1 = new UserPreference(localContext, null, j, (View.OnClickListener)localObject1, (View.OnClickListener)localObject2);
                ((UserPreference)localObject1).setKey("id=" + localUserInfo.id);
                ((ArrayList)localObject3).add(localObject1);
                if (localUserInfo.isAdmin()) {
                  ((UserPreference)localObject1).setSummary(2131692920);
                }
                ((UserPreference)localObject1).setTitle(localUserInfo.name);
                ((UserPreference)localObject1).setSelectable(false);
                break label111;
                break;
                bool1 = true;
                break label231;
                bool1 = false;
                break label231;
                if (localUserInfo.isGuest()) {
                  break label254;
                }
                i = 1;
                break label256;
                i = 0;
                break label256;
                localObject1 = null;
                break label275;
              }
              label411:
              ((UserPreference)localObject1).setSummary(2131692917);
              break label141;
              label422:
              if (!localUserInfo.isRestricted()) {
                continue;
              }
              ((UserPreference)localObject1).setSummary(2131692914);
              continue;
            }
            setPhotoId((Preference)localObject1, localUserInfo);
            break;
          }
        }
        ((UserPreference)localObject1).setIcon(getEncircledDefaultIcon());
      }
    }
    if (this.mAddingUser)
    {
      localObject1 = new UserPreference(getPrefContext(), null, -10, null, null);
      ((UserPreference)localObject1).setEnabled(false);
      ((UserPreference)localObject1).setTitle(this.mAddingUserName);
      ((UserPreference)localObject1).setIcon(getEncircledDefaultIcon());
      ((ArrayList)localObject3).add(localObject1);
    }
    if ((!this.mUserCaps.mIsGuest) && ((this.mUserCaps.mCanAddGuest) || (this.mUserCaps.mDisallowAddUserSetByAdmin)))
    {
      localObject2 = getPrefContext();
      if ((this.mUserCaps.mIsAdmin) && (bool2))
      {
        localObject1 = this;
        localObject2 = new UserPreference((Context)localObject2, null, -11, (View.OnClickListener)localObject1, null);
        ((UserPreference)localObject2).setTitle(2131689590);
        ((UserPreference)localObject2).setIcon(getEncircledDefaultIcon());
        ((ArrayList)localObject3).add(localObject2);
        if (!this.mUserCaps.mDisallowAddUser) {
          break label814;
        }
        localObject1 = this.mUserCaps.mEnforcedAdmin;
        label636:
        ((UserPreference)localObject2).setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject1);
        ((UserPreference)localObject2).setSelectable(false);
      }
    }
    else
    {
      Collections.sort((List)localObject3, UserPreference.SERIAL_NUMBER_COMPARATOR);
      getActivity().invalidateOptionsMenu();
      if (((ArrayList)localObject4).size() > 0) {
        loadIconsAsync((List)localObject4);
      }
      localObject2 = getPreferenceScreen();
      ((PreferenceScreen)localObject2).removeAll();
      localObject1 = new OPPreferenceHeaderMargin(SettingsBaseApplication.mApplication);
      ((OPPreferenceHeaderMargin)localObject1).setOrder(-1);
      ((PreferenceScreen)localObject2).addPreference((Preference)localObject1);
      if (!this.mUserCaps.mCanAddRestrictedProfile) {
        break label820;
      }
      this.mUserListCategory.removeAll();
      this.mUserListCategory.setOrder(Integer.MAX_VALUE);
      ((PreferenceScreen)localObject2).addPreference(this.mUserListCategory);
    }
    label814:
    label820:
    for (localObject1 = this.mUserListCategory;; localObject1 = localObject2)
    {
      localObject3 = ((Iterable)localObject3).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (UserPreference)((Iterator)localObject3).next();
        ((UserPreference)localObject4).setOrder(Integer.MAX_VALUE);
        ((PreferenceGroup)localObject1).addPreference((Preference)localObject4);
      }
      localObject1 = null;
      break;
      localObject1 = null;
      break label636;
    }
    if (((this.mUserCaps.mCanAddUser) || (this.mUserCaps.mDisallowAddUserSetByAdmin)) && (Utils.isDeviceProvisioned(getActivity())))
    {
      bool2 = this.mUserManager.canAddMoreUsers();
      this.mAddUser.setOrder(Integer.MAX_VALUE);
      ((PreferenceScreen)localObject2).addPreference(this.mAddUser);
      bool1 = this.mUserManager.canSwitchUsers();
      localObject1 = this.mAddUser;
      if ((!bool2) || (this.mAddingUser)) {
        bool1 = false;
      }
      ((DimmableIconPreference)localObject1).setEnabled(bool1);
      if (bool2) {
        break label1169;
      }
      this.mAddUser.setSummary(getString(2131692924, new Object[] { Integer.valueOf(getMaxRealUsers()) }));
      if (this.mAddUser.isEnabled())
      {
        localObject3 = this.mAddUser;
        if (!this.mUserCaps.mDisallowAddUser) {
          break label1180;
        }
        localObject1 = this.mUserCaps.mEnforcedAdmin;
        label988:
        ((DimmableIconPreference)localObject3).setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject1);
      }
    }
    ((PreferenceScreen)localObject2).addPreference(new OPPreferenceDivider(SettingsBaseApplication.mApplication));
    if ((this.mUserCaps.mIsAdmin) && ((!this.mUserCaps.mDisallowAddUser) || (this.mUserCaps.mDisallowAddUserSetByAdmin)))
    {
      this.mLockScreenSettings.setOrder(Integer.MAX_VALUE);
      ((PreferenceScreen)localObject2).addPreference(this.mLockScreenSettings);
      localObject1 = this.mAddUserWhenLocked;
      if (Settings.Global.getInt(getContentResolver(), "add_users_when_locked", 0) != 1) {
        break label1186;
      }
      bool1 = true;
      label1084:
      ((OPRestrictedSwitchPreference)localObject1).setChecked(bool1);
      this.mAddUserWhenLocked.setOnPreferenceChangeListener(this);
      localObject3 = this.mAddUserWhenLocked;
      if (!this.mUserCaps.mDisallowAddUser) {
        break label1191;
      }
    }
    label1169:
    label1180:
    label1186:
    label1191:
    for (localObject1 = this.mUserCaps.mEnforcedAdmin;; localObject1 = null)
    {
      ((OPRestrictedSwitchPreference)localObject3).setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject1);
      if (emergencyInfoActivityPresent(getContext()))
      {
        this.mEmergencyInfoPreference.setOnPreferenceClickListener(this);
        this.mEmergencyInfoPreference.setOrder(Integer.MAX_VALUE);
        ((PreferenceScreen)localObject2).addPreference(this.mEmergencyInfoPreference);
      }
      return;
      this.mAddUser.setSummary(null);
      break;
      localObject1 = null;
      break label988;
      bool1 = false;
      break label1084;
    }
  }
  
  public int getHelpResource()
  {
    return 2131693025;
  }
  
  protected int getMetricsCategory()
  {
    return 96;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 10)
    {
      if ((paramInt2 != 0) && (hasLockscreenSecurity())) {
        addUserNow(2);
      }
      return;
    }
    this.mEditUserInfoController.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public void onClick(View paramView)
  {
    int i;
    if ((paramView.getTag() instanceof UserPreference)) {
      i = ((UserPreference)paramView.getTag()).getUserId();
    }
    switch (paramView.getId())
    {
    case 2131362513: 
    default: 
      return;
    case 2131362514: 
      paramView = RestrictedLockUtils.checkIfRestrictionEnforced(getContext(), "no_remove_user", UserHandle.myUserId());
      if (paramView != null)
      {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), paramView);
        return;
      }
      onRemoveUserClicked(i);
      return;
    }
    onManageUserClicked(i, false);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      if (paramBundle.containsKey("adding_user")) {
        this.mAddedUserId = paramBundle.getInt("adding_user");
      }
      if (paramBundle.containsKey("removing_user")) {
        this.mRemovingUserId = paramBundle.getInt("removing_user");
      }
      this.mEditUserInfoController.onRestoreInstanceState(paramBundle);
    }
    paramBundle = getActivity();
    this.mUserCaps = UserCapabilities.create(paramBundle);
    this.mUserManager = ((UserManager)paramBundle.getSystemService("user"));
    if (!this.mUserCaps.mEnabled) {
      return;
    }
    int i = UserHandle.myUserId();
    addPreferencesFromResource(2131230876);
    this.mUserListCategory = ((PreferenceGroup)findPreference("user_list"));
    this.mMePreference = new UserPreference(getPrefContext(), null, i, null, null);
    this.mMePreference.setKey("user_me");
    this.mMePreference.setOnPreferenceClickListener(this);
    if (this.mUserCaps.mIsAdmin) {
      this.mMePreference.setSummary(2131692920);
    }
    this.mAddUser = ((DimmableIconPreference)findPreference("user_add"));
    this.mAddUser.useAdminDisabledSummary(false);
    if ((this.mUserCaps.mCanAddUser) && (Utils.isDeviceProvisioned(getActivity())))
    {
      this.mAddUser.setOnPreferenceClickListener(this);
      if (!this.mUserCaps.mCanAddRestrictedProfile) {
        this.mAddUser.setTitle(2131692913);
      }
    }
    this.mLockScreenSettings = ((PreferenceGroup)findPreference("lock_screen_settings"));
    this.mAddUserWhenLocked = ((OPRestrictedSwitchPreference)findPreference("add_users_when_locked"));
    this.mEmergencyInfoPreference = findPreference("emergency_info");
    setHasOptionsMenu(true);
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.USER_REMOVED");
    localIntentFilter.addAction("android.intent.action.USER_INFO_CHANGED");
    paramBundle.registerReceiverAsUser(this.mUserChangeReceiver, UserHandle.ALL, localIntentFilter, null, this.mHandler);
    loadProfile();
    updateUserList();
    this.mShouldUpdateUserList = false;
    if (Settings.Global.getInt(getContext().getContentResolver(), "device_provisioned", 0) == 0)
    {
      getActivity().finish();
      return;
    }
    this.tm = ((TelephonyManager)paramBundle.getSystemService("phone"));
    this.tm.listen(this.mPhoneStateListener, 32);
  }
  
  public Dialog onCreateDialog(final int paramInt)
  {
    Object localObject1 = getActivity();
    if (localObject1 == null) {
      return null;
    }
    final Object localObject2;
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
      UserDialogs.createRemoveDialog(getActivity(), this.mRemovingUserId, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.-wrap10(UserSettings.this);
        }
      });
    case 5: 
      return new AlertDialog.Builder((Context)localObject1).setMessage(2131692937).setPositiveButton(17039370, null).create();
    case 2: 
      localObject2 = getActivity().getPreferences(0);
      final boolean bool = ((SharedPreferences)localObject2).getBoolean("key_add_user_long_message_displayed", false);
      int i;
      if (bool)
      {
        i = 2131692931;
        if (paramInt != 2) {
          break label203;
        }
      }
      for (paramInt = 1;; paramInt = 2)
      {
        new AlertDialog.Builder((Context)localObject1).setTitle(2131692929).setMessage(i).setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            UserSettings.-wrap3(UserSettings.this, paramInt);
            if (!bool) {
              localObject2.edit().putBoolean("key_add_user_long_message_displayed", true).apply();
            }
          }
        }).setNegativeButton(17039360, null).create();
        i = 2131692930;
        break;
      }
    case 3: 
      new AlertDialog.Builder((Context)localObject1).setTitle(2131692932).setMessage(2131692933).setPositiveButton(2131692935, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.-wrap11(UserSettings.this, UserSettings.-get1(UserSettings.this));
        }
      }).setNegativeButton(2131692936, null).create();
    case 4: 
      new AlertDialog.Builder((Context)localObject1).setMessage(2131692934).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.-wrap11(UserSettings.this, UserSettings.-get1(UserSettings.this));
        }
      }).setNegativeButton(17039360, null).create();
    case 6: 
      localObject2 = new ArrayList();
      HashMap localHashMap1 = new HashMap();
      localHashMap1.put("title", getString(2131692927));
      localHashMap1.put("summary", getString(2131692925));
      HashMap localHashMap2 = new HashMap();
      localHashMap2.put("title", getString(2131692928));
      localHashMap2.put("summary", getString(2131692926));
      ((List)localObject2).add(localHashMap1);
      ((List)localObject2).add(localHashMap2);
      localObject1 = new AlertDialog.Builder((Context)localObject1);
      localObject2 = new SimpleAdapter(((AlertDialog.Builder)localObject1).getContext(), (List)localObject2, 2130969087, new String[] { "title", "summary" }, new int[] { 2131361894, 2131362024 });
      ((AlertDialog.Builder)localObject1).setTitle(2131692923);
      ((AlertDialog.Builder)localObject1).setAdapter((ListAdapter)localObject2, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          paramAnonymousDialogInterface = UserSettings.this;
          if (paramAnonymousInt == 0) {}
          for (paramAnonymousInt = 1;; paramAnonymousInt = 2)
          {
            UserSettings.-wrap7(paramAnonymousDialogInterface, paramAnonymousInt);
            return;
          }
        }
      });
      return ((AlertDialog.Builder)localObject1).create();
    case 7: 
      new AlertDialog.Builder((Context)localObject1).setMessage(2131692915).setPositiveButton(2131692916, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.-wrap6(UserSettings.this);
        }
      }).setNegativeButton(17039360, null).create();
    case 8: 
      label203:
      new AlertDialog.Builder((Context)localObject1).setTitle(2131692957).setMessage(2131692958).setPositiveButton(2131692959, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          UserSettings.-wrap4(UserSettings.this);
        }
      }).setNegativeButton(17039360, null).create();
    }
    return this.mEditUserInfoController.createDialog(this, this.mMePreference.getIcon(), this.mMePreference.getTitle(), 2131691056, this, Process.myUserHandle());
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    Object localObject = (UserManager)getContext().getSystemService(UserManager.class);
    if (((UserManager)localObject).hasUserRestriction("no_remove_user")) {}
    for (int i = 0;; i = 1)
    {
      boolean bool = ((UserManager)localObject).canSwitchUsers();
      if ((!this.mUserCaps.mIsAdmin) && (i != 0) && (bool))
      {
        localObject = this.mUserManager.getUserName();
        paramMenu.add(0, 1, 0, getResources().getString(2131692939, new Object[] { localObject })).setShowAsAction(0);
      }
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (!this.mUserCaps.mEnabled) {
      return;
    }
    getActivity().unregisterReceiver(this.mUserChangeReceiver);
    this.tm.listen(this.mPhoneStateListener, 0);
  }
  
  public void onDialogShowing()
  {
    super.onDialogShowing();
    setOnDismissListener(this);
  }
  
  public void onDismiss(DialogInterface arg1)
  {
    synchronized (this.mUserLock)
    {
      this.mRemovingUserId = -1;
      updateUserList();
      return;
    }
  }
  
  public void onLabelChanged(CharSequence paramCharSequence)
  {
    this.mMePreference.setTitle(paramCharSequence);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      onRemoveUserClicked(UserHandle.myUserId());
      return true;
    }
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onPause()
  {
    this.mShouldUpdateUserList = true;
    super.onPause();
  }
  
  public void onPhotoChanged(Drawable paramDrawable)
  {
    this.mMePreference.setIcon(paramDrawable);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int j = 0;
    if (paramPreference == this.mAddUserWhenLocked)
    {
      paramPreference = (Boolean)paramObject;
      paramObject = getContentResolver();
      int i = j;
      if (paramPreference != null)
      {
        i = j;
        if (paramPreference.booleanValue()) {
          i = 1;
        }
      }
      Settings.Global.putInt((ContentResolver)paramObject, "add_users_when_locked", i);
      return true;
    }
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mMePreference)
    {
      if (this.mUserCaps.mIsGuest)
      {
        showDialog(8);
        return true;
      }
      if (this.mUserManager.isLinkedUser()) {
        onManageUserClicked(UserHandle.myUserId(), false);
      }
    }
    do
    {
      do
      {
        return false;
        showDialog(9);
        return false;
        if (!(paramPreference instanceof UserPreference)) {
          break;
        }
        int i = ((UserPreference)paramPreference).getUserId();
        paramPreference = this.mUserManager.getUserInfo(i);
      } while (isInitialized(paramPreference));
      this.mHandler.sendMessage(this.mHandler.obtainMessage(2, paramPreference.id, paramPreference.serialNumber));
      return false;
      if (paramPreference == this.mAddUser)
      {
        if (Long.valueOf(getAvailableInternalMemorySize()).longValue() < 209715200L)
        {
          Toast.makeText(getContext(), 2131690331, 1).show();
          return false;
        }
        if (this.mUserCaps.mCanAddRestrictedProfile)
        {
          showDialog(6);
          return false;
        }
        onAddUserClicked(1);
        return false;
      }
    } while (paramPreference != this.mEmergencyInfoPreference);
    paramPreference = new Intent("android.settings.EDIT_EMERGENGY_INFO");
    paramPreference.setFlags(67108864);
    startActivity(paramPreference);
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    if (!this.mUserCaps.mEnabled) {
      return;
    }
    if (this.mShouldUpdateUserList)
    {
      this.mUserCaps.updateAddUserCapabilities(getActivity());
      loadProfile();
      updateUserList();
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    this.mEditUserInfoController.onSaveInstanceState(paramBundle);
    paramBundle.putInt("adding_user", this.mAddedUserId);
    paramBundle.putInt("removing_user", this.mRemovingUserId);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.mEditUserInfoController.startingActivityForResult();
    super.startActivityForResult(paramIntent, paramInt);
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        UserInfo localUserInfo = ((UserManager)this.mContext.getSystemService(UserManager.class)).getUserInfo(UserHandle.myUserId());
        this.mSummaryLoader.setSummary(this, this.mContext.getString(2131693589, new Object[] { localUserInfo.name }));
      }
    }
  }
  
  private static class UserCapabilities
  {
    boolean mCanAddGuest;
    boolean mCanAddRestrictedProfile = true;
    boolean mCanAddUser = true;
    boolean mDisallowAddUser;
    boolean mDisallowAddUserSetByAdmin;
    boolean mEnabled = true;
    RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    boolean mIsAdmin;
    boolean mIsGuest;
    
    public static UserCapabilities create(Context paramContext)
    {
      Object localObject = (UserManager)paramContext.getSystemService("user");
      UserCapabilities localUserCapabilities = new UserCapabilities();
      if ((!UserManager.supportsMultipleUsers()) || (Utils.isMonkeyRunning()))
      {
        localUserCapabilities.mEnabled = false;
        return localUserCapabilities;
      }
      localObject = ((UserManager)localObject).getUserInfo(UserHandle.myUserId());
      localUserCapabilities.mIsGuest = ((UserInfo)localObject).isGuest();
      localUserCapabilities.mIsAdmin = ((UserInfo)localObject).isAdmin();
      if ((((DevicePolicyManager)paramContext.getSystemService("device_policy")).isDeviceManaged()) || (Utils.isVoiceCapable(paramContext))) {
        localUserCapabilities.mCanAddRestrictedProfile = false;
      }
      localUserCapabilities.updateAddUserCapabilities(paramContext);
      return localUserCapabilities;
    }
    
    public String toString()
    {
      return "UserCapabilities{mEnabled=" + this.mEnabled + ", mCanAddUser=" + this.mCanAddUser + ", mCanAddRestrictedProfile=" + this.mCanAddRestrictedProfile + ", mIsAdmin=" + this.mIsAdmin + ", mIsGuest=" + this.mIsGuest + ", mCanAddGuest=" + this.mCanAddGuest + ", mDisallowAddUser=" + this.mDisallowAddUser + ", mEnforcedAdmin=" + this.mEnforcedAdmin + '}';
    }
    
    public void updateAddUserCapabilities(Context paramContext)
    {
      this.mEnforcedAdmin = RestrictedLockUtils.checkIfRestrictionEnforced(paramContext, "no_add_user", UserHandle.myUserId());
      boolean bool2 = RestrictedLockUtils.hasBaseUserRestriction(paramContext, "no_add_user", UserHandle.myUserId());
      if ((this.mEnforcedAdmin == null) || (bool2))
      {
        bool1 = false;
        this.mDisallowAddUserSetByAdmin = bool1;
        if (this.mEnforcedAdmin != null) {
          break label129;
        }
        bool1 = bool2;
        label50:
        this.mDisallowAddUser = bool1;
        this.mCanAddUser = true;
        if ((this.mIsAdmin) && (UserManager.getMaxSupportedUsers() >= 2)) {
          break label134;
        }
        label74:
        this.mCanAddUser = false;
        label79:
        if ((!this.mIsAdmin) && (Settings.Global.getInt(paramContext.getContentResolver(), "add_users_when_locked", 0) != 1)) {
          break label150;
        }
      }
      label129:
      label134:
      label150:
      for (boolean bool1 = true;; bool1 = false)
      {
        if ((this.mIsGuest) || (this.mDisallowAddUser)) {
          bool1 = false;
        }
        this.mCanAddGuest = bool1;
        return;
        bool1 = true;
        break;
        bool1 = true;
        break label50;
        if (!UserManager.supportsMultipleUsers()) {
          break label74;
        }
        if (!this.mDisallowAddUser) {
          break label79;
        }
        break label74;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\users\UserSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
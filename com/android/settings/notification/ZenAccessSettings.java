package com.android.settings.notification;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo.DisplayNameComparator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.ArraySet;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ZenAccessSettings
  extends EmptyTextSettings
{
  private static final String ENABLED_SERVICES_SEPARATOR = ":";
  private Context mContext;
  private NotificationManager mNoMan;
  private final SettingObserver mObserver = new SettingObserver();
  private PackageManager mPkgMan;
  
  private static void deleteRules(Context paramContext, final String paramString)
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        ((NotificationManager)this.val$context.getSystemService(NotificationManager.class)).removeAutomaticZenRules(paramString);
      }
    });
  }
  
  private ArraySet<String> getEnabledNotificationListeners()
  {
    ArraySet localArraySet = new ArraySet();
    Object localObject = Settings.Secure.getString(getContext().getContentResolver(), "enabled_notification_listeners");
    if (!TextUtils.isEmpty((CharSequence)localObject))
    {
      localObject = ((String)localObject).split(":");
      int i = 0;
      while (i < localObject.length)
      {
        ComponentName localComponentName = ComponentName.unflattenFromString(localObject[i]);
        if (localComponentName != null) {
          localArraySet.add(localComponentName.getPackageName());
        }
        i += 1;
      }
    }
    return localArraySet;
  }
  
  private boolean hasAccess(String paramString)
  {
    return this.mNoMan.isNotificationPolicyAccessGrantedForPackage(paramString);
  }
  
  private void reloadList()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    Object localObject2 = new ArrayList();
    Object localObject3 = this.mNoMan.getPackagesRequestingNotificationPolicyAccess();
    final Object localObject4;
    if (!((ArraySet)localObject3).isEmpty())
    {
      localObject1 = this.mPkgMan.getInstalledApplications(0);
      if (localObject1 != null)
      {
        localObject1 = ((Iterable)localObject1).iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject4 = (ApplicationInfo)((Iterator)localObject1).next();
          if (((ArraySet)localObject3).contains(((ApplicationInfo)localObject4).packageName)) {
            ((ArrayList)localObject2).add(localObject4);
          }
        }
      }
    }
    Object localObject1 = getEnabledNotificationListeners();
    ((ArraySet)localObject3).addAll((ArraySet)localObject1);
    Collections.sort((List)localObject2, new PackageItemInfo.DisplayNameComparator(this.mPkgMan));
    localObject2 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (ApplicationInfo)((Iterator)localObject2).next();
      localObject4 = ((ApplicationInfo)localObject3).packageName;
      final CharSequence localCharSequence = ((ApplicationInfo)localObject3).loadLabel(this.mPkgMan);
      SwitchPreference localSwitchPreference = new SwitchPreference(getPrefContext());
      localSwitchPreference.setPersistent(false);
      localSwitchPreference.setIcon(((ApplicationInfo)localObject3).loadIcon(this.mPkgMan));
      localSwitchPreference.setTitle(localCharSequence);
      localSwitchPreference.setChecked(hasAccess((String)localObject4));
      if (((ArraySet)localObject1).contains(localObject4))
      {
        localSwitchPreference.setEnabled(false);
        localSwitchPreference.setSummary(getString(2131693520));
      }
      localSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          if (((Boolean)paramAnonymousObject).booleanValue()) {
            new ZenAccessSettings.ScaryWarningDialogFragment().setPkgInfo(localObject4, localCharSequence).show(ZenAccessSettings.this.getFragmentManager(), "dialog");
          }
          for (;;)
          {
            return false;
            new ZenAccessSettings.FriendlyWarningDialogFragment().setPkgInfo(localObject4, localCharSequence).show(ZenAccessSettings.this.getFragmentManager(), "dialog");
          }
        }
      });
      localPreferenceScreen.addPreference(localSwitchPreference);
    }
  }
  
  private static void setAccess(Context paramContext, final String paramString, final boolean paramBoolean)
  {
    AsyncTask.execute(new Runnable()
    {
      public void run()
      {
        ((NotificationManager)this.val$context.getSystemService(NotificationManager.class)).setNotificationPolicyAccessGranted(paramString, paramBoolean);
      }
    });
  }
  
  protected int getMetricsCategory()
  {
    return 180;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = getActivity();
    this.mPkgMan = this.mContext.getPackageManager();
    this.mNoMan = ((NotificationManager)this.mContext.getSystemService(NotificationManager.class));
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this.mContext));
  }
  
  public void onPause()
  {
    super.onPause();
    getContentResolver().unregisterContentObserver(this.mObserver);
  }
  
  public void onResume()
  {
    super.onResume();
    reloadList();
    getContentResolver().registerContentObserver(Settings.Secure.getUriFor("enabled_notification_policy_access_packages"), false, this.mObserver);
    getContentResolver().registerContentObserver(Settings.Secure.getUriFor("enabled_notification_listeners"), false, this.mObserver);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setEmptyText(2131693251);
  }
  
  public static class FriendlyWarningDialogFragment
    extends DialogFragment
  {
    static final String KEY_LABEL = "l";
    static final String KEY_PKG = "p";
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      Object localObject = getArguments();
      paramBundle = ((Bundle)localObject).getString("p");
      localObject = ((Bundle)localObject).getString("l");
      localObject = getResources().getString(2131693521, new Object[] { localObject });
      String str = getResources().getString(2131693522);
      new AlertDialog.Builder(getContext()).setMessage(str).setTitle((CharSequence)localObject).setCancelable(true).setPositiveButton(2131690994, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ZenAccessSettings.-wrap0(ZenAccessSettings.FriendlyWarningDialogFragment.this.getContext(), paramBundle);
          ZenAccessSettings.-wrap2(ZenAccessSettings.FriendlyWarningDialogFragment.this.getContext(), paramBundle, false);
        }
      }).setNegativeButton(2131690993, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
    }
    
    public FriendlyWarningDialogFragment setPkgInfo(String paramString, CharSequence paramCharSequence)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("p", paramString);
      if (TextUtils.isEmpty(paramCharSequence)) {}
      for (;;)
      {
        localBundle.putString("l", paramString);
        setArguments(localBundle);
        return this;
        paramString = paramCharSequence.toString();
      }
    }
  }
  
  public static class ScaryWarningDialogFragment
    extends DialogFragment
  {
    static final String KEY_LABEL = "l";
    static final String KEY_PKG = "p";
    
    public Dialog onCreateDialog(final Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      Object localObject = getArguments();
      paramBundle = ((Bundle)localObject).getString("p");
      localObject = ((Bundle)localObject).getString("l");
      localObject = getResources().getString(2131693518, new Object[] { localObject });
      String str = getResources().getString(2131693519);
      new AlertDialog.Builder(getContext()).setMessage(str).setTitle((CharSequence)localObject).setCancelable(true).setPositiveButton(2131690774, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ZenAccessSettings.-wrap2(ZenAccessSettings.ScaryWarningDialogFragment.this.getContext(), paramBundle, true);
        }
      }).setNegativeButton(2131690775, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
    }
    
    public ScaryWarningDialogFragment setPkgInfo(String paramString, CharSequence paramCharSequence)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("p", paramString);
      if (TextUtils.isEmpty(paramCharSequence)) {}
      for (;;)
      {
        localBundle.putString("l", paramString);
        setArguments(localBundle);
        return this;
        paramString = paramCharSequence.toString();
      }
    }
  }
  
  private final class SettingObserver
    extends ContentObserver
  {
    public SettingObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      ZenAccessSettings.-wrap1(ZenAccessSettings.this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenAccessSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
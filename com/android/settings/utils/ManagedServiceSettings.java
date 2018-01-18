package com.android.settings.utils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment.InstantiationException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageItemInfo.DisplayNameComparator;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;
import com.android.settings.notification.EmptyTextSettings;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class ManagedServiceSettings
  extends EmptyTextSettings
{
  private static Config mConfig;
  protected static Context mContext;
  protected static ServiceListing mServiceListing;
  private TextView mEmpty;
  private PackageManager mPM;
  
  public ManagedServiceSettings()
  {
    mConfig = getConfig();
  }
  
  private void updateList(List<ServiceInfo> paramList)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    Collections.sort(paramList, new PackageItemInfo.DisplayNameComparator(this.mPM));
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      ServiceInfo localServiceInfo = (ServiceInfo)paramList.next();
      final ComponentName localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
      final String str = localServiceInfo.loadLabel(this.mPM).toString();
      SwitchPreference localSwitchPreference = new SwitchPreference(getPrefContext());
      localSwitchPreference.setPersistent(false);
      localSwitchPreference.setIcon(localServiceInfo.loadIcon(this.mPM));
      localSwitchPreference.setTitle(str);
      localSwitchPreference.setChecked(mServiceListing.isEnabled(localComponentName));
      localSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
          return ManagedServiceSettings.this.setEnabled(localComponentName, str, bool);
        }
      });
      localPreferenceScreen.addPreference(localSwitchPreference);
    }
  }
  
  protected abstract Config getConfig();
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    mContext = getActivity();
    this.mPM = mContext.getPackageManager();
    mServiceListing = new ServiceListing(mContext, mConfig);
    mServiceListing.addCallback(new ServiceListing.Callback()
    {
      public void onServicesReloaded(List<ServiceInfo> paramAnonymousList)
      {
        ManagedServiceSettings.-wrap0(ManagedServiceSettings.this, paramAnonymousList);
      }
    });
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(mContext));
  }
  
  public void onPause()
  {
    super.onPause();
    mServiceListing.setListening(false);
  }
  
  public void onResume()
  {
    super.onResume();
    mServiceListing.reload();
    mServiceListing.setListening(true);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    setEmptyText(mConfig.emptyText);
  }
  
  protected boolean setEnabled(ComponentName paramComponentName, String paramString, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      mServiceListing.setEnabled(paramComponentName, false);
      return true;
    }
    if (mServiceListing.isEnabled(paramComponentName)) {
      return true;
    }
    try
    {
      new ScaryWarningDialogFragment().setServiceInfo(paramComponentName, paramString).show(getFragmentManager(), "dialog");
      return false;
    }
    catch (Fragment.InstantiationException paramComponentName)
    {
      paramComponentName.printStackTrace();
    }
    return false;
  }
  
  public static class Config
  {
    public int emptyText;
    public String intentAction;
    public String noun;
    public String permission;
    public String secondarySetting;
    public String setting;
    public String tag;
    public int warningDialogSummary;
    public int warningDialogTitle;
  }
  
  public static class ScaryWarningDialogFragment
    extends DialogFragment
  {
    static final String KEY_COMPONENT = "c";
    static final String KEY_LABEL = "l";
    
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      final Object localObject = getArguments();
      paramBundle = ((Bundle)localObject).getString("l");
      localObject = ComponentName.unflattenFromString(((Bundle)localObject).getString("c"));
      String str = getResources().getString(ManagedServiceSettings.-get0().warningDialogTitle, new Object[] { paramBundle });
      paramBundle = getResources().getString(ManagedServiceSettings.-get0().warningDialogSummary, new Object[] { paramBundle });
      new AlertDialog.Builder(ManagedServiceSettings.mContext).setMessage(paramBundle).setTitle(str).setCancelable(true).setPositiveButton(2131690774, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ManagedServiceSettings.mServiceListing.setEnabled(localObject, true);
        }
      }).setNegativeButton(2131690775, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
      }).create();
    }
    
    public ScaryWarningDialogFragment setServiceInfo(ComponentName paramComponentName, String paramString)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("c", paramComponentName.flattenToString());
      localBundle.putString("l", paramString);
      setArguments(localBundle);
      return this;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\ManagedServiceSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
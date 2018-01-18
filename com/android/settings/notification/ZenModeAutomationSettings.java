package com.android.settings.notification;

import android.app.AlertDialog.Builder;
import android.app.AutomaticZenRule;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.utils.ManagedServiceSettings.Config;
import com.android.settings.utils.ZenServiceListing;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;

public class ZenModeAutomationSettings
  extends ZenModeSettingsBase
{
  static final ManagedServiceSettings.Config CONFIG = ;
  private static final Comparator<Map.Entry<String, AutomaticZenRule>> RULE_COMPARATOR = new Comparator()
  {
    private String key(AutomaticZenRule paramAnonymousAutomaticZenRule)
    {
      int i;
      if (ZenModeConfig.isValidScheduleConditionId(paramAnonymousAutomaticZenRule.getConditionId())) {
        i = 1;
      }
      for (;;)
      {
        return i + paramAnonymousAutomaticZenRule.getName().toString();
        if (ZenModeConfig.isValidEventConditionId(paramAnonymousAutomaticZenRule.getConditionId())) {
          i = 2;
        } else {
          i = 3;
        }
      }
    }
    
    public int compare(Map.Entry<String, AutomaticZenRule> paramAnonymousEntry1, Map.Entry<String, AutomaticZenRule> paramAnonymousEntry2)
    {
      int i = Long.compare(((AutomaticZenRule)paramAnonymousEntry1.getValue()).getCreationTime(), ((AutomaticZenRule)paramAnonymousEntry2.getValue()).getCreationTime());
      if (i != 0) {
        return i;
      }
      return key((AutomaticZenRule)paramAnonymousEntry1.getValue()).compareTo(key((AutomaticZenRule)paramAnonymousEntry2.getValue()));
    }
  };
  private PackageManager mPm;
  private ZenServiceListing mServiceListing;
  
  private String computeRuleSummary(AutomaticZenRule paramAutomaticZenRule, boolean paramBoolean, CharSequence paramCharSequence)
  {
    String str = computeZenModeCaption(getResources(), paramAutomaticZenRule.getInterruptionFilter());
    if ((paramAutomaticZenRule != null) && (paramAutomaticZenRule.isEnabled())) {}
    for (paramAutomaticZenRule = getString(2131693288, new Object[] { str }); paramBoolean; paramAutomaticZenRule = getString(2131693336)) {
      return paramAutomaticZenRule;
    }
    return getString(2131693289, new Object[] { paramCharSequence, paramAutomaticZenRule });
  }
  
  private static String computeZenModeCaption(Resources paramResources, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 4: 
      return paramResources.getString(2131693203);
    case 2: 
      return paramResources.getString(2131693202);
    }
    return paramResources.getString(2131693204);
  }
  
  private static ManagedServiceSettings.Config getConditionProviderConfig()
  {
    ManagedServiceSettings.Config localConfig = new ManagedServiceSettings.Config();
    localConfig.tag = "ZenModeSettings";
    localConfig.setting = "enabled_notification_policy_access_packages";
    localConfig.secondarySetting = "enabled_notification_listeners";
    localConfig.intentAction = "android.service.notification.ConditionProviderService";
    localConfig.permission = "android.permission.BIND_CONDITION_PROVIDER_SERVICE";
    localConfig.noun = "condition provider";
    return localConfig;
  }
  
  public static ZenRuleInfo getRuleInfo(PackageManager paramPackageManager, ServiceInfo paramServiceInfo)
  {
    if ((paramServiceInfo == null) || (paramServiceInfo.metaData == null)) {
      return null;
    }
    String str = paramServiceInfo.metaData.getString("android.service.zen.automatic.ruleType");
    Object localObject = getSettingsActivity(paramServiceInfo);
    if ((str == null) || (str.trim().isEmpty())) {}
    while (localObject == null) {
      return null;
    }
    localObject = new ZenRuleInfo();
    ((ZenRuleInfo)localObject).serviceComponent = new ComponentName(paramServiceInfo.packageName, paramServiceInfo.name);
    ((ZenRuleInfo)localObject).settingsAction = "android.settings.ZEN_MODE_EXTERNAL_RULE_SETTINGS";
    ((ZenRuleInfo)localObject).title = str;
    ((ZenRuleInfo)localObject).packageName = paramServiceInfo.packageName;
    ((ZenRuleInfo)localObject).configurationActivity = getSettingsActivity(paramServiceInfo);
    ((ZenRuleInfo)localObject).packageLabel = paramServiceInfo.applicationInfo.loadLabel(paramPackageManager);
    ((ZenRuleInfo)localObject).ruleInstanceLimit = paramServiceInfo.metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
    return (ZenRuleInfo)localObject;
  }
  
  private Intent getRuleIntent(String paramString1, ComponentName paramComponentName, String paramString2)
  {
    paramString2 = new Intent().addFlags(67108864).putExtra("android.service.notification.extra.RULE_ID", paramString2);
    if (paramComponentName != null)
    {
      paramString2.setComponent(paramComponentName);
      return paramString2;
    }
    paramString2.setAction(paramString1);
    return paramString2;
  }
  
  private static ComponentName getSettingsActivity(ServiceInfo paramServiceInfo)
  {
    if ((paramServiceInfo == null) || (paramServiceInfo.metaData == null)) {
      return null;
    }
    paramServiceInfo = paramServiceInfo.metaData.getString("android.service.zen.automatic.configurationActivity");
    if (paramServiceInfo != null) {
      return ComponentName.unflattenFromString(paramServiceInfo);
    }
    return null;
  }
  
  private void showAddRuleDialog()
  {
    new ZenRuleSelectionDialog(this.mContext, this.mServiceListing)
    {
      public void onExternalRuleSelected(ZenRuleInfo paramAnonymousZenRuleInfo)
      {
        paramAnonymousZenRuleInfo = new Intent().setComponent(paramAnonymousZenRuleInfo.configurationActivity);
        ZenModeAutomationSettings.this.startActivity(paramAnonymousZenRuleInfo);
      }
      
      public void onSystemRuleSelected(ZenRuleInfo paramAnonymousZenRuleInfo)
      {
        ZenModeAutomationSettings.-wrap5(ZenModeAutomationSettings.this, paramAnonymousZenRuleInfo);
      }
    }.show();
  }
  
  private void showDeleteRuleDialog(final String paramString, CharSequence paramCharSequence)
  {
    new AlertDialog.Builder(this.mContext).setMessage(getString(2131693269, new Object[] { paramCharSequence })).setNegativeButton(2131690993, null).setPositiveButton(2131693270, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        MetricsLogger.action(ZenModeAutomationSettings.this.mContext, 175);
        ZenModeAutomationSettings.this.removeZenRule(paramString);
      }
    }).show();
  }
  
  private void showNameRuleDialog(final ZenRuleInfo paramZenRuleInfo)
  {
    new ZenRuleNameDialog(this.mContext, null)
    {
      public void onOk(String paramAnonymousString)
      {
        MetricsLogger.action(ZenModeAutomationSettings.this.mContext, 173);
        paramAnonymousString = new AutomaticZenRule(paramAnonymousString, paramZenRuleInfo.serviceComponent, paramZenRuleInfo.defaultConditionId, 2, true);
        paramAnonymousString = ZenModeAutomationSettings.this.addZenRule(paramAnonymousString);
        if (paramAnonymousString != null) {
          ZenModeAutomationSettings.this.startActivity(ZenModeAutomationSettings.-wrap1(ZenModeAutomationSettings.this, paramZenRuleInfo.settingsAction, null, paramAnonymousString));
        }
      }
    }.show();
  }
  
  private Map.Entry<String, AutomaticZenRule>[] sortedRules()
  {
    Map.Entry[] arrayOfEntry = (Map.Entry[])this.mRules.toArray(new Map.Entry[this.mRules.size()]);
    Arrays.sort(arrayOfEntry, RULE_COMPARATOR);
    return arrayOfEntry;
  }
  
  private void updateControls()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.removeAll();
    Object localObject1 = sortedRules();
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      Object localObject2 = localObject1[i];
      localObject2 = new ZenRulePreference(getPrefContext(), (Map.Entry)localObject2);
      if (((ZenRulePreference)localObject2).appExists) {
        localPreferenceScreen.addPreference((Preference)localObject2);
      }
      i += 1;
    }
    localObject1 = new Preference(getPrefContext());
    ((Preference)localObject1).setIcon(2130837929);
    ((Preference)localObject1).setTitle(2131693266);
    ((Preference)localObject1).setPersistent(false);
    ((Preference)localObject1).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        MetricsLogger.action(ZenModeAutomationSettings.this.mContext, 172);
        ZenModeAutomationSettings.-wrap3(ZenModeAutomationSettings.this);
        return true;
      }
    });
    localPreferenceScreen.addPreference((Preference)localObject1);
  }
  
  protected int getMetricsCategory()
  {
    return 142;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230892);
    this.mPm = this.mContext.getPackageManager();
    this.mServiceListing = new ZenServiceListing(this.mContext, CONFIG);
    this.mServiceListing.reloadApprovedServices();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (isUiRestricted()) {
      return;
    }
    updateControls();
  }
  
  protected void onZenModeChanged() {}
  
  protected void onZenModeConfigChanged()
  {
    updateControls();
  }
  
  private class LoadIconTask
    extends AsyncTask<ApplicationInfo, Void, Drawable>
  {
    private final WeakReference<Preference> prefReference;
    
    public LoadIconTask(Preference paramPreference)
    {
      this.prefReference = new WeakReference(paramPreference);
    }
    
    protected Drawable doInBackground(ApplicationInfo... paramVarArgs)
    {
      return paramVarArgs[0].loadIcon(ZenModeAutomationSettings.-get0(ZenModeAutomationSettings.this));
    }
    
    protected void onPostExecute(Drawable paramDrawable)
    {
      if (paramDrawable != null)
      {
        Preference localPreference = (Preference)this.prefReference.get();
        if (localPreference != null) {
          localPreference.setIcon(paramDrawable);
        }
      }
    }
  }
  
  private class ZenRulePreference
    extends Preference
  {
    final boolean appExists;
    private final View.OnClickListener mDeleteListener = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ZenModeAutomationSettings.-wrap4(ZenModeAutomationSettings.this, ZenModeAutomationSettings.ZenRulePreference.this.mId, ZenModeAutomationSettings.ZenRulePreference.this.mName);
      }
    };
    final String mId;
    final CharSequence mName;
    
    public ZenRulePreference(Map.Entry<String, AutomaticZenRule> paramEntry)
    {
      super();
      ComponentName localComponentName;
      AutomaticZenRule localAutomaticZenRule = (AutomaticZenRule)localComponentName.getValue();
      this.mName = localAutomaticZenRule.getName();
      this.mId = ((String)localComponentName.getKey());
      boolean bool3 = ZenModeConfig.isValidScheduleConditionId(localAutomaticZenRule.getConditionId());
      boolean bool2 = ZenModeConfig.isValidEventConditionId(localAutomaticZenRule.getConditionId());
      boolean bool1;
      if (!bool3) {
        bool1 = bool2;
      }
      for (;;)
      {
        try
        {
          paramEntry = ZenModeAutomationSettings.-get0(ZenModeAutomationSettings.this).getApplicationInfo(localAutomaticZenRule.getOwner().getPackageName(), 0);
          new ZenModeAutomationSettings.LoadIconTask(ZenModeAutomationSettings.this, this).execute(new ApplicationInfo[] { paramEntry });
          setSummary(ZenModeAutomationSettings.-wrap2(ZenModeAutomationSettings.this, localAutomaticZenRule, bool1, paramEntry.loadLabel(ZenModeAutomationSettings.-get0(ZenModeAutomationSettings.this))));
          this.appExists = true;
          setTitle(localAutomaticZenRule.getName());
          setPersistent(false);
          if (!bool3) {
            break label235;
          }
          paramEntry = "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS";
          localComponentName = ZenModeAutomationSettings.-wrap0(ZenModeAutomationSettings.-get1(ZenModeAutomationSettings.this).findService(localAutomaticZenRule.getOwner()));
          setIntent(ZenModeAutomationSettings.-wrap1(ZenModeAutomationSettings.this, paramEntry, localComponentName, this.mId));
          if (localComponentName != null) {
            break label252;
          }
          setSelectable(bool1);
          setWidgetLayoutResource(2130969131);
          return;
        }
        catch (PackageManager.NameNotFoundException this$1)
        {
          setIcon(2130837989);
          this.appExists = false;
          return;
        }
        bool1 = true;
        continue;
        label235:
        if (bool2)
        {
          paramEntry = "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS";
        }
        else
        {
          paramEntry = "";
          continue;
          label252:
          bool1 = true;
        }
      }
    }
    
    public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
    {
      super.onBindViewHolder(paramPreferenceViewHolder);
      View localView = paramPreferenceViewHolder.findViewById(2131362828);
      if (localView != null) {
        localView.setOnClickListener(this.mDeleteListener);
      }
      paramPreferenceViewHolder.setDividerAllowedAbove(true);
      paramPreferenceViewHolder.setDividerAllowedBelow(true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeAutomationSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
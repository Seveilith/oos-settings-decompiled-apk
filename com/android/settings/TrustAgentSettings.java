package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.widget.LockPatternUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.util.List;

public class TrustAgentSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String SERVICE_INTERFACE = "android.service.trust.TrustAgentService";
  private final ArraySet<ComponentName> mActiveAgents = new ArraySet();
  private ArrayMap<ComponentName, AgentInfo> mAvailableAgents;
  private DevicePolicyManager mDpm;
  private LockPatternUtils mLockPatternUtils;
  
  private void loadActiveAgents()
  {
    List localList = this.mLockPatternUtils.getEnabledTrustAgents(UserHandle.myUserId());
    if (localList != null) {
      this.mActiveAgents.addAll(localList);
    }
  }
  
  private void saveActiveAgents()
  {
    this.mLockPatternUtils.setEnabledTrustAgents(this.mActiveAgents, UserHandle.myUserId());
  }
  
  private void updateAgents()
  {
    Object localObject = getActivity();
    if (this.mAvailableAgents == null) {
      this.mAvailableAgents = findAvailableTrustAgents();
    }
    if (this.mLockPatternUtils == null) {
      this.mLockPatternUtils = new LockPatternUtils(getActivity());
    }
    loadActiveAgents();
    PreferenceGroup localPreferenceGroup = (PreferenceGroup)getPreferenceScreen().findPreference("trust_agents");
    localPreferenceGroup.removeAll();
    localObject = RestrictedLockUtils.checkIfKeyguardFeaturesDisabled((Context)localObject, 16, UserHandle.myUserId());
    int j = this.mAvailableAgents.size();
    int i = 0;
    while (i < j)
    {
      AgentInfo localAgentInfo = (AgentInfo)this.mAvailableAgents.valueAt(i);
      OPRestrictedSwitchPreference localOPRestrictedSwitchPreference = new OPRestrictedSwitchPreference(getPrefContext());
      localOPRestrictedSwitchPreference.useAdminDisabledSummary(true);
      localAgentInfo.preference = localOPRestrictedSwitchPreference;
      localOPRestrictedSwitchPreference.setPersistent(false);
      localOPRestrictedSwitchPreference.setTitle(localAgentInfo.label);
      localOPRestrictedSwitchPreference.setIcon(localAgentInfo.icon);
      localOPRestrictedSwitchPreference.setPersistent(false);
      localOPRestrictedSwitchPreference.setOnPreferenceChangeListener(this);
      localOPRestrictedSwitchPreference.setChecked(this.mActiveAgents.contains(localAgentInfo.component));
      if ((localObject != null) && (this.mDpm.getTrustAgentConfiguration(null, localAgentInfo.component) == null))
      {
        localOPRestrictedSwitchPreference.setChecked(false);
        localOPRestrictedSwitchPreference.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
      }
      localPreferenceGroup.addPreference(localAgentInfo.preference);
      i += 1;
    }
  }
  
  ArrayMap<ComponentName, AgentInfo> findAvailableTrustAgents()
  {
    PackageManager localPackageManager = getActivity().getPackageManager();
    List localList = localPackageManager.queryIntentServices(new Intent("android.service.trust.TrustAgentService"), 128);
    ArrayMap localArrayMap = new ArrayMap();
    int j = localList.size();
    localArrayMap.ensureCapacity(j);
    int i = 0;
    if (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localList.get(i);
      if (localResolveInfo.serviceInfo == null) {}
      for (;;)
      {
        i += 1;
        break;
        if (TrustAgentUtils.checkProvidePermission(localResolveInfo, localPackageManager))
        {
          ComponentName localComponentName = TrustAgentUtils.getComponentName(localResolveInfo);
          AgentInfo localAgentInfo = new AgentInfo();
          localAgentInfo.label = localResolveInfo.loadLabel(localPackageManager);
          localAgentInfo.icon = localResolveInfo.loadIcon(localPackageManager);
          localAgentInfo.component = localComponentName;
          localArrayMap.put(localComponentName, localAgentInfo);
        }
      }
    }
    return localArrayMap;
  }
  
  protected int getMetricsCategory()
  {
    return 91;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService(DevicePolicyManager.class));
    addPreferencesFromResource(2131230870);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ((paramPreference instanceof SwitchPreference))
    {
      int j = this.mAvailableAgents.size();
      int i = 0;
      while (i < j)
      {
        AgentInfo localAgentInfo = (AgentInfo)this.mAvailableAgents.valueAt(i);
        if (localAgentInfo.preference == paramPreference)
        {
          if (((Boolean)paramObject).booleanValue()) {
            if (!this.mActiveAgents.contains(localAgentInfo.component)) {
              this.mActiveAgents.add(localAgentInfo.component);
            }
          }
          for (;;)
          {
            saveActiveAgents();
            return true;
            this.mActiveAgents.remove(localAgentInfo.component);
          }
        }
        i += 1;
      }
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
    removePreference("dummy_preference");
    updateAgents();
  }
  
  public static final class AgentInfo
  {
    ComponentName component;
    public Drawable icon;
    CharSequence label;
    SwitchPreference preference;
    
    public int compareTo(AgentInfo paramAgentInfo)
    {
      return this.component.compareTo(paramAgentInfo.component);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof AgentInfo)) {
        return this.component.equals(((AgentInfo)paramObject).component);
      }
      return true;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\TrustAgentSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
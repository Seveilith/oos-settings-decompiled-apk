package com.android.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.fingerprint.SetupSkipDialog;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.android.setupwizardlib.GlifPreferenceLayout;

public class OxygenSetupChooseLockGeneric
  extends OxygenChooseLockGeneric
{
  private static final String KEY_UNLOCK_SET_DO_LATER = "unlock_set_do_later";
  
  Class<? extends PreferenceFragment> getFragmentClass()
  {
    return SetupChooseLockGenericFragment.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupChooseLockGenericFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getOxygenTheme(getIntent()), paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public static class SetupChooseLockGenericFragment
    extends OxygenChooseLockGeneric.ChooseLockGenericFragment
  {
    public static final String EXTRA_PASSWORD_QUALITY = ":settings:password_quality";
    
    protected void addHeaderView()
    {
      if (this.mForFingerprint)
      {
        setHeaderView(2130968882);
        return;
      }
      setHeaderView(2130968883);
    }
    
    protected void addPreferences()
    {
      if (this.mForFingerprint)
      {
        super.addPreferences();
        return;
      }
      addPreferencesFromResource(2131230824);
    }
    
    protected void disableUnusablePreferences(int paramInt, boolean paramBoolean)
    {
      super.disableUnusablePreferencesImpl(Math.max(paramInt, 65536), true);
    }
    
    protected Intent getEncryptionInterstitialIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
    {
      paramContext = OxygenSetupEncryptionInterstitial.createStartIntent(paramContext, paramInt, paramBoolean, paramIntent);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, long paramLong, int paramInt4)
    {
      paramContext = OxygenSetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramLong);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString, int paramInt4)
    {
      paramContext = OxygenSetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramString);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4)
    {
      paramContext = OxygenSetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, long paramLong, int paramInt)
    {
      paramContext = OxygenSetupChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, String paramString, int paramInt)
    {
      paramContext = OxygenSetupChooseLockPattern.createIntent(paramContext, paramBoolean, paramString);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    {
      paramContext = OxygenSetupChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      if (paramInt2 != 0)
      {
        Intent localIntent = paramIntent;
        if (paramIntent == null) {
          localIntent = new Intent();
        }
        localIntent.putExtra(":settings:password_quality", new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
        getPackageManager().setComponentEnabledSetting(new ComponentName("com.android.settings", "com.android.settings.SetupRedactionInterstitial"), 1, 1);
        super.onActivityResult(paramInt1, paramInt2, localIntent);
      }
    }
    
    public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      return ((GlifPreferenceLayout)paramViewGroup).onCreateRecyclerView(paramLayoutInflater, paramViewGroup, paramBundle);
    }
    
    public boolean onPreferenceTreeClick(Preference paramPreference)
    {
      if ("unlock_set_do_later".equals(paramPreference.getKey()))
      {
        SetupSkipDialog.newInstance(getActivity().getIntent().getBooleanExtra(":settings:frp_supported", false)).show(getFragmentManager());
        return true;
      }
      return super.onPreferenceTreeClick(paramPreference);
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      paramView = (GlifPreferenceLayout)paramView;
      paramView.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
      paramView.setDividerInset(0);
      paramView.setIcon(getContext().getDrawable(2130837993));
      if (this.mForFingerprint) {}
      for (int i = 2131691144;; i = 2131691146)
      {
        if (getActivity() != null) {
          getActivity().setTitle(i);
        }
        paramView.setHeaderText(i);
        setDivider(null);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenSetupChooseLockGeneric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
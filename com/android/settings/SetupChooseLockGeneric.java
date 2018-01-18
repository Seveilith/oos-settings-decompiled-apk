package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.fingerprint.SetupSkipDialog;
import com.android.setupwizardlib.SetupWizardPreferenceLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class SetupChooseLockGeneric
  extends ChooseLockGeneric
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
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getSettingTheme(), paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTheme();
    ((DrawerLayout)findViewById(2131362553)).setFitsSystemWindows(false);
    ((ListView)findViewById(2131362557)).setFitsSystemWindows(false);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public void setTheme()
  {
    setTheme(2131821452);
    ActionBar localActionBar = getActionBar();
    if (localActionBar != null)
    {
      localActionBar.setBackgroundDrawable(new ColorDrawable(17170445));
      localActionBar.setDisplayHomeAsUpEnabled(true);
      localActionBar.setTitle("");
    }
    getWindow().addFlags(-2147417856);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {}
    for (int i = 9472;; i = 1280)
    {
      getWindow().getDecorView().setSystemUiVisibility(i);
      getWindow().setStatusBarColor(0);
      return;
    }
  }
  
  public static class SetupChooseLockGenericFragment
    extends ChooseLockGeneric.ChooseLockGenericFragment
    implements NavigationBar.NavigationBarListener
  {
    public static final String EXTRA_PASSWORD_QUALITY = ":settings:password_quality";
    
    protected void addHeaderView()
    {
      if (this.mForFingerprint)
      {
        setHeaderView(2130968989);
        return;
      }
      setHeaderView(2130968990);
    }
    
    protected void addPreferences()
    {
      if (this.mForFingerprint)
      {
        super.addPreferences();
        return;
      }
      addPreferencesFromResource(2131230857);
    }
    
    protected void disableUnusablePreferences(int paramInt, boolean paramBoolean)
    {
      super.disableUnusablePreferencesImpl(Math.max(paramInt, 65536), true);
    }
    
    protected Intent getEncryptionInterstitialIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
    {
      paramContext = SetupEncryptionInterstitial.createStartIntent(paramContext, paramInt, paramBoolean, paramIntent);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, long paramLong, int paramInt4)
    {
      paramContext = SetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramLong);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString, int paramInt4)
    {
      paramContext = SetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramString);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPasswordIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4)
    {
      paramContext = SetupChooseLockPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, long paramLong, int paramInt)
    {
      paramContext = SetupChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean, String paramString, int paramInt)
    {
      paramContext = SetupChooseLockPattern.createIntent(paramContext, paramBoolean, paramString);
      SetupWizardUtils.copySetupExtras(getActivity().getIntent(), paramContext);
      return paramContext;
    }
    
    protected Intent getLockPatternIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
    {
      paramContext = SetupChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2);
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
      return ((SetupWizardPreferenceLayout)paramViewGroup).onCreateRecyclerView(paramLayoutInflater, paramViewGroup, paramBundle);
    }
    
    public void onNavigateBack()
    {
      Activity localActivity = getActivity();
      if (localActivity != null) {
        localActivity.onBackPressed();
      }
    }
    
    public void onNavigateNext() {}
    
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
      paramView = (SetupWizardPreferenceLayout)paramView;
      paramView.setDividerInset(getContext().getResources().getDimensionPixelSize(2131755097));
      paramBundle = paramView.getNavigationBar();
      Button localButton = paramBundle.getNextButton();
      localButton.setText(null);
      localButton.setEnabled(false);
      paramBundle.setNavigationBarListener(this);
      paramView.setIllustration(2130838163, 2130838513);
      paramView.setBackgroundTile(2130838217);
      if (!this.mForFingerprint) {
        paramView.setHeaderText(2131691146);
      }
      for (;;)
      {
        paramView.getHeaderTextView().setTextColor(getResources().getColor(2131493771));
        setDivider(null);
        return;
        paramView.setHeaderText(2131691144);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupChooseLockGeneric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
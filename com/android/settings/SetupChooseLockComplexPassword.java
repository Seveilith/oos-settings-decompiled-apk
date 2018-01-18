package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.util.SystemBarHelper;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class SetupChooseLockComplexPassword
  extends ChooseLockComplexPassword
{
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, long paramLong)
  {
    Intent localIntent = ChooseLockComplexPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramLong);
    localIntent.setClass(paramContext, SetupChooseLockComplexPassword.class);
    localIntent.putExtra("extra_prefs_show_button_bar", false);
    return localIntent;
  }
  
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString)
  {
    paramString = ChooseLockComplexPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean, paramString);
    paramString.setClass(paramContext, SetupChooseLockComplexPassword.class);
    paramString.putExtra("extra_prefs_show_button_bar", false);
    return paramString;
  }
  
  public static Intent createIntent(Context paramContext, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = ChooseLockComplexPassword.createIntent(paramContext, paramInt1, paramInt2, paramInt3, paramBoolean1, paramBoolean2);
    localIntent.setClass(paramContext, SetupChooseLockComplexPassword.class);
    localIntent.putExtra("extra_prefs_show_button_bar", false);
    return localIntent;
  }
  
  Class<? extends Fragment> getFragmentClass()
  {
    return SetupChooseLockComplexPasswordFragment.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupChooseLockComplexPasswordFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    super.onApplyThemeResource(paramTheme, SetupWizardUtils.getTheme(getIntent()), paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setTheme();
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
  
  public static class SetupChooseLockComplexPasswordFragment
    extends ChooseLockComplexPassword.ChooseLockComplexPasswordFragment
    implements NavigationBar.NavigationBarListener
  {
    private SetupWizardLayout mLayout;
    private NavigationBar mNavigationBar;
    
    protected Intent getRedactionInterstitialIntent(Context paramContext)
    {
      return null;
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      this.mLayout = ((SetupWizardLayout)paramLayoutInflater.inflate(2130968991, paramViewGroup, false));
      this.mNavigationBar = this.mLayout.getNavigationBar();
      this.mNavigationBar.setNavigationBarListener(this);
      return this.mLayout;
    }
    
    public void onNavigateBack()
    {
      Activity localActivity = getActivity();
      if (localActivity != null) {
        localActivity.onBackPressed();
      }
    }
    
    public void onNavigateNext()
    {
      handleNext();
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      SystemBarHelper.setImeInsetView(this.mLayout);
      SetupWizardUtils.setImmersiveMode(getActivity());
      this.mLayout.setHeaderText(getActivity().getTitle());
      ((TextView)this.mLayout.findViewById(2131362140)).setTextColor(getResources().getColor(2131493771));
    }
    
    protected void setNextEnabled(boolean paramBoolean)
    {
      this.mNavigationBar.getNextButton().setEnabled(paramBoolean);
    }
    
    protected void setNextText(int paramInt)
    {
      this.mNavigationBar.getNextButton().setText(paramInt);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupChooseLockComplexPassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
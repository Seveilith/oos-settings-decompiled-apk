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
import android.os.UserHandle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class SetupChooseLockPattern
  extends ChooseLockPattern
{
  public static Intent createIntent(Context paramContext, boolean paramBoolean, long paramLong)
  {
    Intent localIntent = ChooseLockPattern.createIntent(paramContext, paramBoolean, paramLong, UserHandle.myUserId());
    localIntent.setClass(paramContext, SetupChooseLockPattern.class);
    return localIntent;
  }
  
  public static Intent createIntent(Context paramContext, boolean paramBoolean, String paramString)
  {
    paramString = ChooseLockPattern.createIntent(paramContext, paramBoolean, paramString, UserHandle.myUserId());
    paramString.setClass(paramContext, SetupChooseLockPattern.class);
    return paramString;
  }
  
  public static Intent createIntent(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = ChooseLockPattern.createIntent(paramContext, paramBoolean1, paramBoolean2, UserHandle.myUserId());
    localIntent.setClass(paramContext, SetupChooseLockPattern.class);
    return localIntent;
  }
  
  Class<? extends Fragment> getFragmentClass()
  {
    return SetupChooseLockPatternFragment.class;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupChooseLockPatternFragment.class.getName().equals(paramString);
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
    setTheme(2131821455);
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
  
  public static class SetupChooseLockPatternFragment
    extends ChooseLockPattern.ChooseLockPatternFragment
    implements NavigationBar.NavigationBarListener
  {
    private NavigationBar mNavigationBar;
    private Button mRetryButton;
    
    protected Intent getRedactionInterstitialIntent(Context paramContext)
    {
      return null;
    }
    
    public void onClick(View paramView)
    {
      if (paramView == this.mRetryButton)
      {
        handleLeftButton();
        return;
      }
      super.onClick(paramView);
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      paramLayoutInflater = (SetupWizardLayout)paramLayoutInflater.inflate(2130968992, paramViewGroup, false);
      this.mNavigationBar = paramLayoutInflater.getNavigationBar();
      this.mNavigationBar.setNavigationBarListener(this);
      paramLayoutInflater.setHeaderText(getActivity().getTitle());
      ((TextView)paramLayoutInflater.findViewById(2131362140)).setTextColor(getResources().getColor(2131493771));
      return paramLayoutInflater;
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
      handleRightButton();
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      this.mRetryButton = ((Button)paramView.findViewById(2131362558));
      this.mRetryButton.setOnClickListener(this);
      this.mRetryButton.setVisibility(8);
      super.onViewCreated(paramView, paramBundle);
      SetupWizardUtils.setImmersiveMode(getActivity());
    }
    
    protected void setRightButtonEnabled(boolean paramBoolean)
    {
      this.mNavigationBar.getNextButton().setEnabled(paramBoolean);
    }
    
    protected void setRightButtonText(int paramInt)
    {
      this.mNavigationBar.getNextButton().setText(paramInt);
    }
    
    protected void updateStage(ChooseLockPattern.ChooseLockPatternFragment.Stage paramStage)
    {
      super.updateStage(paramStage);
      Button localButton = this.mRetryButton;
      if (paramStage == ChooseLockPattern.ChooseLockPatternFragment.Stage.FirstChoiceValid) {}
      for (boolean bool = true;; bool = false)
      {
        localButton.setEnabled(bool);
        switch (-getcom-android-settings-ChooseLockPattern$ChooseLockPatternFragment$StageSwitchesValues()[paramStage.ordinal()])
        {
        default: 
          return;
        }
      }
      this.mRetryButton.setVisibility(8);
      return;
      this.mRetryButton.setVisibility(8);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupChooseLockPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
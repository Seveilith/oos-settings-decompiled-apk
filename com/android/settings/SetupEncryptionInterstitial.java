package com.android.settings;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.setupwizardlib.SetupWizardPreferenceLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;
import java.io.PrintStream;

public class SetupEncryptionInterstitial
  extends EncryptionInterstitial
{
  public static Intent createStartIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
  {
    paramIntent = EncryptionInterstitial.createStartIntent(paramContext, paramInt, paramBoolean, paramIntent);
    paramIntent.setClass(paramContext, SetupEncryptionInterstitial.class);
    paramIntent.putExtra("extra_prefs_show_button_bar", false).putExtra(":settings:show_fragment_title_resid", -1);
    return paramIntent;
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", SetupEncryptionInterstitialFragment.class.getName());
    return localIntent;
  }
  
  public void initThemes()
  {
    System.out.println("zhuyang--initThemes");
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
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupEncryptionInterstitialFragment.class.getName().equals(paramString);
  }
  
  protected void onApplyThemeResource(Resources.Theme paramTheme, int paramInt, boolean paramBoolean)
  {
    paramInt = SetupWizardUtils.getSettingTheme();
    System.out.println("zhuyang--onApplyThemeResource");
    super.onApplyThemeResource(paramTheme, paramInt, paramBoolean);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    initThemes();
    ((DrawerLayout)findViewById(2131362553)).setFitsSystemWindows(false);
    ((ListView)findViewById(2131362557)).setFitsSystemWindows(false);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public static class SetupEncryptionInterstitialFragment
    extends EncryptionInterstitial.EncryptionInterstitialFragment
    implements NavigationBar.NavigationBarListener
  {
    protected TextView createHeaderView()
    {
      return (TextView)LayoutInflater.from(getActivity()).inflate(2130968994, null, false);
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
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      paramView = (SetupWizardPreferenceLayout)paramView;
      paramView.setDividerInset(getContext().getResources().getDimensionPixelSize(2131755096));
      paramView.setIllustration(2130838163, 2130838513);
      paramView.setBackgroundTile(2130838217);
      paramBundle = paramView.getNavigationBar();
      paramBundle.setNavigationBarListener(this);
      paramBundle = paramBundle.getNextButton();
      paramBundle.setText(null);
      paramBundle.setEnabled(false);
      paramView.setHeaderText(2131693352);
      paramView.getHeaderTextView().setTextColor(getResources().getColor(2131493771));
      paramView = getActivity();
      if (paramView != null) {
        SetupWizardUtils.setImmersiveMode(paramView);
      }
      setDivider(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SetupEncryptionInterstitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
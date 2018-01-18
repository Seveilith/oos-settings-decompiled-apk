package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.android.setupwizardlib.GlifPreferenceLayout;

public class OxygenSetupEncryptionInterstitial
  extends OxygenEncryptionInterstitial
{
  public static Intent createStartIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
  {
    paramIntent = EncryptionInterstitial.createStartIntent(paramContext, paramInt, paramBoolean, paramIntent);
    paramIntent.setClass(paramContext, OxygenSetupEncryptionInterstitial.class);
    paramIntent.putExtra("extra_prefs_show_button_bar", false).putExtra(":settings:show_fragment_title_resid", -1);
    return paramIntent;
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", SetupEncryptionInterstitialFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return SetupEncryptionInterstitialFragment.class.getName().equals(paramString);
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
  
  public static class SetupEncryptionInterstitialFragment
    extends OxygenEncryptionInterstitial.EncryptionInterstitialFragment
  {
    public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      return ((GlifPreferenceLayout)paramViewGroup).onCreateRecyclerView(paramLayoutInflater, paramViewGroup, paramBundle);
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      paramView = (GlifPreferenceLayout)paramView;
      paramView.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
      paramView.setDividerInset(getContext().getResources().getDimensionPixelSize(2131755098));
      paramView.setIcon(getContext().getDrawable(2130837993));
      paramView.setHeaderText(2131693352);
      setDivider(null);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenSetupEncryptionInterstitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
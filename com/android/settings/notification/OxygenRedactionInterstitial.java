package com.android.settings.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.android.settings.RestrictedRadioButton;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;

public class OxygenRedactionInterstitial
  extends SettingsActivity
{
  public static Intent createStartIntent(Context paramContext, int paramInt)
  {
    Intent localIntent = new Intent(paramContext, OxygenRedactionInterstitial.class);
    if (Utils.isManagedProfile(UserManager.get(paramContext), paramInt)) {}
    for (int i = 2131693221;; i = 2131693216) {
      return localIntent.putExtra(":settings:show_fragment_title_resid", i).putExtra("android.intent.extra.USER_ID", paramInt);
    }
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", RedactionInterstitialFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return RedactionInterstitialFragment.class.getName().equals(paramString);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public static class RedactionInterstitialFragment
    extends SettingsPreferenceFragment
    implements RadioGroup.OnCheckedChangeListener, View.OnClickListener
  {
    private RadioGroup mRadioGroup;
    private RestrictedRadioButton mRedactSensitiveButton;
    private RestrictedRadioButton mShowAllButton;
    private int mUserId;
    
    private void checkNotificationFeaturesAndSetDisabled(RestrictedRadioButton paramRestrictedRadioButton, int paramInt)
    {
      paramRestrictedRadioButton.setDisabledByAdmin(RestrictedLockUtils.checkIfKeyguardFeaturesDisabled(getActivity(), paramInt, this.mUserId));
    }
    
    private void loadFromSettings()
    {
      int j;
      int k;
      if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_show_notifications", 0, this.mUserId) != 0)
      {
        j = 1;
        if (Settings.Secure.getIntForUser(getContentResolver(), "lock_screen_allow_private_notifications", 1, this.mUserId) == 0) {
          break label93;
        }
        k = 1;
        label38:
        int m = 2131362402;
        i = m;
        if (j != 0)
        {
          if ((k != 0) && (!this.mShowAllButton.isDisabledByAdmin())) {
            break label98;
          }
          i = m;
          if (this.mRedactSensitiveButton.isDisabledByAdmin()) {}
        }
      }
      label93:
      label98:
      for (int i = 2131362400;; i = 2131362401)
      {
        this.mRadioGroup.check(i);
        return;
        j = 0;
        break;
        k = 0;
        break label38;
      }
    }
    
    protected int getMetricsCategory()
    {
      return 74;
    }
    
    public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
    {
      int j = 1;
      int i;
      if (paramInt == 2131362401)
      {
        i = 1;
        if (paramInt == 2131362402) {
          break label72;
        }
        paramInt = 1;
        label19:
        paramRadioGroup = getContentResolver();
        if (i == 0) {
          break label77;
        }
        i = 1;
        label30:
        Settings.Secure.putIntForUser(paramRadioGroup, "lock_screen_allow_private_notifications", i, this.mUserId);
        paramRadioGroup = getContentResolver();
        if (paramInt == 0) {
          break label82;
        }
      }
      label72:
      label77:
      label82:
      for (paramInt = j;; paramInt = 0)
      {
        Settings.Secure.putIntForUser(paramRadioGroup, "lock_screen_show_notifications", paramInt, this.mUserId);
        return;
        i = 0;
        break;
        paramInt = 0;
        break label19;
        i = 0;
        break label30;
      }
    }
    
    public void onClick(View paramView)
    {
      if (paramView.getId() == 2131362403)
      {
        paramView = (OxygenRedactionInterstitial)getActivity();
        if (paramView != null)
        {
          paramView.setResult(-1, paramView.getResultIntentData());
          finish();
        }
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      return paramLayoutInflater.inflate(2130968881, paramViewGroup, false);
    }
    
    public void onResume()
    {
      super.onResume();
      checkNotificationFeaturesAndSetDisabled(this.mShowAllButton, 12);
      checkNotificationFeaturesAndSetDisabled(this.mRedactSensitiveButton, 4);
      loadFromSettings();
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      this.mRadioGroup = ((RadioGroup)paramView.findViewById(2131362399));
      this.mShowAllButton = ((RestrictedRadioButton)paramView.findViewById(2131362401));
      this.mRedactSensitiveButton = ((RestrictedRadioButton)paramView.findViewById(2131362400));
      this.mRadioGroup.setOnCheckedChangeListener(this);
      this.mUserId = Utils.getUserIdFromBundle(getContext(), getActivity().getIntent().getExtras());
      if (Utils.isManagedProfile(UserManager.get(getContext()), this.mUserId))
      {
        ((TextView)paramView.findViewById(2131361987)).setText(2131693220);
        this.mShowAllButton.setText(2131693217);
        this.mRedactSensitiveButton.setText(2131693218);
        ((RadioButton)paramView.findViewById(2131362402)).setText(2131693219);
      }
      ((Button)paramView.findViewById(2131362403)).setOnClickListener(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\OxygenRedactionInterstitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
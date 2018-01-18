package com.android.settings;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.android.setupwizardlib.GlifPreferenceLayout;
import java.util.List;

public class OxygenEncryptionInterstitial
  extends SettingsActivity
{
  private static final int CHOOSE_LOCK_REQUEST = 100;
  protected static final String EXTRA_PASSWORD_QUALITY = "extra_password_quality";
  public static final String EXTRA_REQUIRE_PASSWORD = "extra_require_password";
  protected static final String EXTRA_UNLOCK_METHOD_INTENT = "extra_unlock_method_intent";
  private static final String TAG = EncryptionInterstitial.class.getSimpleName();
  
  public static Intent createStartIntent(Context paramContext, int paramInt, boolean paramBoolean, Intent paramIntent)
  {
    return new Intent(paramContext, EncryptionInterstitial.class).putExtra("extra_password_quality", paramInt).putExtra(":settings:show_fragment_title_resid", 2131693352).putExtra("extra_require_password", paramBoolean).putExtra("extra_unlock_method_intent", paramIntent);
  }
  
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":settings:show_fragment", EncryptionInterstitialFragment.class.getName());
    return localIntent;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    return EncryptionInterstitialFragment.class.getName().equals(paramString);
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ((LinearLayout)findViewById(2131362554)).setFitsSystemWindows(false);
  }
  
  public static class EncryptionInterstitialFragment
    extends SettingsPreferenceFragment
    implements DialogInterface.OnClickListener
  {
    private static final int ACCESSIBILITY_WARNING_DIALOG = 1;
    private static final String KEY_ENCRYPT_DONT_REQUIRE_PASSWORD = "encrypt_dont_require_password";
    private static final String KEY_ENCRYPT_REQUIRE_PASSWORD = "encrypt_require_password";
    private Preference mDontRequirePasswordToDecrypt;
    private boolean mPasswordRequired;
    private int mRequestedPasswordQuality;
    private Preference mRequirePasswordToDecrypt;
    private Intent mUnlockMethodIntent;
    
    private void setRequirePasswordState(boolean paramBoolean)
    {
      this.mPasswordRequired = paramBoolean;
    }
    
    protected int getMetricsCategory()
    {
      return 48;
    }
    
    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      super.onActivityResult(paramInt1, paramInt2, paramIntent);
      if ((paramInt1 == 100) && (paramInt2 != 0))
      {
        getActivity().setResult(paramInt2, paramIntent);
        finish();
      }
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1)
      {
        setRequirePasswordState(true);
        startLockIntent();
      }
      while (paramInt != -2) {
        return;
      }
      setRequirePasswordState(false);
    }
    
    public void onCreate(Bundle paramBundle)
    {
      super.onCreate(paramBundle);
      addPreferencesFromResource(2131230838);
      findPreference("encrypt_dont_require_password").setViewId(2131361813);
      this.mRequirePasswordToDecrypt = findPreference("encrypt_require_password");
      this.mDontRequirePasswordToDecrypt = findPreference("encrypt_dont_require_password");
      boolean bool = getActivity().getIntent().getBooleanExtra("for_fingerprint", false);
      paramBundle = getActivity().getIntent();
      this.mRequestedPasswordQuality = paramBundle.getIntExtra("extra_password_quality", 0);
      this.mUnlockMethodIntent = ((Intent)paramBundle.getParcelableExtra("extra_unlock_method_intent"));
      switch (this.mRequestedPasswordQuality)
      {
      default: 
        if (!bool) {
          break;
        }
      }
      for (int k = 2131693359;; k = 2131693356)
      {
        int j = 2131693362;
        int i = 2131693365;
        paramBundle = (TextView)LayoutInflater.from(getActivity()).inflate(2130968698, null, false);
        paramBundle.setText(k);
        setHeaderView(paramBundle);
        this.mRequirePasswordToDecrypt.setTitle(j);
        this.mDontRequirePasswordToDecrypt.setTitle(i);
        setRequirePasswordState(getActivity().getIntent().getBooleanExtra("extra_require_password", true));
        return;
        if (bool) {}
        for (k = 2131693358;; k = 2131693355)
        {
          j = 2131693361;
          i = 2131693364;
          break;
        }
        if (bool) {}
        for (k = 2131693357;; k = 2131693354)
        {
          j = 2131693360;
          i = 2131693363;
          break;
        }
      }
    }
    
    public Dialog onCreateDialog(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException();
      }
      int i;
      switch (this.mRequestedPasswordQuality)
      {
      default: 
        i = 2131693368;
        paramInt = 2131693371;
        localObject = AccessibilityManager.getInstance(getActivity()).getEnabledAccessibilityServiceList(-1);
        if (!((List)localObject).isEmpty()) {
          break;
        }
      }
      for (Object localObject = "";; localObject = ((AccessibilityServiceInfo)((List)localObject).get(0)).getResolveInfo().loadLabel(getPackageManager()))
      {
        return new AlertDialog.Builder(getActivity()).setTitle(i).setMessage(getString(paramInt, new Object[] { localObject })).setCancelable(true).setPositiveButton(17039370, this).setNegativeButton(17039360, this).create();
        i = 2131693367;
        paramInt = 2131693370;
        break;
        i = 2131693366;
        paramInt = 2131693369;
        break;
      }
    }
    
    public RecyclerView onCreateRecyclerView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      return ((GlifPreferenceLayout)paramViewGroup).onCreateRecyclerView(paramLayoutInflater, paramViewGroup, paramBundle);
    }
    
    public boolean onPreferenceTreeClick(Preference paramPreference)
    {
      if (paramPreference.getKey().equals("encrypt_require_password"))
      {
        if ((!AccessibilityManager.getInstance(getActivity()).isEnabled()) || (this.mPasswordRequired))
        {
          setRequirePasswordState(true);
          startLockIntent();
          return true;
        }
        setRequirePasswordState(false);
        showDialog(1);
        return true;
      }
      setRequirePasswordState(false);
      startLockIntent();
      return true;
    }
    
    public void onViewCreated(View paramView, Bundle paramBundle)
    {
      super.onViewCreated(paramView, paramBundle);
      paramView = (GlifPreferenceLayout)paramView;
      paramView.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
      paramView.setIcon(getContext().getDrawable(2130837993));
      paramView.setHeaderText(getActivity().getTitle());
      setDivider(null);
    }
    
    protected void startLockIntent()
    {
      if (this.mUnlockMethodIntent != null)
      {
        this.mUnlockMethodIntent.putExtra("extra_require_password", this.mPasswordRequired);
        startActivityForResult(this.mUnlockMethodIntent, 100);
        return;
      }
      Log.wtf(OxygenEncryptionInterstitial.-get0(), "no unlock intent to start");
      finish();
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OxygenEncryptionInterstitial.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
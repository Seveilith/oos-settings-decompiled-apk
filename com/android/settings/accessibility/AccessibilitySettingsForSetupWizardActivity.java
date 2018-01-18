package com.android.settings.accessibility;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.settings.SettingsActivity;
import com.android.setupwizardlib.view.NavigationBar;
import com.android.setupwizardlib.view.NavigationBar.NavigationBarListener;
import com.oneplus.settings.utils.OPUtils;

public class AccessibilitySettingsForSetupWizardActivity
  extends SettingsActivity
{
  private static final String SAVE_KEY_TITLE = "activity_title";
  private boolean mSendExtraWindowStateChanged;
  
  public void onAttachFragment(Fragment paramFragment)
  {
    if (this.mSendExtraWindowStateChanged) {
      getWindow().getDecorView().sendAccessibilityEvent(32);
    }
  }
  
  protected void onCreate(final Bundle paramBundle)
  {
    setMainContentId(2131361933);
    super.onCreate(paramBundle);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {
      getWindow().getDecorView().setSystemUiVisibility(8192);
    }
    paramBundle = (FrameLayout)findViewById(2131362550);
    LayoutInflater.from(this).inflate(2130968602, paramBundle);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    setIsDrawerPresent(false);
    paramBundle = (LinearLayout)findViewById(2131362554);
    paramBundle.setFitsSystemWindows(false);
    paramBundle.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
    {
      public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
      {
        paramBundle.setPadding(0, paramAnonymousWindowInsets.getSystemWindowInsetTop(), 0, 0);
        return paramAnonymousWindowInsets;
      }
    });
    paramBundle = (NavigationBar)findViewById(2131361934);
    paramBundle.setVisibility(8);
    paramBundle.setNavigationBarListener(new NavigationBar.NavigationBarListener()
    {
      public void onNavigateBack()
      {
        AccessibilitySettingsForSetupWizardActivity.this.onNavigateUp();
      }
      
      public void onNavigateNext() {}
    });
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    return true;
  }
  
  public boolean onNavigateUp()
  {
    onBackPressed();
    getWindow().getDecorView().sendAccessibilityEvent(32);
    return true;
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    super.onRestoreInstanceState(paramBundle);
    setTitle(paramBundle.getCharSequence("activity_title"));
  }
  
  public void onResume()
  {
    super.onResume();
    this.mSendExtraWindowStateChanged = false;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putCharSequence("activity_title", getTitle());
    super.onSaveInstanceState(paramBundle);
  }
  
  public void startPreferencePanel(String paramString, Bundle paramBundle, int paramInt1, CharSequence paramCharSequence, Fragment paramFragment, int paramInt2)
  {
    if (!TextUtils.isEmpty(paramCharSequence)) {
      setTitle(paramCharSequence);
    }
    for (;;)
    {
      paramBundle.putInt("help_uri_resource", 0);
      startPreferenceFragment(Fragment.instantiate(this, paramString, paramBundle), true);
      this.mSendExtraWindowStateChanged = true;
      return;
      if (paramInt1 > 0) {
        setTitle(getString(paramInt1));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accessibility\AccessibilitySettingsForSetupWizardActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
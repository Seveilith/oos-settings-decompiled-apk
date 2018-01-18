package com.android.settings.fingerprint;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.InstrumentedActivity;
import com.android.setupwizardlib.GlifLayout;
import com.oneplus.settings.utils.OPUtils;

public abstract class FingerprintEnrollBase
  extends InstrumentedActivity
  implements View.OnClickListener
{
  public static final int RESULT_FINISHED = 1;
  static final int RESULT_SKIP = 2;
  static final int RESULT_TIMEOUT = 3;
  protected byte[] mToken;
  protected int mUserId;
  
  protected Intent getEnrollingIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
    localIntent.putExtra("hw_auth_token", this.mToken);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    return localIntent;
  }
  
  protected GlifLayout getLayout()
  {
    return (GlifLayout)findViewById(2131362138);
  }
  
  protected Button getNextButton()
  {
    return (Button)findViewById(2131362013);
  }
  
  protected void initViews()
  {
    Button localButton = getNextButton();
    if (localButton != null) {
      localButton.setOnClickListener(this);
    }
  }
  
  public void onClick(View paramView)
  {
    if (paramView == getNextButton()) {
      onNextButtonClick();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ActionBar localActionBar = getActionBar();
    if (localActionBar != null) {
      localActionBar.setDisplayHomeAsUpEnabled(true);
    }
    getWindow().addFlags(-2147417856);
    if (OPUtils.isWhiteModeOn(getContentResolver())) {}
    for (int i = 9472;; i = 1280)
    {
      getWindow().getDecorView().setSystemUiVisibility(i);
      if (supportStatusBarTransparent()) {
        getWindow().setStatusBarColor(0);
      }
      this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
      if ((paramBundle != null) && (this.mToken == null)) {
        this.mToken = paramBundle.getByteArray("hw_auth_token");
      }
      this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
      return;
    }
  }
  
  protected void onNextButtonClick() {}
  
  protected void onPostCreate(Bundle paramBundle)
  {
    super.onPostCreate(paramBundle);
    initViews();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putByteArray("hw_auth_token", this.mToken);
  }
  
  protected void setHeaderIcon(int paramInt)
  {
    getLayout().setIcon(getResources().getDrawable(paramInt));
  }
  
  protected void setHeaderText(int paramInt)
  {
    setHeaderText(paramInt, false);
  }
  
  protected void setHeaderText(int paramInt, boolean paramBoolean)
  {
    TextView localTextView = getLayout().getHeaderTextView();
    CharSequence localCharSequence1 = localTextView.getText();
    CharSequence localCharSequence2 = getText(paramInt);
    if ((localCharSequence1 != localCharSequence2) || (paramBoolean))
    {
      if (!TextUtils.isEmpty(localCharSequence1)) {
        localTextView.setAccessibilityLiveRegion(1);
      }
      getLayout().setHeaderText(localCharSequence2);
      setTitle(localCharSequence2);
    }
  }
  
  protected boolean supportStatusBarTransparent()
  {
    return false;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fingerprint\FingerprintEnrollBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
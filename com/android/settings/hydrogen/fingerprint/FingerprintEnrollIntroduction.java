package com.android.settings.hydrogen.fingerprint;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserManager;
import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.ChooseLockGeneric;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settingslib.HelpUtils;
import com.android.setupwizardlib.SetupWizardRecyclerLayout;
import com.android.setupwizardlib.items.IItem;
import com.android.setupwizardlib.items.Item;
import com.android.setupwizardlib.items.RecyclerItemAdapter;
import com.android.setupwizardlib.items.RecyclerItemAdapter.OnItemSelectedListener;

public class FingerprintEnrollIntroduction
  extends FingerprintEnrollBase
  implements RecyclerItemAdapter.OnItemSelectedListener
{
  protected static final int CHOOSE_LOCK_GENERIC_REQUEST = 1;
  protected static final int FINGERPRINT_FIND_SENSOR_REQUEST = 2;
  protected static final int LEARN_MORE_REQUEST = 3;
  private boolean mHasPassword;
  private UserManager mUserManager;
  
  private void launchChooseLock()
  {
    Intent localIntent = getChooseLockIntent();
    long l = ((FingerprintManager)getSystemService(FingerprintManager.class)).preEnroll();
    localIntent.putExtra("minimum_quality", 65536);
    localIntent.putExtra("hide_disabled_prefs", true);
    localIntent.putExtra("has_challenge", true);
    localIntent.putExtra("challenge", l);
    localIntent.putExtra("for_fingerprint", true);
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    startActivityForResult(localIntent, 1);
  }
  
  private void launchFindSensor(byte[] paramArrayOfByte)
  {
    Intent localIntent = getFindSensorIntent();
    if (paramArrayOfByte != null) {
      localIntent.putExtra("hw_auth_token", paramArrayOfByte);
    }
    if (this.mUserId != 55536) {
      localIntent.putExtra("android.intent.extra.USER_ID", this.mUserId);
    }
    startActivityForResult(localIntent, 2);
  }
  
  private void updatePasswordQuality()
  {
    boolean bool = false;
    if (new ChooseLockSettingsHelper(this).utils().getActivePasswordQuality(this.mUserManager.getCredentialOwnerProfile(this.mUserId)) != 0) {
      bool = true;
    }
    this.mHasPassword = bool;
  }
  
  protected Intent getChooseLockIntent()
  {
    return new Intent(this, ChooseLockGeneric.class);
  }
  
  protected Intent getFindSensorIntent()
  {
    return new Intent(this, FingerprintEnrollFindSensor.class);
  }
  
  protected int getMetricsCategory()
  {
    return 243;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    int j = 2;
    if (paramInt2 == 1) {}
    for (int i = 1; paramInt1 == 2; i = 0)
    {
      if ((i == 0) && (paramInt2 != 2)) {
        break label78;
      }
      paramInt1 = j;
      if (i != 0) {
        paramInt1 = -1;
      }
      setResult(paramInt1, paramIntent);
      finish();
      return;
    }
    if ((paramInt1 == 1) && (i != 0))
    {
      updatePasswordQuality();
      launchFindSensor(paramIntent.getByteArrayExtra("hw_auth_token"));
      return;
    }
    label78:
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  protected void onCancelButtonClick()
  {
    finish();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968724);
    setHeaderText(2131691071);
    setHeaderTextColor(2131493771);
    paramBundle = (SetupWizardRecyclerLayout)findViewById(2131362138);
    this.mUserManager = UserManager.get(this);
    RecyclerItemAdapter localRecyclerItemAdapter = (RecyclerItemAdapter)paramBundle.getAdapter();
    localRecyclerItemAdapter.setOnItemSelectedListener(this);
    ((Item)localRecyclerItemAdapter.findItemById(2131362829)).setTitle(LearnMoreSpan.linkify(getText(2131691073), getString(2131693030)));
    paramBundle.setDividerInset(0);
    updatePasswordQuality();
  }
  
  public void onItemSelected(IItem paramIItem)
  {
    switch (((Item)paramIItem).getId())
    {
    default: 
      return;
    case 2131362013: 
      onNextButtonClick();
      return;
    }
    onCancelButtonClick();
  }
  
  protected void onNextButtonClick()
  {
    if (!this.mHasPassword)
    {
      launchChooseLock();
      return;
    }
    launchFindSensor(null);
  }
  
  private static class LearnMoreSpan
    extends URLSpan
  {
    private static final String TAG = "LearnMoreSpan";
    private static final Typeface TYPEFACE_MEDIUM = Typeface.create("sans-serif-medium", 0);
    
    private LearnMoreSpan(String paramString)
    {
      super();
    }
    
    public static CharSequence linkify(CharSequence paramCharSequence, String paramString)
    {
      int i = 0;
      SpannableString localSpannableString = new SpannableString(paramCharSequence);
      Annotation[] arrayOfAnnotation = (Annotation[])localSpannableString.getSpans(0, localSpannableString.length(), Annotation.class);
      int k;
      int m;
      if (TextUtils.isEmpty(paramString))
      {
        j = arrayOfAnnotation.length;
        i = 0;
        while (i < j)
        {
          paramString = arrayOfAnnotation[i];
          k = localSpannableString.getSpanStart(paramString);
          m = localSpannableString.getSpanEnd(paramString);
          paramCharSequence = TextUtils.concat(new CharSequence[] { paramCharSequence.subSequence(0, k), localSpannableString.subSequence(m, localSpannableString.length()) });
          i += 1;
        }
        return paramCharSequence;
      }
      paramCharSequence = new SpannableStringBuilder(localSpannableString);
      int j = arrayOfAnnotation.length;
      while (i < j)
      {
        Object localObject = arrayOfAnnotation[i];
        k = localSpannableString.getSpanStart(localObject);
        m = localSpannableString.getSpanEnd(localObject);
        localObject = new LearnMoreSpan(paramString);
        paramCharSequence.setSpan(localObject, k, m, localSpannableString.getSpanFlags(localObject));
        i += 1;
      }
      return paramCharSequence;
    }
    
    public void onClick(View paramView)
    {
      Object localObject = paramView.getContext();
      localObject = HelpUtils.getHelpIntent((Context)localObject, getURL(), ((Context)localObject).getClass().getName());
      try
      {
        paramView.startActivityForResult((Intent)localObject, 3);
        return;
      }
      catch (ActivityNotFoundException paramView)
      {
        Log.w("LearnMoreSpan", "Actvity was not found for intent, " + ((Intent)localObject).toString());
      }
    }
    
    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
      paramTextPaint.setTypeface(TYPEFACE_MEDIUM);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\hydrogen\fingerprint\FingerprintEnrollIntroduction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
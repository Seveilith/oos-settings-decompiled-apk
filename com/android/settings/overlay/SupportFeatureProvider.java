package com.android.settings.overlay;

import android.accounts.Account;
import android.annotation.IntDef;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.android.settings.support.SupportPhone;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract interface SupportFeatureProvider
{
  public abstract Intent getAccountLoginIntent();
  
  public abstract String getCurrentCountryCodeIfHasConfig(int paramInt);
  
  public abstract int getDisclaimerStringResId();
  
  public abstract String getEstimatedWaitTime(Context paramContext, int paramInt);
  
  public abstract Intent getHelpIntent(Context paramContext);
  
  public abstract CharSequence getOperationHours(Context paramContext, int paramInt, String paramString, boolean paramBoolean);
  
  public abstract List<String> getPhoneSupportCountries();
  
  public abstract List<String> getPhoneSupportCountryCodes();
  
  public abstract Intent getSignInHelpIntent(Context paramContext);
  
  public abstract Account getSupportEligibleAccount(Context paramContext);
  
  public abstract SupportPhone getSupportPhones(String paramString, boolean paramBoolean);
  
  public abstract Intent getTipsAndTricksIntent(Context paramContext);
  
  public abstract boolean isAlwaysOperating(int paramInt, String paramString);
  
  public abstract boolean isOperatingNow(int paramInt);
  
  public abstract boolean isSupportTypeEnabled(Context paramContext, int paramInt);
  
  public abstract void refreshOperationRules();
  
  public abstract void setShouldShowDisclaimerDialog(Context paramContext, boolean paramBoolean);
  
  public abstract boolean shouldShowDisclaimerDialog(Context paramContext);
  
  public abstract void startSupport(Activity paramActivity, Account paramAccount, int paramInt);
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({1L, 2L, 3L})
  public static @interface SupportType
  {
    public static final int CHAT = 3;
    public static final int EMAIL = 1;
    public static final int PHONE = 2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\overlay\SupportFeatureProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
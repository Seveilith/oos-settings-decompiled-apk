package com.android.settings.accounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;

public class ProviderPreference
  extends RestrictedPreference
{
  private String mAccountType;
  
  public ProviderPreference(Context paramContext, String paramString, Drawable paramDrawable, CharSequence paramCharSequence)
  {
    super(paramContext);
    setLayoutResource(2130968831);
    this.mAccountType = paramString;
    setIcon(paramDrawable);
    setPersistent(false);
    setTitle(paramCharSequence);
    useAdminDisabledSummary(true);
  }
  
  public void checkAccountManagementAndSetDisabled(int paramInt)
  {
    setDisabledByAdmin(RestrictedLockUtils.checkIfAccountManagementDisabled(getContext(), getAccountType(), paramInt));
  }
  
  public String getAccountType()
  {
    return this.mAccountType;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\accounts\ProviderPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
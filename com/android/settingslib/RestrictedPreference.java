package com.android.settingslib;

import android.content.Context;
import android.os.UserHandle;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.R.attr;
import android.util.AttributeSet;
import android.view.View;

public class RestrictedPreference
  extends Preference
{
  RestrictedPreferenceHelper mHelper;
  
  public RestrictedPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RestrictedPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.preferenceStyle, 16842894));
  }
  
  public RestrictedPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RestrictedPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setWidgetLayoutResource(R.layout.restricted_icon);
    this.mHelper = new RestrictedPreferenceHelper(paramContext, this, paramAttributeSet);
  }
  
  public void checkRestrictionAndSetDisabled(String paramString)
  {
    this.mHelper.checkRestrictionAndSetDisabled(paramString, UserHandle.myUserId());
  }
  
  public void checkRestrictionAndSetDisabled(String paramString, int paramInt)
  {
    this.mHelper.checkRestrictionAndSetDisabled(paramString, paramInt);
  }
  
  public boolean isDisabledByAdmin()
  {
    return this.mHelper.isDisabledByAdmin();
  }
  
  protected void onAttachedToHierarchy(PreferenceManager paramPreferenceManager)
  {
    this.mHelper.onAttachedToHierarchy();
    super.onAttachedToHierarchy(paramPreferenceManager);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mHelper.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(R.id.restricted_icon);
    if (paramPreferenceViewHolder != null) {
      if (!isDisabledByAdmin()) {
        break label40;
      }
    }
    label40:
    for (int i = 0;; i = 8)
    {
      paramPreferenceViewHolder.setVisibility(i);
      return;
    }
  }
  
  public void performClick()
  {
    if (!this.mHelper.performClick()) {
      super.performClick();
    }
  }
  
  public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    if (this.mHelper.setDisabledByAdmin(paramEnforcedAdmin)) {
      notifyChanged();
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if ((paramBoolean) && (isDisabledByAdmin()))
    {
      this.mHelper.setDisabledByAdmin(null);
      return;
    }
    super.setEnabled(paramBoolean);
  }
  
  public void useAdminDisabledSummary(boolean paramBoolean)
  {
    this.mHelper.useAdminDisabledSummary(paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\RestrictedPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
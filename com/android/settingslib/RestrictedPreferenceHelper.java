package com.android.settingslib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class RestrictedPreferenceHelper
{
  private String mAttrUserRestriction = null;
  private final Context mContext;
  private boolean mDisabledByAdmin;
  private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
  private final Preference mPreference;
  private boolean mUseAdminDisabledSummary = false;
  
  public RestrictedPreferenceHelper(Context paramContext, Preference paramPreference, AttributeSet paramAttributeSet)
  {
    this.mContext = paramContext;
    this.mPreference = paramPreference;
    label120:
    label139:
    boolean bool;
    if (paramAttributeSet != null)
    {
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RestrictedPreference);
      TypedValue localTypedValue = localTypedArray.peekValue(R.styleable.RestrictedPreference_userRestriction);
      paramAttributeSet = null;
      paramPreference = paramAttributeSet;
      if (localTypedValue != null)
      {
        paramPreference = paramAttributeSet;
        if (localTypedValue.type == 3)
        {
          if (localTypedValue.resourceId == 0) {
            break label120;
          }
          paramPreference = paramContext.getText(localTypedValue.resourceId);
        }
      }
      if (paramPreference == null) {}
      for (paramContext = null;; paramContext = paramPreference.toString())
      {
        this.mAttrUserRestriction = paramContext;
        if (!RestrictedLockUtils.hasBaseUserRestriction(this.mContext, this.mAttrUserRestriction, UserHandle.myUserId())) {
          break label139;
        }
        this.mAttrUserRestriction = null;
        return;
        paramPreference = localTypedValue.string;
        break;
      }
      paramContext = localTypedArray.peekValue(R.styleable.RestrictedPreference_useAdminDisabledSummary);
      if (paramContext != null)
      {
        if (paramContext.type != 18) {
          break label184;
        }
        if (paramContext.data == 0) {
          break label178;
        }
        bool = true;
      }
    }
    for (;;)
    {
      this.mUseAdminDisabledSummary = bool;
      return;
      label178:
      bool = false;
      continue;
      label184:
      bool = false;
    }
  }
  
  public void checkRestrictionAndSetDisabled(String paramString, int paramInt)
  {
    setDisabledByAdmin(RestrictedLockUtils.checkIfRestrictionEnforced(this.mContext, paramString, paramInt));
  }
  
  public boolean isDisabledByAdmin()
  {
    return this.mDisabledByAdmin;
  }
  
  public void onAttachedToHierarchy()
  {
    if (this.mAttrUserRestriction != null) {
      checkRestrictionAndSetDisabled(this.mAttrUserRestriction, UserHandle.myUserId());
    }
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    if (this.mDisabledByAdmin) {
      paramPreferenceViewHolder.itemView.setEnabled(true);
    }
    if (this.mUseAdminDisabledSummary)
    {
      paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908304);
      if (paramPreferenceViewHolder != null)
      {
        if (!this.mDisabledByAdmin) {
          break label56;
        }
        paramPreferenceViewHolder.setText(R.string.disabled_by_admin_summary_text);
        paramPreferenceViewHolder.setVisibility(0);
      }
    }
    return;
    label56:
    paramPreferenceViewHolder.setVisibility(8);
  }
  
  public boolean performClick()
  {
    if (this.mDisabledByAdmin)
    {
      RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
      return true;
    }
    return false;
  }
  
  public boolean setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin paramEnforcedAdmin)
  {
    boolean bool1;
    if (paramEnforcedAdmin != null)
    {
      bool2 = true;
      this.mEnforcedAdmin = paramEnforcedAdmin;
      bool1 = false;
      if (this.mDisabledByAdmin != bool2)
      {
        this.mDisabledByAdmin = bool2;
        bool1 = true;
      }
      paramEnforcedAdmin = this.mPreference;
      if (!bool2) {
        break label51;
      }
    }
    label51:
    for (boolean bool2 = false;; bool2 = true)
    {
      paramEnforcedAdmin.setEnabled(bool2);
      return bool1;
      bool2 = false;
      break;
    }
  }
  
  public void useAdminDisabledSummary(boolean paramBoolean)
  {
    this.mUseAdminDisabledSummary = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\RestrictedPreferenceHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
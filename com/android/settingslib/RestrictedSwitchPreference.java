package com.android.settingslib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.UserHandle;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.preference.R.attr;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class RestrictedSwitchPreference
  extends SwitchPreference
{
  RestrictedPreferenceHelper mHelper;
  String mRestrictedSwitchSummary = null;
  boolean mUseAdditionalSummary = false;
  
  public RestrictedSwitchPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RestrictedSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, TypedArrayUtils.getAttr(paramContext, R.attr.switchPreferenceStyle, 16843629));
  }
  
  public RestrictedSwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public RestrictedSwitchPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setWidgetLayoutResource(R.layout.restricted_switch_widget);
    this.mHelper = new RestrictedPreferenceHelper(paramContext, this, paramAttributeSet);
    boolean bool;
    TypedValue localTypedValue2;
    if (paramAttributeSet != null)
    {
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RestrictedSwitchPreference);
      TypedValue localTypedValue1 = paramAttributeSet.peekValue(R.styleable.RestrictedSwitchPreference_useAdditionalSummary);
      if (localTypedValue1 != null)
      {
        if (localTypedValue1.type != 18) {
          break label199;
        }
        if (localTypedValue1.data == 0) {
          break label193;
        }
        bool = true;
        this.mUseAdditionalSummary = bool;
      }
      localTypedValue2 = paramAttributeSet.peekValue(R.styleable.RestrictedSwitchPreference_restrictedSwitchSummary);
      localTypedValue1 = null;
      paramAttributeSet = localTypedValue1;
      if (localTypedValue2 != null)
      {
        paramAttributeSet = localTypedValue1;
        if (localTypedValue2.type == 3)
        {
          if (localTypedValue2.resourceId == 0) {
            break label205;
          }
          paramAttributeSet = paramContext.getString(localTypedValue2.resourceId);
        }
      }
      label144:
      if (paramAttributeSet != null) {
        break label214;
      }
    }
    label193:
    label199:
    label205:
    label214:
    for (paramAttributeSet = null;; paramAttributeSet = paramAttributeSet.toString())
    {
      this.mRestrictedSwitchSummary = paramAttributeSet;
      if (this.mRestrictedSwitchSummary == null) {
        this.mRestrictedSwitchSummary = paramContext.getString(R.string.disabled_by_admin);
      }
      if (this.mUseAdditionalSummary)
      {
        setLayoutResource(R.layout.restricted_switch_preference);
        useAdminDisabledSummary(false);
      }
      return;
      bool = false;
      break;
      bool = false;
      break;
      paramAttributeSet = localTypedValue2.string;
      break label144;
    }
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
    View localView1 = paramPreferenceViewHolder.findViewById(R.id.restricted_icon);
    View localView2 = paramPreferenceViewHolder.findViewById(16908352);
    int i;
    if (localView1 != null)
    {
      if (isDisabledByAdmin())
      {
        i = 0;
        localView1.setVisibility(i);
      }
    }
    else
    {
      if (localView2 != null)
      {
        if (!isDisabledByAdmin()) {
          break label117;
        }
        i = 8;
        label62:
        localView2.setVisibility(i);
      }
      if (!this.mUseAdditionalSummary) {
        break label129;
      }
      paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(R.id.additional_summary);
      if (paramPreferenceViewHolder != null)
      {
        if (!isDisabledByAdmin()) {
          break label122;
        }
        paramPreferenceViewHolder.setText(this.mRestrictedSwitchSummary);
        paramPreferenceViewHolder.setVisibility(0);
      }
    }
    label117:
    label122:
    label129:
    do
    {
      return;
      i = 8;
      break;
      i = 0;
      break label62;
      paramPreferenceViewHolder.setVisibility(8);
      return;
      paramPreferenceViewHolder = (TextView)paramPreferenceViewHolder.findViewById(16908304);
    } while ((paramPreferenceViewHolder == null) || (!isDisabledByAdmin()));
    paramPreferenceViewHolder.setText(this.mRestrictedSwitchSummary);
    paramPreferenceViewHolder.setVisibility(0);
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\RestrictedSwitchPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
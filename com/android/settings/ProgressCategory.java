package com.android.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCategory
  extends ProgressCategoryBase
{
  private int mEmptyTextRes;
  private boolean mNoDeviceFoundAdded;
  private Preference mNoDeviceFoundPreference;
  private boolean mProgress = false;
  
  public ProgressCategory(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ProgressCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0);
  }
  
  public ProgressCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ProgressCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130968921);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131362448);
    int i;
    int j;
    if (getPreferenceCount() != 0)
    {
      if ((getPreferenceCount() != 1) || (getPreference(0) != this.mNoDeviceFoundPreference)) {
        break label146;
      }
      i = 1;
      if (!this.mProgress) {
        break label151;
      }
      j = 0;
      label50:
      paramPreferenceViewHolder.setVisibility(j);
      if ((this.mProgress) || (i == 0)) {
        break label157;
      }
      if (!this.mNoDeviceFoundAdded)
      {
        if (this.mNoDeviceFoundPreference == null)
        {
          this.mNoDeviceFoundPreference = new Preference(getPreferenceManager().getContext());
          this.mNoDeviceFoundPreference.setLayoutResource(2130968904);
          this.mNoDeviceFoundPreference.setTitle(this.mEmptyTextRes);
          this.mNoDeviceFoundPreference.setSelectable(false);
        }
        addPreference(this.mNoDeviceFoundPreference);
        this.mNoDeviceFoundAdded = true;
      }
    }
    label146:
    label151:
    label157:
    while (!this.mNoDeviceFoundAdded)
    {
      return;
      i = 1;
      break;
      i = 0;
      break;
      j = 8;
      break label50;
    }
    removePreference(this.mNoDeviceFoundPreference);
    this.mNoDeviceFoundAdded = false;
  }
  
  public void setEmptyTextRes(int paramInt)
  {
    this.mEmptyTextRes = paramInt;
  }
  
  public void setProgress(boolean paramBoolean)
  {
    this.mProgress = paramBoolean;
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ProgressCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */
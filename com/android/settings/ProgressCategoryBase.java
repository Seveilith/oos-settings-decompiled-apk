package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.util.AttributeSet;

public abstract class ProgressCategoryBase
  extends PreferenceCategory
{
  public ProgressCategoryBase(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ProgressCategoryBase(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ProgressCategoryBase(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ProgressCategoryBase(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public abstract void setProgress(boolean paramBoolean);
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ProgressCategoryBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */